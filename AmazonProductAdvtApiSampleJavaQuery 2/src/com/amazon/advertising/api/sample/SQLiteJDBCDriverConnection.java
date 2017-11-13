package com.amazon.advertising.api.sample;

import java.sql.Statement;
import java.util.Map;
import java.util.Map.Entry;

import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
/**
 *
 * @author sqlitetutorial.net
 */
public class SQLiteJDBCDriverConnection {
     /**
     * Connect to a sample database
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:MBA_DB";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
        return conn;
    }
    
    private String getMapValues(Map<String,String> data){
    	String result="";
    	for (Entry<String, String>  e : data.entrySet()) {
    		if (!result.isEmpty()){
    			result+=",";
    		}
    		result+="'"+e.getValue()+"'";
		}
    	return result;
    }
    
    private String getMapColumns(Map<String,String> data){
    	String result="";
    	for (Entry<String, String>  e : data.entrySet()) {
    		if (!result.isEmpty()){
    			result+=",";
    		}
    		result+="'"+e.getKey()+"'";
		}
    	return result;
    }
    
    public void insert(String tbName,Map<String,String> data) throws SQLException{
    	Connection cn = connect();
    	Statement s;
		
			
			s = cn.createStatement();
			String Values= getMapValues(data);
			String Columns= getMapColumns(data);
	    	String sql = String.format( "insert into %s  (%s) values(%s);",tbName, Columns, Values);
	    //	System.out.println(sql);
	    	s.execute(sql);
		
    	try {
			cn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        connect();
    }
}