package fr.pfgen.cgh.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.server.utils.SQLUtils;

public class QcParamTable {

	public static List<String> getQcParamNames(ConnectionPool pool) {
		String query = "SELECT qc_param_name FROM qc_params ORDER BY qc_param_id";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			List<String> qcNames = new ArrayList<String>();
			while(rs.next()){
				qcNames.add(rs.getString("qc_param_name"));
			}
			rs.close();
			ps.close();
			return qcNames;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}

	public static void addQcToFrame(ConnectionPool pool, Map<String, String> qcMap, int frameID) {
		String query = "INSERT INTO qc_values (frame_id,qc_param_id,qc_value) VALUES (?,(SELECT qc_param_id FROM qc_params WHERE qc_param_name=?),?)";
		Connection con = null;
		PreparedStatement ps = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setInt(1, frameID);
			for (String qcName : qcMap.keySet()) {
				ps.setString(2, qcName);
				ps.setString(3, qcMap.get(qcName));
				ps.executeUpdate();
			}
			ps.close();
		}catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot insert qc from frame: "+frameID);
		}finally{
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}
}
