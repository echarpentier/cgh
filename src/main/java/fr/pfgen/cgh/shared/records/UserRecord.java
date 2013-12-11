package fr.pfgen.cgh.shared.records;

import java.io.Serializable;

import fr.pfgen.cgh.shared.enums.UserStatus;

@SuppressWarnings("serial")
public class UserRecord implements Serializable{

	private int userID;
	private String firstname;
	private String lastname;
	private String email;
	private String office_number;
	private int teamID;
	private String teamName;
	private String appID;
	private UserStatus appStatus;
	private String password;
	private String loginText;
	
	public String getLoginText() {
		return loginText;
	}
	public void setLoginText(String loginText) {
		this.loginText = loginText;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOffice_number() {
		return office_number;
	}
	public void setOffice_number(String office_number) {
		this.office_number = office_number;
	}
	public int getTeamID() {
		return teamID;
	}
	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public String getAppID() {
		return appID;
	}
	public void setAppID(String appID) {
		this.appID = appID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public UserStatus getAppStatus() {
		return appStatus;
	}
	public void setAppStatus(UserStatus appStatus) {
		this.appStatus = appStatus;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + userID;
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
		UserRecord other = (UserRecord) obj;
		if (userID != other.userID)
			return false;
		return true;
	}
}
