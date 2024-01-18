import java.util.Optional;
public class VariableReferenceNode implements Node {
	public Token tName;
	public Node name;
	public Optional<Node> index;
	/**
	 * Two parameter constructor for the VariableReferenceNdoe class
	 * @param name
	 * @param index
	 */
	public VariableReferenceNode(Node name) {
		this.name = name;
	}
	/**
	 * throws an exception at the line of this variable reference node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception) throws Exception{
		if(tName != null)throw new Exception(exception + " on line: " + tName.lineNumber);
		if(name != null) name.exceptionToThrow(exception);
	}
	/**
	 * Single parameter constructor for the VariableReferenceNode class
	 * @param name
	 */
	public VariableReferenceNode(Token tName) {
		this.tName = tName;
	}
	/**
	 * Gets the name of the variableReferenceNode
	 * @return name
	 */
	public String getName() {
		if(tName == null) return name.toString();
		if(tName.value == null)return (""+tName).toLowerCase();
		return tName.value;
	}
	/**
	 * The display for any given VariableReferenceNode
	 * @returns display of this object
	 */
	public String toString() {
		String toReturn = "";
		if(tName == null) toReturn += name.toString();
		else if(tName.value == null) {
			String toLower = "" + tName;
			toLower = toLower.toLowerCase();
			toReturn += toLower;
		}
		else toReturn += tName.value;
		if(index != null) toReturn += "[" + index.get() + "]";
		return toReturn;
	}
}
