package nomble.MSPal.Commands;

public enum EnumSection{
	GENERAL("General"),
	BOT("Bot"),
	REACTION("Reactions"),
	LOG("Logging"),
	FUN("Fun");
	
	private String name;
	
	private EnumSection(String s){
		name = s;
	}
	
	public String toString(){
		return name;
	}
	
	public static EnumSection fromString(String s){
		for(EnumSection es : EnumSection.values()){
			if(es.toString().equals(s)){
				return es;
			}
		}
		return null;
	}
}
