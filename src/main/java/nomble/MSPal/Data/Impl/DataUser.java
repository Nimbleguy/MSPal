package nomble.MSPal.Data.Impl;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.Arrays;

import javax.crypto.Cipher;

import nomble.MSPal.Core.Util;
import nomble.MSPal.Data.IData;

/**
 * PRIMARY VARBINARY(255) uid, BLOB cap,
 * BLOB uidi, BLOB capi,
 * BLOB uidh, BLOB caph
 * 
 * User Data Stored:
 * User IDs (HMAC-SHA256'd)
 */
public class DataUser implements IData{
	private final String table;
	
	public DataUser(){
		table = Util.getSQLPrefix() + "user";
	}
	
	public void setCapability(long l, char c, boolean b){
		try(PreparedStatement ps = Util.getSQL().get("SELECT * FROM " + table + " WHERE uidh = ?");
			PreparedStatement psd = Util.getSQL().get("DELETE FROM " + table + " WHERE uidh = ?");
			PreparedStatement psi = Util.getSQL().get("INSERT INTO " + table + " (uid, uidi, uidh, cap, capi, caph) VALUES (?)")){
			
			String s = String.valueOf(l);
			byte[] h = Util.mac(s.getBytes("UTF-8"));
			
			ps.setBytes(1, h);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				String ss = getCapabilities(l);
				if(ss != null){
					ss.replace(String.valueOf(c), "");
					if(b){
						ss += c;
					}
					
					psd.setBytes(1, h);
					psd.executeUpdate();
					
					byte[] iv = Util.getIV(16);
					psi.setBytes(1, Util.cipher(s.getBytes("UTF-8"), iv, Cipher.ENCRYPT_MODE));
					psi.setBytes(2, iv);
					psi.setBytes(3, h);
					iv = Util.getIV(16);
					psi.setBytes(1, Util.cipher(ss.getBytes("UTF-8"), iv, Cipher.ENCRYPT_MODE));
					psi.setBytes(2, iv);
					psi.setBytes(3, Util.mac(ss.getBytes("UTF-8")));
					
					psi.executeUpdate();
				}
			}
		}
		catch(SQLException | UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public String getCapabilities(long l){
		try(PreparedStatement ps = Util.getSQL().get("SELECT * FROM " + table + " WHERE uidh = ?")){
			String s = String.valueOf(l);
			byte[] h = Util.mac(s.getBytes("UTF-8"));
			
			ps.setBytes(1, h);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				byte[] ca = rs.getBytes("cap");
				byte[] ci = rs.getBytes("capi");
				byte[] ch = rs.getBytes("caph");
				
				byte[] cc = Util.cipher(ca, ci, Cipher.DECRYPT_MODE);
				if(Arrays.equals(Util.mac(cc), ch)){
					return new String(cc, "UTF-8");
				}
			}
		}
		catch(SQLException | UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return null;
	}
}
