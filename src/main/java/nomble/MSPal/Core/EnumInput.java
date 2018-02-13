package nomble.MSPal.Core;

public enum EnumInput{
	OWNER('o'),
	ENCRYPT('e'),
	PASTEBIN('p'),
	SQLADDR('a'),
	SQLUSER('u'),
	SQLPASS('P'),
	SQLPREF('F');
	
	private char option;
	
	private EnumInput(char o){
		option = o;
	}
	
	public char getOption(){
		return option;
	}
	
	public static EnumInput getInput(char c){
		for(EnumInput ei : EnumInput.values()){
			if(ei.option == c){
				return ei;
			}
		}
		return null;
	}
}
