package nomble.MSPal.Data.Impl;

import java.io.UnsupportedEncodingException;
import java.sql.*;

import nomble.MSPal.Core.Util;
import nomble.MSPal.Data.IData;

/**
 * PRIMARY VARBINARY(255) hmac
 * 
 * User Data Stored:
 * User IDs (HMAC-SHA256'd)
 */
public class DataConsent implements IData{
	private final String table;
	
	public DataConsent(){
		table = Util.getSQLPrefix() + "consent";
	}
	
	public void setConsent(long l, boolean b){
		try(PreparedStatement psd = Util.getSQL().get("DELETE FROM " + table + " WHERE hmac = ?");
			PreparedStatement psi = Util.getSQL().get("INSERT INTO " + table + " (hmac) VALUES (?)")){
			String s = String.valueOf(l);
			byte[] h = Util.mac(s.getBytes("UTF-8"));
			
			psd.setBytes(1, h);
			psd.executeUpdate();
			
			if(b){
				psi.setBytes(1, h);
				psi.executeUpdate();
			}
		}
		catch(SQLException | UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public boolean getConsent(long l){
		try(PreparedStatement ps = Util.getSQL().get("SELECT * FROM " + table + " WHERE hmac = ?")){
			String s = String.valueOf(l);
			byte[] h = Util.mac(s.getBytes("UTF-8"));
			
			ps.setBytes(1, h);
			
			ResultSet rs = ps.executeQuery();
			return rs.next();
		}
		catch(SQLException | UnsupportedEncodingException e){
			e.printStackTrace();
			return false;
		}
	}
}
