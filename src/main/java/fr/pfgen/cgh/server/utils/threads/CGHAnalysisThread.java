package fr.pfgen.cgh.server.utils.threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.server.database.ArrayTable;
import fr.pfgen.cgh.server.database.ConnectionPool;
import fr.pfgen.cgh.server.database.DetectionTable;
import fr.pfgen.cgh.server.database.FrameTable;
import fr.pfgen.cgh.server.database.QcParamTable;
import fr.pfgen.cgh.server.database.ScanParamTable;
import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.server.utils.IOUtils;
import fr.pfgen.cgh.server.utils.ServerUtils;
import fr.pfgen.cgh.server.utils.files.AnalysisConfigFile;
import fr.pfgen.cgh.server.utils.files.ReportCNVFile;
import fr.pfgen.cgh.server.utils.files.ReportQCFile;
import fr.pfgen.cgh.shared.records.AnalysisParamsRecord;
import fr.pfgen.cgh.shared.records.ArrayRecord;
import fr.pfgen.cgh.shared.records.DetectionRecord;
import fr.pfgen.cgh.shared.records.FrameRecord;

public class CGHAnalysisThread extends Thread{
	
	private ConnectionPool pool;
	private Map<String, File> appFiles;
	private File analysisFolder;
	private List<File> feFiles;
	private File configFile;
	private File destFolder;
	private Boolean firstArray;
	private Integer projectID;
	private Integer userID;
	private File logFile;
	
	public CGHAnalysisThread(ConnectionPool pool, Map<String, File> appFiles, File analysisFolder, List<File> feFiles, File configFile, File destFolder, Boolean firstArray, Integer projectID, Integer userID, File logFile){
		this.analysisFolder = analysisFolder;
		this.feFiles = feFiles;
		this.configFile = configFile;
		this.destFolder = destFolder;
		this.firstArray = firstArray;
		this.projectID = projectID;
		this.userID = userID;
		this.logFile = logFile;
		this.pool = pool;
		this.appFiles = appFiles;
	}
	
	@Override
	public void run(){
		
		PrintWriter logPW = null;
		ProcessBuilder procBuilder = null;
		
		for (File feFile : feFiles) {
			String[] cmd = {appFiles.get("PerlBin").getAbsolutePath(), appFiles.get("PScripts").getAbsolutePath()+"/cgh_scan.pl", "-c", configFile.getAbsolutePath(), "-r", analysisFolder.getAbsolutePath(), feFile.getAbsolutePath()};
			
			Process process = null;
			BufferedReader br = null;
			File arrayResultFolder = null;
			
			try{
				procBuilder = new ProcessBuilder(cmd);
				procBuilder.redirectErrorStream(true);
				logPW = new PrintWriter(new FileWriter(logFile, true));
			
				process = procBuilder.start();
				br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while((line = br.readLine()) != null){
					logPW.println(line);
					logPW.flush();
				}
				logPW.flush();
				process.waitFor();
				
				arrayResultFolder = getArrayResultFolder();
				
				if (process.exitValue()==0){
					
					if (checkIfResultsOK(arrayResultFolder)){
						Map<String, String> buildAndGrid = getBuildGrid(arrayResultFolder);
						if (firstArray!=null){
							if (firstArray){
								if (buildAndGrid==null || buildAndGrid.isEmpty()){
									logPW.println("Cannot get genomic build and grid name for first array analysed. Deleting result folder.");
									ServerUtils.deleteDirectory(arrayResultFolder);
								}else{
									addBuildAndGridToConfigFile(buildAndGrid);
									firstArray = false;
								}
							}
						}
						File resultDestFolder = new File(new File(destFolder, "results"), arrayResultFolder.getName());
						IOUtils.copyDirectory(arrayResultFolder, resultDestFolder);
						feFile = IOUtils.gzFile(feFile, true);
						File frameDestFile = new File(new File(destFolder, "data"), feFile.getName());
						IOUtils.copyFile(feFile.getAbsolutePath(), frameDestFile.getAbsolutePath());
						try{
							logPW.println("Adding results to database...");
							logPW.println();
							addResultsToDB(frameDestFile, resultDestFolder, buildAndGrid);
						}catch(Exception e){
							e.printStackTrace();
							ServerUtils.deleteDirectory(resultDestFolder);
							frameDestFile.delete();
						}
					}else{
						logPW.println("Results seem corrupted, deleting temporary result directory");
						ServerUtils.deleteDirectory(arrayResultFolder);
					}
				}else{
					logPW.println("Deleting temporary result directory");
					ServerUtils.deleteDirectory(arrayResultFolder);
				}
				logPW.flush();
				logPW.close();
				br.close();
			}catch(Throwable e){
				e.printStackTrace();
				if (arrayResultFolder!=null){
					ServerUtils.deleteDirectory(arrayResultFolder);
				}
			}finally{
				if(process!=null) {try { process.destroy();} catch(Exception err) {throw new RuntimeException(err);}}
				IOUtils.safeClose(br);
				IOUtils.safeClose(logPW);
				if (arrayResultFolder!=null){
					ServerUtils.deleteDirectory(arrayResultFolder);
				}
			}
		}
	}
	
