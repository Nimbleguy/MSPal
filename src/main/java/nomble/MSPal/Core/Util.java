package nomble.MSPal.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nomble.MSPal.Data.SQL;

public class Util{
	private static Pattern cmd = Pattern.compile(":.+?:( .+)*");
	private static SQL sql = new SQL();

	public static List<String[]> getCommand(String s){
		Matcher m = cmd.matcher(s);
		List<String[]> l = new ArrayList<String[]>();

		while(m.find()){
			l.add(m.group().split(" "));
		}

		return l;
	}

	public static String getPrefix(long g){
		return ":";
	}

	public static String getSuffix(long g){
		return ":";
	}

	public static String getSQL(){
		return "";
	}
}
