import java.util.Optional;
import java.util.HashMap;
public class OperationNode implements Node{
	public Node leftValue;
	public Optional<Node> rightValue;
	public Operation operand;
	public enum Operation {EQUALS, NOTEQUALS, LESSER, LESSEREQUALS, GREATER, GREATEREQUALS, AND, OR, NOT, MATCH, NOTMATCH, 
		DOLLAR, PREINC, POSTINC, PREDEC, POSTDEC, UNARYPOS, UNARYNEG, IN, EXPONENT, ADD, SUBTRACT,MULTIPLY, DIVIDE,MODULO, CONCATENATION};
	/**
	 * Constructor for OperationNode with all 3 parameters in it
	 * @param leftValue
	 * @param operand
	 * @param rightValue
	 */
	public OperationNode(Node leftValue, Operation operand, Optional<Node> rightValue) {
		this.leftValue = leftValue;
		this.operand = operand;
		this.rightValue = rightValue;
	}
	/**
	 * Throw the exception of the target of this operation node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		if(leftValue != null) leftValue.exceptionToThrow(exception);
		if(!(rightValue.equals(Optional.empty()))) rightValue.get().exceptionToThrow(exception);
	}
	/**
	 * leftValue OperationNode constructor
	 * @param value
	 * @param operand
	 */
	public OperationNode(Node value, Operation operand){
		leftValue = value;
		this.operand = operand;
	}
	/**
	 * rightValue OperationNode constructor
	 * @param operand
	 * @param value
	 */
	public OperationNode(Operation operand, Optional<Node> value) {
		rightValue = value;
		this.operand = operand;
	}
	/**
	 * the display of a given OperationNode
	 * @return toReturn
	 */
	public String toString() {
		HashMap<Operation, String> operands = new HashMap<>();
		Operation[] operations = {Operation.EQUALS, Operation.NOTEQUALS, Operation.LESSER, Operation.LESSEREQUALS, Operation.GREATER, 
				Operation.GREATEREQUALS, Operation.AND, Operation.OR, Operation.NOT, Operation.MATCH, Operation.NOTMATCH, Operation.DOLLAR, 
				Operation.PREINC, Operation.POSTINC, Operation.PREDEC, Operation.POSTDEC, Operation.UNARYPOS, Operation.UNARYNEG, 
				Operation.EXPONENT, Operation.ADD, Operation.SUBTRACT, Operation.MULTIPLY, Operation.DIVIDE, Operation.MODULO, 
				Operation.CONCATENATION};
		String[] symbols = {"==", "!=", "<", "<=", ">", ">=", "&&", "||", "!", "~", "!~", "$", "++", "++", "--", "--", "+", "-", "^", 
				"+", "-", "*", "/", "%", " "};
		for(int i = 0; i < symbols.length; i++)
			operands.put(operations[i], symbols[i]);
		String toReturn = "";
		if(leftValue != null && rightValue != null) {
			if(operand.equals(Operation.IN)) return leftValue + " in " + rightValue.get();
			toReturn += leftValue + operands.get(operand) + rightValue.get();
		}
		else if(leftValue != null) toReturn += leftValue + operands.get(operand);
		else toReturn += operands.get(operand) + rightValue.get();
		return toReturn;
	}
}
