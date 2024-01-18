import java.util.LinkedList;
import java.util.Optional;
public class BlockNode implements Node {
	public LinkedList<StatementNode> statements = new LinkedList<>();
	public Optional<Node> conditional;
	/**
	 * Constructor for the BlockNode class
	 * @param condition
	 */
	public BlockNode(Optional<Node> condition) {
		conditional = condition;
	}
	/**
	 * Does nothing
	 */
	public void exceptionToThrow(String exception) {}
	/**
	 * Displays the information for this given block of code
	 * @return info
	 */
	public String toString() {
		String info = "";
		if(!conditional.equals(Optional.empty()))
			info += "(" + conditional.get().toString() + ")\n";
		info += "{\n";
		for(StatementNode statement: statements)
			info += statement.toString() + "\n";
		return info + "}";
	}
}
