package fr.pfgen.cgh.client.services;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.pfgen.cgh.shared.records.UserRecord;

@RemoteServiceRelativePath("UserService")
public interface UserService extends GenericGwtRpcService<UserRecord> {
	UserRecord checkUserSessionOnServer();
	UserRecord loginUser(String appID, String password);
	String changeUserPassword(int userID, String password);
}
