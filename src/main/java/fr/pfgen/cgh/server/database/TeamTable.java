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
import fr.pfgen.cgh.shared.records.TeamRecord;

public class TeamTable {

	public static String constructQuery(String sortBy, Map<String, String> filterCriteria) {
		String query = new String();
		
		//default query
		query = "SELECT * FROM teams";
		
		if (filterCriteria!=null && !filterCriteria.isEmpty()){
			Map<String,String> critMap = new Hashtable<String, String>();
			if (filterCriteria.containsKey("team_id")){
				critMap.put("team_id", filterCriteria.get("team_id"));
			}
			if (!critMap.isEmpty()){
				query = query.concat(DatabaseUtils.constructWhereClause(critMap));
			}
		}
		
		query = query.concat(DatabaseUtils.constructOrderByClause(sortBy));
		return query;
	}

	public static List<TeamRecord> getTeams(ConnectionPool pool, String query, Integer startRow, Integer endRow) {
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
			
			List<TeamRecord> teamList = new ArrayList<TeamRecord>();
			while(rs.next()){
				TeamRecord rec = new TeamRecord();
				rec.setTeamID(rs.getInt("team_id"));
				rec.setTeamName(rs.getString("team_name"));
				rec.setTeamLeader(rs.getString("team_leader"));
				
				teamList.add(rec);
			}
			rs.close();
			stmt.close();
			return teamList;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(stmt);
			pool.close(con);
		}
	}

	public static List<String> getTeamNames(ConnectionPool pool) {
		String query = "SELECT team_name FROM teams";
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		List<String> list = new ArrayList<String>();
		try{
			con = pool.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()){
				list.add(rs.getString("team_name"));
			}
			rs.close();
			stmt.close();
			return list;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rs);
			SQLUtils.safeClose(stmt);
			pool.close(con);
		}
	}

	public static TeamRecord addTeam(ConnectionPool pool, TeamRecord data) {
		String query = "INSERT INTO teams (team_name,team_leader) VALUES (?,?)";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rsk = null;
		
		try{
			con = pool.getConnection();
			ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, data.getTeamName());
			ps.setString(2, data.getTeamLeader());
			
			ps.executeUpdate();
			rsk = ps.getGeneratedKeys();
			if (rsk.next()){
				data.setTeamID(rsk.getInt(1));
			}
			rsk.close();
			ps.close();
			return data;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			SQLUtils.safeClose(rsk);
			SQLUtils.safeClose(ps);
			pool.close(con);
		}
	}
}
