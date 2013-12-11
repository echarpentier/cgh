package fr.pfgen.cgh.server.utils;

import java.io.File;
import fr.pfgen.cgh.server.utils.threads.AnalysisRunningThread;
import fr.pfgen.cgh.shared.enums.AnalysisType;

public class AnalysisRunningPojo{

	private AnalysisType type;
	private int id;
	private File logFile;
	private AnalysisRunningThread thread;
	
	public AnalysisRunningPojo(int id,AnalysisType type){
		this.id = id;
		this.type = type;
	}
	
	public AnalysisRunningPojo(){
		
	}
	
	public AnalysisType getType() {
		return type;
	}
	public void setType(AnalysisType type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public File getLogFile() {
		return logFile;
	}
	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}
	public AnalysisRunningThread getThread() {
		return thread;
	}
	public void setThread(AnalysisRunningThread thread) {
		this.thread = thread;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnalysisRunningPojo other = (AnalysisRunningPojo) obj;
		if (id != other.id)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
