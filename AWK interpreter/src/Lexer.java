import java.util.LinkedList;
import java.util.HashMap;
public class Lexer {
	private int lineNumber; 
	private StringHandler feeder;
	private int position;
	private String value = "";
	public LinkedList<Token> tokens;
	private HashMap<String, Token.TokenType> oneSpecial;
	private HashMap<String, Token.TokenType> twoSpecials;
	private HashMap<String, Token.TokenType> specialWords;
	
	/**
	 * Constructor for the Lexer class, takes a document and makes a StringHandler using that document
	 * @param document
	 */
	public Lexer(String document) {
		feeder = new StringHandler(document);
		lineNumber = 1;
		position = 1;
		tokens = new LinkedList<>();
		makeMaps();
	}
	/**
	 * Goes through each character in the given String using the StringHandler and creates different tokens
	 * @throws Exception 
	 */
	public void lex() throws Exception {
		//make sure the document is not finished
		while(!feeder.isDone()) {
			char current = getCurrent();
			//as long as the current character is a white space: do not make a token and move on
			while(isSpace(current))
				if(!feeder.isDone()) current = getCurrent();
				else return;
			//Checks to see if there's a symbol and makes that token if it is
			Token isSymbol = processSymbol(current, position);
			if(isSymbol != null) tokens.add(isSymbol);
			//is this a comment?
			else if(current == '#') {
				position--;
				while(!feeder.isDone() && current != '\n')
					current = feeder.getChar();
				lineNumber++;
			}
			//is this a quote?
			else if(current == '"') {
				tokens.add(processStringLiteral(current, position));
			}
			//is this a pattern?
			else if(current == '`') tokens.add(processPattern(current, position));
			//otherwise check if it is a word, a special word, a separator, or a number, if none of the above, throw an exception
			else if(current == '\n' || (!feeder.isDone() && current == '\\' && feeder.peek() == 'n')) {
				if(current == '\\') current = getCurrent();
				tokens.add(new Token(lineNumber, position, Token.TokenType.SEPARATOR));
				lineNumber++;
				position = 0;
			}
			else if(isLetter(current)) tokens.add(processWord(current, position));
			else if(isNumber(current) || (current == '.' && !feeder.isDone() && isNumber(feeder.peek()))) 
				tokens.add(processNumber(current, position));
			else if(current == ';') tokens.add(new Token(lineNumber, position, Token.TokenType.SEPARATOR));
			else {
				position--;exceptionToThrow();
			}
			value = "";
		}
	}
	/**
	 * Creates a word token based off of the future characters and ends when the word ends
	 * @param check
	 * @param position
	 * @return a word token
	 * @throws Exception
	 */
	public Token processWord(char check, int beginningOfToken) throws Exception{
		//keep incrementing until there are no longer letters and adding to the word, at the end, check if the word is a special word
		boolean wordEnd = false;
		while(wordEnd == false) {
			value += check;
			if(feeder.isDone() || !isLetter(feeder.peek())) wordEnd = true;
			else check = getCurrent();
		}
		for(String key: specialWords.keySet())
			if(value.equals(key)) return new Token(lineNumber, beginningOfToken, specialWords.get(key));
		wordEnd = false;
		if(feeder.isDone() || (!isLetter(feeder.peek()) && !isNumber(feeder.peek()) && feeder.peek() != '_')) {
			return new Token(lineNumber, beginningOfToken, Token.TokenType.WORD, value);
		}
		//checks to see that the document isn't finished and the word isn't over yet
		while(wordEnd == false && !feeder.isDone()) {
			//all cases will be handled here or in the lex() method unless a period in which case, throw an exception
			if(!isSpace(check) && !isNumber(check) && check != '_' && !isLetter(check) && check != ';' && !isSymbol(check)) 
				exceptionToThrow();
			if(!feeder.isDone() && (isLetter(feeder.peek()) || isNumber(feeder.peek()) || feeder.peek() == '_')) {
				check = getCurrent();
				value += check;
			}
			else wordEnd = true;
		}
		return new Token(lineNumber, beginningOfToken, Token.TokenType.WORD, value);
	}
	/**
	 * Makes a "string" and returns whatever was inside the quotation marks
	 * @param c
	 * @param p
	 * @return a new token containing the "string" value
	 */
	public Token processStringLiteral(char check, int beginningOfToken) throws Exception {
		do {
			if(feeder.isDone())
				throw new Exception("Error! String literal uncompleted at line: " + lineNumber + ", position: " + beginningOfToken);
			check = getCurrent();
			//make sure that if there is a quote, it may not be the end of the string
			if(feeder.remainder().length() > 1 && check == ('\\') && feeder.peek() == '"') {
				value += '"';
				feeder.swallow(1);
			}
			else value += check;
		}while(check != '"');
		return new Token(lineNumber, beginningOfToken, Token.TokenType.STRINGLITERALS, value.substring(0, value.length() - 1));
	}
	/**
	 * 
	 */
	public Token processPattern(char check, int beginningOfToken) throws Exception {
		do {
			if(feeder.isDone())
				throw new Exception("Error! Pattern uncompleted at line: " + lineNumber + ", position: " + beginningOfToken);
			check = getCurrent();
			value += check;
		}while(check != '`');
		return new Token(lineNumber, beginningOfToken, Token.TokenType.PATTERN, value.substring(0, value.length() - 1));
	}
	/**
	 * Creates a number token based off of the future characters and ends when the number ends
	 * @param check
	 * @param position
	 * @return a word token
	 * @throws Exception
	 */
	public Token processNumber(char check, int beginningOfToken) throws Exception {
		value += check;
		boolean wordEnd = false;
		//checks to see if the next character is a character that can be added in the number token
		while(wordEnd == false && !feeder.isDone()) {
			check = feeder.peek();
			//throw an exception if there is more than one '.' in one number
			if(check == '.' && value.contains(".") || (!isNumber(check) && check != '.' && !isSpace(check) && 
					check != '\n' && !isSymbol(check) && check != ';')) exceptionToThrow();
			if(isNumber(check) || check == '.') value += getCurrent();
			if(feeder.isDone() || (!isNumber(feeder.peek()) && feeder.peek() != '.')) wordEnd = true; 
		}
		return new Token(lineNumber, beginningOfToken, Token.TokenType.NUMBER, value);
	}
	/**
	 * Makes an either one character or two character symbol
	 * @param c
	 * @param p
	 * @return a token according to the following symbol's symbol
	 */
	public Token processSymbol(char firstCharacter, int beginningOfToken) {
		if(feeder.remainder().length() >= 1) {
			char check = feeder.peek();
			if(twoSpecials.get("" + firstCharacter + check) != null){
				position++;
				feeder.swallow(1);
				return new Token(lineNumber, beginningOfToken, twoSpecials.get("" + firstCharacter + check));
			}
		}
		if(oneSpecial.get("" + firstCharacter) != null) return new Token(lineNumber, beginningOfToken, oneSpecial.get("" + firstCharacter));
		return null;
	}
	/**
	 * Checks whether the given character is a letter or not
	 * @param check
	 * @return is letter or not
	 */
	public boolean isLetter(char check) {
		if(check >= 65 && check <= 90) return true;
		if(check >= 97 && check <= 122) return true;
		return false;
	}
	/**
	 * Checks whether the given character is a symbol
	 * @param check
	 * @return is symbol or not
	 */
	public boolean isSymbol(char check) {
		String symbol = "" + check;
		for(String key: oneSpecial.keySet()) {
			if(symbol.equals(key)) return true;
		}
		return false;
	}
	/**
	 * Checks whether the given character is a number or not
	 * @param check
	 * @return is number or not
	 */
	public boolean isNumber(char check) {
		if(check >= 48 && check <= 57) return true;
		return false;
	}
	/**
	 * Throws exception for the methods in order to shorten their code
	 * @param check
	 * @throws Exception
	 */
	public void exceptionToThrow() throws Exception{
			throw new Exception("Error! Unknown character at line: " + lineNumber + ", position: " + position);
	}
	/**
	 * Method to help get the current char and increment the position
	 * @return current character
	 */
	public char getCurrent() {
		position++;
		return feeder.getChar();
	}
	/**
	 * Make the hashMaps
	 */
	public void makeMaps() {
		oneSpecial = new HashMap<>();
		twoSpecials = new HashMap<>();
		specialWords = new HashMap<>();
		//Makes the hashMap for symbols containing only one character
		Token.TokenType[] oneSpecialTypes = {Token.TokenType.STARTBRACKET, Token.TokenType.ENDBRACKET, Token.TokenType.STARTBRACE, 
				Token.TokenType.ENDBRACE, Token.TokenType.STARTPAREN, Token.TokenType.ENDPAREN, Token.TokenType.DOLLAR, Token.TokenType.TILDA, 
				Token.TokenType.LESSER, Token.TokenType.GREATER, Token.TokenType.ASSIGN, Token.TokenType.EXCLAMATION, Token.TokenType.AMPERSAND,
				Token.TokenType.PLUS, Token.TokenType.MINUS, Token.TokenType.CARROT, Token.TokenType.QUESTION, Token.TokenType.COLON, 
				Token.TokenType.ASTERISK, Token.TokenType.SLASH, Token.TokenType.PERCENT, Token.TokenType.PIPE, Token.TokenType.COMMA};
		String oneCharacterSpecials = "{}[]()$~<>=!&+-^?:*/%|,";
		for(int i = 0; i < oneCharacterSpecials.length(); i++) {
			String toPut = "" + oneCharacterSpecials.charAt(i);
			oneSpecial.put(toPut, oneSpecialTypes[i]);
		}
		//Makes the hashMap for symbols containing two letters
		Token.TokenType[] twoSpecialTypes = {Token.TokenType.LESSOREQUALS, Token.TokenType.INCREMENT, Token.TokenType.DECREMENT, 
				Token.TokenType.GREATOREQUALS, Token.TokenType.EQUALS, Token.TokenType.NOTEQUAL, Token.TokenType.POWEREQUALS, 
				Token.TokenType.MODEQUALS, Token.TokenType.TIMESEUQLS, Token.TokenType.DIVIDEEQUALS, Token.TokenType.PLUSEQUALS, 
				Token.TokenType.MINUSEQUALS, Token.TokenType.NOTMATCH, Token.TokenType.AND, Token.TokenType.APPEND, Token.TokenType.OR};
		String[] twoCharacters = {"<=","++","--",">=","==","!=","^=","%=","*=","/=","+=","-=","!~","&&",">>","||"};
		for(int i = 0; i < twoCharacters.length; i++) 
			twoSpecials.put(twoCharacters[i], twoSpecialTypes[i]);
		//Makes the hashMap for symbols containing words
		Token.TokenType[] wordTokens = {Token.TokenType.WHILE, Token.TokenType.IF, Token.TokenType.DO, Token.TokenType.FOR, 
				Token.TokenType.BREAK, Token.TokenType.CONTINUE, Token.TokenType.ELSE, Token.TokenType.RETURN, Token.TokenType.BEGIN, 
				Token.TokenType.END, Token.TokenType.PRINT, Token.TokenType.PRINTF, Token.TokenType.NEXT, Token.TokenType.IN, 
				Token.TokenType.DELETE, Token.TokenType.GETLINE, Token.TokenType.EXIT, Token.TokenType.NEXTFILE, Token.TokenType.FUNCTION};
		String[] words = {"while", "if", "do", "for", "break", "continue", "else", "return", "BEGIN", "END", "print", "printf", "next", 
				"in", "delete", "getline", "exit", "nextfile", "function"};
		for(int i = 0; i < words.length; i++)
			specialWords.put(words[i], wordTokens[i]);
	}
	/**
	 * Checks to see that the given character is just white space
	 * @param check
	 * @return is white space
	 */
	public boolean isSpace(char check) {
		if(check == ' ' || check == '\t' || check == '\r') return true;
		return false;
	}
	/**
	 * Creates a String to return in order to display the Lexer class
	 * @return the String to display the Lexer
	 */
	public String toString() {
		String toReturn = "";
		for(Token t: tokens) {
			toReturn += t.toString() + " ";
		}
		if(toReturn.length() > 0)return toReturn.substring(0, toReturn.length() - 1);
		return "";
	}
	/**
	 * returns the LinkedList of tokens for this class
	 * @return tokens
	 */
	public LinkedList<Token> getTokens(){
		return tokens;
	}
}