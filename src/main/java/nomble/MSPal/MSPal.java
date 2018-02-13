package nomble.MSPal;

import java.security.Security;

import nomble.MSPal.Core.Bot;
import nomble.MSPal.Core.EnumInput;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class MSPal{
	public static Bot pal;

	public static void main(String[] args){
		EnumInput n = null;
		String[] sa = new String[EnumInput.values().length];
		String t = null;
		for(String s : args){
			if(n != null){
				sa[n.ordinal()] = s;
				n = null;
			}
			else if(s.startsWith("-")){
				if(s.equals("-h")){
					System.out.println(	"------- MSPal -------\n" +
										"java -jar pal.jar [OPTIONS] (TOKEN)\n" +
										" -------------------\n" +
										"-o:\n" +
										"   Bot owner UID.\n" +
										"-e:\n" +
										"   Database encryption password.\n" +
										"-p:\n" +
										"   Pastebin API ID.\n" +
										"-a:\n" +
										"   SQL database IP/Name.\n" +
										"-u:\n" +
										"   SQL database username.\n" +
										"-P:\n" +
										"   SQL database password.\n" +
										"-F:\n" +
										"   SQL database table prefix.\n");
					System.exit(0);
				}

				if(s.length() != 2 || (n = EnumInput.getInput(s.toCharArray()[1])) == null){
					System.out.println("Invalid input: " + s);
					System.exit(-1);
				}
			}
			else{
				t = s;
			}
		}
		if(t == null){
			System.out.println("No token provided!");
		}

		Security.addProvider(new BouncyCastleProvider());

		pal = new Bot(t, sa);
		pal.init();
	}
}
