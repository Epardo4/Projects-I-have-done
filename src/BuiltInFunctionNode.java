import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
public class BuiltInFunctionNode extends FunctionNode{
	public boolean variadac;
	private Function<HashMap<String, Object>, String> doit;
	public BuiltInFunctionNode(Function<HashMap<String, Object>, String> func, boolean variadac) {
		this.variadac = variadac;
		doit = func;
	}
	public String execute(HashMap<String,Object> params) {
		return doit.apply(params);
	}
}
