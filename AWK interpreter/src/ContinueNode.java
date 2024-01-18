
public class ContinueNode extends StatementNode{
	/**
	 * Empty constructor for the ContinueNode class
	 */
	public ContinueNode() {}
	/**
	 * Displays information for the given continue statement
	 * @return continue statement display
	 */
	public String toString() {
		return "continue;";
	}
	/**
	 * Does nothing 
	 */
	public void exceptionToThrow(String exception) {}
}
