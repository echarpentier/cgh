package fr.pfgen.cgh.server.services;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import fr.pfgen.cgh.client.services.UserService;
import fr.pfgen.cgh.server.database.ConnectionPool;
import fr.pfgen.cgh.server.database.UserTable;
import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.shared.records.UserRecord;
import fr.pfgen.cgh.shared.sharedUtils.GenericGwtRpcList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UserServiceImpl extends RemoteServiceServlet implements UserService{

	private ConnectionPool pool;
	private Hashtable<String, File> appFiles;
	
	@SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pool = (ConnectionPool)getServletContext().getAttribute("ConnectionPool");
		appFiles = (Hashtable<String, File>)getServletContext().getAttribute("ApplicationFiles");
	}
	
	@Override
	public List<UserRecord> fetch(Integer startRow, Integer endRow, String sortBy, Map<String, String> filterCriteria) {
		GenericGwtRpcList<UserRecord> outList = new GenericGwtRpcList<UserRecord>();
	    
    	String query = UserTable.constructQuery(sortBy,filterCriteria);
    	
    	outList.setTotalRows(DatabaseUtils.countRowInQuery(pool, query,false));
    	
    	List<UserRecord> out = UserTable.getUsers(pool,query,startRow,endRow);
    	
    	outList.addAll(out);
    	
    	return outList;
	}

	@Override
	public UserRecord add(UserRecord data) {
		return UserTable.addUser(pool, data);
	}

	@Override
	public UserRecord update(UserRecord data) {
		return UserTable.updateUser(pool,data);
	}

	@Override
	public void remove(UserRecord data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String download(String sortBy, Map<String, String> filterCriteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserRecord checkUserSessionOnServer() {
		HttpSession session = getThreadLocalRequest().getSession();
	    if (session.isNew()){
	    	return null;
	    }
	  	return (UserRecord) session.getAttribute("CghUser");
	}

	@Override
	public UserRecord loginUser(String appID, String password) {
		UserRecord user = UserTable.checkUserLogin(pool, appID, password);
		if (user.getLoginText().startsWith("User authenticated !")){
			HttpSession session = getThreadLocalRequest().getSession();
		    session.setMaxInactiveInterval(20);
		    session.setAttribute("CghUser", user);
		}
		return user;
	}

	@Override
	public String changeUserPassword(int userID, String password) {
		return UserTable.changePassword(pool, userID, password);
	}
}
