import java.util.HashMap;

public class AssignmentNode extends StatementNode{
	public Node target;
	public Node expression;
	/**
	 * Constructor for the AssignemntNode class
	 * @param target
	 */
	public AssignmentNode(Node target) {
		this.target = target;
	}
	/**
	 * A second constructor for the AssignmentNode class
	 * @param target
	 * @param expression
	 */
	public AssignmentNode(Node target, Node expression) {
		this.target = target;
		this.expression = expression;
	}
	/**
	 * Throw the exception of the target of this assignment node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		target.exceptionToThrow(exception);
	}
	/**
	 * Creates a string to print the assignmentNode
	 * @return what to print
	 */
	public String toString() {
		String toReturn = target.toString() + " ";
		//if it's a += kind of assignmentNode, then print it out as such
		if(expression instanceof OperationNode) {
			OperationNode exp = (OperationNode)expression;
			if(exp.operand.equals(OperationNode.Operation.PREINC) || exp.operand.equals(OperationNode.Operation.POSTINC) || 
					exp.operand.equals(OperationNode.Operation.PREDEC) || exp.operand.equals(OperationNode.Operation.POSTDEC))
				return exp.toString() + ";";
			if(!target.equals(exp.leftValue)) toReturn += "= " + expression;
			else {
			HashMap<OperationNode.Operation, String> operands = new HashMap<>();
			OperationNode.Operation[] operations = {OperationNode.Operation.EXPONENT, OperationNode.Operation.ADD, OperationNode.Operation.SUBTRACT, 
					OperationNode.Operation.MULTIPLY, OperationNode.Operation.DIVIDE, OperationNode.Operation.MODULO};
			String[] symbols = {"^", "+", "-", "*", "/", "%"};
			for(int i = 0; i < symbols.length; i++)
				operands.put(operations[i], symbols[i]);
			toReturn += operands.get(exp.operand) + "= " + exp.rightValue.get().toString();
			}
		}
		else toReturn += "= " + expression;
		return toReturn;
	}
}
