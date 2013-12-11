package fr.pfgen.cgh.server.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import fr.pfgen.cgh.client.services.AnalysisService;
import fr.pfgen.cgh.server.database.ArrayTable;
import fr.pfgen.cgh.server.database.ConnectionPool;
import fr.pfgen.cgh.server.utils.AnalysisRunningPojo;
import fr.pfgen.cgh.server.utils.GlobalDefs;
import fr.pfgen.cgh.server.utils.IOUtils;
import fr.pfgen.cgh.server.utils.ServerUtils;
import fr.pfgen.cgh.server.utils.files.AnalysisConfigFile;
import fr.pfgen.cgh.server.utils.threads.AnalysisRunningThread;
import fr.pfgen.cgh.server.utils.threads.CGHAnalysisThread;
import fr.pfgen.cgh.shared.enums.AnalysisType;
import fr.pfgen.cgh.shared.enums.ConfigFileType;
import fr.pfgen.cgh.shared.enums.FileType;
import fr.pfgen.cgh.shared.records.AnalysisParamsRecord;
import fr.pfgen.cgh.shared.records.ArrayRecord;
import fr.pfgen.cgh.shared.records.ProjectRecord;
import fr.pfgen.cgh.shared.records.UserRecord;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class AnalysisServiceImpl extends RemoteServiceServlet implements AnalysisService {

	private ConnectionPool pool;
	private Hashtable<String, File> appFiles;
	
	@SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pool = (ConnectionPool)getServletContext().getAttribute("ConnectionPool");
		appFiles = (Hashtable<String, File>)getServletContext().getAttribute("ApplicationFiles");
	}

	@Override
	public AnalysisParamsRecord getDefaultAnalysisParams() {
		return GlobalDefs.getInstance().getDefaultAnalysisParamRecord();
	}
	
	@Override
	public String saveProjectConfigFile(String projectName, AnalysisParamsRecord rec) {
		AnalysisConfigFile confFile = new AnalysisConfigFile(appFiles, projectName, ConfigFileType.PROJECT);
		if (confFile.exists()){
			return "Error: project configuration file already exists";
		}
		confFile.writeConfigFile(rec);
		if (confFile.exists()){
			return "Configuration created successfully for project "+projectName;
		}else{
			return "Error: could not create configuration file for project";
		}
	}
	
	@Override
	public String saveUserConfigFile(String appID, AnalysisParamsRecord rec) {
		AnalysisConfigFile confFile = new AnalysisConfigFile(appFiles, appID, ConfigFileType.USER);
		
		confFile.writeConfigFile(rec);
		if (confFile.exists()){
			return "Configuration created successfully for user "+appID;
		}else{
			return "Error: could not create configuration file for user";
		}
	}
	
	@Override
	public Boolean projectConfigFileExists(String projectName) {
		AnalysisConfigFile confFile = new AnalysisConfigFile(appFiles, projectName, ConfigFileType.PROJECT);
		return confFile.exists();
	}
	
	@Override
	public Boolean userConfigFileExists(String appID){
		AnalysisConfigFile confFile = new AnalysisConfigFile(appFiles, appID, ConfigFileType.USER);
		return confFile.exists();
	}

	@Override
	public Boolean isProjectAnalysisRunning(int projectID) {
		@SuppressWarnings("unchecked")
		Set<AnalysisRunningPojo> runningAnalysis = (Set<AnalysisRunningPojo>)getServletContext().getAttribute("AnalysisRunning");
		if (runningAnalysis==null) return false;
		
		AnalysisRunningPojo arp = new AnalysisRunningPojo(projectID, AnalysisType.PROJECT);
		if (runningAnalysis.contains(arp)){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public Boolean isUserAnalysisRunning(int userID){
		@SuppressWarnings("unchecked")
		Set<AnalysisRunningPojo> runningAnalysis = (Set<AnalysisRunningPojo>)getServletContext().getAttribute("AnalysisRunning");
		if (runningAnalysis==null) return false;
		
		AnalysisRunningPojo arp = new AnalysisRunningPojo(userID, AnalysisType.USER);
		if (runningAnalysis.contains(arp)){
			return true;
		}else{
			return false;
		}
	}
	
	private synchronized void addAnalysisRunning(AnalysisRunningPojo arp){
		@SuppressWarnings("unchecked")
		Set<AnalysisRunningPojo> runningAnalysis = (Set<AnalysisRunningPojo>)getServletContext().getAttribute("AnalysisRunning");
		if (runningAnalysis==null){
			Set<AnalysisRunningPojo> newSet = new HashSet<AnalysisRunningPojo>();
			newSet.add(arp);
			getServletContext().setAttribute("AnalysisRunning", newSet);
		}else{
			runningAnalysis.add(arp);
		}
	}
	
	private synchronized void removeAnalysisRunning(AnalysisRunningPojo arp){
		@SuppressWarnings("unchecked")
		Set<AnalysisRunningPojo> runningAnalysis = (Set<AnalysisRunningPojo>)getServletContext().getAttribute("AnalysisRunning");
		if (runningAnalysis!=null){
			runningAnalysis.remove(arp);
		}
		arp.getLogFile().delete();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Boolean removeProjectAnalysis(int projectID) {
		AnalysisRunningPojo arp = new AnalysisRunningPojo(projectID, AnalysisType.PROJECT);
		AnalysisRunningPojo ap = null;
		for (AnalysisRunningPojo a : (Set<AnalysisRunningPojo>)getServletContext().getAttribute("AnalysisRunning")) {
			if (a.equals(arp)){ap = a;break;}
		}
		removeAnalysisRunning(ap);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Boolean removeUserAnalysis(int userID) {
		AnalysisRunningPojo arp = new AnalysisRunningPojo(userID, AnalysisType.USER);
		AnalysisRunningPojo ap = null;
		for (AnalysisRunningPojo a : (Set<AnalysisRunningPojo>)getServletContext().getAttribute("AnalysisRunning")) {
			if (a.equals(arp)){ap = a;break;}
		}
		removeAnalysisRunning(ap);
		return true;
	}
	
	@Override
	public Map<String, String> projectAnalysisAdvances(int projectID) {
		@SuppressWarnings("unchecked")
		Set<AnalysisRunningPojo> runningAnalysis = (Set<AnalysisRunningPojo>)getServletContext().getAttribute("AnalysisRunning");
		if (runningAnalysis==null) return null;
		
		AnalysisRunningPojo arp = new AnalysisRunningPojo(projectID, AnalysisType.PROJECT);
		
		if (runningAnalysis.contains(arp)){
			Map<String, String> map = new HashMap<String, String>();
			AnalysisRunningPojo ap = null;
			for (AnalysisRunningPojo a : runningAnalysis) {
				if (a.equals(arp)){ap = a;break;}
			}
			try {
				if (ap.getThread().isAlive()){
					map.put("running", "yes");
				}else{
					map.put("running", "no");
				}
				map.put("log", IOUtils.fileContentToString(ap.getLogFile()));
				return map;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
	
	@Override
	public Map<String, String> userAnalysisAdvances(int userID) {
		@SuppressWarnings("unchecked")
		Set<AnalysisRunningPojo> runningAnalysis = (Set<AnalysisRunningPojo>)getServletContext().getAttribute("AnalysisRunning");
		if (runningAnalysis==null) return null;
		
		AnalysisRunningPojo arp = new AnalysisRunningPojo(userID, AnalysisType.USER);
		
		if (runningAnalysis.contains(arp)){
			Map<String, String> map = new HashMap<String, String>();
			AnalysisRunningPojo ap = null;
			for (AnalysisRunningPojo a : runningAnalysis) {
				if (a.equals(arp)){ap = a;break;}
			}
			try {
				if (ap.getThread().isAlive()){
					map.put("running", "yes");
				}else{
					map.put("running", "no");
				}
				map.put("log", IOUtils.fileContentToString(ap.getLogFile()));
				return map;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}

	@Override
	public synchronized String launchAnalysisForProject(String filename, ProjectRecord project, UserRecord user) {
		
		AnalysisRunningPojo arp = new AnalysisRunningPojo(project.getProjectID(), AnalysisType.PROJECT);
		addAnalysisRunning(arp);
		
		File tmpFolder = appFiles.get("temporaryFolder");
		File uploadedFile = new File(filename);

		FileType ft = FileType.getFileType(uploadedFile.getName());
		if (ft==null) return "Error: cannot find file extension type";
		
		File analysisFolder;
		try{
			analysisFolder = IOUtils.createTempDir(tmpFolder);
		}catch(IOException e){
			e.printStackTrace();
			removeAnalysisRunning(arp);
			return "Error: could not create temporary folder for analysis";
		}
		
		List<File> files = getFileToProcess(uploadedFile, ft, analysisFolder);
		if (files.isEmpty()){
			ServerUtils.deleteDirectory(analysisFolder);
			removeAnalysisRunning(arp);
			return "Error: no FE files found in uploaded file";
		}
		
		Map<String, String> crit = new Hashtable<String, String>();
		crit.put("project_id", String.valueOf(project.getProjectID()));
		String query = ArrayTable.constructQuery(null, crit);
		List<ArrayRecord> arraysInProject = ArrayTable.getArrays(pool, query, null, null);
		
		Boolean firstArray;
		if (arraysInProject.isEmpty()){
			firstArray = true;
		}else{
			firstArray = false;
		}
		AnalysisConfigFile configFile = new AnalysisConfigFile(appFiles, project.getProjectName(), ConfigFileType.PROJECT);
		File destFolder = new File(project.getProjectPath());
		File logFile;
		try {
			logFile = File.createTempFile("logFile_", ".txt", tmpFolder);
		} catch (IOException e) {
			e.printStackTrace();
			removeAnalysisRunning(arp);
			return "Error: could not create log file";
		}
		arp.setLogFile(logFile);
		
		CGHAnalysisThread t = new CGHAnalysisThread(pool, appFiles, analysisFolder, files, configFile.getFile(), destFolder, firstArray, project.getProjectID(), user.getUserID(), logFile);
		
		ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
		threadExecutor.execute(t);
		threadExecutor.shutdown();
		
		AnalysisRunningThread listeningThread = new AnalysisRunningThread(threadExecutor, analysisFolder, uploadedFile);
		listeningThread.start();
		arp.setThread(listeningThread);
		
		return "";
	}
	
	@Override
	public synchronized String launchAnalysisForUser(String filename, UserRecord user) {
		
		AnalysisRunningPojo arp = new AnalysisRunningPojo(user.getUserID(), AnalysisType.USER);
		addAnalysisRunning(arp);
		
		File tmpFolder = appFiles.get("temporaryFolder");
		File uploadedFile = new File(filename);

		FileType ft = FileType.getFileType(uploadedFile.getName());
		if (ft==null) return "Error: cannot find file extension type";
		
		File analysisFolder;
		try{
			analysisFolder = IOUtils.createTempDir(tmpFolder);
		}catch(IOException e){
			e.printStackTrace();
			removeAnalysisRunning(arp);
			return "Error: could not create temporary folder for analysis";
		}
		
		List<File> files = getFileToProcess(uploadedFile, ft, analysisFolder);
		if (files.isEmpty()){
			ServerUtils.deleteDirectory(analysisFolder);
			return "Error: no FE files found in uploaded file";
		}
		
		AnalysisConfigFile configFile = new AnalysisConfigFile(appFiles, user.getAppID(), ConfigFileType.USER);
		File userArraysFolder = new File(appFiles.get("mainFile"), "UserArrays");
		if (!userArraysFolder.exists()){
			userArraysFolder.mkdir();
		}
		
		File destFolder = new File(userArraysFolder, user.getAppID());
		File logFile;
		try {
			logFile = File.createTempFile("logFile_", ".txt", tmpFolder);
		} catch (IOException e) {
			e.printStackTrace();
			removeAnalysisRunning(arp);
			return "Error: could not create log file";
		}
		arp.setLogFile(logFile);
		
		CGHAnalysisThread t = new CGHAnalysisThread(pool, appFiles, analysisFolder, files, configFile.getFile(), destFolder, null, null, user.getUserID(), logFile);
		
		ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
		threadExecutor.execute(t);
		threadExecutor.shutdown();
		
		AnalysisRunningThread listeningThread = new AnalysisRunningThread(threadExecutor, analysisFolder, uploadedFile);
		listeningThread.start();
		arp.setThread(listeningThread);
		
		return "";
	}
	
	private List<File> getFileToProcess(File uploadedFile, FileType ft, File analysisFolder){
		List<File> fileList = new ArrayList<File>();
		Pattern feFilePatter = Pattern.compile("^US\\d+_(\\d+)_S0\\d_CGH_10\\d_[A-Za-z0-9]{5}(_\\d_\\d)?\\.txt(\\.gz)?$");
		switch (ft) {
			case TXT:
				if (feFilePatter.matcher(uploadedFile.getName()).matches()){
					fileList.add(uploadedFile);
				}
				break;
			case GZ:
				if (feFilePatter.matcher(uploadedFile.getName()).matches()){
					fileList.add(uploadedFile);
				}
				break;
			case ZIP:
				BufferedOutputStream dest = null;
		        BufferedInputStream is = null;
		        
				try {     
					File tmpFolder = IOUtils.createTempDir(analysisFolder);
					
					ZipEntry entry;
			        ZipFile zipfile = new ZipFile(uploadedFile);
			        Enumeration<? extends ZipEntry> e = zipfile.entries();
			        while(e.hasMoreElements()) {
			        	entry = e.nextElement();
			        	if (!feFilePatter.matcher(entry.getName()).matches()){
			        		continue;
			        	}
			        	File f = new File(tmpFolder, entry.getName());
			            is = new BufferedInputStream(zipfile.getInputStream(entry));
			            dest = new BufferedOutputStream(new FileOutputStream(f));
			            IOUtils.copyTo(is, dest);
			            dest.flush();
			            dest.close();
			            is.close();
			            fileList.add(f);
			         }
			      }catch(Exception e) {
			         e.printStackTrace();
			         return null;
			      }finally{
			    	  IOUtils.safeClose(is);
			    	  IOUtils.safeClose(dest);
			      }
				break;
			case TAR:
				//return "Error: cannot process this file type, please send an FE file ('.txt') or archive ('.zip')";
				//TODO import API to process
			case TARGZ:
				//return "Error: cannot process this file type, please send an FE file ('.txt') or archive ('.zip')";
				//TODO import API to process
			default:
				//return "Error: cannot process this file type, please send an FE file ('.txt') or archive ('.zip')";
		}
		
		return fileList;
	}
}
