package nomble.MSPal;

import nomble.MSPal.Core.Bot;

public class MSPal{
	public static Bot pal;

	public static void main(String[] args){
		if(args.length < 2 || args.length > 3){
			System.err.println("Invalid amount of arguments. Must be either 2 or 3.");
			System.exit(-1);
		}

		if(args.length == 3){
			pal = new Bot(args[0], args[1], args[2]);
		}
		else{
			pal = new Bot(args[0], args[1], null);
		}

		pal.init();
	}
}
