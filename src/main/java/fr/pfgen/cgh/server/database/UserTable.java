package fr.pfgen.cgh.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import fr.pfgen.cgh.server.utils.DatabaseUtils;
import fr.pfgen.cgh.server.utils.MD5Password;
import fr.pfgen.cgh.server.utils.SQLUtils;
import fr.pfgen.cgh.shared.enums.UserStatus;
import fr.pfgen.cgh.shared.records.UserRecord;

public class UserTable {
	
	public static UserRecord checkUserLogin(ConnectionPool pool, String appID, String password) {
		String query = "SELECT * FROM users u JOIN teams t ON u.team_id=t.team_id WHERE app_id=?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection con = null;
		
		UserRecord user = new UserRecord();
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, appID);
			rs = ps.executeQuery();
			if (rs.next()) {
				do {
					if (MD5Password.testPassword(password,rs.getString("app_passwd"))){
						user.setLoginText("User authenticated !\nWelcome "+rs.getString("firstname")+" "+rs.getString("lastname"));
						user.setUserID(rs.getInt("user_id"));
						user.setFirstname(rs.getString("firstname"));
						user.setLastname(rs.getString("lastname"));
						user.setEmail(rs.getString("email"));
						user.setOffice_number(rs.getString("office_number"));
						user.setAppID(rs.getString("app_id"));
						user.setAppStatus(UserStatus.parse(rs.getString("app_status")));
						user.setTeamID(rs.getInt("team_id"));
						user.setTeamName(rs.getString("team_name"));
					}else{
						user.setLoginText("Incorrect password for user \""+appID+"\"");
					}
				} while (rs.next());
			} else {
				user.setLoginText("Incorrect username");
			}
			rs.close();
			ps.close();		
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
		return user;
	}

	public static String constructQuery(String sortBy, Map<String, String> filterCriteria) {
		String query = new String();
		
		//default query
		query = "SELECT * FROM users u JOIN teams t ON u.team_id=t.team_id";
		
		if (filterCriteria!=null && !filterCriteria.isEmpty()){
			Map<String,String> critMap = new Hashtable<String, String>();
			if (filterCriteria.containsKey("team_id")){
				critMap.put("t.team_id", filterCriteria.get("team_id"));
			}
			if (filterCriteria.containsKey("user_id")){
				critMap.put("u.user_id", filterCriteria.get("user_id"));
			}
			if (!critMap.isEmpty()){
				query = query.concat(DatabaseUtils.constructWhereClause(critMap));
			}
		}
		
		query = query.concat(DatabaseUtils.constructOrderByClause(sortBy));
		return query;
	}

	public static List<UserRecord> getUsers(ConnectionPool pool, String query, Integer startRow, Integer endRow) {
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
			
			List<UserRecord> userList = new ArrayList<UserRecord>();
			while(rs.next()){
				UserRecord rec = new UserRecord();
				rec.setUserID(rs.getInt("user_id"));
				rec.setFirstname(rs.getString("firstname"));
				rec.setLastname(rs.getString("lastname"));
				rec.setEmail(rs.getString("email"));
				rec.setOffice_number(rs.getString("office_number"));
				rec.setAppID(rs.getString("app_id"));
				//rec.setPassword(rs.getString("app_passwd"));
				rec.setAppStatus(UserStatus.parse(rs.getString("app_status")));
				rec.setTeamID(rs.getInt("team_id"));
				rec.setTeamName(rs.getString("team_name"));
				
				userList.add(rec);
			}
			rs.close();
			stmt.close();
			return userList;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(stmt);
			pool.close(con);
		}
	}

	public static UserRecord addUser(ConnectionPool pool, UserRecord data) {
		String query = "INSERT INTO users (firstname,lastname,email,office_number,team_id,app_id,app_passwd,app_status) VALUES (?,?,?,?,(SELECT team_id FROM teams WHERE team_name=?),?,md5(?),?)";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rsk = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, data.getFirstname());
			ps.setString(2, data.getLastname());
			ps.setString(3, data.getEmail());
			if (data.getOffice_number()==null || data.getOffice_number().isEmpty()){
				ps.setNull(4, Types.VARCHAR);
			}else{
				ps.setString(4, data.getOffice_number());
			}
			ps.setString(5, data.getTeamName());
			ps.setString(6, data.getAppID());
			ps.setString(7, data.getPassword());
			ps.setString(8, UserStatus.toDBformat(data.getAppStatus()));
			
			ps.executeUpdate();
			rsk = ps.getGeneratedKeys();
			if (rsk.next()){
				data.setUserID(rsk.getInt(1));
			}else{
			    data.setLoginText("Could not insert user in database");
			}
			rsk.close();
			ps.close();
			return data;
		}catch(SQLException e){
			e.printStackTrace();
		    data.setLoginText("Could not insert user in database");
		    return data;
		}finally{
			SQLUtils.safeClose(rsk);
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}

	public static UserRecord updateUser(ConnectionPool pool, UserRecord data) {
		String query = "UPDATE IGNORE users SET firstname=?,lastname=?,email=?,office_number=?,app_status=? WHERE user_id=?";
		Connection con = null;
		PreparedStatement ps = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, data.getFirstname());
			ps.setString(2, data.getLastname());
			ps.setString(3, data.getEmail());
			ps.setString(4, data.getOffice_number());
			ps.setString(5, UserStatus.toDBformat(data.getAppStatus()));
			ps.setInt(6, data.getUserID());
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

	public static String changePassword(ConnectionPool pool, int userID, String password) {
		String query = "UPDATE users SET app_passwd=md5(?) WHERE user_id=?";
		Connection con = null;
		PreparedStatement ps = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, password);
			ps.setInt(2, userID);
			ps.executeUpdate();
			ps.close();
			return "Successfully changed password";
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}
}
