
public class ForEachNode extends StatementNode{
	public BlockNode block;
	public OperationNode operation;
	/**
	 * Constructor for the ForEachNode class
	 * @param left
	 */
	public ForEachNode(OperationNode operation) {
		this.operation = operation;
	}
	/**
	 * Displays information for the given for each loop
	 * @return for each loop display
	 */
	public String toString() {
		return "for(" + operation.toString() + ") " + block.toString();
	}
	/**
	 * Throw the exception of the target of this for each node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		operation.exceptionToThrow(exception);
	}
}
