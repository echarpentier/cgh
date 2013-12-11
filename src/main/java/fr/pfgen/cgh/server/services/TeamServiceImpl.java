package fr.pfgen.cgh.server.services;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import fr.pfgen.cgh.client.services.TeamService;
import fr.pfgen.cgh.server.database.ConnectionPool;
import fr.pfgen.cgh.server.database.TeamTable;
import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.shared.records.TeamRecord;
import fr.pfgen.cgh.shared.sharedUtils.GenericGwtRpcList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class TeamServiceImpl extends RemoteServiceServlet implements TeamService{

	private ConnectionPool pool;
	private Hashtable<String, File> appFiles;
	
	@SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pool = (ConnectionPool)getServletContext().getAttribute("ConnectionPool");
		appFiles = (Hashtable<String, File>)getServletContext().getAttribute("ApplicationFiles");
	}
	
	@Override
	public List<TeamRecord> fetch(Integer startRow, Integer endRow, String sortBy, Map<String, String> filterCriteria) {
		GenericGwtRpcList<TeamRecord> outList = new GenericGwtRpcList<TeamRecord>();
	    
    	String query = TeamTable.constructQuery(sortBy,filterCriteria);
    	
    	outList.setTotalRows(DatabaseUtils.countRowInQuery(pool, query,false));
    	
    	List<TeamRecord> out = TeamTable.getTeams(pool,query,startRow,endRow);
    	
    	outList.addAll(out);
    	
    	return outList;
	}

	@Override
	public TeamRecord add(TeamRecord data) {
		return TeamTable.addTeam(pool,data);
	}

	@Override
	public TeamRecord update(TeamRecord data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(TeamRecord data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String download(String sortBy, Map<String, String> filterCriteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getTeamNames() {
		return TeamTable.getTeamNames(pool);
	}

}
