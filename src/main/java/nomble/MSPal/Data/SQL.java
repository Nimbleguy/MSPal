package nomble.MSPal.Data;

import java.security.SecureRandom;
import java.sql.*;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import nomble.MSPal.MSPal;
import nomble.MSPal.Data.Impl.DataSettings;

import org.bouncycastle.crypto.generators.SCrypt;

public class SQL{
	private Connection conn;
	private byte[] aes;
	private byte[] hmac;

	public SQL(String c, String u, String p){
		try{
			conn = DriverManager.getConnection("jdbc:mysql://" + c, u, p);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}

	public PreparedStatement get(String s){
		try{
			return conn.prepareStatement(s);
		}
		catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] getAES(){
		return aes;
	}
	public byte[] getHMAC(){
		return hmac;
	}
	
	public boolean password(String p){
		DataSettings ds = MSPal.pal.getData(DataSettings.class);
		
		String ss = ds.getSetting("salt");
		String cs = ds.getSetting("csum");
		String ps = ds.getSetting("pass");
		
		// Get salt
		byte[] s = new byte[16];
		if(ss == null){
			SecureRandom sr = new SecureRandom();
			sr.nextBytes(s);
			
			ss = DatatypeConverter.printHexBinary(s);
			ds.setSetting("salt", ss);
		}
		else{
			s = DatatypeConverter.parseHexBinary(ss);
		}
		
		// Get keys (128 checksum, 128 aes, 256 hmac)
		byte[] k = SCrypt.generate(p.getBytes(), s, 16384, 8, 1, 512);
		
		byte[] c = Arrays.copyOfRange(k, 0, 16);
		byte[] a = Arrays.copyOfRange(k, 16, 32);
		byte[] h = Arrays.copyOfRange(k, 32, 64);
		
		// Get, check, and store key hash.
		byte[] u = new byte[16];
		if(cs == null){
			SecureRandom sr = new SecureRandom();
			sr.nextBytes(u);
			
			cs = DatatypeConverter.printHexBinary(u);
			ds.setSetting("csum", cs);
		}
		else{
			u = DatatypeConverter.parseHexBinary(cs);
		}
		
		byte[] e = SCrypt.generate(c, u, 16384, 8, 1, 128);
		
		String es = DatatypeConverter.printHexBinary(e);
		if(ps == null){
			ps = es;
			ds.setSetting("pass", ps);
		}
		else if(!ps.equals(es)){
			return false;
		}
			
		// Set internal keys.
		aes = a;
		hmac = h;
		
		return true;
	}
}
