package fr.pfgen.cgh.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import fr.pfgen.cgh.server.utils.SQLUtils;

public class ScanParamTable {

	public static Map<String, String> getScanParams(ConnectionPool pool){
		String query = "SELECT * FROM scan_params";
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		Map<String, String> scanParamMap = new HashMap<String, String>();
		
		try{
			con = pool.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()){
				scanParamMap.put(rs.getString("scan_param_name"), rs.getString("scan_param_def"));
			}
			rs.close();
			stmt.close();
			return scanParamMap;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(stmt);
			pool.close(con);
		}
	}
	
	public static Map<String, String> getScanParamsValuesForArray(ConnectionPool pool, int arrayID){
		String query = "SELECT * FROM scan_params sp JOIN scan_param_values sv ON sp.scan_param_id=sv.scan_param_id WHERE array_id=?";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Map<String, String> scanParamMap = new HashMap<String, String>();
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setInt(1, arrayID);
			rs = ps.executeQuery();
			while(rs.next()){
				scanParamMap.put(rs.getString("scan_param_name"), rs.getString("scan_param_value"));
			}
			rs.close();
			ps.close();
			return scanParamMap;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}

	public static void addScanParamsToArray(ConnectionPool pool, Map<String, String> paramMap, int arrayID) {
		String query = "INSERT IGNORE INTO scan_param_values (array_id,scan_param_id,scan_param_value) VALUES (?,(SELECT scan_param_id FROM scan_params WHERE scan_param_name=?),?)";
		PreparedStatement ps = null;
		Connection con = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setInt(1, arrayID);
			for (String param : paramMap.keySet()) {
				ps.setString(2, param);
				ps.setString(3, paramMap.get(param));
				ps.executeUpdate();
			}
			ps.close();
		}catch(SQLException e){
			e.printStackTrace();
			throw new RuntimeException("Cannot insert scan parameters into database for array: "+arrayID);
		}finally{
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}
}
