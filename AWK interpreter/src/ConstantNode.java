
public class ConstantNode implements Node{
	public Token value;
	/**
	 * Constructor for the ConstantNode
	 * @param value
	 */
	public ConstantNode(Token value) {
		this.value = value;
	}
	/**
	 * returns the display for this object
	 * @return value in Stirng format
	 */
	public String toString() {
		return value.value + "";
	}
	/**
	 * throws an exception at the line of this token
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		throw new Exception(exception + " on line: " + value.lineNumber);
	}
}
