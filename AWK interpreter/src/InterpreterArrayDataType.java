import java.util.HashMap;
public class InterpreterArrayDataType extends InterpreterDataType {
	HashMap<String, InterpreterDataType> value;
	/**
	 * Constructor for the InterpreterArrayDataType class
	 * @param value
	 */
	public InterpreterArrayDataType(HashMap<String, InterpreterDataType> value) {
		this.value = value;
	}
	/**
	 * Displays the variables in the array
	 * @return toReturn
	 */
	public String toString() {
		String toReturn = "";
		for(String s : value.keySet())
			toReturn += s + " = " + value.get(s).toString() + "\n";
		return toReturn;
	}
	public String[] toArray() {
		Object[] keys = value.keySet().toArray();
		String[] data = new String[keys.length];
		for(int i = 0; i < keys.length; i++) {
			data[i] = value.get(keys[i].toString()).toString();
		}
		return data;
	}
}
