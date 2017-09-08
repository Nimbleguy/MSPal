package nomble.MSPal.Data.Impl;

import java.sql.*;

public class SQL{
	private Connection conn;

	public SQL(String c, String u, String p){
		conn = DriverManager.getConnection("jdbc:mysql://" + c, u, p);
	}

	public PreparedStatement get(String s){
		return conn.prepareStatement(s);
	}
}
