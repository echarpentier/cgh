package fr.pfgen.cgh.server.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import fr.pfgen.cgh.server.database.ConnectionPool;

public class DatabaseUtils {

	public static SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
	
	public static Date stringToDate(String sDate) throws ParseException {
        return formatter.parse(sDate);
    }
    
    public static Timestamp toDBDateFormat(Date utilDate) throws ParseException {
    	return new Timestamp(utilDate.getTime());
    }
    
    public static String dateToString(Date date) {
        return formatter.format(date);
        
    }
    
    /**
     * Counts the row in a query.
     * If execute=FALSE, the fields prompted in the query will be replaced by "count(*)" and ResultSet will contained only the row number, which will be returned.
     * If execute=TRUE, the query will be executed and size of the ResultSet will be returned.
     * 
     * @param pool
     * @param query
     * @param execute
     * @return int - the row number of the executed query
     */
    public static int countRowInQuery(ConnectionPool pool, String query, boolean execute){
		int numberOfRows = 0;
		String count = query.replaceFirst("^SELECT\\s[\\w.*,\\s]+\\sFROM", "SELECT count(*) FROM");
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = pool.getConnection();
			if (execute){
				ps = con.prepareStatement(query);
				rs = ps.executeQuery();
				rs.last();
				numberOfRows = rs.getRow();
			}else{
				ps = con.prepareStatement(count);
				rs = ps.executeQuery();
				rs.next();
				numberOfRows = rs.getInt(1);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			if (execute){
				System.out.println(query);
			}else{
				System.out.println(count);
			}
			throw new RuntimeException(e);
		} finally {
			if (rs!=null){
				try{rs.close();}catch(SQLException e){}
			}
			if (ps!=null){
				try{ps.close();}catch(SQLException e){}
			}
			pool.close(con);
		}
		return numberOfRows;	
	}
    
    public static String constructWhereClause(Map<String,String> dbFieldValueMap){
    	StringBuilder whereClause = new StringBuilder();
    	if (dbFieldValueMap!=null && !dbFieldValueMap.isEmpty()){
    		whereClause.append(" WHERE ");
    		for (String tableField : dbFieldValueMap.keySet()) {
    			if (tableField.contains(";") || dbFieldValueMap.get(tableField).contains(";")) throw new RuntimeException("Found a ';' while constructing where clause: "+tableField+" "+dbFieldValueMap.get(tableField));
				if (tableField.toLowerCase().contains("delete") || dbFieldValueMap.get(tableField).toLowerCase().contains("delete")) throw new RuntimeException("Found the word 'delete' while contructing where clause: "+tableField+" "+dbFieldValueMap.get(tableField));
				if (tableField.toLowerCase().contains("update") || dbFieldValueMap.get(tableField).toLowerCase().contains("update")) throw new RuntimeException("Found the word 'update' while contructing where clause: "+tableField+" "+dbFieldValueMap.get(tableField));
				if (tableField.toLowerCase().contains("insert") || dbFieldValueMap.get(tableField).toLowerCase().contains("insert")) throw new RuntimeException("Found the word 'insert' while contructing where clause: "+tableField+" "+dbFieldValueMap.get(tableField));

				whereClause.append(tableField+"=\""+dbFieldValueMap.get(tableField)+"\" AND ");
			}
    		whereClause.append("TRUE");
    	}else{
    		whereClause.append("");
    	}
    	return whereClause.toString();
    }
    
    public static String constructOrderByClause(String sortByString){
    	StringBuilder orderBy = new StringBuilder();
    	if (sortByString != null && !sortByString.isEmpty()){
    		orderBy.append(" ORDER BY ");
			String[] orderByCrits = sortByString.split(",");
			for (String crit : orderByCrits) {
				if (crit.startsWith("-")){
					orderBy.append(crit.replaceFirst("-", "")+" DESC,");
				}else{
					orderBy.append(crit+" ASC,");
				}
			}
			if (orderBy.toString().endsWith(",")){
				orderBy.deleteCharAt(orderBy.lastIndexOf(","));
			}
		}else{
			orderBy.append("");
		}
		return orderBy.toString();
    }
    
    public static void initDatabase(ConnectionPool pool, File sqlFile){
        Connection con = null;
        Statement st = null;
        String mySb=null;
        try{
        	con = pool.getConnection();
            mySb=IOUtils.copyToString(sqlFile);
            
            //Remove comments
            Pattern commentPattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
            mySb = commentPattern.matcher(mySb).replaceAll("");

            // We use ";" as a delimiter for each request then we are sure to have well formed statements
            String[] inst = mySb.split(";");

            st = con.createStatement();

            for(int i = 0; i<inst.length; i++){
                // we ensure that there is no spaces before or after the request string
                // in order not to execute empty statements
                if(!inst[i].trim().isEmpty()){
                    st.executeUpdate(inst[i]);
                }
            }
            st.close();
        }catch(IOException e){
        	throw new RuntimeException(e);
        }catch(SQLException e){
        	throw new RuntimeException(e);
        }finally{
        	SQLUtils.safeClose(st);
        	pool.close(con);
        }
    }
    
    public static void safeClose(ResultSet rs){
    	try {
    		if(rs!=null) rs.close();
		}catch (SQLException e) {}	
	}
    
    public static void safeClose(PreparedStatement ps){
    	try {
    		if(ps!=null) ps.close();
		}catch (SQLException e) {}	
	}
    
    public static void safeClose(Statement stmt){
    	try {
    		if(stmt!=null) stmt.close();
		}catch (SQLException e) {}	
	}
    
    public static String realPathToDbPath(String realPath){
    	return realPath.replaceFirst(GlobalDefs.getInstance().getCghPath(), GlobalDefs.getInstance().getCghPathReplacementInDB());
	}
	
	public static String dbPathToRealPath(String dbPath){
		return dbPath.replaceFirst(GlobalDefs.getInstance().getCghPathReplacementInDB(), GlobalDefs.getInstance().getCghPath());
	}
}
