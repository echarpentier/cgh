package fr.pfgen.cgh.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.pfgen.cgh.shared.records.UserRecord;

public interface UserServiceAsync extends GenericGwtRpcServiceAsync<UserRecord> {
	void checkUserSessionOnServer(AsyncCallback<UserRecord> asyncCallback);
	void loginUser(String appID, String password, AsyncCallback<UserRecord> asyncCallback);
	void changeUserPassword(int userID, String password, AsyncCallback<String> asyncCallback);
}
