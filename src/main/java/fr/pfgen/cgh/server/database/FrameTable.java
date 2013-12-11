package fr.pfgen.cgh.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.server.utils.SQLUtils;
import fr.pfgen.cgh.shared.records.FrameRecord;

public class FrameTable {

	public static Map<Integer,String> getFrameIdsForArray(ConnectionPool pool, int arrayID) {
		String query = "SELECT frame_id,frame_name FROM frames WHERE array_id=? ORDER BY frame_name";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Map<Integer, String> frameIds = new LinkedHashMap<Integer, String>();
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setInt(1, arrayID);
			rs = ps.executeQuery();
			while(rs.next()){
				frameIds.put(rs.getInt("frame_id"),rs.getString("frame_name"));
			}
			rs.close();
			ps.close();
			return frameIds;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}

	public static String constructQuery(String sortBy, Map<String, String> filterCriteria) {
		String query = new String();
		
		//default query
		query = "SELECT * FROM ((frames JOIN arrays ON arrays.array_id=frames.array_id) LEFT JOIN projects ON projects.project_id=arrays.project_id)";
		
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
			if (filterCriteria.containsKey("frame_id")){
				critMap.put("frames.frame_id", filterCriteria.get("frame_id"));
			}
			if (!critMap.isEmpty()){
				query = query.concat(DatabaseUtils.constructWhereClause(critMap));
			}
		}
		
		query = query.concat(DatabaseUtils.constructOrderByClause(sortBy));
		return query;
	}

	public static List<FrameRecord> getFrames(ConnectionPool pool, String query, Integer startRow, Integer endRow) {
		if (startRow != null && endRow != null && endRow >= startRow) {
			Integer size = endRow-startRow;
        	query = query.concat(" LIMIT "+startRow+","+size);
		}
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String qcQuery = "SELECT * FROM qc_values JOIN qc_params ON qc_params.qc_param_id=qc_values.qc_param_id WHERE frame_id=? ORDER BY qc_params.qc_param_id";
	
		PreparedStatement psQC = null;
		ResultSet rsQC = null;
		
		try{
			con = pool.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			psQC = con.prepareStatement(qcQuery);
			
			List<FrameRecord> frameList = new ArrayList<FrameRecord>();
			while(rs.next()){
				FrameRecord rec = new FrameRecord();
				rec.setID(rs.getInt("frame_id"));
				rec.setName(rs.getString("frame_name"));
				rec.setRefName(rs.getString("ref_name"));
				rec.setTestName(rs.getString("test_name"));
				rec.setArrayID(rs.getInt("array_id"));
				rec.setArrayName(rs.getString("array_name"));
				rec.setFEFilePath(DatabaseUtils.dbPathToRealPath(rs.getString("fe_file_path")));
				rec.setResultFolderPath(DatabaseUtils.dbPathToRealPath(rs.getString("result_folder_path")));
				
				psQC.setInt(1, rs.getInt("frame_id"));
				rsQC = psQC.executeQuery();
				Map<String, String> qcMap = new LinkedHashMap<String, String>();
				
				while(rsQC.next()){
					qcMap.put(rsQC.getString("qc_param_name"), rsQC.getString("qc_value"));
				}
				rsQC.close();
				rec.setQcResultMap(qcMap);
				
				frameList.add(rec);
			}
			rs.close();
			stmt.close();
			return frameList;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rsQC);
			SQLUtils.safeClose(psQC);
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(stmt);
			pool.close(con);
		}
	}

	public static FrameRecord addFrame(ConnectionPool pool, FrameRecord frameRec) {
		String query = "REPLACE INTO frames (frame_name,array_id,fe_file_path,result_folder_path) VALUES (?,?,?,?)";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rsk = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, frameRec.getName());
			ps.setInt(2, frameRec.getArrayID());
			ps.setString(3, DatabaseUtils.realPathToDbPath(frameRec.getFEFilePath()));
			ps.setString(4, DatabaseUtils.realPathToDbPath(frameRec.getResultFolderPath()));
			ps.executeUpdate();
			rsk = ps.getGeneratedKeys();
			if (rsk.next()){
				frameRec.setID(rsk.getInt(1));
			}else{
				return null;
			}
			rsk.close();
			ps.close();
			return frameRec;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rsk);
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}

	public static FrameRecord updateIds(ConnectionPool pool, FrameRecord data) {
		String query = "UPDATE frames SET ref_name=?,test_name=? WHERE frame_id=?";
		Connection con = null;
		PreparedStatement ps = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			if (data.getRefName()==null || data.getRefName().isEmpty()){
				ps.setNull(1, Types.VARCHAR);
			}else{
				ps.setString(1, data.getRefName());
			}
			if (data.getTestName()==null || data.getTestName().isEmpty()){
				ps.setNull(2, Types.VARCHAR);
			}else{
				ps.setString(2, data.getTestName());
			}
			ps.setInt(3, data.getID());
			ps.executeUpdate();
			ps.close();
			return data;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}
}
