
public class ForNode extends StatementNode{
	public AssignmentNode initializer;
	public OperationNode condition;
	public AssignmentNode increment;
	public BlockNode block;
	/**
	 * Empty constructor for the ForNode class
	 */
	public ForNode() {}
	/**
	 * Displays information for the given for loop
	 * @return for loop display
	 */
	public String toString() {
		String incrementInfo = increment.toString();
		String initializerInfo = initializer.toString();
		if(initializerInfo.contains(";")) initializerInfo = initializerInfo.substring(0, initializerInfo.length() - 1);
		if(incrementInfo.contains(";")) incrementInfo = incrementInfo.substring(0, incrementInfo.length() - 1); 
		return "for(" + initializerInfo + "; " + condition.toString() + "; " + incrementInfo + ") " + block.toString();
	}
	/**
	 * Throw the exception of the target of this for node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		initializer.exceptionToThrow(exception);
	}
}
