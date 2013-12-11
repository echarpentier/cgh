package fr.pfgen.cgh.server.servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import fr.pfgen.cgh.server.database.ConnectionPool;
import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.server.utils.GlobalDefs;
import fr.pfgen.cgh.server.utils.ServerUtils;
import fr.pfgen.cgh.server.utils.files.AnalysisConfigFile;
import fr.pfgen.cgh.shared.enums.ConfigFileType;
import fr.pfgen.cgh.shared.enums.DelDupColors;
import fr.pfgen.cgh.shared.enums.YesNo;
import fr.pfgen.cgh.shared.records.AnalysisParamsRecord;

public class CghContextListener implements ServletContextListener {
	
	@Override
	public void contextDestroyed(ServletContextEvent ctx) {
		ConnectionPool pool = (ConnectionPool)ctx.getServletContext().getAttribute("ConnectionPool");
		if (pool != null){
			pool.closeAllConnections();
			pool = null;
			ctx.getServletContext().removeAttribute("ConnectionPool");
		}
	
		@SuppressWarnings("unchecked")
		Map<String, File> m = (Map<String, File>)ctx.getServletContext().getAttribute("ApplicationFiles");
		if(m!=null){	
			File tmpFolder = m.get("temporaryFolder");
			if(tmpFolder!=null) ServerUtils.deleteDirectory(tmpFolder);
		}		
	}

