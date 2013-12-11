package fr.pfgen.cgh.server.utils.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.pfgen.cgh.server.utils.IOUtils;
import fr.pfgen.cgh.shared.enums.ConfigFileType;
import fr.pfgen.cgh.shared.enums.DelDupColors;
import fr.pfgen.cgh.shared.enums.YesNo;
import fr.pfgen.cgh.shared.records.AnalysisParamsRecord;

public class AnalysisConfigFile{

	private Map<String, File> appFiles;
	private String name;
	private ConfigFileType type;
	private String fileName;
	
	public AnalysisConfigFile(Map<String, File> appFiles, String name, ConfigFileType type){
		if (appFiles==null) throw new IllegalArgumentException("The hashtable appFile cannot be null");
		if (name==null && type != ConfigFileType.DEFAULT) throw new IllegalArgumentException("The name cannot be null while creation of a project or user configuration file");
		if (type==null) throw new IllegalArgumentException("The configuration file type cannot be null");
		this.appFiles = appFiles;
		this.name = name;
		this.type = type;
		createFileName();
	}
	
	private void createFileName(){
		fileName = getConfigFileName(type, name);
	}
	
	public boolean writeConfigFile(AnalysisParamsRecord analysisParams){	
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new FileWriter(new File(appFiles.get("configFolder"), fileName)));
			pw.println("R_script_format:"+appFiles.get("RScripts").getAbsolutePath()+"/format.R");
			pw.println("R_script_GC:"+appFiles.get("RScripts").getAbsolutePath()+"/GC_lowess.R");
			pw.println("R_script_order:"+appFiles.get("RScripts").getAbsolutePath()+"/order.R");
			
			pw.println("CNVE:"+appFiles.get("cnveFolder").getAbsolutePath());
			pw.println("freq_GC:"+appFiles.get("freqGCFolder").getAbsolutePath());
			pw.println("designs:"+appFiles.get("designFolder").getAbsolutePath());
			
			pw.println("path_perl:"+appFiles.get("PerlBin"));
			pw.println("path_Rscript:"+appFiles.get("RScriptBin"));
			
			Map<String, String> analysisParamsMap = analysisParamsToMap(analysisParams);
			for (String param : analysisParamsMap.keySet()) {
				pw.println(param+":"+analysisParamsMap.get(param));
			}
			
			pw.flush();
			pw.close();
			return true;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}finally{
			IOUtils.safeClose(pw);
		}
	}
	
	public boolean exists(){
		File confFile = new File(appFiles.get("configFolder"), fileName);
		return confFile.exists();
	}
	
	public static Map<String, String> analysisParamsToMap(AnalysisParamsRecord r){
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		map.put("mask", r.getMask().toString().toLowerCase());
		map.put("gc_lowess", r.getGcLowess().toString().toLowerCase());
		map.put("col_del", r.getDelColor().toString().toLowerCase());
		map.put("col_dup", r.getDupColor().toString().toLowerCase());
		//map.put("gonosomes", "yes");
		
		map.put("s_min_positive_probes", String.valueOf(r.getS_min_positive_probes()));
		map.put("s_ratio_probes_over_positive", String.valueOf(r.getS_ratio_probes_over_positive()));
		map.put("s_min_total_score", String.valueOf(r.getS_min_total_score()));
		map.put("s_ratio_score_over_probes", String.valueOf(r.getS_ratio_score_over_probes()));
		map.put("s_probes_times_median", String.valueOf(r.getS_probes_times_median()));
		
		map.put("l_min_positive_probes", String.valueOf(r.getL_min_positive_probes()));
		map.put("l_ratio_probes_over_positive", String.valueOf(r.getL_ratio_probes_over_positive()));
		map.put("l_min_total_score", String.valueOf(r.getL_min_total_score()));
		map.put("l_ratio_score_over_probes", String.valueOf(r.getL_ratio_score_over_probes()));
		map.put("l_probes_times_median", String.valueOf(r.getL_probes_times_median()));
		map.put("l_min_median", String.valueOf(r.getL_min_median()));
		
		return map;
	}
	
	public File getFile(){
		File confFile = new File(appFiles.get("configFolder"), fileName);
		if(confFile.exists()){
			return confFile;
		}else{
			return null;
		}
	}
	
	public static String getConfigFileName(ConfigFileType type, String name){
		if (type==null) return null;
		if (name==null && type!=ConfigFileType.DEFAULT) return null;
		String typeToString = "";
		switch (type) {
		case PROJECT:
			typeToString = "project_";
			break;
		case USER:
			typeToString = "user_";
			break;
		case DEFAULT:
			return "default_cgh_config_file.txt";
		default:
			return null;
		}
		return "conf_"+typeToString+name+".txt";
	}

	public static AnalysisParamsRecord fileToAnalysisParamRecord(File configFile){
		AnalysisParamsRecord rec = new AnalysisParamsRecord();
		
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader br = null;
		
		try{
			br = IOUtils.openFile(configFile);
			String line;
			while((line=br.readLine())!=null){
				if (line.isEmpty()) continue;
				if (line.startsWith("#")) continue;
				String[] linesplit = line.split(":");
				if (linesplit.length!=2) continue;
				map.put(linesplit[0], linesplit[1]);
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}finally{
			IOUtils.safeClose(br);
		}
		
		rec.setMask(YesNo.parse(map.get("mask")));
		rec.setGcLowess(YesNo.parse(map.get("gc_lowess")));
		rec.setDelColor(DelDupColors.parse(map.get("col_del")));
		rec.setDupColor(DelDupColors.parse(map.get("col_dup")));
		
		rec.setS_min_positive_probes(Integer.parseInt(map.get("s_min_positive_probes")));
		rec.setS_min_total_score(Double.parseDouble(map.get("s_min_total_score")));
		rec.setS_probes_times_median(Double.parseDouble(map.get("s_probes_times_median")));
		rec.setS_ratio_probes_over_positive(Double.parseDouble(map.get("s_ratio_probes_over_positive")));
		rec.setS_ratio_score_over_probes(Double.parseDouble(map.get("s_ratio_score_over_probes")));
		
		rec.setL_min_positive_probes(Integer.parseInt(map.get("l_min_positive_probes")));
		rec.setL_min_total_score(Double.parseDouble(map.get("l_min_total_score")));
		rec.setL_probes_times_median(Double.parseDouble(map.get("l_probes_times_median")));
		rec.setL_ratio_probes_over_positive(Double.parseDouble(map.get("l_ratio_probes_over_positive")));
		rec.setL_ratio_score_over_probes(Double.parseDouble(map.get("l_ratio_score_over_probes")));
		rec.setL_min_median(Double.parseDouble(map.get("l_min_median")));
		
		return rec;
	}
}
