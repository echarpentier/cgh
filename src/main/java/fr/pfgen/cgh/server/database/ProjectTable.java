package fr.pfgen.cgh.server.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.server.utils.SQLUtils;
import fr.pfgen.cgh.shared.records.ProjectRecord;

public class ProjectTable {

	public static String constructQuery(String sortBy, Map<String, String> filterCriteria) {
		String query = new String();
		
		//default query
		query = "SELECT * FROM (projects JOIN users ON projects.user_id=users.user_id) JOIN teams ON teams.team_id=users.team_id";
		
		if (filterCriteria!=null && !filterCriteria.isEmpty()){
			Map<String,String> critMap = new Hashtable<String, String>();
			if (filterCriteria.containsKey("project_id")){
				critMap.put("projects.project_id", filterCriteria.get("project_id"));
			}
			if (filterCriteria.containsKey("project_name")){
				critMap.put("projects.project_name", filterCriteria.get("project_name"));
			}
			if (filterCriteria.containsKey("user_id")){
				critMap.put("users.user_id", filterCriteria.get("user_id"));
			}
			if (filterCriteria.containsKey("team_id")){
				critMap.put("teams.team_id", filterCriteria.get("team_id"));
			}
			if (!critMap.isEmpty()){
				query = query.concat(DatabaseUtils.constructWhereClause(critMap));
			}
		}
		
		if (sortBy==null){
			sortBy = "projects.project_name";
		}
		
		query = query.concat(DatabaseUtils.constructOrderByClause(sortBy));
		return query;
	}

	public static List<ProjectRecord> getProjects(ConnectionPool pool, String query, Integer startRow, Integer endRow) {
		if (startRow != null && endRow != null && endRow >= startRow) {
			Integer size = endRow-startRow;
        	query = query.concat(" LIMIT "+startRow+","+size);
		}
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			con = pool.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			List<ProjectRecord> projectList = new ArrayList<ProjectRecord>();
			while(rs.next()){
				ProjectRecord rec = new ProjectRecord();
				rec.setProjectID(rs.getInt("project_id"));
				rec.setProjectName(rs.getString("project_name"));
				rec.setProjectManager(rs.getString("project_manager"));
				rec.setProjectPath(DatabaseUtils.dbPathToRealPath(rs.getString("project_path")));
				rec.setTeamID(rs.getInt("team_id"));
				rec.setTeamName(rs.getString("team_name"));
				rec.setUserID(rs.getInt("user_id"));
				rec.setUserName(rs.getString("firstname")+" "+rs.getString("lastname"));
				
				projectList.add(rec);
			}
			rs.close();
			stmt.close();
			return projectList;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(stmt);
			pool.close(con);
		}
	}

	public static List<Map<String, String>> getProjectParams(ConnectionPool pool, int projectID) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>(2);
		Map<String, String> paramValueMap = new HashMap<String, String>();
		Map<String, String> paramDefMap = new HashMap<String, String>();
		list.add(paramValueMap);
		list.add(paramDefMap);
		
		String query = "SELECT (SELECT count(*) FROM arrays WHERE project_id=?) AS array_number,a.genomic_build,a.design_name,sp.scan_param_name,sp.scan_param_def,sv.scan_param_value FROM ((scan_params sp JOIN scan_param_values sv ON sp.scan_param_id=sv.scan_param_id) JOIN arrays a ON sv.array_id=a.array_id) WHERE a.array_id=(SELECT array_id FROM arrays JOIN projects ON projects.project_id=arrays.project_id WHERE projects.project_id=? LIMIT 1)";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setInt(1, projectID);
			ps.setInt(2, projectID);
			rs = ps.executeQuery();
			boolean firstRes = true;
			while(rs.next()){
				paramValueMap.put(rs.getString("scan_param_name"), rs.getString("scan_param_value"));
				paramDefMap.put(rs.getString("scan_param_name"), rs.getString("scan_param_def"));
				if (firstRes){
					paramValueMap.put("genomic_build", rs.getString("genomic_build"));
					paramDefMap.put("genomic_build", "genomic build of arrays in this project");
					paramValueMap.put("design_name", rs.getString("design_name"));
					paramDefMap.put("design_name", "design of arrays in this project");
					paramValueMap.put("array_number", rs.getString("array_number"));
					paramDefMap.put("array_number", "number of arrays in project");
					firstRes = false;
				}
			}
			rs.close();
			ps.close();
			return list;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}

	public static List<String> getProjectNames(ConnectionPool pool) {
		List<String> projectNames = new ArrayList<String>();
		
		String query = "SELECT project_name FROM projects";
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			con = pool.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()){
				projectNames.add(rs.getString("project_name"));
			}
			rs.close();
			stmt.close();
			return projectNames;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(stmt);
			pool.close(con);
		}
	}

	public static ProjectRecord addNewProject(ConnectionPool pool, Hashtable<String, File> appFiles, ProjectRecord data) {
		String query = "INSERT INTO projects (project_name, project_manager, project_path, user_id) VALUES (?,?,?,?)";
		
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rsk = null;
		
		File newProjectFolder = null;
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, data.getProjectName());
			if (data.getProjectManager()==null){
				ps.setNull(2, Types.VARCHAR);
			}else{
				ps.setString(2, data.getProjectManager());
			}
			newProjectFolder = new File(appFiles.get("projectsFolder"),data.getProjectName());
			if (newProjectFolder.exists()){
				throw new RuntimeException("A folder already exists with this name: "+newProjectFolder.getAbsolutePath());
			}else if (!newProjectFolder.mkdir()){
				throw new RuntimeException("Cannot create: "+newProjectFolder.getAbsolutePath());
			}
			ps.setString(3, DatabaseUtils.realPathToDbPath(newProjectFolder.getAbsolutePath()));
			ps.setInt(4, data.getUserID());
			ps.executeUpdate();
			rsk = ps.getGeneratedKeys();
			if (rsk.next()){
				data.setProjectID(rsk.getInt(1));
				data.setProjectPath(DatabaseUtils.realPathToDbPath(newProjectFolder.getAbsolutePath()));
			}else{
				data = null;
			}
			rsk.close();
			ps.close();
			return data;
		}catch(SQLException e){
			e.printStackTrace();
			newProjectFolder.delete();
			return null;
		}finally{
			SQLUtils.safeClose(rsk);
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}
}
