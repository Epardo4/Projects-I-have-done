
public class Token {
	 enum TokenType {NUMBER, WORD, SEPARATOR, STARTBRACKET, ENDBRACKET, STARTBRACE, ENDBRACE, STARTPAREN, ENDPAREN, DOLLAR, TILDA, 
		 LESSER, GREATER, ASSIGN, EXCLAMATION, PLUS, MINUS, CARROT, QUESTION, COLON, ASTERISK, SLASH, PERCENT, AMPERSAND, PIPE, COMMA, 
		 LESSOREQUALS, INCREMENT, DECREMENT, GREATOREQUALS, EQUALS, NOTEQUAL, POWEREQUALS, MODEQUALS, TIMESEUQLS, DIVIDEEQUALS, 
		 PLUSEQUALS, MINUSEQUALS, NOTMATCH, AND, APPEND, OR, STRINGLITERALS, WHILE, IF, DO, FOR, BREAK, CONTINUE, ELSE, RETURN, BEGIN, 
		 END, PRINT, PRINTF, NEXT, IN, DELETE, GETLINE, EXIT, NEXTFILE, FUNCTION, PATTERN}
	 //>=  ++  --  <=  ==  !=  ^=  %=  *=  /=  +=  -=  !~   &&   >>   || { } [ ] ( ) $ ~ = < > !  + ^ - ?  : * / % ; \n | ,
	 //GREATOREQUALS INCREMENT DECREMENT LESSOREQUALS EQUALS NOTEQUALS POWEREQUALS MODEQUALS TIMESEQUALS DIVIDEEQUALS PLUSEQUALS MINUSEQUALS
	 //NOTMATH AND APPEND OR STARTBRACKET END BRACKET STARTBRACE ENDBRACE STARTPAREN ENDPAREN DOLLAR TILDA ASSIGN GREATER LESSER EXCLAMATION
	 //PLUS CARROT MINUS QUESTION COLON ASTERISK SLASH PERCENT SEPARATOR SEPARATOR PIPE COMMA
	 public String value;
	 public int lineNumber;
	 public int position; 
	 public TokenType type;
	 /**A constructor to make a token without adding a value to it
	 	@param lineNumber
	 	@param position
	 	@param tokenType
	 */
	 public Token(int lineNumber, int position, TokenType type) {
		 this.lineNumber = lineNumber;
		 this.position = position;
		 this.type = type;
	 }
	 /**A constructor to make a token and adds a value to it
	 	@param lineNumber
	 	@param position
	 	@param tokenType
	 	@param value
	 */
	 public Token(int lineNumber, int position, TokenType type, String value) {
		 this.lineNumber = lineNumber;
		 this.position = position;
		 this.type = type;
		 this.value = value;
	 }
	 public TokenType getType() {
		 return type;
	 }
	 /** creates a string that contains the information of this token
	  * @return a string that contains the information of this token
	  */
	 public String toString() {
		 if(value != null) return type + "(" + value + ")";
		 return "" + type;
	 }
}