package nomble.MSPal.Data.Impl;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.Arrays;

import javax.crypto.Cipher;

import nomble.MSPal.Core.Util;
import nomble.MSPal.Data.IData;

/**
 * PRIMARY VARBINARY(255) gid, BLOB rno,
 * BLOB gidi, BLOB rnoi,
 * BLOB gidh, BLOB rnoh
 * 
 * User Data Stored:
 * Guild IDs (HMAC-SHA256'd)
 */
public class DataGuild implements IData{
	private final String table;
	
	public DataGuild(){
		table = Util.getSQLPrefix() + "guild";
	}
	
	public void setCommandBan(long l, String s, boolean b){
		try(PreparedStatement psd = Util.getSQL().get("DELETE FROM " + table + " WHERE gidh = ?");
			PreparedStatement psi = Util.getSQL().get("INSERT INTO " + table + " (gid, gidi, gidh, rno, rnoi, rnoh) VALUES (?, ?, ?, ?, ?, ?)")){
			
			String sl = String.valueOf(l);
			byte[] h = Util.mac(sl.getBytes("UTF-8"));
			
			String ss = getCommandBans(l);
			if(ss == null){
				ss = "";
			}
			ss = ss.replaceAll(s + "|", "");
			if(b){
				ss += s + "|";
			}
			
			psd.setBytes(1, h);
			psd.executeUpdate();
			
			byte[] iv = Util.getIV(16);
			psi.setBytes(1, Util.cipher(sl.getBytes("UTF-8"), iv, Cipher.ENCRYPT_MODE));
			psi.setBytes(2, iv);
			psi.setBytes(3, h);
			iv = Util.getIV(16);
			psi.setBytes(4, Util.cipher(ss.getBytes("UTF-8"), iv, Cipher.ENCRYPT_MODE));
			psi.setBytes(5, iv);
			psi.setBytes(6, Util.mac(ss.getBytes("UTF-8")));
			
			psi.executeUpdate();
		}
		catch(SQLException | UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public String getCommandBans(long l){
		if(l != -1){
			try(PreparedStatement ps = Util.getSQL().get("SELECT * FROM " + table + " WHERE gidh = ?")){
				String s = String.valueOf(l);
				byte[] h = Util.mac(s.getBytes("UTF-8"));
				
				ps.setBytes(1, h);
				
				ResultSet rs = ps.executeQuery();
				if(rs.next()){
					byte[] ca = rs.getBytes("rno");
					byte[] ci = rs.getBytes("rnoi");
					byte[] ch = rs.getBytes("rnoh");
					
					byte[] cc = Util.cipher(ca, ci, Cipher.DECRYPT_MODE);
					if(Arrays.equals(Util.mac(cc), ch)){
						return new String(cc, "UTF-8");
					}
				}
			}
			catch(SQLException | UnsupportedEncodingException e){
				e.printStackTrace();
			}
		}
		return null;
	}
}
