package fr.pfgen.cgh.client.services;

import java.util.Map;

import fr.pfgen.cgh.shared.records.AnalysisParamsRecord;
import fr.pfgen.cgh.shared.records.ProjectRecord;
import fr.pfgen.cgh.shared.records.UserRecord;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnalysisServiceAsync {

	void getDefaultAnalysisParams(AsyncCallback<AnalysisParamsRecord> asyncCallback);
	
	void saveProjectConfigFile(String projectName, AnalysisParamsRecord rec, AsyncCallback<String> asyncCallback);
	void projectConfigFileExists(String projectName, AsyncCallback<Boolean> asyncCallback);
	void isProjectAnalysisRunning(int projectID, AsyncCallback<Boolean> asyncCallback);
	void launchAnalysisForProject(String fileName, ProjectRecord project, UserRecord user, AsyncCallback<String> asyncCallback);
	void projectAnalysisAdvances(int projectID, AsyncCallback<Map<String, String>> asyncCallback);
	void removeProjectAnalysis(int projectID, AsyncCallback<Boolean> asyncCallback);
	
	void saveUserConfigFile(String appID, AnalysisParamsRecord rec, AsyncCallback<String> asyncCallback);
	void userConfigFileExists(String appID, AsyncCallback<Boolean> asyncCallback);
	void isUserAnalysisRunning(int userID, AsyncCallback<Boolean> asyncCallback);
	void launchAnalysisForUser(String fileName, UserRecord user, AsyncCallback<String> asyncCallback);
	void userAnalysisAdvances(int userID, AsyncCallback<Map<String, String>> asyncCallback);
	void removeUserAnalysis(int userID, AsyncCallback<Boolean> asyncCallback);

	
}
