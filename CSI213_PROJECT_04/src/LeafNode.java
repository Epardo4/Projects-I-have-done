/*
 * Class inheriting the TrieNode class to build a leaf for the tree
 * E.Pardo
 * April 13, 2023
 */
import java.util.LinkedList;
public class LeafNode extends TrieNode{
	private LinkedList<String> data; //The data for a leaf
	private int depth; //the depth of the Trie
	/**
	 * Constructor for the LeafNode class
	 */
	public LeafNode(int levels) {
		super(levels);
		setEndOfWord();
	}
	/**
	 * Get the data for this LeafNode
	 */
	@Override
	public Object getData() {
		return data;
	}
	/**
	 * Check if the LinkedList for data contains a given value
	 */
	public boolean contains(String value) {
		return data.contains(value);
	}
	/**
	 * Set the data for this LeafNode to a given LinkedList
	 */
	public void setLeafData(LinkedList<String> toSet) {
		data = toSet;
	}
	/**
	 * Checks if the value is in data
	 * @param value
	 * @param index
	 * @return if the value is in data
	 */
	public boolean exists(String value, int index){
		if(data.contains(value)) return true;
		return false;
	}
	/**
	 * Inserts the value into the 
	 */
	public boolean insert(String value, int index) {
		if(exists(value, 0)) return false;
		data.add(value);
		return true;
	}
}
