package fr.pfgen.cgh.server.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.server.database.ConnectionPool;
import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.server.utils.SQLUtils;
import fr.pfgen.cgh.shared.records.ArrayRecord;

public class ArrayTable {

	public static String constructQuery(String sortBy, Map<String, String> filterCriteria){
		
		String query = new String();
		
		//default query
		query = "SELECT * FROM (((arrays LEFT JOIN projects ON arrays.project_id=projects.project_id) JOIN users ON users.user_id=arrays.user_id) JOIN teams ON teams.team_id=users.team_id)";
		
		if (filterCriteria!=null && !filterCriteria.isEmpty()){
			Map<String,String> critMap = new Hashtable<String, String>();
			if (filterCriteria.containsKey("project_id")){
				critMap.put("arrays.project_id", filterCriteria.get("project_id"));
			}
			if (filterCriteria.containsKey("user_id")){
				critMap.put("arrays.user_id", filterCriteria.get("user_id"));
			}
			if (filterCriteria.containsKey("team_id")){
				critMap.put("teams.team_id", filterCriteria.get("team_id"));
			}
			if (filterCriteria.containsKey("array_id")){
				critMap.put("arrays.array_id", filterCriteria.get("array_id"));
			}
			if (!critMap.isEmpty()){
				query = query.concat(DatabaseUtils.constructWhereClause(critMap));
			}
		}
		
		if (sortBy==null){
			sortBy = "scan_date,array_name";
		}
		query = query.concat(DatabaseUtils.constructOrderByClause(sortBy));
		return query;
	}
	
	public static List<ArrayRecord> getArrays(ConnectionPool pool, String query, Integer startRow, Integer endRow){
		if (startRow != null && endRow != null && endRow >= startRow) {
			Integer size = endRow-startRow;
        	query = query.concat(" LIMIT "+startRow+","+size);
		}
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String frameQuery = "SELECT count(*) FROM frames WHERE array_id=?";
		PreparedStatement psFrames = null;
		ResultSet rsFrames = null;
	
		try{
			con = pool.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			psFrames = con.prepareStatement(frameQuery);
			
			List<ArrayRecord> arrayList = new ArrayList<ArrayRecord>();
			while(rs.next()){
				ArrayRecord rec = new ArrayRecord();
				rec.setID(rs.getInt("array_id"));
				
				
				psFrames.setInt(1, rs.getInt("array_id"));
				rsFrames = psFrames.executeQuery();
				while(rsFrames.next()){
					rec.setFrameNumber(rsFrames.getInt(1));
				}
				rsFrames.close();
				
				rec.setName(rs.getString("array_name"));
				//rec.setFrameNumber(rs.getInt("frame_number"));
				rec.setDesignName(rs.getString("design_name"));
				rec.setGenomicBuild(rs.getString("genomic_build"));
				int projectID = rs.getInt("project_id");
				if (!rs.wasNull()){
					rec.setProjectId(projectID);
					rec.setProjectName(rs.getString("project_name"));
				}
				rec.setScanDate(rs.getTimestamp("scan_date"));
				rec.setUserID(rs.getInt("user_id"));
				rec.setUserName(rs.getString("firstname")+" "+rs.getString("lastname"));
				arrayList.add(rec);
			}
			psFrames.close();
			rs.close();
			stmt.close();
			return arrayList;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(stmt);
			SQLUtils.safeClose(rsFrames);
			SQLUtils.safeClose(psFrames);
			pool.close(con);
		}
	}

	public static List<File> getResultFoldersForArray(ConnectionPool pool, int arrayID) {
		String query = "SELECT result_folder_path FROM arrays a JOIN frames f ON a.array_id=f.array_id WHERE a.array_id=? ORDER BY f.frame_name";
		
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setInt(1, arrayID);
			rs = ps.executeQuery();
			List<File> folderList = new ArrayList<File>();
			while(rs.next()){
				String folderPath = DatabaseUtils.dbPathToRealPath(rs.getString("result_folder_path"));
				File folderFile = new File(folderPath);
				if (!folderFile.exists() || !folderFile.isDirectory()){
					throw new RuntimeException(folderFile.getAbsolutePath()+" appears in database and cannot be found on server");
				}else{
					folderList.add(folderFile);
				}
			}
			rs.close();
			ps.close();
			return folderList;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}

	public static ArrayRecord addArray(ConnectionPool pool, ArrayRecord arrayRec) {
		String query;
		if (arrayRec.getProjectId()==null){
			query = "SELECT * FROM arrays WHERE array_name=? AND project_id IS NULL AND user_id=?";
		}else{
			query = "SELECT * FROM arrays WHERE array_name=? AND project_id=?";
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection con = null;
		
		String insert = "INSERT INTO arrays (array_name,scan_date,genomic_build,design_name,fe_version,project_id,user_id) VALUES (?,?,?,?,?,?,?)";
		PreparedStatement psInsert = null;
		ResultSet rsk = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			psInsert = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, arrayRec.getName());
			if (arrayRec.getProjectId()==null){
				ps.setInt(2, arrayRec.getUserID());
			}else{
				ps.setInt(2, arrayRec.getProjectId());
			}
			rs = ps.executeQuery();
			if (rs.next()){
				arrayRec.setID(rs.getInt("array_id"));
			}else{
				psInsert.setString(1, arrayRec.getName());
				psInsert.setTimestamp(2, DatabaseUtils.toDBDateFormat(arrayRec.getScanDate()));
				psInsert.setString(3, arrayRec.getGenomicBuild());
				psInsert.setString(4, arrayRec.getDesignName());
				psInsert.setString(5, arrayRec.getFeVersion());
				if (arrayRec.getProjectId()==null){
					psInsert.setNull(6, Types.INTEGER);
				}else{
					psInsert.setInt(6, arrayRec.getProjectId());
				}
				psInsert.setInt(7, arrayRec.getUserID());
				psInsert.executeUpdate();
				rsk = psInsert.getGeneratedKeys();
				if (rsk.next()){
					arrayRec.setID(rsk.getInt(1));
				}else{
					return null;
				}
				rsk.close();
			}
			rs.close();
			ps.close();
			psInsert.close();
			return arrayRec;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(ps);
			SQLUtils.safeClose(rsk);
			SQLUtils.safeClose(psInsert);
			pool.close(con);
		}
	}
}
