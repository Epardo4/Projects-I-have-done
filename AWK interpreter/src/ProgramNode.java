import java.util.LinkedList;
public class ProgramNode {
	// BEGIN BlockNodes, END BlockNodes, other BlockNodes and FunctionNodes
	public LinkedList<BlockNode> begins = new LinkedList<>();
	public LinkedList<BlockNode> ends = new LinkedList<>();
	public LinkedList<BlockNode> others  = new LinkedList<>();
	public LinkedList<FunctionNode> functions  = new LinkedList<>();
	/**
	 * Constructor for the ProgramNode class
	 */
	public ProgramNode() {
		//Empty constructor
	}
	/**
	 * presents the program in the order of beginning blocks, other blocks, functions, and ending blocks of code
	 * @return program display
	 */
	public String toString() {
		String info = "";
		for(BlockNode b: begins)
			info += "BEGIN\n" + b.toString() + "\n";
		for(Node o: others)
			info += o.toString() + "\n";
		for(FunctionNode f: functions)
			info += f.toString() + "\n";
		for(BlockNode e: ends)
			info += "END\n" + e.toString() + "\n";
		return info;
	}
}
