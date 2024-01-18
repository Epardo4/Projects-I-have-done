
public class TernaryOperator extends StatementNode{
	public Node condition;
	public Node isTrue;
	public Node isFalse;
	/**
	 * Constructor for the TernaryOperator class
	 * @param condition
	 */
	public TernaryOperator(Node condition) {
		this.condition = condition;
	}
	/**
	 * Creates a string to print the TernaryOperator
	 * @return what to print
	 */
	public String toString() {
		String toReturn = condition.toString() + " ? ";
		String trueStatement = isTrue.toString();
		String falseStatement = isFalse.toString();
		if(trueStatement.contains(";")) trueStatement = trueStatement.substring(0,trueStatement.length()-1);
		if(falseStatement.contains(";")) falseStatement = falseStatement.substring(0,falseStatement.length()-1);
		return condition.toString() + " ? " + trueStatement + " : " + falseStatement + ";";
	}
	/**
	 * Throw the exception of the target of this ternary node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		condition.exceptionToThrow(exception);
	}
}
