
public class ReturnType {
	public enum Type {NORMAL, BREAK, CONTINUE, RETURN};
	public Type type;
	public String value;
	public ReturnType(Type type) {
		this.type = type;
	}
	public ReturnType(Type type, String value) {
		this.type = type;
		this.value = value;
	}
	public String toString() {
		if(value == null) return (type + "").toLowerCase();
		String toReturn = (type + ": ").toLowerCase();
		return toReturn + value;
	}
}
