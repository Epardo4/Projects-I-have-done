import java.util.Optional;
public class IfNode extends StatementNode{
	public Optional<Node> condition;
	public IfNode next;
	public BlockNode block;
	/**
	 * Constructor for the IfNode class
	 */
	public IfNode() {
		condition = Optional.empty();
	}
	/**
	 * /**
	 * Displays information for the given if statement
	 * @return function if statement
	 */
	public String toString() {
		String toReturn = "";
		if(!condition.equals(Optional.empty())) toReturn += "if(" + condition.get().toString() + ") " + block.toString();
		else toReturn += block.toString();
		if(next != null) 
			toReturn += "else " + next.toString();
		return toReturn;
	}
	/**
	 * Throw the exception of the target of this if node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		block.exceptionToThrow(exception);
	}
}
