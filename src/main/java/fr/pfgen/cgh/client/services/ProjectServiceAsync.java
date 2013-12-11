package fr.pfgen.cgh.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.pfgen.cgh.shared.records.ProjectRecord;

public interface ProjectServiceAsync extends GenericGwtRpcServiceAsync<ProjectRecord> {

	void getProjectParams(int projectID, AsyncCallback<List<Map<String, String>>> asyncCallback);
	void getProjectNames(AsyncCallback<List<String>> asyncCallback);

}
