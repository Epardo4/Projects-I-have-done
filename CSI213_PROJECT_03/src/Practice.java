import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class Practice {
	public static void main(String[] args) {
		var list = new ListArray<String>();
		var toAdd = new ListArray<String>();
		list.add("It's");
		list.add("My");
		list.add("Friend!");
		list.add("Hello");
		list.add("Today");
		list.add("Me");
		list.add("Find");
		System.out.println(list.toString());
		toAdd.add("Banana");
		toAdd.add("Split");
		toAdd.add("We");
		toAdd.add("Rolled");
		toAdd.add("Out");
		toAdd.add("To");
		toAdd.add("Finario");
		list.addAll(4, toAdd);
		System.out.println(list.toString());
	}
}