	private void addResultsToDB(File feDestFile, File resultDestFolder, Map<String, String> params){
		ArrayRecord arrayRec = new ArrayRecord();
		arrayRec.setName(params.get("file").split("_")[0]);
		arrayRec.setGenomicBuild(params.get("genomic build"));
		arrayRec.setDesignName(params.get("grid name"));
		try {
			arrayRec.setScanDate(DatabaseUtils.stringToDate(params.get("scan date")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		arrayRec.setFeVersion(params.get("FE version"));
		if (projectID!=null){
			arrayRec.setProjectId(projectID);
		}
		arrayRec.setUserID(userID);
		
		arrayRec = ArrayTable.addArray(pool, arrayRec);
		if (arrayRec==null){
			throw new RuntimeException("Cannot add array to database");
		}
		
		AnalysisParamsRecord paramRec = AnalysisConfigFile.fileToAnalysisParamRecord(configFile);
		Map<String, String> paramMap = AnalysisConfigFile.analysisParamsToMap(paramRec);
		ScanParamTable.addScanParamsToArray(pool, paramMap, arrayRec.getID());
		
		FrameRecord frameRec = new FrameRecord();
		frameRec.setName(params.get("file"));
		frameRec.setFEFilePath(feDestFile.getAbsolutePath());
		frameRec.setResultFolderPath(resultDestFolder.getAbsolutePath());
		frameRec.setArrayID(arrayRec.getID());
		
		frameRec = FrameTable.addFrame(pool, frameRec);
		if (frameRec == null){
			throw new RuntimeException("Cannot add frame to database");
		}
		
		File QCFile = resultDestFolder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith("_reportQC.txt")) return true;
				return false;
			}
		})[0];
		
		ReportQCFile reportQcFile = new ReportQCFile(QCFile);
		Map<String, String> qcMap = reportQcFile.getMapFromFile();
		QcParamTable.addQcToFrame(pool, qcMap, frameRec.getID());
		
		File CNVFile = resultDestFolder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith("_reportCNV.txt")) return true;
				return false;
			}
		})[0];
		
		ReportCNVFile reportCNVFile = new ReportCNVFile(CNVFile);
		List<DetectionRecord> detList = reportCNVFile.getDetectionsFromFile();
		DetectionTable.addCNVToFrame(pool, detList, frameRec.getID());
	}
	
	private File getArrayResultFolder(){
		File[] resultDir = analysisFolder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith("results_")){ 
					return true;
				}else{
					return false;
				}
			}
		});
		
		if (resultDir[0]!=null && resultDir[0].exists()){
			return resultDir[0];
		}else{
			return null;
		}
	}
	
	private boolean checkIfResultsOK(File arrayResultFolder){
		if (arrayResultFolder.listFiles().length!=8) return false;
		return true;
	}
	
	private Map<String, String> getBuildGrid(File arrayResultFolder){
		Map<String, String> map = new Hashtable<String, String>();
		
		for (File file : arrayResultFolder.listFiles()) {
			if (file.getName().endsWith("_frameParams.txt")){
				BufferedReader br = null;
				
				try{
					br = IOUtils.openFile(file);
					String line;
					while((line = br.readLine())!=null){
						String[] linesplit = line.split("\\t");
						if (linesplit.length==2){
							map.put(linesplit[0], linesplit[1]);
						}
					}
					br.close();
				}catch(IOException e){
					e.printStackTrace();
				}finally{
					IOUtils.safeClose(br);
				}
				break;
			}
		}
		return map;
	}
	
	private void addBuildAndGridToConfigFile(Map<String, String> map){
		PrintWriter pw = null;
		
		try{
			pw = new PrintWriter(new FileWriter(configFile, true));
			pw.println("GenomicBuild"+":"+map.get("genomic build"));
			pw.println("Grid_Name"+":"+map.get("grid name"));
			pw.flush();
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			IOUtils.safeClose(pw);
		}
	}
}