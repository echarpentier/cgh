package fr.pfgen.cgh.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.pfgen.cgh.shared.records.TeamRecord;

public interface TeamServiceAsync extends GenericGwtRpcServiceAsync<TeamRecord> {

	void getTeamNames(AsyncCallback<List<String>> asyncCallback);

}
