package fr.pfgen.cgh.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.pfgen.cgh.shared.records.TeamRecord;

@RemoteServiceRelativePath("TeamService")
public interface TeamService extends GenericGwtRpcService<TeamRecord> {

	List<String> getTeamNames();
}
