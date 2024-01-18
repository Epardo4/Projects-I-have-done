import java.util.LinkedList;
import java.util.Optional;

public class Parser {
	private TokenHandler feeder;
	/**
	 * Constructor for the Parser class
	 * @param tokens
	 */
	public Parser(LinkedList<Token> tokens) {
		feeder = new TokenHandler(tokens);	
	}
	/**
	 * Deletes all separators in a row from the beginning of feeder until it ends or meets something that is not a separator
	 * @return of any separators were found and removed from the list
	 */
	public boolean acceptSeparators() {
		boolean isTrue = false;
		Optional<Token> separator;
		do {
			separator = feeder.matchAndRemove(Token.TokenType.SEPARATOR);
			if(!separator.equals(Optional.empty())) isTrue = true;
		}while(feeder.moreTokens() && !separator.equals(Optional.empty()));
		return isTrue;
	}
	/**
	 * Parses through the tokens and decides what each token represents in the linked list
	 * @return a new ProgramNode
	 * @throws Exception
	 */
	public ProgramNode parse() throws Exception{
		ProgramNode program = new ProgramNode();
		boolean check = false;
		//continues to loop through the TokenHandler to see what the next token represents
		while(feeder.moreTokens()) {
			acceptSeparators();
			if(!feeder.matchAndRemove(Token.TokenType.FUNCTION).equals(Optional.empty()))
				check = parseFunction(program);
			else
				check = parseAction(program);
			if(check == false) throw new Exception("This program has an ucompleted function or action");
		}
		return program;
	}
	/**
	 * Returns a block Node containing everything between the two curly braces - {} with the given BlockNode
	 * @return BlockNode
	 * @throws Exception
	 */
	public BlockNode parseBlock(BlockNode block) throws Exception{
		acceptSeparators();
		if(feeder.matchAndRemove(Token.TokenType.STARTBRACKET).equals(Optional.empty())) exceptionToThrow("a block of code does not begin with a '{'");
			StatementNode statement = parseStatement();
			while(statement != null) {
				block.statements.add(statement);
				statement = parseStatement();
			}
		if(feeder.matchAndRemove(Token.TokenType.ENDBRACKET).equals(Optional.empty())) exceptionToThrow("a block of code does not end with a '}'");
		acceptSeparators();
		return block;
	}
	/**
	 * Finds if the next thing is a statement and returns, otherwise throws and error
	 * @return StatementNode
	 * @throws Exception
	 */
	public StatementNode parseStatement() throws Exception{
		acceptSeparators();
		if(!feeder.matchAndRemove(Token.TokenType.CONTINUE).equals(Optional.empty())) {
			endOfStatement();
			return new ContinueNode();
		}
		if(!feeder.matchAndRemove(Token.TokenType.BREAK).equals(Optional.empty())) {
			endOfStatement();
			return new BreakNode();
		}
		if(!feeder.matchAndRemove(Token.TokenType.IF).equals(Optional.empty()))
			return parseIf(true);
		if(!feeder.matchAndRemove(Token.TokenType.FOR).equals(Optional.empty()))
			return parseFor();
		if(!feeder.matchAndRemove(Token.TokenType.DELETE).equals(Optional.empty()))
			return parseDelete();
		if(!feeder.matchAndRemove(Token.TokenType.WHILE).equals(Optional.empty()))
			return parseWhile();
		//endOfStatement to check that the end of the statement has a ;, }, or a new line
		if(!feeder.matchAndRemove(Token.TokenType.DO).equals(Optional.empty())) {
			return parseDoWhile();
		}
		if(!feeder.matchAndRemove(Token.TokenType.RETURN).equals(Optional.empty())) {
			Optional<Node> toParse = parseOperation();
			return parseReturn(toParse);
		}
		Optional<Node> check = parseOperation();
		if(!check.equals(Optional.empty())) {
			if(check.get() instanceof AssignmentNode) {
				endOfStatement();
				return (AssignmentNode)check.get();
			}
			if(check.get() instanceof FunctionCallNode) {
				endOfStatement();
				return (FunctionCallNode)check.get();
			}
			if(check.get() instanceof TernaryOperator) {
				endOfStatement();
				TernaryOperator ternary = (TernaryOperator)check.get();
				if(!(ternary.isFalse instanceof StatementNode)) exceptionToThrow("The false condition of a ternary operator is not a statement");
				if(!(ternary.isTrue instanceof StatementNode)) exceptionToThrow("The true condition of a ternary operator is not a statement");
				return (TernaryOperator)check.get();
			}
		}
		return null;
	}
	/**
	 * Handles return statements
	 * @param the statement to return
	 * @return ReturnNode
	 * @throws Exception
	 */
	public ReturnNode parseReturn(Optional<Node> toReturn) throws Exception{
		if(toReturn.equals(Optional.empty()))
			if(feeder.peek(0).equals(Optional.empty())) exceptionToThrow("Value expected after return");
		endOfStatement();
		return new ReturnNode(toReturn.get());
	}
	/**
	 * checks to see if the next thing is (, then parses as a function call, otherwise returns null
	 * @param name
	 * @return FunctionCallNode
	 * @throws Exception
	 */
	public FunctionCallNode parseFunctionCall(Node name) throws Exception{
		if(name == null) {
			Optional<Token> check;
			if(!(check = feeder.matchAndRemove(Token.TokenType.GETLINE)).equals(Optional.empty()))
				name = new VariableReferenceNode(check.get());
			else if(!(check = feeder.matchAndRemove(Token.TokenType.NEXT)).equals(Optional.empty()))
				name = new VariableReferenceNode(check.get());
			else if(!(check = feeder.matchAndRemove(Token.TokenType.NEXTFILE)).equals(Optional.empty()))
				name = new VariableReferenceNode(check.get());
			else if(!(check = feeder.matchAndRemove(Token.TokenType.PRINT)).equals(Optional.empty()))
				name = new VariableReferenceNode(check.get());
			else if(!(check = feeder.matchAndRemove(Token.TokenType.PRINTF)).equals(Optional.empty()))
				name = new VariableReferenceNode(check.get());
			else if(!(check = feeder.matchAndRemove(Token.TokenType.EXIT)).equals(Optional.empty()))
				name = new VariableReferenceNode(check.get());
			if(name == null) return null;
			if(!feeder.peek(0).get().type.equals(Token.TokenType.STARTPAREN)) return new FunctionCallNode(name);
		}
		if(feeder.matchAndRemove(Token.TokenType.STARTPAREN).equals(Optional.empty())) return null;
		FunctionCallNode functionCall = new FunctionCallNode(name);
		Optional<Node> param = parseOperation();
		//continues to check that there are parameters and adds them to parameter list in functionCall
		while(!param.equals(Optional.empty())) {
			//if there's supposed to be a comma here, and there isn't, throw exception
			if(!(feeder.moreTokens() && feeder.peek(0).get().type.equals(Token.TokenType.ENDPAREN)) &&
				feeder.matchAndRemove(Token.TokenType.COMMA).equals(Optional.empty())) exceptionToThrow("Expected ',' in a function call");
			acceptSeparators();
			functionCall.parameters.add(param.get());
			param = parseOperation();
		}
		if(feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty())) exceptionToThrow("Expected ')' in a function call");
		return functionCall;
	}
	/**
	 * Parses while loops
	 * @return WhileNode
	 * @throws Exception
	 */
	public WhileNode parseWhile() throws Exception{
		if(feeder.matchAndRemove(Token.TokenType.STARTPAREN).equals(Optional.empty())) exceptionToThrow("Missing '(' after the word \"while\"");
		Optional<Node> check = parseOperation();
		if(check.equals(Optional.empty())) exceptionToThrow("Condition expected in while loop declaration");
		if(!(check.get() instanceof OperationNode)) exceptionToThrow("Condition expected in while loop declaration");
		OperationNode condition = (OperationNode)check.get();
		if(feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty())) exceptionToThrow("Missing ')' after the word \"while\"");
		WhileNode whileNode = new WhileNode(condition);
		whileNode.block = isBlock(new BlockNode(Optional.empty()));
		return whileNode;
	}
	/**
	 * Parses do while loops
	 * @return DoWhileNode
	 * @throws Exception
	 */
	public DoWhileNode parseDoWhile() throws Exception{
		DoWhileNode doNode = new DoWhileNode();
		doNode.block = parseBlock(new BlockNode(Optional.empty()));
		acceptSeparators();
		if(feeder.matchAndRemove(Token.TokenType.WHILE).equals(Optional.empty())) exceptionToThrow("Missing while statement in a do-while loop");
		if(feeder.matchAndRemove(Token.TokenType.STARTPAREN).equals(Optional.empty())) exceptionToThrow("Missing '(' after the word \"while\"");
		Optional<Node> check = parseOperation();
		if(check.equals(Optional.empty())) exceptionToThrow("Condition expected in do-while loop declaration");
		if(!(check.get() instanceof OperationNode)) exceptionToThrow("Condition expected in do-while loop declaration");
		OperationNode condition = (OperationNode)check.get();
		if(feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty())) exceptionToThrow("Missing ')' after the word \"while\"");
		doNode.condition = condition;
		endOfStatement();
		return doNode;
	}
	/**
	 * Checks to see that there is a ; } or new line after a statement to make sure that it has a valid end
	 * @throws Exception
	 */
	public void endOfStatement() throws Exception{
		if(feeder.tokens.size() == 0)
			throw new Exception("Incomplete program");
		if(!feeder.peek(0).get().type.equals(Token.TokenType.ENDBRACKET) && !acceptSeparators())
			throw new Exception("new line or ';' expected after a statement on line: " + feeder.peek(0).get().lineNumber);
	}
	/**
	 * Parses both for loops and for each loops
	 * @return StatementNode
	 * @throws Exception
	 */
	public StatementNode parseFor() throws Exception{
		if(feeder.matchAndRemove(Token.TokenType.STARTPAREN).equals(Optional.empty())) exceptionToThrow("Missing '(' after the word \"for\"");
		Optional<Node> first = parseOperation();
		//if the first thing after '(' is a VariableReferenceNode, it must be a for-each loop and has syntax:   for(a in b)
		if(first.equals(Optional.empty())) exceptionToThrow("for loop declaration is empty");
		if(first.get() instanceof OperationNode && ((OperationNode)first.get()).operand.equals(OperationNode.Operation.IN)) {
			OperationNode declaration = (OperationNode)first.get();
			ForEachNode forEach = new ForEachNode(declaration);
			if(feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty())) exceptionToThrow("Missing ')' after the word \"for\"");
			forEach.block = isBlock(new BlockNode(Optional.empty()));
			return forEach;
		}
		//otherwise, if there's an AssignmentNode after '(', must be for loop and has syntax: for(assignment; condition; assignment)
		else if(first.get() instanceof AssignmentNode) {
			ForNode forNode = new ForNode();
			forNode.initializer = (AssignmentNode)first.get();
			if(!acceptSeparators()) exceptionToThrow("new line or ';' expected in for loop declaration");
			first = parseOperation();
			if(first.equals(Optional.empty())) exceptionToThrow("condition expected in for loop declaration");
			if(!(first.get() instanceof OperationNode)) exceptionToThrow("condition expected in for loop declaration");
			OperationNode condition = (OperationNode)first.get();
			forNode.condition = condition;
			if(!acceptSeparators()) exceptionToThrow("new line or ';' expected in for loop declaration");
			first = parseOperation();
			if(first.equals(Optional.empty())) exceptionToThrow("incrementation expected in for loop declaration");
			if(!(first.get() instanceof AssignmentNode)) exceptionToThrow("incrementation expected in for loop declaration");
			forNode.increment = (AssignmentNode)first.get();
			if(feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty())) exceptionToThrow("Missing ')' after the word \"for\"");
			forNode.block = isBlock(new BlockNode(Optional.empty()));
			return forNode;
		}
		//in any other case, this is not a valid for loop
		exceptionToThrow("Invalid for loop declaration");
		return null;
	}
	/**
	 * Parses delete statements
	 * @return DeleteNode
	 * @throws Exception
	 */
	public DeleteNode parseDelete() throws Exception{
		if(feeder.matchAndRemove(Token.TokenType.STARTPAREN).equals(Optional.empty())) exceptionToThrow("Missing '(' after the word \"delete\"");
		Optional<Node> toDelete = parseOperation();
		if(toDelete.equals(Optional.empty())) exceptionToThrow("Missing an array in a delete call");
		if(!(toDelete.get() instanceof VariableReferenceNode)) exceptionToThrow("Missing an array in a delete call");
		//the thing being called in delete must be an array
		VariableReferenceNode array = (VariableReferenceNode)toDelete.get();
		if(feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty())) exceptionToThrow("Missing ')' after the word \"delete\"");
		endOfStatement();
		return new DeleteNode(array);
	}
	/**
	 * Parses through if, else if, and if statements based on the parameter
	 * @param isIf
	 * 
	 */
	public IfNode parseIf(boolean isIf) throws Exception{
		acceptSeparators();
		IfNode toReturn = new IfNode();
		//if there was an if statement (isIf == true), then use this portion of this function so it has syntax if(condtion) or else if(condition)
		if(isIf) {
			if(feeder.matchAndRemove(Token.TokenType.STARTPAREN).equals(Optional.empty())) exceptionToThrow("Missing '(' after the word \"if\"");
			Optional<Node> next = parseOperation();
			if(next.equals(Optional.empty())) exceptionToThrow("Missing condition for an if");
			toReturn.condition = next;
			if(feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty())) exceptionToThrow("Missing ')' after the word \"if\"");
			acceptSeparators();
			toReturn.block = isBlock(new BlockNode(Optional.empty()));
			acceptSeparators();
			if(!feeder.matchAndRemove(Token.TokenType.ELSE).equals(Optional.empty()))
				toReturn.next = parseIf(false);
		}
		 //This happens when isIf was false because that means that there was an else
		else {
			//if there is an if found, recur with isIf = true
			if(!feeder.peek(0).equals(Optional.empty()) && !feeder.matchAndRemove(Token.TokenType.IF).equals(Optional.empty())) 
				return parseIf(true);
			acceptSeparators();
			toReturn.block = isBlock(new BlockNode(Optional.empty()));
			if(!feeder.matchAndRemove(Token.TokenType.ELSE).equals(Optional.empty()))
				toReturn.next = parseIf(false);
		}
		return toReturn;
	}
	/**
	 * Checks if the block of code has a '{', if not, just call parseStatement once, otherwise, call parseBlock
	 * @param block
	 * @return BlockNode
	 * @throws Exception
	 */
	public BlockNode isBlock(BlockNode block) throws Exception{
		if(!feeder.moreTokens()) throw new Exception("Block of code missing at the end of program");
		if(!feeder.peek(0).get().type.equals(Token.TokenType.STARTBRACKET)) {
			StatementNode statement = parseStatement();
			if(statement == null) exceptionToThrow("Block is empty");
			block.statements.add(statement);
		}
		else parseBlock(block);
		return block;
	}
	/**
	 * Returns a new empty Optional value for now
	 * @return Optional<Node>
	 */
	public Optional<Node> parseOperation() throws Exception{
		Node left = expression();
		if(left == null) return Optional.empty();
		if(left.toString().contains("++") || left.toString().contains("--")) return Optional.of(left);
		Optional<Node> operand = Optional.of(left);
		if(!operand.equals(Optional.empty()) && operand.get() instanceof FunctionCallNode)
			return operand;
		if(!feeder.matchAndRemove(Token.TokenType.TILDA).equals(Optional.empty()))
			operand = Optional.of(new OperationNode(operand.get(), OperationNode.Operation.MATCH, parseOperation()));
		else if(!feeder.matchAndRemove(Token.TokenType.NOTMATCH).equals(Optional.empty()))
			operand = Optional.of(new OperationNode(operand.get(), OperationNode.Operation.NOTMATCH, parseOperation()));
		else if(!operand.equals(Optional.empty()) && operand.get() instanceof OperationNode && 
				!((OperationNode)operand.get()).operand.equals(OperationNode.Operation.DOLLAR)) return operand;
		//Parses through simple operations such as: + - * / ++ -- ^ %
		if(!feeder.matchAndRemove(Token.TokenType.INCREMENT).equals(Optional.empty()))
			operand = Optional.of(new AssignmentNode(operand.get(), new OperationNode(operand.get(), OperationNode.Operation.POSTINC)));
		else if(!feeder.matchAndRemove(Token.TokenType.DECREMENT).equals(Optional.empty()))
			operand = Optional.of(new AssignmentNode(operand.get(), new OperationNode(operand.get(), OperationNode.Operation.POSTDEC)));
		//Also right associative, makes different assignments
		else if(!feeder.matchAndRemove(Token.TokenType.PLUSEQUALS).equals(Optional.empty())) 
			operand = Optional.of(rightAssociative(operand.get(), Token.TokenType.PLUSEQUALS));
		else if(!feeder.matchAndRemove(Token.TokenType.POWEREQUALS).equals(Optional.empty())) 
			operand = Optional.of(rightAssociative(operand.get(), Token.TokenType.POWEREQUALS));
		else if(!feeder.matchAndRemove(Token.TokenType.MODEQUALS).equals(Optional.empty())) 
			operand = Optional.of(rightAssociative(operand.get(), Token.TokenType.MODEQUALS));
		else if(!feeder.matchAndRemove(Token.TokenType.TIMESEUQLS).equals(Optional.empty())) 
			operand = Optional.of(rightAssociative(operand.get(), Token.TokenType.TIMESEUQLS));
		else if(!feeder.matchAndRemove(Token.TokenType.DIVIDEEQUALS).equals(Optional.empty())) 
			operand = Optional.of(rightAssociative(operand.get(), Token.TokenType.DIVIDEEQUALS));
		else if(!feeder.matchAndRemove(Token.TokenType.MINUSEQUALS).equals(Optional.empty())) 
			operand = Optional.of(rightAssociative(operand.get(), Token.TokenType.MINUSEQUALS));
		else if(!feeder.matchAndRemove(Token.TokenType.ASSIGN).equals(Optional.empty())) 
			operand = Optional.of(rightAssociative(operand.get(), Token.TokenType.ASSIGN));
		if(!feeder.matchAndRemove(Token.TokenType.IN).equals(Optional.empty())) {
			Optional<Node> right = parseOperation();
			if(right.equals(Optional.empty())) exceptionToThrow("Not a complete \"IN\" statement");
			operand = Optional.of(new OperationNode(operand.get(), OperationNode.Operation.IN, right));
		}
		//handles all things conditional here
		if(!feeder.matchAndRemove(Token.TokenType.LESSER).equals(Optional.empty())) {
			operand = Optional.of(new OperationNode(operand.get(), OperationNode.Operation.LESSER, parseOperation()));
		}
		else if(!feeder.matchAndRemove(Token.TokenType.LESSOREQUALS).equals(Optional.empty()))
			operand = Optional.of(new OperationNode(operand.get(), OperationNode.Operation.LESSEREQUALS, parseOperation()));
		else if(!feeder.matchAndRemove(Token.TokenType.NOTEQUAL).equals(Optional.empty()))
			operand = Optional.of(new OperationNode(operand.get(), OperationNode.Operation.NOTEQUALS, parseOperation()));
		else if(!feeder.matchAndRemove(Token.TokenType.EQUALS).equals(Optional.empty())) 
			operand = Optional.of(new OperationNode(operand.get(), OperationNode.Operation.EQUALS, parseOperation()));
		else if(!feeder.matchAndRemove(Token.TokenType.GREATER).equals(Optional.empty()))
			operand = Optional.of(new OperationNode(operand.get(), OperationNode.Operation.GREATER, parseOperation()));
		else if(!feeder.matchAndRemove(Token.TokenType.GREATOREQUALS).equals(Optional.empty()))
			operand = Optional.of(new OperationNode(operand.get(), OperationNode.Operation.GREATEREQUALS, parseOperation()));
		if(!(operand.equals(Optional.empty())) && operand.get() instanceof OperationNode)
			operand = Optional.of(parseCondition((OperationNode)operand.get()));
		return Optional.of(concatenation(operand.get()));
	}
	/**
	 * parses all multidimensional arrays in these two formats:     a[b,c,d]    and or      a[b][c][d]
	 * @param name
	 * @return an appropriate array
	 */
	public VariableReferenceNode parseArrays(Node name) throws Exception{
		VariableReferenceNode array = new VariableReferenceNode(name);
		array.index = parseOperation();
		//parses through arrays that look like:    a[b,c,d]
		if(!feeder.matchAndRemove(Token.TokenType.COMMA).equals(Optional.empty())) {
			VariableReferenceNode newArray = new VariableReferenceNode(array);
			return parseArrays(newArray);
		}
		if(feeder.matchAndRemove(Token.TokenType.ENDBRACE).equals(Optional.empty())) exceptionToThrow("Missing ']' at the end of an array declaration");
		//parses through arrays that look like:      a[b][c][d]
		if(!feeder.matchAndRemove(Token.TokenType.STARTBRACE).equals(Optional.empty())) {
			VariableReferenceNode newArray = new VariableReferenceNode(array);
			return parseArrays(newArray);
		}
		return array;
	}
	/**
	 * Concats the given operation and whatever string comes next in the sequence of tokens
	 * @param operation
	 * @return a new concated String
	 * @throws Exception
	 */
	public Node concatenation(Node left) throws Exception {
		//checks if the next token is a string and concats it with the given operator
		Node right = expression();
		if(right == null) return left;
		return concatenation(new OperationNode(left, OperationNode.Operation.CONCATENATION, Optional.of(right)));
	}
	/**
	 * Checks all different kinds of condition expressions
	 * @param condition
	 * @return a ternary expression
	 * @throws Exception
	 */
	public Node parseCondition(OperationNode condition) throws Exception {
		//if there is && or ||, the recur if the original condition is conditional
		if(!feeder.matchAndRemove(Token.TokenType.AND).equals(Optional.empty())) {
			Optional<Node> check = parseOperation();
			if(!check.equals(Optional.empty()) && check.get() instanceof OperationNode) {
				OperationNode checkOperation = (OperationNode)check.get();
			}
			condition = new OperationNode(condition, OperationNode.Operation.AND, check);
			return condition;
		}
		if(!feeder.matchAndRemove(Token.TokenType.OR).equals(Optional.empty())) {
			Optional<Node> check = parseOperation();
			if(!check.equals(Optional.empty()) && check.get() instanceof OperationNode) {
				OperationNode checkOperation = (OperationNode)check.get();
			}
			condition = new OperationNode(condition, OperationNode.Operation.OR, check);
			return condition;
		}
		//checks that the following follows this format     condition ? true : false     and if it doesn't, just return the original condition
		if(!feeder.matchAndRemove(Token.TokenType.QUESTION).equals(Optional.empty())) {
			TernaryOperator ternary = new TernaryOperator(condition);
			ternary.isTrue = parseOperation().get();
			if(feeder.matchAndRemove(Token.TokenType.COLON).equals(Optional.empty())) exceptionToThrow("Missing ':' in a ternary expression");
			ternary.isFalse = parseOperation().get();
			return ternary;
		}
		return condition;
	}
	/**
	 * Checks that the function from the TokenHandler meets all the function requirements
	 * @param program
	 * @return if is a valid function
	 * @throws Exception
	 */
	public boolean parseFunction(ProgramNode program) throws Exception{
		FunctionNode function = null;
		acceptSeparators();
		//If the next token is a word, make that the name, and make a new function
		Optional<Token> info = feeder.matchAndRemove(Token.TokenType.WORD);
		if(!info.equals(Optional.empty())) function = new FunctionNode(info);
		else exceptionToThrow("Function is missing a name");
		acceptSeparators();
		//Makes sure function is in this format: function functionName(functionParameters *a,b,c*) {blockOfCode}, otherwise, throw exception
		if(feeder.matchAndRemove(Token.TokenType.STARTPAREN).equals(Optional.empty())) exceptionToThrow("Paramter expected in function declaration, found ','");
		//continue to loop through the parameters to see that they are correctly formatted as shown above
		if(feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty())) {
			while(!info.equals(Optional.empty())) {
				acceptSeparators();
				if(!feeder.matchAndRemove(Token.TokenType.COMMA).equals(Optional.empty())) exceptionToThrow("Paramter expected in function declaration, found ','");
				info = feeder.matchAndRemove(Token.TokenType.WORD);
				acceptSeparators();
				function.parameterList.add(info);
				info = feeder.matchAndRemove(Token.TokenType.COMMA);
				acceptSeparators();
				if(!info.equals(Optional.empty()) && !feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty()))
					exceptionToThrow("Paramter expected in function declaration, found ')'");
			}
			acceptSeparators();
			if(feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty())) exceptionToThrow("Missing ')' after function declaration");
		}
		acceptSeparators();
		function.block = parseBlock(new BlockNode(Optional.empty()));
		//add the newly made and correctly formatted function into the programNode "tree"
		program.functions.add(function);
		return true;
	}
	/**
	 * Parses through some of the possible operands and function calls to return an appropriate Node
	 * @return Optional<Node>
	 * @throws Exception
	 */
	public Optional<Node> parseBottomLevel() throws Exception{
		//checks what the next symbol is and creates a new ConstantNode, OperationNode, or PatternNode
		if(!feeder.matchAndRemove(Token.TokenType.DOLLAR).equals(Optional.empty())) 
			return Optional.of(new OperationNode(OperationNode.Operation.DOLLAR, Optional.of(expression())));
		Optional<Token> check = feeder.matchAndRemove(Token.TokenType.STRINGLITERALS);
		if(!check.equals(Optional.empty())) return Optional.of(new ConstantNode(check.get()));
		check = feeder.matchAndRemove(Token.TokenType.NUMBER);
		if(!check.equals(Optional.empty())) return Optional.of(new ConstantNode(check.get()));
		check = feeder.matchAndRemove(Token.TokenType.PATTERN);
		if(!check.equals(Optional.empty())) return Optional.of(new PatternNode(check.get()));
		if(!feeder.matchAndRemove(Token.TokenType.EXCLAMATION).equals(Optional.empty()))
			return Optional.of(new OperationNode(OperationNode.Operation.NOT, Optional.of(expression())));
		if(!feeder.matchAndRemove(Token.TokenType.MINUS).equals(Optional.empty()))
			return Optional.of(new OperationNode(OperationNode.Operation.UNARYNEG, Optional.of(expression())));
		if(!feeder.matchAndRemove(Token.TokenType.PLUS).equals(Optional.empty()))
			return Optional.of(new OperationNode(OperationNode.Operation.UNARYPOS, Optional.of(expression())));
		if(!feeder.matchAndRemove(Token.TokenType.INCREMENT).equals(Optional.empty())){
			Optional<Node> toInc = parseBottomLevel();
			if(!toInc.equals(Optional.empty())) {
				if(toInc.get().toString().contains("++") || toInc.get().toString().contains("--"))
					exceptionToThrow("Cannot use preincrement or predecrement here");
				return Optional.of(new AssignmentNode(toInc.get(), new OperationNode(OperationNode.Operation.PREINC,Optional.of(toInc.get()))));
			}
			Optional<Token> error = feeder.peek(0);
			if(error.equals(Optional.empty()))
				throw new Exception("Missing a term at the end of program");
			throw new Exception("Missing a term on line: " + error.get().lineNumber + " at position: " + error.get().position);
		}
		if(!feeder.matchAndRemove(Token.TokenType.DECREMENT).equals(Optional.empty())) {
			Optional<Node> toInc = parseBottomLevel();
			if(!toInc.equals(Optional.empty())) {
				if(toInc.get().toString().contains("++") || toInc.get().toString().contains("--"))
					exceptionToThrow("Cannot use preincrement or predecrement here");
				return Optional.of(new AssignmentNode(toInc.get(), new OperationNode(OperationNode.Operation.PREDEC,Optional.of(toInc.get()))));
			}
			Optional<Token> error = feeder.peek(0);
			if(error.equals(Optional.empty()))
				throw new Exception("Missing a term at the end of program");
			throw new Exception("Missing a term on line: " + error.get().lineNumber + " at position: " + error.get().position);
		}
		//checks if the next token is a function name     getline, print, printf, etc. Forces it to a FunctionCallNode
		FunctionCallNode functionCall;
		//if it wasn't a function name, then just check to see it is a function call, otherwise return it
		Node name = parseLeftValue();
		functionCall = parseFunctionCall(name);
		if(functionCall != null) return Optional.of(functionCall);
		if (name != null) return Optional.of(name);
		return Optional.empty();
	}
	/**
	 * Handles assignments using right association
	 * @param left
	 * @param assign
	 * @return a new Assignment
	 * @throws Exception
	 */
	public AssignmentNode rightAssociative(Node left, Token.TokenType assign) throws Exception{
		if(assign.equals(Token.TokenType.PLUSEQUALS)) 
			return new AssignmentNode(left, new OperationNode(left, OperationNode.Operation.ADD, parseOperation()));
		if(assign.equals(Token.TokenType.POWEREQUALS))
			return new AssignmentNode(left, new OperationNode(left, OperationNode.Operation.EXPONENT, parseOperation()));
		if(assign.equals(Token.TokenType.MODEQUALS))
			return new AssignmentNode(left, new OperationNode(left, OperationNode.Operation.MODULO, parseOperation()));
		if(assign.equals(Token.TokenType.TIMESEUQLS)) 
			return new AssignmentNode(left, new OperationNode(left, OperationNode.Operation.MULTIPLY, parseOperation()));
		if(assign.equals(Token.TokenType.DIVIDEEQUALS))
			return new AssignmentNode(left, new OperationNode(left, OperationNode.Operation.DIVIDE, parseOperation()));
		if(assign.equals(Token.TokenType.MINUSEQUALS)) 
			return new AssignmentNode(left, new OperationNode(left, OperationNode.Operation.SUBTRACT, parseOperation()));
		if(assign.equals(Token.TokenType.ASSIGN))
			return new AssignmentNode(left, parseOperation().get());
		return null;
	}
	/**
	 * Handles factors such as one value or things inside of parenthesis
	 * @return a new factor
	 * @throws Exception
	 */
	public Node factor() throws Exception{
		Optional<Token> num = feeder.matchAndRemove(Token.TokenType.NUMBER);
		if(!num.equals(Optional.empty())) return new ConstantNode(num.get());
		Optional<Node> op = parseBottomLevel();
		if(!op.equals(Optional.empty())) return op.get();
		if(!feeder.matchAndRemove(Token.TokenType.STARTPAREN).equals(Optional.empty())) {
			//Node exp = expression();
			Optional<Node> exp = parseOperation();
			if(exp.equals(Optional.empty())) exceptionToThrow("Missing expression between parenthesis");
			if(feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty())) 
				exceptionToThrow("Missing parenthesis after expression");
			return exp.get();
		}
		return null;
	}
	/**
	 * Handles exponents
	 * @return a new exponent operation
	 */
	public Node exponent() throws Exception{
		Node left = factor();
		do {
			Optional<Token> operator = feeder.matchAndRemove(Token.TokenType.CARROT);
			if(operator.equals(Optional.empty())) return left;
			Node right = exponent();
			left = new OperationNode(left, OperationNode.Operation.EXPONENT, Optional.of(right));
		}while(true);
	}
	/**
	 * Handles terms such as * % /
	 * @return a new term
	 */
	public Node term() throws Exception {
		Node left = exponent();
		do {
			Optional<Token> operator = feeder.matchAndRemove(Token.TokenType.ASTERISK);
			if(operator.equals(Optional.empty())) operator = feeder.matchAndRemove(Token.TokenType.SLASH);
			if(operator.equals(Optional.empty())) operator = feeder.matchAndRemove(Token.TokenType.PERCENT);
			if(operator.equals(Optional.empty())) return left;
			Node right = exponent();
			if(operator.get().type.equals(Token.TokenType.ASTERISK))
				left = new OperationNode(left, OperationNode.Operation.MULTIPLY, Optional.of(right));
			else if(operator.get().type.equals(Token.TokenType.SLASH))
				left = new OperationNode(left, OperationNode.Operation.DIVIDE, Optional.of(right));
			else left = new OperationNode(left, OperationNode.Operation.MODULO, Optional.of(right));
		}while(true);
	}
	/**
	 * Handles expressions such as + or -
	 * @return a new expression
	 */
	public Node expression() throws Exception {
		Node left = term();
		do {
			Optional<Token> operator = feeder.matchAndRemove(Token.TokenType.PLUS);
			if(operator.equals(Optional.empty())) operator = feeder.matchAndRemove(Token.TokenType.MINUS);
			if(operator.equals(Optional.empty())) return left;
			Node right = term();
			if(operator.get().type.equals(Token.TokenType.PLUS))
				left = new OperationNode(left, OperationNode.Operation.ADD, Optional.of(right));
			else left = new OperationNode(left, OperationNode.Operation.SUBTRACT, Optional.of(right));
		}while(true);
	}
	/**
	 * In a given operation: a = b, parseLeftValue creates any left operand
	 * @return lValue
	 * @throws Exception
	 */
	public Node parseLeftValue() throws Exception{
		Optional<Token> check = feeder.matchAndRemove(Token.TokenType.WORD);
		VariableReferenceNode name = null;
		if(!check.equals(Optional.empty()))
			name = new VariableReferenceNode(check.get());
		if(!feeder.matchAndRemove(Token.TokenType.STARTBRACE).equals(Optional.empty()))
			name = parseArrays(name);
		return name;
	}
	/**
	 * Checks with method to send the action to - block of code, or operation and adds to program "tree"
	 * @param program
	 * @return is valid action
	 * @throws Exception
	 */
	public boolean parseAction(ProgramNode program) throws Exception{
		acceptSeparators();
		//if the block of code starts with "BEGIN", send to parseBlock()
		if(!feeder.matchAndRemove(Token.TokenType.BEGIN).equals(Optional.empty())) {
			//add block to the program
			program.begins.add(parseBlock(new BlockNode(Optional.empty())));
			return true;
		}
		//if the block of code starts with "END", send to parseBlock()
		else if (!feeder.matchAndRemove(Token.TokenType.END).equals(Optional.empty())) {
			//add block to the program
			program.ends.add(parseBlock(new BlockNode(Optional.empty())));
			return true;
		}
		//in all other cases, send to parseOperation()
		else if(!feeder.matchAndRemove(Token.TokenType.STARTPAREN).equals(Optional.empty())){
			Optional<Node> condition = parseOperation();
			if(condition.equals(Optional.empty())) exceptionToThrow("Missing a condition at the beginning of a block");
			if(!(condition.get() instanceof OperationNode)) exceptionToThrow("Missing a condition at the beginning of a block");
			OperationNode conditional = (OperationNode)condition.get();
			if(feeder.matchAndRemove(Token.TokenType.ENDPAREN).equals(Optional.empty())) exceptionToThrow("Missing '(' after condition at the beginning of a block");
			acceptSeparators();
			program.others.add(parseBlock(new BlockNode(condition)));
			return true;
		}
		else {
			program.others.add(parseBlock(new BlockNode(Optional.empty())));
			return true;
		}
	}
	/**
	 * Throws one exception if the program is over, or a different exception if it's not. Each expression uses the specified error message
	 * @param error
	 * @throws Exception
	 */
	public void exceptionToThrow(String error) throws Exception{
		if(feeder.peek(0).equals(Optional.empty()))
			throw new Exception(error + " at the end of the program");
		throw new Exception(error + " on line: " + feeder.peek(0).get().lineNumber);
	}
}