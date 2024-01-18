import static org.junit.Assert.*;
import java.util.Optional;
import java.util.HashMap;
import java.util.LinkedList;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
public class Interpreter_Unit_Tests {
	Lexer lexer;
	/** test that making a Lexer works
	 */
	@Test
	public void makeNewLexer() {
		lexer = new Lexer("");
		Assert.assertEquals("", lexer.toString());
	}
	/** test that we can make word tokens
	 * @throws Exception 
	 */
	@Test
	public void wordTokens() throws Exception {
		lexer = new Lexer("Hello Goodbye");
		lexer.lex();
		Assert.assertEquals("WORD(Hello) WORD(Goodbye)", lexer.toString());
	}
	/**
	 * test that the Lexer can make word tokens containing digits and _ 
	 * @throws Exception 
	 */
	@Test
	public void wordWithNums() throws Exception {
		lexer = new Lexer("Are y0u There_Because_1mportant");
		lexer.lex();
		Assert.assertEquals("WORD(Are) WORD(y0u) WORD(There_Because_1mportant)", lexer.toString());
	}
	/**
	 * test that we can make number tokens
	 * @throws Exception
	 */
	@Test
	public void numberTokens() throws Exception {
		lexer = new Lexer("134 42.35 9.67 .453");
		lexer.lex();
		Assert.assertEquals("NUMBER(134) NUMBER(42.35) NUMBER(9.67) NUMBER(.453)", lexer.toString());
	}
	/**
	 * test that white space, other than new lines can be skipped
	 * @throws Exception
	 */
	@Test
	public void skipWhiteSpace() throws Exception {
		lexer = new Lexer("134 				   \r \r \t 	\r 42.35 9.67 .453");
		lexer.lex();
		Assert.assertEquals("NUMBER(134) NUMBER(42.35) NUMBER(9.67) NUMBER(.453)", lexer.toString());
	}
	/**
	 * test that whitespace at the end is ignored
	 * @throws Exception
	 */
	@Test
	public void ignoreWhiteSpace() throws Exception {
		lexer = new Lexer("Hello Goodbye          					\r \r \r");
		lexer.lex();
		Assert.assertEquals("WORD(Hello) WORD(Goodbye)", lexer.toString());
	}
	/**
	 * test that the separator token works and takes no string value
	 * @throws Exception
	 */
	@Test
	public void testNewLine() throws Exception {
		lexer = new Lexer("\n \n \n");
		lexer.lex();
		Assert.assertEquals("SEPARATOR SEPARATOR SEPARATOR", lexer.toString());
	}
	/**
	 * Test to see that all of the symbols work properly
	 */
	@Test
	public void testSymbols() throws Exception {
		lexer = new Lexer(">=  ++  --  <=  ==  !=  ^=  %=  *=  /=  +=  -=  !~   &&   >>   || { } [ ] ( ) $ ~ = <"
				+ " > !  + ^ - ?  : * / % ; \\n | ,");
		lexer.lex();
		String test = "GREATOREQUALS INCREMENT DECREMENT LESSOREQUALS EQUALS NOTEQUAL POWEREQUALS MODEQUALS TIMESEUQLS DIVIDEEQUALS PLUSEQUALS MINUSEQUALS NOTMATCH AND APPEND OR STARTBRACKET ENDBRACKET STARTBRACE ENDBRACE STARTPAREN ENDPAREN DOLLAR TILDA ASSIGN LESSER GREATER EXCLAMATION PLUS CARROT MINUS QUESTION COLON ASTERISK SLASH PERCENT SEPARATOR SEPARATOR PIPE COMMA";
		Assert.assertEquals(test, lexer.toString());
	}
	/**
	 * Test to see that all of the key words work properly
	 */
	@Test
	public void testKeyWords() throws Exception{
		lexer = new Lexer("while if do for break continue else return BEGIN END print "
				+ "printf next in delete getline exit nextfile function");
		lexer.lex();
		String test = "WHILE IF DO FOR BREAK CONTINUE ELSE RETURN BEGIN END PRINT PRINTF NEXT IN DELETE GETLINE EXIT NEXTFILE FUNCTION";
		Assert.assertEquals(test, lexer.toString());
	}
	/**
	 * Test to see that making patterns works
	 */
	@Test
	public void patterns() throws Exception{
		lexer = new Lexer("`how are you`");
		lexer.lex();
		Assert.assertEquals("PATTERN(how are you)", lexer.toString());
	}
	/**
	 * Test that checks that using multiple decimal points works
	 * @throws Exception 
	 */
	@Test(expected = Exception.class)
	public void Decimal() throws Exception {
		lexer = new Lexer(".5.6.7.8");
		lexer.lex();
		String expected = "";
		Assert.assertEquals("", expected);
	}
	/**
	 * Test that checks that the TokenHandler works
	 * @throws Exception
	 */
	@Test
	public void tokenHandlerTest() throws Exception {
		lexer = new Lexer("Hello my friend, my name is john");
		lexer.lex();
		TokenHandler testTokens = new TokenHandler(lexer.getTokens());
		Assert.assertTrue(testTokens.moreTokens());
		Assert.assertTrue(!testTokens.matchAndRemove(Token.TokenType.WORD).equals(Optional.empty()));
		testTokens = new TokenHandler(new LinkedList<Token>());
		Assert.assertFalse(testTokens.moreTokens());
	}
	/**
	 * Tests left associativity
	 * @throws Exception
	 */
	@Test
	public void testLeft() throws Exception{
		lexer = new Lexer("{a = a+b-c*d%e/f}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Assert.assertTrue(program.toString().contains("{\na = a+b-c*d%e/f\n}"));
		lexer = new Lexer("{i = a[a+b,c-d,e][f+g]}");
		lexer.lex();
		parse = new Parser(lexer.getTokens());
		program = parse.parse();
		Assert.assertTrue(program.toString().contains("{\ni = a[a+b][c-d][e][f+g]\n}"));
	}
	/**
	 * Tests right associativity
	 * @throws Exception
	 */
	@Test
	public void testRight() throws Exception{
		lexer = new Lexer("{a = a^b^c^d}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Assert.assertTrue(program.toString().contains("{\na = a^b^c^d\n}"));
		lexer = new Lexer("{a += b -= c *= d /= e ^= f %= g}");
		lexer.lex();
		parse = new Parser(lexer.getTokens());
		program = parse.parse();
		Assert.assertTrue(program.toString().contains("{\na += b -= c *= d /= e ^= f %= g\n}"));
	}
	/**
	 * Tests parseBlock with and without conditions
	 * @throws Exception
	 */
	@Test
	public void blockTest() throws Exception{
		lexer = new Lexer("{a+=b;a-=b;a++;b--}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Assert.assertTrue(program.toString().contains("{\na += b\na -= b\na++;\nb--;\n}"));
		lexer = new Lexer("(a<=b){a+=b;a-=b;--b;--a}");
		lexer.lex();
		parse = new Parser(lexer.getTokens());
		program = parse.parse();
		Assert.assertTrue(program.toString().contains("(a<=b)\n{\na += b\na -= b\n--b;\n--a;\n}"));
	}
	/**
	 * tests ternary
	 * @throws Exception
	 */
	@Test
	public void ternaryTest() throws Exception{
		lexer = new Lexer("{a<b?a++:a--}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Assert.assertTrue(program.toString().contains("{\na<b ? a++ : a--;\n}"));
	}
	/**
	 * Tests parsing statements
	 * @throws Exception
	 */
	@Test
	public void statementTest() throws Exception{
		lexer = new Lexer("{continue;break;delete(a[b]);return abc;}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Assert.assertTrue(program.toString().contains("{\ncontinue;\nbreak;\ndelete(a[b]);\nreturn abc;\n}"));
	}
	/**
	 * Tests parsing if statements
	 * @throws Exception
	 */
	@Test
	public void ifTest() throws Exception{
		lexer = new Lexer("{if(a==b){a += b;a++}else if(a!=b){a++;}else if(a!=b&&a<b||a>b){a--;b--;}else a = b;}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Assert.assertTrue(program.toString().contains( 
				"{\nif(a==b) {\na += b\na++;\n}else if(a!=b) {\na++;\n}else if(a!=b&&a<b||a>b) {\na--;\nb--;\n}else {\na = b\n}\n}"));
	}
	/**
	 * Tests parsing for loops
	 * @throws Exception
	 */
	@Test
	public void forTest() throws Exception{
		lexer = new Lexer("{for(a = 0; a < b; a++){c[a] = a;}for(a in c)printf(a);}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Assert.assertTrue(program.toString().contains("{\nfor(a = 0; a<b; a++) {\nc[a] = a\n}\nfor(a in c) {\nprintf(a);\n}\n}"));
	}
	/**
	 * Tests parsing while and do-while loops
	 * @throws Exception
	 */
	@Test
	public void whileTest() throws Exception{
		lexer = new Lexer("{while(a > 0)a--;do{a++;}while(a < 10);}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Assert.assertTrue(program.toString().contains("{\nwhile(a>0) {\na--;\n}\ndo {\na++;\n}while(a<10);\n}"));
	}
	/**
	 * Test that checks that the acceptSeparators() method works
	 * @throws Exception
	 */
	@Test
	public void acceptSeparatorsTest() throws Exception{
		lexer = new Lexer(";;;;;;;;;;;;;;;;;;;;;;;;;;");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		Assert.assertTrue(parse.acceptSeparators());
	}
	/**
	 * Test that getIDT works with math operations, assignments, and field references
	 * @throws Exception
	 */
	@Test
	public void testGetIDTVars() throws Exception{
		Lexer lexer = new Lexer("{k = 1+2-3^4^2%10*6/8;$(1+1)=k;a=\"Hello\";b=\"Bye\";c=a b}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Path filePath = Paths.get("C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/testFile.txt");
		Interpreter interp = new Interpreter(program, filePath);
		HashMap<String, InterpreterDataType> map = new HashMap<>();
		BlockNode b = program.others.get(0);
		for(StatementNode s : b.statements)
			interp.getIDT(s, map);
		Assert.assertEquals("3.0", map.get("k").toString());
		Assert.assertEquals("3.0", interp.variables.get("$2").toString());
		Assert.assertEquals("Hello", map.get("a").toString());
		Assert.assertEquals("Bye", map.get("b").toString());
		Assert.assertEquals("HelloBye", map.get("c").toString());
	}
	/**
	 * tests conditionals using getIDTs
	 * @Throws Exception
	 */
	@Test
	public void testGetIDTCond() throws Exception{
		Lexer lexer = new Lexer("{a=\"Hello\";b=a~`He`;3<4?c=3:c=4;d=hello!=hello;e=1<2&&1>0;f=1>2||1<2;g=!f}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Path filePath = Paths.get("C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/testFile.txt");
		Interpreter interp = new Interpreter(program, filePath);
		HashMap<String, InterpreterDataType> map = new HashMap<>();
		BlockNode b = program.others.get(0);
		for(StatementNode s : b.statements)
			interp.getIDT(s, map);
		Assert.assertEquals("1", map.get("b").toString());
		Assert.assertEquals("3",map.get("c").toString());
		Assert.assertEquals("0",map.get("d").toString());
		Assert.assertEquals("1",map.get("e").toString());
		Assert.assertEquals("1",map.get("f").toString());
		Assert.assertEquals("0",map.get("g").toString());
	}
	/**
	 * Tests the unary operands and the increment/decrement operators
	 * @throws Exception
	 */
	@Test
	public void testGetIDTUnary() throws Exception{
		Lexer lexer = new Lexer("{a=1;a++;++a;b=3;b--;--b;c=-5;c=-c;d=5;d=-d;e=-2;e=+e}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Path filePath = Paths.get("C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/testFile.txt");
		Interpreter interp = new Interpreter(program, filePath);
		HashMap<String, InterpreterDataType> map = new HashMap<>();
		BlockNode b = program.others.get(0);
		for(StatementNode s : b.statements)
			interp.getIDT(s, map);
		Assert.assertEquals("3.0",map.get("a").toString());
		Assert.assertEquals("1.0",map.get("b").toString());
		Assert.assertEquals("5.0",map.get("c").toString());
		Assert.assertEquals("-5.0",map.get("d").toString());
		Assert.assertEquals("2.0",map.get("e").toString());
	}
	/**
	 * Tests that in and variable reference nodes work properly in getIDT
	 * @throws Exception
	 */
	@Test
	public void testGetIDTIn() throws Exception{
		Lexer lexer = new Lexer("{a=1 in array; b=hello in array; c=bye in array; d=array[\"hello\"]}");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Path filePath = Paths.get("C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/testFile.txt");
		Interpreter interp = new Interpreter(program, filePath);
		HashMap<String, InterpreterDataType> map = new HashMap<>();
		HashMap<String, InterpreterDataType> temp = new HashMap<>();
		temp.put("1", new InterpreterDataType("1"));
		temp.put("hello", new InterpreterDataType("hello"));
		map.put("array", new InterpreterArrayDataType(temp));
		BlockNode b = program.others.get(0);
		for(StatementNode s : b.statements)
			interp.getIDT(s, map);
		Assert.assertEquals("1",map.get("a").toString());
		Assert.assertEquals("1",map.get("b").toString());
		Assert.assertEquals("0",map.get("c").toString());
		Assert.assertEquals("hello",map.get("d").toString());
	}
	/**
	 * Tests both user created and not built in function calls
	 * @throws Exception
	 */
	public void testFunctionCalls() throws Exception{
		Lexer lexer = new Lexer("function myFunction(a, b){if(a < b) return a; return b;}{a = myFunction(0, 10); "
				+ "b = substr(\"Hello There My Friend How Are You\", a, 21); c = sprintf(\"%s %s %s\", \"Hello\", \"There\", \"My Friend\")}");
		lexer.lex();
		Parser parser = new Parser(lexer.getTokens());
		ProgramNode program = parser.parse();
		Path filePath = Paths.get("C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/testFile.txt");
		Interpreter interp = new Interpreter(program, filePath);
		HashMap<String, InterpreterDataType> map = new HashMap<>();
		interp.interpretBlocks(program.others.get(0), map);
		Assert.assertEquals("0",map.get("a").toString());
		Assert.assertEquals(map.get("b").toString(), map.get("c").toString());
		Assert.assertEquals("Hello There My Friend", map.get("c").toString());
	}
	/**
	 * tests all loop functionalities
	 * @throws Exception
	 */
	@Test
	public void testLoops() throws Exception{
		Lexer lexer = new Lexer("function makeString(num){for(i = 0; i < 5; i++){e[i] = \"Your number is: \" i;}for(element in e)"
				+ "{if(num > element) return e[element];num += element;}}{a = 0;b = 0;c = 0;d = 0;while(a < 10){if(a > 6)break;a++;}"
				+ "do{b++}while(b < 20);for(i = 0; i < 15;i++){if(i > 10) continue;c += i;}d = makeString(0);}");
		lexer.lex();
		Parser parser = new Parser(lexer.getTokens());
		ProgramNode program = parser.parse();
		Path filePath = Paths.get("C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/testFile.txt");
		Interpreter interp = new Interpreter(program, filePath);
		HashMap<String, InterpreterDataType> map = new HashMap<>();
		interp.interpretBlocks(program.others.get(0), map);
		Assert.assertEquals("7.0", map.get("a").toString());
		Assert.assertEquals("20.0", map.get("b").toString());
		Assert.assertEquals("55.0", map.get("c").toString());
		Assert.assertEquals("Your number is: 4.0", map.get("d").toString());
	}
	/**
	 * Tests user text input by changing the field separator
	 * @throws Exception
	 */
	@Test
	public void testInput() throws Exception{
		Lexer lexer = new Lexer("BEGIN{NR = FNR = 0; FS = \",\";}{a = $0;b = $1;}");
		lexer.lex();
		Parser parser = new Parser(lexer.getTokens());
		ProgramNode program = parser.parse();
		Path filePath = Paths.get("C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/testFile.txt");
		Interpreter interp = new Interpreter(program, filePath);
		HashMap<String, InterpreterDataType> map = new HashMap<>();
		interp.interpretBlocks(program.begins.get(0), interp.variables);
		String[] As = {"Hello, world", "How is your life, going", "You are, good world", "With some problems, but that's ok"};
		String[] Bs = {"Hello", "How is your life", "You are", "With some problems"};
		for(int i = 0; i < 4; i++) {
			interp.lines.splitAndAssign();
			interp.interpretBlocks(program.others.get(0), map);
			Assert.assertEquals(interp.variables.get("$0").toString(), map.get("a").toString());
			Assert.assertEquals(interp.variables.get("$1").toString(), map.get("b").toString());
		}
	}
	/**
	 * Tests conditional and non-conditional blocks of code
	 * @throws Exception
	 */
	@Test
	public void testBlocks() throws Exception {
		Lexer lexer = new Lexer("{while(i < 10){i++;}return i;}(1 < 2){i = 10;while(i > 5)i--;return i;}"
				+ "(2 < 1){i = 10;while(i > 5)i--;return i;}");
		lexer.lex();
		Parser parser = new Parser(lexer.getTokens());
		ProgramNode program = parser.parse();
		Path filePath = Paths.get("C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/testFile.txt");
		Interpreter interp = new Interpreter(program, filePath);
		HashMap<String, InterpreterDataType> map = new HashMap<>();
		interp.interpretBlocks(program.others.get(0), map);
		Assert.assertEquals("10.0",map.get("i").toString());
		map.clear();
		interp.interpretBlocks(program.others.get(1), map);
		Assert.assertEquals("5.0", map.get("i").toString());
		map.clear();
		interp.interpretBlocks(program.others.get(2), map);
		Assert.assertEquals(0, map.size());
	}
	/**
	 * Test that checks that build in functions work
	 * @throws Exception
	 */
	@Test
	public void testBuiltIns() throws Exception {
		Lexer lexer = new Lexer("");
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Path filePath = Paths.get("C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/testFile.txt");
		Interpreter interp = new Interpreter(program, filePath);
		interp.variables.put("NR", new InterpreterDataType(""+0));
		interp.variables.put("FNR",new InterpreterDataType(""+0));
		HashMap<String, Object> map = new HashMap<>();
		map.put("0", "Hello");
		BuiltInFunctionNode builtin = (BuiltInFunctionNode)interp.functions.get("length");
		Assert.assertEquals("5", builtin.execute(map));
		map.clear();
		builtin = (BuiltInFunctionNode)interp.functions.get("getline");
		Assert.assertEquals("1", builtin.execute(map));
		map.clear();
		builtin = (BuiltInFunctionNode)interp.functions.get("next");
		Assert.assertEquals("1", builtin.execute(map));
		map.clear();
		map.put("0","HELLO");
		builtin = (BuiltInFunctionNode)interp.functions.get("tolower");
		Assert.assertEquals("hello", builtin.execute(map));
		map.clear();
		map.put("0", "hello");
		builtin = (BuiltInFunctionNode)interp.functions.get("toupper");
		Assert.assertEquals("HELLO", builtin.execute(map));
		map.clear();
		map.put("0", "Peanut");
		map.put("1", "3");
		builtin = (BuiltInFunctionNode)interp.functions.get("substr");
		Assert.assertEquals("anut", builtin.execute(map));
		map.put("2", "2");
		Assert.assertEquals("an", builtin.execute(map));
		map.clear();
		map.put("0", "an");
		map.put("1", "to");
		map.put("2", "Peanuts and some ants");
		builtin = (BuiltInFunctionNode)interp.functions.get("sub");
		Assert.assertEquals("Petouts and some ants", builtin.execute(map));
		map.clear();
		map.put("0", "%s %s %s %s");
		map.put("1", "testArray");
		HashMap<String, InterpreterDataType> testArray = new HashMap<>();
		testArray.put("0", new InterpreterDataType("Hello"));
		testArray.put("1", new InterpreterDataType("Dave"));
		testArray.put("2", new InterpreterDataType("I'm"));
		testArray.put("3", new InterpreterDataType("John"));
		interp.variables.put("testArray", new InterpreterArrayDataType(testArray));
		builtin = (BuiltInFunctionNode)interp.functions.get("sprintf");
		Assert.assertEquals("Hello Dave I'm John", builtin.execute(map));
		interp.variables.remove("testArray");
		map.remove("1");
		map.put("1", "Hello");
		map.put("2", "Dave");
		map.put("3", "I'm");
		map.put("4", "John");
		builtin = (BuiltInFunctionNode)interp.functions.get("sprintf");
		Assert.assertEquals("Hello Dave I'm John", builtin.execute(map));
		map.clear();
		map.put("0", "Hi, there, I'm, Micky, Mouse");
		map.put("1", "array");
		map.put("2", ",");
		builtin = (BuiltInFunctionNode)interp.functions.get("split");
		builtin.execute(map);
		Assert.assertTrue(interp.variables.containsKey("array"));
		map.clear();
		map.put("0", "Hi, there, I'm, Micky, Mouse");
		map.put("1", ",");
		map.put("2", "array");
		builtin = (BuiltInFunctionNode)interp.functions.get("match");
		builtin.execute(map);
		Assert.assertTrue(interp.variables.containsKey("array"));
		map.clear();
		map.put("0", "Peanut");
		map.put("1", "an");
		builtin = (BuiltInFunctionNode)interp.functions.get("index");
		Assert.assertEquals("3", builtin.execute(map));
		map.clear();
		map.put("0", "an");
		map.put("1", "to");
		map.put("2", "Peanuts and some ants");
		builtin = (BuiltInFunctionNode)interp.functions.get("gsub");
		Assert.assertEquals("Petouts tod some tots", builtin.execute(map));
		map.clear();
	}
}
