package fr.pfgen.cgh.shared.records;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ArrayRecord implements Serializable,Record{

	private int ID;
	private String name;
	private int frameNumber;
	private Date scanDate;
	private String genomicBuild;
	private String designName;
	private String feVersion;
	private Integer projectId;
	private String projectName;
	private Integer userID;
	private String userName;

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
	public int getFrameNumber() {
		return frameNumber;
	}
	public void setFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
	}
	public Date getScanDate() {
		return scanDate;
	}
	public void setScanDate(Date scanDate) {
		this.scanDate = scanDate;
	}
	public String getGenomicBuild() {
		return genomicBuild;
	}
	public void setGenomicBuild(String genomicBuild) {
		this.genomicBuild = genomicBuild;
	}
	public String getDesignName() {
		return designName;
	}
	public void setDesignName(String designName) {
		this.designName = designName;
	}
	public String getFeVersion() {
		return feVersion;
	}
	public void setFeVersion(String feVersion) {
		this.feVersion = feVersion;
	}
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Integer getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
		ArrayRecord other = (ArrayRecord) obj;
		if (ID != other.ID)
			return false;
		return true;
	}
}
