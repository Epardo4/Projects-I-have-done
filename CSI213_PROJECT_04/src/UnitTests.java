/*
 * Class to test Trie, TrieNode, and LeafNode
 * E.Pardo
 * April 13, 2023
 */
import org.junit.Assert;
import org.junit.Test;
public class UnitTests {
	/**
	 * Tests whether insert and exist works without depth
	 */
	@Test
	public void testInsert() {
		var test = new Trie(10);
		String[] here = {"Hello","there","General","kenobi","How","are","You"};
		String[] notHere = {"this","Is","not","In","Trie"};
		for(String s : here) test.insert(s);
		for(String s : here) Assert.assertTrue(test.exists(s));
		for(String s : notHere) Assert.assertFalse(test.exists(s));
	}
	/**
	 * Tests whether insert and exist works with depth
	 */
	@Test
	public void testdepth() {
		var test1 = new Trie(1);
		var test2 = new Trie(2);
		var test3 = new Trie(3);
		String[] here = {"hello","Heart","heretics","Instance","into","Arent","artsy","Flirts","crimson"};
		String[] notHere = {"Head","heard","Inside","ingloo","Arms","arch","Flint","flimsy","Crime","crickey"};
		for(String s : here) test1.insert(s);
		for(String s : here) Assert.assertTrue(test1.exists(s));
		for(String s : notHere) Assert.assertFalse(test1.exists(s));
		for(String s : here) test2.insert(s);
		for(String s : here) Assert.assertTrue(test2.exists(s));
		for(String s : notHere) Assert.assertFalse(test2.exists(s));
		for(String s : here) test3.insert(s);
		for(String s : here) Assert.assertTrue(test3.exists(s));
		for(String s : notHere) Assert.assertFalse(test3.exists(s));
	}
}
