
public class DoWhileNode extends StatementNode{
	public OperationNode condition;
	public BlockNode block;
	/**
	 * Empty constructor for the DoWhileNode class
	 */
	public DoWhileNode() {}
	/**
	 * Displays information for the given do while loop
	 * @return do while loop display
	 */
	public String toString() {
		return "do " + block.toString() + "while(" + condition.toString() + ");";
	}
	/**
	 * Throw the exception of the target of this do while node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		condition.exceptionToThrow(exception);
	}
}
