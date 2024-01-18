
public class Practice {
	public static void main(String [] args) {
		var t = new Trie(1);
		String[] st = {"Hello","hillo","what","are","apple","you","Doing"};
		String[] it = {"this","is","not","here","head","and"};
		for(String s : st)
			t.insert(s);
		for(String s : st)
			System.out.println(t.exists(s) + "    " + s);
		for(String s : it)
			System.out.println(t.exists(s) + "    " + s);
		
	}
}
