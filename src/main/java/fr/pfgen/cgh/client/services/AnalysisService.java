package fr.pfgen.cgh.client.services;

import java.util.Map;

import fr.pfgen.cgh.shared.records.AnalysisParamsRecord;
import fr.pfgen.cgh.shared.records.ProjectRecord;
import fr.pfgen.cgh.shared.records.UserRecord;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("AnalysisService")
public interface AnalysisService extends RemoteService {
	AnalysisParamsRecord getDefaultAnalysisParams();
	
	String saveProjectConfigFile(String projectName, AnalysisParamsRecord rec);
	Boolean projectConfigFileExists(String projectName);
	Boolean isProjectAnalysisRunning(int projectID);
	String launchAnalysisForProject(String filename, ProjectRecord project, UserRecord user);
	Map<String, String> projectAnalysisAdvances(int projectID);
	Boolean removeProjectAnalysis(int projectID);
	
	String saveUserConfigFile(String appID, AnalysisParamsRecord rec);
	Boolean userConfigFileExists(String appID);
	Boolean isUserAnalysisRunning(int userID);
	String launchAnalysisForUser(String fileName, UserRecord user);
	Map<String, String> userAnalysisAdvances(int userID);
	Boolean removeUserAnalysis(int userID);
}
