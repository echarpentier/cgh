package fr.pfgen.cgh.server.services;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import fr.pfgen.cgh.client.services.ProjectService;
import fr.pfgen.cgh.server.database.ConnectionPool;
import fr.pfgen.cgh.server.database.ProjectTable;
import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.shared.records.ProjectRecord;
import fr.pfgen.cgh.shared.sharedUtils.GenericGwtRpcList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ProjectServiceImpl extends RemoteServiceServlet implements ProjectService {

	private ConnectionPool pool;
	private Hashtable<String, File> appFiles;
	
	@SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pool = (ConnectionPool)getServletContext().getAttribute("ConnectionPool");
		appFiles = (Hashtable<String, File>)getServletContext().getAttribute("ApplicationFiles");
	}
	
	@Override
	public List<ProjectRecord> fetch(Integer startRow, Integer endRow, String sortBy, Map<String, String> filterCriteria) {
		GenericGwtRpcList<ProjectRecord> outList = new GenericGwtRpcList<ProjectRecord>();
	    
    	String query = ProjectTable.constructQuery(sortBy,filterCriteria);
    	
    	outList.setTotalRows(DatabaseUtils.countRowInQuery(pool, query,false));
    	
    	List<ProjectRecord> out = ProjectTable.getProjects(pool,query,startRow,endRow);
    	
    	outList.addAll(out);
    	
    	return outList;
	}

	@Override
	public ProjectRecord add(ProjectRecord data) {
		return ProjectTable.addNewProject(pool, appFiles, data);
	}

	@Override
	public ProjectRecord update(ProjectRecord data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(ProjectRecord data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String download(String sortBy, Map<String, String> filterCriteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, String>> getProjectParams(int projectID) {
		return ProjectTable.getProjectParams(pool,projectID);
	}

	@Override
	public List<String> getProjectNames() {
		return ProjectTable.getProjectNames(pool);
	}
}
