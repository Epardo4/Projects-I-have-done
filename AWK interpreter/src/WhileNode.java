
public class WhileNode extends StatementNode {
	public OperationNode condition;
	public BlockNode block;
	/**
	 * Constructor for the WhileNode class
	 * @param condition
	 */
	public WhileNode(OperationNode condition) {
		this.condition = condition;
	}
	/**
	 * Displays information for the given while loop
	 * @return while loop display
	 */
	public String toString() {
		return "while(" + condition.toString() + ") " + block.toString();
	}
	/**
	 * Throw the exception of the target of this while node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		condition.exceptionToThrow(exception);
	}
}
