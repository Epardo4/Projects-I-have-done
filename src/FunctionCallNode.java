import java.util.LinkedList;
public class FunctionCallNode extends StatementNode{
	public Node functionName;
	public LinkedList<Node> parameters;
	/**
	 * Constructor for the FunctionCallNode class
	 * @param functionName
	 */
	public FunctionCallNode(Node functionName) {
		this.functionName = functionName;
		parameters = new LinkedList<>();
	}
	/**
	 * Displays information for the given function call
	 * @return function call display
	 */
	public String toString() {
		String toReturn =  functionName.toString() + "(";
		for(int i = 0; i < parameters.size() - 1; i++)
			toReturn += parameters.get(i).toString() + ", ";
		if(parameters.size() > 0) toReturn += parameters.get(parameters.size() - 1);
		return toReturn + ");";
	}
	/**
	 * Throw the exception of the target of this function call node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		functionName.exceptionToThrow(exception);
	}
}
