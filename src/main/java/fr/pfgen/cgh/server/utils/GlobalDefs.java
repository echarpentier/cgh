package fr.pfgen.cgh.server.utils;

import fr.pfgen.cgh.shared.records.AnalysisParamsRecord;

public class GlobalDefs{
	private String cghPath = null;
	private String cghPathReplacementInDB = null;
	private static GlobalDefs INSTANCE = null;
	private AnalysisParamsRecord analysisParamRecord = null;

	private GlobalDefs(){
	
	}

	public String getCghPathReplacementInDB() {
		return cghPathReplacementInDB;
	}

	public synchronized void setCghPathReplacementInDB(String cghPathReplacementInDB) {
		if(this.cghPathReplacementInDB!=null && !this.cghPathReplacementInDB.equals(cghPathReplacementInDB)) throw new IllegalStateException("The main cgh path replacement in DB cannot be changed once initialized !!");
		this.cghPathReplacementInDB = cghPathReplacementInDB;
	}

	public String getCghPath(){
		return cghPath;
	}
	
	public synchronized void setCghPath(String path){
		if(this.cghPath!=null && !this.cghPath.equals(path)) throw new IllegalStateException("The main cgh path cannot be changed once initialized !!");
		this.cghPath = path;
	}
	
	public AnalysisParamsRecord getDefaultAnalysisParamRecord(){
		return analysisParamRecord;
	}
	
	public synchronized void setDefaultAnalysisParamRecord(AnalysisParamsRecord rec){
		if (this.analysisParamRecord!=null) throw new IllegalStateException("The default analysis param record cannot be changed once initialized !!");
		this.analysisParamRecord = rec;
	}

	public static GlobalDefs getInstance(){
		if(INSTANCE==null){
			synchronized (GlobalDefs.class){
				if(INSTANCE==null){
					INSTANCE = new GlobalDefs();
				}
			}
		}
		return INSTANCE;
	}
}
