package fr.pfgen.cgh.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.pfgen.cgh.shared.records.ProjectRecord;

@RemoteServiceRelativePath("ProjectService")
public interface ProjectService extends GenericGwtRpcService<ProjectRecord> {

	List<Map<String, String>> getProjectParams(int projectID);
	List<String> getProjectNames();
}
