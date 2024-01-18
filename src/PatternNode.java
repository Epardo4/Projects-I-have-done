
public class PatternNode implements Node{
	private Token value;
	/**
	 * Constructor for PatternNode
	 * @param value
	 */
	public PatternNode(Token value) {
		this.value = value;
	}
	/**
	 * The display of this object
	 * @return value as a String
	 */
	public String toString() {
		return "" + value.value;
	}
	/**
	 * throws an exception at the line of this token
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		throw new Exception(exception + " on line: " + value.lineNumber);
	}
}
