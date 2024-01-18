import java.util.HashMap;
import java.util.regex.*;
import java.util.Optional;
import java.util.Set;
import java.nio.file.*;
import java.util.function.Function;
import java.util.ArrayList;
public class Interpreter {
	public class LineManager{
		private ArrayList<String> document;
		/**
		 * Constructor for the LineManager class
		 * @param document
		 */
		public LineManager(ArrayList<String> document) {
			this.document = document;
		}
		/**
		 * splits the next line of the document by the field separator
		 * @return if there was something to split
		 */
		public boolean splitAndAssign() {
			int NR = (int)(float)tryFloat(variables.get("NR").toString());
			int FNR = (int)(float)tryFloat(variables.get("FNR").toString());
			int NF = (int)(float)tryFloat(variables.get("NF").toString());
			if(NR >= document.size()) return false;
			if(document.get(NR).equals("")) {
				NR++; 
				variables.put("NR", new InterpreterDataType(""+NR));
				FNR++;
				variables.put("FNR", new InterpreterDataType(""+FNR));
				return false;
			}
			//clear the global variable hashmap of all field references
			for(int i = 0; i <= NF; i++) {
				String toRemove = "$" + i;
				variables.remove(toRemove);
			}
			//set new field references
			variables.put("$0", new InterpreterDataType(document.get(NR).toString()));
			String[] toSplit = document.get(NR).split(variables.get("FS").value);
			NF = toSplit.length;
			variables.put("NF", new InterpreterDataType(""+NF));
			for(int i = 1; i <= NF; i++) {
				String name = "$" + i;
				InterpreterDataType variable = new InterpreterDataType(toSplit[i-1]);
				variables.put(name, variable);
			}
			NR++;
			variables.put("NR", new InterpreterDataType(""+NR));
			FNR++;
			variables.put("FNR", new InterpreterDataType(""+FNR));
			return true;
		}
	}
	public HashMap<String, InterpreterDataType> variables = new HashMap<>();
	public HashMap<String, FunctionNode> functions = new HashMap<>();
	public LineManager lines;
	public ProgramNode program;
	/**
	 * Constructor for the Interpreter Class
	 * @param program
	 * @param myPath
	 * @throws Exception
	 */
	public Interpreter(ProgramNode program, Path myPath) throws Exception {
		//enter all of the global variables needed into the hashmap and declare lines
		variables.put("FILENAME", new InterpreterDataType(myPath.getFileName().toString()));
		variables.put("FS", new InterpreterDataType(" "));
		variables.put("OFMT", new InterpreterDataType("%.6g"));
		variables.put("OFS", new InterpreterDataType(" "));
		variables.put("ORS", new InterpreterDataType("\n"));
		variables.put("NR", new InterpreterDataType("0"));
		variables.put("FNR", new InterpreterDataType("0"));
		variables.put("NF", new InterpreterDataType("0"));
		this.program = program;
		if(myPath != null) 
			lines = new LineManager((ArrayList)Files.readAllLines(myPath));
		else lines = new LineManager(new ArrayList<String>());
		//add the program's fucntions to the interpreter
		for(FunctionNode function : program.functions)
			functions.put(function.name.get().value, function);
		//add all built in functions
		functions.put("length", new BuiltInFunctionNode(makeBuiltIns("length"), false));
		functions.put("getline", new BuiltInFunctionNode(makeBuiltIns("getline"), false));
		functions.put("next", new BuiltInFunctionNode(makeBuiltIns("next"), false));
		functions.put("tolower", new BuiltInFunctionNode(makeBuiltIns("tolower"), false));
		functions.put("toupper", new BuiltInFunctionNode(makeBuiltIns("toupper"), false));
		functions.put("substr", new BuiltInFunctionNode(makeBuiltIns("substr"), false));
		functions.put("sub", new BuiltInFunctionNode(makeBuiltIns("sub"), false));
		functions.put("sprintf", new BuiltInFunctionNode(makeBuiltIns("sprintf"), true));
		functions.put("split", new BuiltInFunctionNode(makeBuiltIns("split"), false));
		functions.put("match", new BuiltInFunctionNode(makeBuiltIns("match"), false));
		functions.put("index", new BuiltInFunctionNode(makeBuiltIns("index"), false));
		functions.put("gsub", new BuiltInFunctionNode(makeBuiltIns("gsub"), false));
		functions.put("printf", new BuiltInFunctionNode(makeBuiltIns("printf"), true));
		functions.put("print", new BuiltInFunctionNode(makeBuiltIns("print"), true));
		interpretProgram();
	}
	/**
	 * interprets everything in the program starting with begins, then runs every other block for every line of user input, then runs ends
	 * @throws Exception
	 */
	public void interpretProgram() throws Exception {
		for(BlockNode block : program.begins)
			interpretBlocks(block, variables);
		while(lines.splitAndAssign()) {
			for(BlockNode block: program.others) {
				interpretBlocks(block, new HashMap<String, InterpreterDataType>());
			}
		}
		for(BlockNode block : program.ends)
			interpretBlocks(block, variables);
	}
	/**
	 * evaluates the StatementNodes of the program and creates an InterpreterDataType for their values
	 * @param evaluate
	 * @param localVars
	 * @return an InterpreterDataType
	 * @throws Exception
	 */
	public InterpreterDataType getIDT(Node evaluate, HashMap<String, InterpreterDataType> localVars) throws Exception{
		//For assignment node, check to see if it's a field reference, if it is set that in global variables, otherwise set in local variables
		if(evaluate instanceof AssignmentNode) {
			AssignmentNode toAssign = (AssignmentNode)evaluate;
			//if target isn't a variable reference or field reference, throw exception
			if(!(toAssign.target instanceof VariableReferenceNode) && !(toAssign.target instanceof OperationNode && 
					((OperationNode)toAssign.target).operand.equals(OperationNode.Operation.DOLLAR)))
				exceptionToThrow("The target of an assignment is node a field reference or variable", toAssign);
			InterpreterDataType idt = getIDT(toAssign.expression, localVars);
			if(toAssign.target instanceof VariableReferenceNode) {
				if(variables.containsKey(toAssign.target.toString())) variables.put(toAssign.target.toString(), idt);
				else {
					VariableReferenceNode target = (VariableReferenceNode)toAssign.target;
					if(target.index == null) localVars.put(toAssign.target.toString(), idt);
					else if(localVars.containsKey(target.getName())) {
						InterpreterArrayDataType array = (InterpreterArrayDataType)localVars.get(target.getName());
						array.value.put(getIDT(target.index.get(), localVars).toString(), getIDT(toAssign.expression, localVars));
					}
					else {
						HashMap<String, InterpreterDataType> entries = new HashMap<>();
						entries.put(getIDT(target.index.get(), localVars).toString(), getIDT(toAssign.expression, localVars));
						InterpreterArrayDataType array = new InterpreterArrayDataType(entries);
						localVars.put(target.getName(), array);
					}
				}
			}
			if(toAssign.target instanceof OperationNode) {
				OperationNode operation = (OperationNode)toAssign.target;
				int dollar = (int)(float)tryFloat(getIDT(operation.rightValue.get(),localVars).toString());
				variables.put(("$" + dollar), idt);
			}
			return idt;
		}
		//just evaluate the input of the node for these three
		if(evaluate instanceof ConstantNode)
			return new InterpreterDataType(evaluate.toString());
		if(evaluate instanceof FunctionCallNode) {
			String run = runFunctionCall(((FunctionCallNode)evaluate), localVars);
			return new InterpreterDataType(run);
		}
		if(evaluate instanceof PatternNode)
			exceptionToThrow("A regular expression cannot be a statement", evaluate);
		//if true, call getIDT on true, otherwise call getIDT on false
		if(evaluate instanceof TernaryOperator) {
			TernaryOperator ternary = (TernaryOperator)evaluate;
			InterpreterDataType value = getIDT(ternary.condition, localVars);
			if(tryFloat(value.toString()) != null && tryFloat(value.toString()) > 0) return getIDT(ternary.isTrue, localVars);
			else return getIDT(ternary.isFalse, localVars);
		}
		//get the value of the variable reference node by getting the variable and it's value or array value
		if(evaluate instanceof VariableReferenceNode) {
			VariableReferenceNode variable = (VariableReferenceNode)evaluate;
			if(variable.index == null) {
				if(variables.get(variable.toString()) != null) return variables.get(variable.toString());
				if(localVars.get(variable.toString()) != null) return localVars.get(variable.toString());
				return new InterpreterDataType("");
			}
			if(variables.get(variable.getName()) == null && localVars.get(variable.getName()) == null) return new InterpreterDataType("");
			InterpreterArrayDataType array;
			if(variables.get(variable.getName()) == null) array = (InterpreterArrayDataType)localVars.get(variable.getName());
			else array = (InterpreterArrayDataType)variables.get(variable.getName());
			InterpreterDataType index = getIDT(variable.index.get(), localVars);
			return array.value.get(index.toString());
		}
		if(evaluate instanceof OperationNode) {
			OperationNode operation = (OperationNode) evaluate;
			//convert to floats here and perform the correct operations on those floats
			if(operation.operand.equals(OperationNode.Operation.ADD)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				return new InterpreterDataType((tryFloat(left) + tryFloat(right)) + "");
			}
			if(operation.operand.equals(OperationNode.Operation.SUBTRACT)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				return new InterpreterDataType((tryFloat(left) - tryFloat(right)) + "");
			}
			if(operation.operand.equals(OperationNode.Operation.MULTIPLY)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				return new InterpreterDataType((tryFloat(left) * tryFloat(right)) + "");
			}
			if(operation.operand.equals(OperationNode.Operation.DIVIDE)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				return new InterpreterDataType((tryFloat(left) / tryFloat(right)) + "");
			}
			if(operation.operand.equals(OperationNode.Operation.MODULO)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				return new InterpreterDataType((tryFloat(left) % tryFloat(right)) + "");
			}
			if(operation.operand.equals(OperationNode.Operation.POSTINC)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				return new InterpreterDataType((tryFloat(left) + 1) + "");
			}
			if(operation.operand.equals(OperationNode.Operation.PREINC)) {
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				return new InterpreterDataType((tryFloat(right) + 1) + "");
			}
			if(operation.operand.equals(OperationNode.Operation.POSTDEC)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				return new InterpreterDataType((tryFloat(left) - 1) + "");
			}
			if(operation.operand.equals(OperationNode.Operation.PREDEC)) {
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				return new InterpreterDataType((tryFloat(right) - 1) + "");
			}
			if(operation.operand.equals(OperationNode.Operation.EXPONENT)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				return new InterpreterDataType(Math.pow(tryFloat(left), tryFloat(right)) + "");
			}
			if(operation.operand.equals(OperationNode.Operation.UNARYPOS)) {
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				return new InterpreterDataType(Math.abs(tryFloat(right)) + "");
			}
			if(operation.operand.equals(OperationNode.Operation.UNARYNEG)) {
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				return new InterpreterDataType((tryFloat(right) * -1) + "");
			}
			//stop converting to float
			if(operation.operand.equals(OperationNode.Operation.CONCATENATION)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				return new InterpreterDataType(left + right);
			}
			//get or add a field reference by converting to float and then integer on the right side of the dollar
			if(operation.operand.equals(OperationNode.Operation.DOLLAR)) {
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				int field = (int)(float)tryFloat(right);
				if(variables.get("$" + field) != null) return variables.get("$"+field);
				return new InterpreterDataType("");
			}
			//evaluate both sides and then check if they are both or either true
			if(operation.operand.equals(OperationNode.Operation.AND)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				if(tryFloat(left) != 0 && tryFloat(right) != 0) return new InterpreterDataType("1");
				return new InterpreterDataType("0");
			}
			if(operation.operand.equals(OperationNode.Operation.OR)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				if(tryFloat(left) != 0 || tryFloat(right) != 0) return new InterpreterDataType("1");
				return new InterpreterDataType("0");
			}
			//use Regular Expressions for match and not match
			if(operation.operand.equals(OperationNode.Operation.MATCH)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				if(!(operation.rightValue.get() instanceof PatternNode)) 
					exceptionToThrow("right value of ~ must be a regular expression",operation);
				String right = operation.rightValue.get().toString();
				Pattern pattern = Pattern.compile(right, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(left);
				boolean matchFound = matcher.find();
			    if(matchFound) return new InterpreterDataType("1");
				return new InterpreterDataType("0");
			}
			if(operation.operand.equals(OperationNode.Operation.NOTMATCH)) {
				String left = getIDT(operation.leftValue, localVars).toString();
				if(!(operation.rightValue.get() instanceof PatternNode)) 
					exceptionToThrow("right value of !~ must be a regular expression", operation);
				String right = operation.rightValue.get().toString();
				Pattern pattern = Pattern.compile(right, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(left);
				boolean matchFound = matcher.find();
			    if(matchFound) return new InterpreterDataType("0");
				return new InterpreterDataType("1");
			}
			//convert to float return the opposite
			if(operation.operand.equals(OperationNode.Operation.NOT)) {
				String right = getIDT(operation.rightValue.get(), localVars).toString();
				if(tryFloat(right) != 0) return new InterpreterDataType("0");
				return new InterpreterDataType("1");
			}
			//check if the left is contained in the hasmap of the right (right must be InterpreterArrayDataType)
			if(operation.operand.equals(OperationNode.Operation.IN)) {
				if(operation.rightValue == null ||
 						!(operation.rightValue.get() instanceof VariableReferenceNode)) 
					exceptionToThrow("Not array on right of in operator", operation);
				String left = operation.leftValue.toString();
				VariableReferenceNode right = (VariableReferenceNode)operation.rightValue.get();
				//if(right.index == null) throw new Exception("Not an array on right of in operator");
				if(variables.get(right.getName()) == null && localVars.get(right.getName()) == null) return new InterpreterDataType("0");
				InterpreterArrayDataType array = null;
				if(variables.get(right.getName()) == null && localVars.get(right.getName()) instanceof InterpreterArrayDataType) 
					array = (InterpreterArrayDataType)localVars.get(right.getName());
				else if(variables.get(right.getName()) instanceof InterpreterArrayDataType) 
					array = (InterpreterArrayDataType)variables.get(right.getName());
				else exceptionToThrow("right side of an in operation is not an array", operation);
				if(array.value.containsKey(left)) return new InterpreterDataType("1");
				return new InterpreterDataType("0");
			}
			String left = getIDT(operation.leftValue, localVars).toString();
			String right = getIDT(operation.rightValue.get(), localVars).toString();
			Float leftF = null;
			Float rightF = null;
			try {
				leftF = Float.parseFloat(left);
			} catch(Exception e) {leftF = null;}
			try {
				rightF = Float.parseFloat(right);
			}catch(Exception e) {rightF = null;}
			//try to compare with floats and if they are not both floats, then only compare with strings
			if(operation.operand.equals(OperationNode.Operation.EQUALS)) {
				if(leftF != null && rightF != null)
					if(leftF.equals(rightF)) return new InterpreterDataType("1");
					else return new InterpreterDataType("0");
				if(left.equals(right)) return new InterpreterDataType("1");
				return new InterpreterDataType("0");
			}
			if(operation.operand.equals(OperationNode.Operation.NOTEQUALS)) {
				if(leftF != null && rightF != null)
					if(!leftF.equals(rightF)) return new InterpreterDataType("1");
					else return new InterpreterDataType("0");
				if(left.equals(right)) return new InterpreterDataType("0");
				return new InterpreterDataType("1");
			}
			if(operation.operand.equals(OperationNode.Operation.LESSER)) {
				if(leftF != null && rightF != null)
					if(leftF.compareTo(rightF) < 0) return new InterpreterDataType("1");
					else return new InterpreterDataType("0");
				if(left.compareTo(right) < 0) return new InterpreterDataType("1");
				return new InterpreterDataType("0");
			}
			if(operation.operand.equals(OperationNode.Operation.LESSEREQUALS)) {
				if(leftF != null && rightF != null)
					if(leftF.compareTo(rightF) <= 0) return new InterpreterDataType("1");
					else return new InterpreterDataType("0");
				if(left.compareTo(right) <= 0) return new InterpreterDataType("1");
				return new InterpreterDataType("0");
			}
			if(operation.operand.equals(OperationNode.Operation.GREATER)) {
				if(leftF != null && rightF != null)
					if(leftF.compareTo(rightF) > 0) return new InterpreterDataType("1");
					else return new InterpreterDataType("0");
				if(left.compareTo(right) > 0) return new InterpreterDataType("1");
				return new InterpreterDataType("0");
			}
			if(operation.operand.equals(OperationNode.Operation.GREATEREQUALS)) {
				if(leftF != null && rightF != null)
					if(leftF.compareTo(rightF) >= 0) return new InterpreterDataType("1");
					else return new InterpreterDataType("0");
				if(left.compareTo(right) >= 0) return new InterpreterDataType("1");
				return new InterpreterDataType("0");
			}
		}
		return null;
	}
	/**
	 * process every possible statement in the program
	 * @param localVars
	 * @param statement
	 * @return ReturnType
	 * @throws Exception
	 */
	public ReturnType processStatement(HashMap<String, InterpreterDataType> localVars, Node statement) throws Exception{
		//if a break or a continue, then return that type
		if(statement instanceof BreakNode) return new ReturnType(ReturnType.Type.BREAK);
		if(statement instanceof ContinueNode) return new ReturnType(ReturnType.Type.CONTINUE);
		if(statement instanceof DeleteNode) {
			DeleteNode delete = (DeleteNode)statement;
			String toDelete = delete.toDelete.getName().toString();
			//does this array exist, if not do nothing
			if(localVars.get(toDelete) == null && variables.get(toDelete) == null)
				return new ReturnType(ReturnType.Type.NORMAL);
			//if it's a local variable check if it's an array, if not, throw and exception
			InterpreterArrayDataType array = null;
			if(localVars.get(toDelete) != null) {
				if(!(localVars.get(toDelete) instanceof InterpreterArrayDataType))
					exceptionToThrow("cannot invoke delete on something that is not an array", delete);
				//if there is an index, delete that index, otherwise delete the whole array
				array = (InterpreterArrayDataType)localVars.get(toDelete);
			}
			//it is definitely a global variable now, so do the same process for the local variables, just for the global variables
			else {
				if(!(variables.get(toDelete) instanceof InterpreterArrayDataType))
					exceptionToThrow("cannot invoke delete on something that is not an array",delete);
				array = (InterpreterArrayDataType)variables.get(toDelete);
			}
			if(delete.toDelete.toString().contains("[") && delete.toDelete.toString().contains("]")) {
				String index = getIDT(delete.toDelete.index.get(), localVars).toString();
				array.value.remove(index);
			}
			else localVars.remove(toDelete);
			return new ReturnType(ReturnType.Type.NORMAL);
		}
		if(statement instanceof DoWhileNode) {
			DoWhileNode dowhile = (DoWhileNode)statement;
			//run the block
			ReturnType value = interpretBlocks(dowhile.block, localVars);
			if(value.type.equals(ReturnType.Type.BREAK)) return value;
			if(value.value != null) return value;
			//check the condition and run the block again
			while(tryFloat(getIDT(dowhile.condition, localVars).toString()) != 0) {
				value = interpretBlocks(dowhile.block, localVars);
				if(value.type.equals(ReturnType.Type.BREAK)) break;
				if(value.value != null) return value;
			}
			return new ReturnType(ReturnType.Type.NORMAL);
		}
		if(statement instanceof ForNode) {
			ForNode fornode = (ForNode)statement;
			//initialize the for loop
			String index = fornode.initializer.target.toString();
			getIDT(fornode.initializer, localVars);
			//check the condition and run the block
			while(tryFloat(getIDT(fornode.condition, localVars).toString()) != 0) {
				ReturnType value = interpretBlocks(fornode.block, localVars);
				if(value.type.equals(ReturnType.Type.BREAK)) break;
				if(value.value != null) return value;
				//increment
				getIDT(fornode.increment, localVars);
			}
			//remove the index from the local variables
			localVars.remove(index);
			return new ReturnType(ReturnType.Type.NORMAL);
		}
		if(statement instanceof ForEachNode) {
			ForEachNode foreach = (ForEachNode)statement;
			//check for the array in globals and locals, return nothing if it isn't found
			if(variables.get(foreach.operation.rightValue.get().toString()) == null && 
					localVars.get(foreach.operation.rightValue.get().toString()) == null)
				return new ReturnType(ReturnType.Type.NORMAL);
			InterpreterArrayDataType array;
			//if the right is in the local variables set the array variable to it's toString()
			if(localVars.get(foreach.operation.rightValue.get().toString()) != null) {
				//if the right side is not an array, throw an exception
				if(!(localVars.get(foreach.operation.rightValue.get().toString()) instanceof InterpreterArrayDataType))
					exceptionToThrow("not an array on the right side of a foreach loop",foreach);
				array = (InterpreterArrayDataType)localVars.get(foreach.operation.rightValue.get().toString());
			}
			//if the right is in the global variables set the array variable to it's toString()
			else {
				//if the right side is not an array, throw an exception
				if(!(variables.get(foreach.operation.rightValue.get().toString()) instanceof InterpreterArrayDataType))
					exceptionToThrow("not an array on the right side of a foreach loop",foreach);
				array = (InterpreterArrayDataType)variables.get(foreach.operation.rightValue.get().toString());
			}
			//run the block over every key in array
			Object[] keys = array.value.keySet().toArray();
			for(Object key : keys) {
				localVars.put(foreach.operation.leftValue.toString(), new InterpreterDataType(key.toString()));
				ReturnType value = interpretBlocks(foreach.block, localVars);
				if(value.type.equals(ReturnType.Type.BREAK)) break;
				if(value.value != null) return value;
			}
			localVars.remove(foreach.operation.leftValue.toString());
			return new ReturnType(ReturnType.Type.NORMAL);
		}
		if(statement instanceof IfNode) {
			IfNode ifStatement = (IfNode)statement;
			//for all if statements in this list of statements
			while(ifStatement != null) {
				//evaluate the condition and run it if either the condition does not exist or is true and then stop
				if(ifStatement.condition.equals(Optional.empty()) || tryFloat(getIDT(ifStatement.condition.get(),localVars).toString()) != 0) {
					ReturnType value = interpretBlocks(ifStatement.block, localVars);
					if(!(value.type.equals(ReturnType.Type.NORMAL))) return value;
					if(value.value != null) return value;
					return new ReturnType(ReturnType.Type.NORMAL);
				}
				//if the condition existed and was not true, get the next if statement
				ifStatement = ifStatement.next;
			}
			return new ReturnType(ReturnType.Type.NORMAL);
		}
		//return the value of the return node
		if(statement instanceof ReturnNode) {
			ReturnNode toReturn = (ReturnNode)statement;
			return new ReturnType(ReturnType.Type.RETURN, getIDT(toReturn.toReturn, localVars).toString());
		}
		if(statement instanceof WhileNode) {
			WhileNode whileNode = (WhileNode)statement;
			//run the block until it either stops or the condition is no longer true
			while(tryFloat(getIDT(whileNode.condition, localVars).toString()) != 0) {
				ReturnType value = interpretBlocks(whileNode.block, localVars);
				if(value.type.equals(ReturnType.Type.BREAK)) break;
				if(value.value != null) return value;
			}
			return new ReturnType(ReturnType.Type.NORMAL);
		}
		//for anything else, try calling getIDT
		if(getIDT(statement, localVars).toString() != null)
			return new ReturnType(ReturnType.Type.NORMAL);
		//the statement variable can no longer be a valid statement, throw an exception in this case
		exceptionToThrow("Not a valid statement",statement);
		return null;
	}
	/**
	 * throws the exception of this node
	 * @throws Exception
	 */
	public void exceptionToThrow(String exception, Node node) throws Exception{
		node.exceptionToThrow(exception);
	}
	/**
	 * interprets each statement of the given block node if the conditon is true or doesn't exist
	 * @param block
	 * @param localVars
	 * @throws Exception
	 * @return ReturnType
	 */
	public ReturnType interpretBlocks(BlockNode block, HashMap<String, InterpreterDataType> localVars) throws Exception{
		//evaluate condition if it isn't empty
		if(!(block.conditional.equals(Optional.empty())) && tryFloat(getIDT(block.conditional.get(), localVars).toString()) == 0)
			return new ReturnType(ReturnType.Type.NORMAL);
		//run each statement of the block until it ends, or reaches a statement that disrupts the block
		for(int i = 0; i < block.statements.size(); i++) {
			ReturnType check = processStatement(localVars, block.statements.get(i));
			if(!(check.type.equals(ReturnType.Type.NORMAL))) return check;
			if(check.value != null) return check;
		}
		return new ReturnType(ReturnType.Type.NORMAL);
	}
	/**
	 * try to convert the given string to a float
	 * @param check
	 * @return float value on success, 0 on failure
	 */
	public Float tryFloat(String check) {
		Float toReturn = 0f;
		try {
			toReturn = Float.parseFloat(check);
		}catch (Exception e) {
			return 0f;
		}
		return toReturn;
	}
	/**
	 * processes the given function call node
	 * @param function
	 * @param localVars
	 * @throws Exception
	 * @return String
	 */
	public String runFunctionCall(FunctionCallNode function, HashMap<String, InterpreterDataType> localVars) throws Exception {
		//if the function does not exist, throw an exception
		if(!functions.containsKey(function.functionName.toString())) 
			exceptionToThrow("The function entered is not defined", function);
		FunctionNode func = functions.get(function.functionName.toString());
		//if it's a built in function, set the hashmap according to the amount of things in the function and call execute
		if(func instanceof BuiltInFunctionNode) {
			BuiltInFunctionNode builtIn = (BuiltInFunctionNode)func;
			HashMap<String, Object> parameters = new HashMap<>();
			for(int i = 0; i < function.parameters.size(); i++) {
				parameters.put("" + i, getIDT(function.parameters.get(i), localVars));
			}
			String check = builtIn.execute(parameters);
			//if execute did not return a string, something went wrong, likely the number of parameters
			if(check == null) exceptionToThrow("invalid built in function call", function);
			return check;
		}
		//we know that it's not built in, so check to make sure that the call is valid, then call it
		else {
			//the call does not work if the amount of parameters for the function def and the function call are different
			if(function.parameters.size() != func.parameterList.size()) 
				exceptionToThrow("Not the correct number of parameters for a function call", function);
			//set the value of params in the call to the corresponding name in the def within a new hashmap
			HashMap<String, InterpreterDataType> parameters = new HashMap<>();
			for(int i = 0; i < func.parameterList.size(); i++) {
				parameters.put(func.parameterList.get(i).get().value, getIDT(function.parameters.get(i), localVars));
			}
			//interpret the fucntion def with the new hashmap
			String value = interpretBlocks(func.block, parameters).value;
			if(value == null) return "";
			return value;
		}
	}
	/**
	 * Creates the Function for each built in function
	 * @param name
	 * @return the Function made
	 */
	public Function<HashMap<String, Object>, String> makeBuiltIns(String name) throws Exception{
		switch(name) {
		case "length" :
			Function<HashMap<String, Object>, String> length = x -> 
			{if(x.size() != 1) return null; for(String s : x.keySet()) return "" + isVariable(x.get(s).toString()).length(); return null;};
			return length;
		case "getline" :
			Function<HashMap<String, Object>, String> getline = x -> {if(x.size() != 0) return null; if(lines.splitAndAssign()) return "1"; return "0";};
			return getline;
		case "next" :
			Function<HashMap<String, Object>, String> next = x -> {if(x.size() != 0) return null; if(lines.splitAndAssign()) return "1"; return "0";};
			return next;
		case "tolower" : 
			Function<HashMap<String, Object>, String> tolower = x -> 
			{if(x.size() != 1) return null; for(String s : x.keySet()) return isVariable(x.get(s).toString()).toLowerCase(); return null;};
			return tolower;
		case "toupper" : 
			Function<HashMap<String, Object>, String> toupper = x -> 
			{if(x.size() != 1) return null; for(String s : x.keySet()) return isVariable(x.get(s).toString()).toUpperCase(); return null;};
			return toupper;
		case "substr" : 
			Function<HashMap<String, Object>,String> substr = x ->
			{	Object[] keys = x.keySet().toArray();
				if(keys.length == 2)
					return isVariable(x.get(keys[0]).toString()).substring((int)(float)(tryFloat(x.get(keys[1]).toString())) - 1);
				if(keys.length == 3) 
					return isVariable(x.get(keys[0]).toString()).substring((int)(float)(tryFloat(x.get(keys[1]).toString())) - 1,
							(int)(float)(tryFloat(x.get(keys[1]).toString())) - 1 + (int)(float)(tryFloat(x.get(keys[2]).toString())));
				return null;
			};
			return substr;
		case "sub" : 
			Function<HashMap<String, Object>, String> sub = x ->
			{	Object[] keys = x.keySet().toArray();
				if(keys.length != 3) return null;
				return isVariable(x.get(keys[2]).toString()).replaceFirst(isVariable(x.get(keys[0]).toString()), isVariable(x.get(keys[1]).toString()));
			};
			return sub;
		case "sprintf" : 
			Function<HashMap<String, Object>, String> sprintf = x ->
			{	Object[] keys = x.keySet().toArray();
				String printable = x.get(keys[0]).toString();
				if(variables.get(x.get(keys[1].toString())) != null && variables.get(x.get(keys[1].toString())) instanceof InterpreterArrayDataType) {
					if(keys.length != 2) return null;
					return String.format(printable, ((InterpreterArrayDataType) variables.get(x.get(keys[1]))).toArray());
				}
				String[] keystrings = new String[keys.length-1];
				for(int i = 1; i < keys.length; i++)
					keystrings[i-1] = isVariable(x.get(keys[i]).toString());
				return String.format(printable, keystrings);
			};
			return sprintf;
		case "split" :
			Function<HashMap<String, Object>, String> split = x ->
			{	Object[] keys = x.keySet().toArray();
				if(keys.length != 3) return null;
				String[] splited = isVariable(x.get(keys[0]).toString()).split(isVariable(x.get(keys[2]).toString()));
				HashMap<String, InterpreterDataType> map = new HashMap<>();
				for(int i = 0; i < splited.length; i++) map.put("" + i, new InterpreterDataType(splited[i]));
				variables.put(x.get(keys[1]).toString(), new InterpreterArrayDataType(map));
				return splited.length + "";
			};
			return split;
		case "match" : 
			Function<HashMap<String, Object>, String> match = x ->
			{	Object[] keys = x.keySet().toArray();
				if(keys.length == 2) {
				int toReturn = 1 + isVariable(x.get(keys[0]).toString()).indexOf(isVariable(x.get(keys[1]).toString()));
				return toReturn + "";
				}
				if(keys.length == 3) {
					int toReturn = 1 + isVariable(x.get(keys[0]).toString()).indexOf(isVariable(x.get(keys[1]).toString()));
					HashMap<String, InterpreterDataType> map = new HashMap<>();
					map.put(isVariable(x.get(keys[1]).toString()), new InterpreterDataType(isVariable(x.get(keys[1]).toString())));
					variables.put(x.get(keys[1]).toString(), new InterpreterArrayDataType(map));
					return toReturn + "";
				}
				return null;
			};
			return match;
		case "index" : 
			Function<HashMap<String, Object>, String> index = x ->
			{	Object[] keys = x.keySet().toArray();
				if(keys.length != 2) return null;
				int toReturn = 1 + isVariable(x.get(keys[0]).toString()).indexOf(isVariable(x.get(keys[1]).toString()));
				return toReturn + "";
			};
			return index;
		case "gsub" : 
			Function<HashMap<String, Object>, String> gsub = x ->
			{	Object[] keys = x.keySet().toArray();
				if(keys.length != 3) return null;
				return isVariable(x.get(keys[2]).toString()).replaceAll(isVariable(x.get(keys[0]).toString()), isVariable(x.get(keys[1]).toString()));
			};
			return gsub;
		case "printf" : 
			Function<HashMap<String, Object>, String> printf = x ->
			{	Object[] keys = x.keySet().toArray();
				String printable = x.get(keys[0]).toString();
				if(keys.length == 2 && variables.get(x.get(keys[1].toString())) != null && 
						variables.get(x.get(keys[1].toString())) instanceof InterpreterArrayDataType) {
					System.out.printf(printable, ((InterpreterArrayDataType) variables.get(x.get(keys[1]))).toArray());
					return "";
				}
				String[] keystrings = new String[keys.length-1];
				for(int i = 1; i < keys.length; i++) {
					keystrings[i-1] = isVariable(x.get(keys[i]).toString());
				}
				System.out.printf(printable, keystrings);
				return "";
			};
			return printf;
		case "print" : 
			Function<HashMap<String, Object>, String> print = x ->
			{	Object[] keys = x.keySet().toArray();
				String printable = "";
				for(Object key : keys) printable += isVariable(x.get(key).toString()) + " ";
				System.out.println(printable.substring(0, printable.length()-1));
				return "";
			};
			return print;
		default : return null;
		}
	}
	/**
	 * checks if the given variable name is one of the global variables
	 * @param check
	 * @return a variable
	 */
	public String isVariable(String check) {
		if(variables.get(check) == null) return check;
		return variables.get(check).toString();
	}
}