	@Override
	public void contextInitialized(ServletContextEvent ctx){
		String cfgFile=ctx.getServletContext().getInitParameter("configurationFile");
		if(cfgFile==null) throw new RuntimeException("No path for configuration file found at application start up !");		
		Properties props = new Properties();
		InputStream xmlStream = null;
		
		try {
			xmlStream=ctx.getServletContext().getResourceAsStream("/WEB-INF/config/"+cfgFile);
			if(xmlStream==null) throw new FileNotFoundException(
					"Cannot get \"/WEB-INF/config/"+cfgFile+"\"."
					+ ctx.getServletContext().getRealPath("/WEB-INF/config/"+cfgFile)+" "+
					ctx.getServletContext().getContextPath()
					);
			props.loadFromXML(xmlStream);
			xmlStream.close();
			ctx.getServletContext().log(props.toString());
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		String driver = props.getProperty("JDBCDriver");
		String uri = props.getProperty("JDBCConnectionURL");
		String login = props.getProperty("DBlogin");
		String password = props.getProperty("DBpassword");
		int connectionPoolSize = java.lang.Integer.parseInt(props.getProperty("ConnectionPoolSize"));
		int connectionPoolMax = java.lang.Integer.parseInt(props.getProperty("ConnectionPoolMax"));
		ConnectionPool connectionPool;
		try {
			connectionPool = new ConnectionPool(driver, uri, login, password, connectionPoolSize, connectionPoolMax, true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		ctx.getServletContext().setAttribute("ConnectionPool", connectionPool);
		
		//Set application Files on server
		final File cghFolder = new File(props.getProperty("CghPath"));
		if (!cghFolder.isDirectory()){
			if (!cghFolder.mkdirs()){
				throw new RuntimeException("Can't create directory "+cghFolder.getAbsolutePath());
			}
		}else{
			if (!cghFolder.canWrite()){
				throw new RuntimeException("Can't write in "+cghFolder.getAbsolutePath());
			}
		}
		
		String cghPathReplacementInDB = props.getProperty("CghPathReplacementInDB");
		if (cghPathReplacementInDB==null || cghPathReplacementInDB.isEmpty()) throw new IllegalStateException("No value for cgh path replacement string in database !!");
		
		final File projectsFolder = new File(cghFolder, props.getProperty("CghPath.ProjectsPath"));
		if (!projectsFolder.isDirectory()){
			if (!projectsFolder.mkdirs()){
				throw new RuntimeException("Can't create directory "+projectsFolder.getAbsolutePath());
			}
		}else{
			if (!projectsFolder.canWrite()){
				throw new RuntimeException("Can't write in "+projectsFolder.getAbsolutePath());
			}
		}
		
		final File RScriptBin = new File(props.getProperty("RscriptBinPath"));
		if (!RScriptBin.exists()){
			throw new RuntimeException(RScriptBin.getAbsolutePath()+" doesn't exist");
		}else if (!RScriptBin.canExecute()){
			throw new RuntimeException("Can't execute "+RScriptBin.getAbsolutePath());
		}
		
		final File PerlBin = new File(props.getProperty("PerlBinPath"));
		if (!PerlBin.exists()){
			throw new RuntimeException(PerlBin.getAbsolutePath()+" doesn't exist");
		}else if (!PerlBin.canExecute()){
			throw new RuntimeException("Can't execute "+PerlBin.getAbsolutePath());
		}
		
		final File resourceFolder = new File(cghFolder, "Resources");
		if (!resourceFolder.exists()){
			throw new RuntimeException("Resource folder doesn't exist: "+resourceFolder.getAbsolutePath());
		}
		
		final File cnveFolder = new File(resourceFolder, "CNVE");
		if (!cnveFolder.exists()){
			throw new RuntimeException("CNVE folder doesn't exist: "+cnveFolder.getAbsolutePath());
		}
		
		final File designFolder = new File(resourceFolder, "designs");
		if (!designFolder.exists()){
			throw new RuntimeException("Designs folder doesn't exist: "+designFolder.getAbsolutePath());
		}
		
		final File freqGCFolder = new File(resourceFolder, "freq_GC");
		if (!freqGCFolder.exists()){
			throw new RuntimeException("Freq GC folder doesn't exist: "+freqGCFolder.getAbsolutePath());
		}
		
		final File rScriptsFolder = new File(resourceFolder, props.getProperty("CghPath.Resources.Rscripts"));
		if (!rScriptsFolder.exists()){
			throw new RuntimeException(rScriptsFolder.getAbsolutePath()+" doesn't exist");
		}else if (!rScriptsFolder.canRead() || !rScriptsFolder.canExecute()){
			throw new RuntimeException("Can't read or execute: "+rScriptsFolder.getAbsolutePath());
		}
		
		final File perlScriptsFolder = new File(resourceFolder, props.getProperty("CghPath.Resources.Pscripts"));
		if (!perlScriptsFolder.exists()){
			throw new RuntimeException(perlScriptsFolder.getAbsolutePath()+" doesn't exist");
		}else if (!perlScriptsFolder.canRead() || !perlScriptsFolder.canExecute()){
			throw new RuntimeException("Can't read or execute: "+perlScriptsFolder.getAbsolutePath());
		}
		
		final File configFolder = new File(resourceFolder, props.getProperty("CghPath.Resources.ConfigFiles"));
		if (!configFolder.exists()){
			throw new RuntimeException(configFolder.getAbsolutePath()+" doesn't exist.  Please create folder and default config file");
		}else if (!configFolder.canRead()){
			throw new RuntimeException("Can't read config files folder");
		}
		
		final File tmpFolder = new File(cghFolder, props.getProperty("CghPath.temporaryFolder"));
		if (!tmpFolder.isDirectory()){
			if (!tmpFolder.mkdirs()){
				throw new RuntimeException("Can't create directory "+tmpFolder.getAbsolutePath());
			}
		}else{
			ServerUtils.deleteDirectory(tmpFolder);
			if (!tmpFolder.mkdirs()){
				throw new RuntimeException("Can't create directory "+tmpFolder.getAbsolutePath());
			}
			if (!tmpFolder.canWrite()){
				throw new RuntimeException("Can't write in "+tmpFolder.getAbsolutePath());
			}
		}
		
		final File imageNotFoundFile = new File(resourceFolder, props.getProperty("CghPath.Resources.ImageNotFoundPath"));
		
		AnalysisParamsRecord analysisParams = new AnalysisParamsRecord();
		
		analysisParams.setMask(YesNo.parse(props.getProperty("mask", "yes")));
		analysisParams.setGcLowess(YesNo.parse(props.getProperty("gc_lowess", "yes")));
		analysisParams.setDelColor(DelDupColors.parse(props.getProperty("col_del", "red")));
		analysisParams.setDupColor(DelDupColors.parse(props.getProperty("col_dup", "green")));
		
		analysisParams.setS_min_positive_probes(Integer.parseInt(props.getProperty("s_min_positive_probes", "5")));
		analysisParams.setS_ratio_probes_over_positive(Double.parseDouble(props.getProperty("s_ratio_probes_over_positive", "1.3")));
		analysisParams.setS_min_total_score(Double.parseDouble(props.getProperty("s_min_total_score", "10")));
		analysisParams.setS_ratio_score_over_probes(Double.parseDouble(props.getProperty("s_ratio_score_over_probes", "1.5")));
		analysisParams.setS_probes_times_median(Double.parseDouble(props.getProperty("s_probes_times_median", "5")));
		
		analysisParams.setL_min_positive_probes(Integer.parseInt(props.getProperty("l_min_positive_probes", "10")));
		analysisParams.setL_ratio_probes_over_positive(Double.parseDouble(props.getProperty("l_ratio_probes_over_positive", "1.5")));
		analysisParams.setL_min_total_score(Double.parseDouble(props.getProperty("l_min_total_score", "10")));
		analysisParams.setL_ratio_score_over_probes(Double.parseDouble(props.getProperty("l_ratio_score_over_probes", "1.2")));
		analysisParams.setL_probes_times_median(Double.parseDouble(props.getProperty("l_probes_times_median", "5")));
		analysisParams.setL_min_median(Double.parseDouble(props.getProperty("l_min_median", "0.4")));
		
		GlobalDefs.getInstance().setDefaultAnalysisParamRecord(analysisParams);
		GlobalDefs.getInstance().setCghPath(cghFolder.getAbsolutePath());
		GlobalDefs.getInstance().setCghPathReplacementInDB(cghPathReplacementInDB);
		
		Map<String, File> appFiles = new Hashtable<String, File>();
		appFiles.put("mainFile", cghFolder);
		appFiles.put("projectsFolder", projectsFolder);
		appFiles.put("RScriptBin", RScriptBin);
		appFiles.put("PerlBin", PerlBin);
		appFiles.put("RScripts", rScriptsFolder);
		appFiles.put("PScripts", perlScriptsFolder);
		appFiles.put("configFolder", configFolder);
		appFiles.put("cnveFolder", cnveFolder);
		appFiles.put("designFolder", designFolder);
		appFiles.put("freqGCFolder", freqGCFolder);
		appFiles.put("temporaryFolder", tmpFolder);
		appFiles.put("imageNotFound", imageNotFoundFile);
		ctx.getServletContext().setAttribute("ApplicationFiles", appFiles);
		
		/*****  Creation of default configuration file for pipeline if it doesn't exist
		 * 
		 */
		AnalysisConfigFile configFile = new AnalysisConfigFile(appFiles, null, ConfigFileType.DEFAULT);
		configFile.writeConfigFile(analysisParams);
		
		
		/*****  Database creation
		 * Queries in axiom.sql are all executed.
		 * Tables will be created if they do not exist ("CREATE TABLE IF NOT EXISTS", "INSERT IGNORE", etc...)
		 * The instructions to init the database are in cgh.sql located under folder specified in servlet context
		 */
		File sqlFile = new File(resourceFolder, props.getProperty("CghPath.Resources.DatabaseCreationFile"));
		DatabaseUtils.initDatabase(connectionPool,sqlFile);
	}
}
