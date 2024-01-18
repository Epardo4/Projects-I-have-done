
public class DeleteNode extends StatementNode {
	public VariableReferenceNode toDelete;
	/**
	 * Constructor for the DeleteNode class
	 * @param toDelete
	 */
	public DeleteNode(VariableReferenceNode toDelete) {
		this.toDelete = toDelete;
	}
	/**
	 * Throw the exception of the target of this delete node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		toDelete.exceptionToThrow(exception);
	}
	/**
	 * Displays information for the given delete statement
	 * @return delete statement display
	 */
	public String toString() {
		return "delete(" + toDelete.toString() + ");";
	}
}
