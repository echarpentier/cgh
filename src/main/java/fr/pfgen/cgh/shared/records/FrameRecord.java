package fr.pfgen.cgh.shared.records;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class FrameRecord implements Serializable,Record {

	private int ID;
	private String name;
	private String refName;
	private String testName;
	private int arrayID;
	private String arrayName;
	private String FEFilePath;
	private String resultFolderPath;
	private Map<String, String> qcResultMap;
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRefName() {
		return refName;
	}
	public void setRefName(String refName) {
		this.refName = refName;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public int getArrayID() {
		return arrayID;
	}
	public void setArrayID(int arrayID) {
		this.arrayID = arrayID;
	}
	public String getArrayName() {
		return arrayName;
	}
	public void setArrayName(String arrayName) {
		this.arrayName = arrayName;
	}
	public String getFEFilePath() {
		return FEFilePath;
	}
	public void setFEFilePath(String fEFilePath) {
		FEFilePath = fEFilePath;
	}
	public String getResultFolderPath() {
		return resultFolderPath;
	}
	public void setResultFolderPath(String resultFolderPath) {
		this.resultFolderPath = resultFolderPath;
	}
	public Map<String, String> getQcResultMap() {
		return qcResultMap;
	}
	public void setQcResultMap(Map<String, String> qcResultMap) {
		this.qcResultMap = qcResultMap;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
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
		FrameRecord other = (FrameRecord) obj;
		if (ID != other.ID)
			return false;
		return true;
	}
}
