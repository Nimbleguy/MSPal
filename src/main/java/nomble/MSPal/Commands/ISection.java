package nomble.MSPal.Commands;

public interface ISection{
	/**
	 * Takes a guild id as an argument.
	 * [][0] is a name, [][1] is a description.
	**/
	public String[][] getInfo(long c);

	/**
	 * [0] is a representative emoji, [1] is the name, [2] is a description, [3] is the EnumSection, [4] is the emoji in string form, [5] is the description to be used in the dedicated help.
	**/
	public String[] desc();

	public void load();
}
