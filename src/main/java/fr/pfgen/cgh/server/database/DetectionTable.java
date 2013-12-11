package fr.pfgen.cgh.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.server.utils.SQLUtils;
import fr.pfgen.cgh.shared.enums.DetectionQuality;
import fr.pfgen.cgh.shared.records.DetectionRecord;

public class DetectionTable {

	public static String constructQuery(String sortBy, Map<String, String> filterCriteria) {
		String query = new String();
		
		//default query
		query = "SELECT * FROM (((detections JOIN frames ON detections.frame_id=frames.frame_id) JOIN arrays ON arrays.array_id=frames.array_id) LEFT JOIN projects on projects.project_id=arrays.project_id)";
		
		if (filterCriteria!=null && !filterCriteria.isEmpty()){
			Map<String,String> critMap = new Hashtable<String, String>();
			if (filterCriteria.containsKey("array_id")){
				critMap.put("arrays.array_id", filterCriteria.get("array_id"));
			}
			if (filterCriteria.containsKey("project_id")){
				critMap.put("projects.project_id", filterCriteria.get("project_id"));
			}
			if (filterCriteria.containsKey("frame_id")){
				critMap.put("frames.frame_id", filterCriteria.get("frame_id"));
			}
			if (!critMap.isEmpty()){
				query = query.concat(DatabaseUtils.constructWhereClause(critMap));
			}
		}
		
		if (sortBy==null){
			sortBy = "frames.frame_id,d_chr,d_start,d_end";
		}
		
		query = query.concat(DatabaseUtils.constructOrderByClause(sortBy));
		return query;
	}

	public static List<DetectionRecord> getDetections(ConnectionPool pool, String query, Integer startRow, Integer endRow) {
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
			
			List<DetectionRecord> detectionList = new ArrayList<DetectionRecord>();
			while(rs.next()){
				DetectionRecord rec = new DetectionRecord();
				rec.setID(rs.getInt("d_id"));
				rec.setChr(rs.getInt("d_chr"));
				rec.setStart(rs.getInt("d_start"));
				rec.setEnd(rs.getInt("d_end"));
				rec.setProbeNumber(rs.getInt("d_probes_number"));
				rec.setLRmedian(rs.getDouble("d_LR_median"));
				rec.setFrameID(rs.getInt("frame_id"));
				rec.setFrameName(rs.getString("frame_name"));
				rec.setGenomicBuild(rs.getString("genomic_build"));
				rec.setQuality(DetectionQuality.parse(rs.getString("d_quality")));
				
				String chr = Integer.toString(rs.getInt("d_chr"));
				if (chr.equals("23")){
					chr = "X";
				}else if (chr.equals("24")){
					chr = "Y";
				}
				rec.setUCSCPosition("chr"+chr+":"+rs.getInt("d_start")+"-"+rs.getInt("d_end"));
				detectionList.add(rec);
			}
			rs.close();
			stmt.close();
			return detectionList;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(stmt);
			pool.close(con);
		}
	}

	public static boolean setQualityToSmForFrame(ConnectionPool pool, int frameID) {
		String query = "UPDATE detections SET d_quality=\"SM\" WHERE frame_id=? AND (d_chr=23 OR d_chr=24)";
		Connection con = null;
		PreparedStatement ps = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setInt(1, frameID);
			ps.execute();
			ps.close();
			return true;
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}finally{
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}

	public static DetectionRecord updateRecordQuality(ConnectionPool pool, DetectionRecord data) {
		String query = "UPDATE detections SET d_quality=? WHERE d_id=?";
		Connection con = null;
		PreparedStatement ps = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			if (data.getQuality()==null){
				ps.setNull(1, java.sql.Types.VARCHAR);
			}else{
				ps.setString(1, data.getQuality().toString());
			}
			ps.setInt(2, data.getID());
			ps.execute();
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

	public static boolean setQualityToNoSmForFrame(ConnectionPool pool, int frameID) {
		String query = "UPDATE detections SET d_quality=NULL WHERE frame_id=? AND (d_chr=23 OR d_chr=24)";
		Connection con = null;
		PreparedStatement ps = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setInt(1, frameID);
			ps.execute();
			ps.close();
			return true;
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}finally{
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}

	public static void addCNVToFrame(ConnectionPool pool, List<DetectionRecord> detList, int frameID) {
		String query = "INSERT INTO detections (d_chr,d_start,d_end,d_probes_number,d_LR_median,frame_id) VALUES (?,?,?,?,?,?)";
		Connection con = null;
		PreparedStatement ps = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setInt(6, frameID);
			for (DetectionRecord detRec : detList) {
				ps.setInt(1, detRec.getChr());
				ps.setInt(2, detRec.getStart());
				ps.setInt(3, detRec.getEnd());
				ps.setInt(4, detRec.getProbeNumber());
				ps.setDouble(5, detRec.getLRmedian());
				ps.executeUpdate();
			}
			ps.close();
		}catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot add detection to frame: "+frameID);
		}finally{
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}
}
