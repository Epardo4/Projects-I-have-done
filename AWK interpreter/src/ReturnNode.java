
public class ReturnNode extends StatementNode {
	public Node toReturn;
	/**
	 * Constructor for the ReturnNode class
	 * @param toReturn
	 */
	public ReturnNode(Node toReturn) {
		this.toReturn = toReturn;
	}
	/**
	 * Displays information for the given return statement
	 * @return return statement display
	 */
	public String toString() {
		return "return " + toReturn + ";";
	}
	/**
	 * Throw the exception of the target of this return node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		toReturn.exceptionToThrow(exception);
	}
}
