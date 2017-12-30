package nomble.MSPal.Core;

import nomble.MSPal.Data.SQL;
import nomble.MSPal.Data.Impl.DataGuild;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Util{
	private static final Pattern cmd = Pattern.compile(":.+?:( .+)*");
	protected static long owner;
	protected static String sqlPrefix;
	protected static SQL sql;
	protected static Bot bot;

	public static List<String[]> getCommand(String s, long g){
		Matcher m = cmd.matcher(s);
		List<String[]> l = new ArrayList<String[]>();

		String sb = bot.getData(DataGuild.class).getCommandBans(g);
		while(m.find()){
			String[] sa = m.group().split(" ");
			if(sb == null || !sb.contains(sa[0].replaceAll(":", "") + "|")){
				l.add(sa);
			}
		}

		return l;
	}

	public static String getPrefix(long l){
		return ":";
	}

	public static String getSuffix(long l){
		return ":";
	}
	
	public static long getOwner(){
		return owner;
	}

	public static String getSQLPrefix(){
		return sqlPrefix;
	}
	
	public static SQL getSQL(){
		return sql;
	}
	
	public static byte[] cipher(byte[] vs, byte[] iv, int t){
		try{
			Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
			Key k = new SecretKeySpec(sql.getAES(), "AES");
			AlgorithmParameterSpec i = new IvParameterSpec(iv);
			
			c.init(t, k, i); // t = Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
			return c.doFinal(vs);
		}
		catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static byte[] mac(byte[] s){
		try{
			Mac m = Mac.getInstance("Hmac-SHA256");
			Key k = new SecretKeySpec(sql.getHMAC(), "RAW");
			
			m.init(k);
			return m.doFinal(s);
		}
		catch(NoSuchAlgorithmException | InvalidKeyException | IllegalStateException e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static byte[] getIV(int i){
		SecureRandom sr = new SecureRandom();
		byte[] b = new byte[i];
		sr.nextBytes(b);
		return b;
	}
}
