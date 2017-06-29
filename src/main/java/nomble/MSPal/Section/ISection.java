package nomble.MSPal.Section;

public interface ISection{
	/**
	 * [][0] is a name, [][1] is a description.
	**/
	public String[][] getInfo(long c);

	/**
	 * [0] is a representative emoji, [1] is the name, [2] is a description.
	**/
	public String[] desc();

	public void load();
}
