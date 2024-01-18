import java.util.LinkedList;
import java.util.Optional;
public class FunctionNode implements Node {
	public Optional<Token> name;
	public BlockNode block;
	public LinkedList<Optional<Token>> parameterList = new LinkedList<>();
	/**
	 * Constructor for the functionNdode class
	 * @param name
	 */
	public FunctionNode(Optional<Token> name) {
		this.name = name;
	}
	/**
	 * Empty constructor for the functionNdode class
	 */
	public FunctionNode() {}
	/**
	 * Displays information for the given function
	 * @return function display
	 */
	public String toString() {
		String info = "function ";
		info += name.toString().substring(14, name.toString().length()-2) + " (";
		for(Optional<Token> t: parameterList)
			info += t.toString().substring(14,t.toString().length()-2) + ", ";
		if(parameterList.size() != 0)
			info = info.substring(0, info.length() - 2);
		info += ") " + block.toString();
		return info;
	}
	/**
	 * Throw the exception of the target of this function node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		Token value = name.get();
		throw new Exception(exception + " on line: " + value.lineNumber);
	}
}
