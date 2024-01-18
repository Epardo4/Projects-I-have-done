/*
 * Class to build a tree with the alphabet using user input
 * E.Pardo
 * April 13, 2023
 */
import java.util.LinkedList;
public class Trie {
	int depth; //how many layers the Trie can branch off to
	TrieNode root; //the root of the Trie
	/**
	 * Constructor for the Trie class
	 * @param depth
	 */
	public Trie(int depth) {
		this.depth = depth;
		root = new TrieNode(depth);
	}
	/**
	 * Checks if value exists in the Trie
	 * @param value
	 * @return if the word exists in the Trie
	 */
	public boolean exists(String value) {
		value = value.toLowerCase();
		return root.exists(value, 0);
	}
	/**
	 * Inserts the given value into the Trie
	 * @param value
	 * @return if the word is already there, return false, otherwise add and return true
	 */
	public boolean insert(String value) {
		value = value.toLowerCase();
		return root.insert(value, 0);
	}
}