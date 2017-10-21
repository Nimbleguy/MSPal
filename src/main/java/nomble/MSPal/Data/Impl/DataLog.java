package nomble.MSPal.Data.Impl;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.time.ZoneOffset;

import javax.crypto.Cipher;
import org.bouncycastle.util.Arrays;

import nomble.MSPal.Core.Util;
import nomble.MSPal.Data.IData;

import sx.blah.discord.handle.obj.IMessage;

/**
 * PRIMARY VARBINARY(255) hmac, BLOB usr, BLOB time, LONGBLOB msg, BLOB mid, BLOB lind,
 * BLOB usri, BLOB timei, BLOB msgi, BLOB midi, BLOB lindi,
 * BLOB usrh, BLOB timeh, BLOB msgh, BLOB midh, PRIMARY VARBINARY(255) lindh
 * 
 * User Data Stored:
 * IDs [Log ID or UID] (HMAC'd)
 * Usernames (AES-128-CTR'd)
 * Timestamps (AES-128-CTR'd)
 * Message Content (AES-128-CTR'd)
 * Message IDs (AES-128-CTR'd)
 */
public class DataLog implements IData{
	private final String table;
	
	public DataLog(){
		table = Util.getSQLPrefix() + "log";
	}
	
	public void addNote(long l, IMessage m){
		try(PreparedStatement psi = Util.getSQL().get("INSERT INTO " + table + " (hmac, time, timei, timeh, msg, msgi, msgh, lind, lindi, lindh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")){
			String s = String.valueOf(l);
			
			byte[] ti = Util.getIV(16);
			byte[] mi = Util.getIV(16);
			byte[] li = Util.getIV(16);
			
			String u = String.valueOf(m.getTimestamp().toEpochSecond(ZoneOffset.UTC));
			String i = String.valueOf(getFreeIndex(l));
			
			psi.setBytes(1, Util.mac(s.getBytes("UTF-8")));
			
			psi.setBytes(2, Util.cipher(u.getBytes("UTF-8"), ti, Cipher.ENCRYPT_MODE));
			psi.setBytes(3, ti);
			psi.setBytes(4, Util.mac(u.getBytes("UTF-8")));
			
			// Split because all note commands have the message at the 3+ index.
			psi.setBytes(5, Util.cipher(m.getContent().split(" ", 3)[2].getBytes("UTF-8"), ti, Cipher.ENCRYPT_MODE));
			psi.setBytes(6, mi);
			psi.setBytes(7, Util.mac(m.getContent().split(" ", 3)[2].getBytes("UTF-8")));
			
			psi.setBytes(8, Util.cipher(i.getBytes("UTF-8"), ti, Cipher.ENCRYPT_MODE));
			psi.setBytes(9, li);
			psi.setBytes(10, Util.mac(i.getBytes("UTF-8")));
			
			psi.executeUpdate();
		}
		catch(SQLException | UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public String[] getLog(long l, short i){
		try(PreparedStatement ps = Util.getSQL().get("SELECT * FROM " + table + " WHERE hmac = ? AND lindh = ?")){
			String sl = String.valueOf(l);
			String si = String.valueOf(i);
			
			ps.setBytes(1, Util.mac(sl.getBytes("UTF-8")));
			ps.setBytes(2, Util.mac(si.getBytes("UTF-8")));
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				String[] sa = new String[5];
				byte[] ba;
				
				ba = Util.cipher(rs.getBytes("usr"), rs.getBytes("usri"), Cipher.DECRYPT_MODE);
				assert Arrays.areEqual(Util.mac(ba), rs.getBytes("usrh"));
				sa[EnumResults.USR.ordinal()] = new String(ba, "UTF-8");
				
				ba = Util.cipher(rs.getBytes("time"), rs.getBytes("timei"), Cipher.DECRYPT_MODE);
				assert Arrays.areEqual(Util.mac(ba), rs.getBytes("timeh"));
				sa[EnumResults.TIME.ordinal()] = new String(ba, "UTF-8");
				
				ba = Util.cipher(rs.getBytes("msg"), rs.getBytes("msgi"), Cipher.DECRYPT_MODE);
				assert Arrays.areEqual(Util.mac(ba), rs.getBytes("msgh"));
				sa[EnumResults.MSG.ordinal()] = new String(ba, "UTF-8");
				
				ba = Util.cipher(rs.getBytes("mid"), rs.getBytes("midi"), Cipher.DECRYPT_MODE);
				assert Arrays.areEqual(Util.mac(ba), rs.getBytes("midh"));
				sa[EnumResults.MID.ordinal()] = new String(ba, "UTF-8");
				
				ba = Util.cipher(rs.getBytes("lind"), rs.getBytes("lindi"), Cipher.DECRYPT_MODE);
				assert Arrays.areEqual(Util.mac(ba), rs.getBytes("lindh"));
				sa[EnumResults.LIND.ordinal()] = new String(ba, "UTF-8");
				
				return sa;
			}
		}
		catch(AssertionError e){
			return null;
		}
		catch(SQLException | UnsupportedEncodingException e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int getFreeIndex(long l){
		try(PreparedStatement ps = Util.getSQL().get("SELECT * FROM " + table + " WHERE hmac = ?")){
			String s = String.valueOf(l);
			
			ps.setBytes(1, Util.mac(s.getBytes("UTF-8")));
			
			ResultSet rs = ps.executeQuery();
			int i = -1;
			while(rs.next()){
				byte[] ia = rs.getBytes("lind");
				byte[] ii = rs.getBytes("lindi");
				byte[] ih = rs.getBytes("lindh");
				
				byte[] ha = Util.cipher(ia, ii, Cipher.DECRYPT_MODE);
				String hs = new String(ha, "UTF-8");
				if(Arrays.areEqual(Util.mac(ha), ih)){
					int hi = Integer.valueOf(hs);
					if(hi > i){
						i = hi;
					}
				}
			}
			
			return i + 1;
		}
		catch(SQLException | UnsupportedEncodingException e){
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public enum EnumResults{
		USR,
		TIME,
		MSG,
		MID,
		LIND
	}
}
