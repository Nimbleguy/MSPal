package nomble.MSPal.Data.Impl;

import java.sql.*;

import nomble.MSPal.Core.Util;
import nomble.MSPal.Data.IData;

/**
 * User Data Stored:
 * N/A
 */
public class DataSettings implements IData{
	private final String table;
	
	public DataSettings(){
		table = Util.getSQLPrefix() + "settings";
	}
	
	public void setSetting(String k, String v){
		try(PreparedStatement ps = Util.getSQL().get("INSERT INTO " + table + " (k, v) VALUES (?, ?) ON DUPLICATE KEY UPDATE v = ?")){
			ps.setString(1, k);
			ps.setString(2, v);
			ps.setString(3, v);
			ps.executeUpdate();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}

	public String getSetting(String k){
		try(PreparedStatement ps = Util.getSQL().get("SELECT * FROM " + table + " WHERE k = ?")){
			ps.setString(1, k);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("v");
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
}
