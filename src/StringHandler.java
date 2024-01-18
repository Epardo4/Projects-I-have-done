
public class StringHandler {
	private String document;
	private int index = 0;
	/** Constructor that makes the StringHandler
	 * @param d
	 */
	public StringHandler(String document)  {
		this.document = document;
	}
	/** Peeks ahead i number of characters in the String
	 * @param i
	 * @return the character i places ahead
	 */
	public char peek(int i) {
		return document.charAt(index + i);
	}
	/** Looks at the character at the current index without moving forward at all
	 * @return character at current index
	 */
	public char peek() {
		return document.charAt(index);
	}
	/** Looks at the string from the current index to i indicies ahead
	 * @param i
	 * @return i characters ahead
	 */
	public String peekString(int i) {
		String peakedString = document.substring(index, index+i);
		return peakedString;
	}
	/** Accesses the current character and moves the index ahead one spot
	 * @return current character
	 */
	public char getChar() {
		char current = document.charAt(index);
		index++;
		return current;
	}
	/** Moves the index ahead i characters
	 * @param i
	 */
	public void swallow(int i) {
		index += i;
	}
	/** Checks if the index is at the end of the String
	 * @return if document is done
	 */
	public boolean isDone() {
		if(index >= document.length()) return true;
		return false;
	}
	/** Gets the remaining characters in the string
	 * @return the rest of the string
	 */
	public String remainder() {
		if(!isDone())
			return document.substring(index);
		return "";
	}
}