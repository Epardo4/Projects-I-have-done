import java.util.LinkedList;

/*
 * Class to build a Node for the tree
 * E.Pardo
 * April 13, 2023
 */
public class TrieNode {
	private char data; //the character that represents the data of this TrieNode
	private TrieNode[] list; //a list of TrieNodes for each letter of the alphabet
	private boolean endOfWord = false; //checks if this letter is the last letter of a word
	private int depth; //depth of the Trie
	/**
	 * The constructor for the TrieNode class
	 */
	public TrieNode(int levels) {
		list = new TrieNode[26];
		depth = levels;
	}
	/**
	 * Accesses the data for this TrieNode
	 * @return the character in this spot
	 */
	public Object getData() {
		//checks if this character has a letter, if not set the character to an empty space
		char[] characters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v'
				,'w','x','y','z'};
		for(char c : characters)
			if(data == c) return c;
		return ' ';
	}
	/**
	 * Mutates the data for this TrieNode to the given letter
	 * @param letter
	 */
	public void setData(char letter) {
		data = letter;
	}
	/**
	 * Accesses the TrieNode from the list at the given index
	 * @param index
	 * @return the TrieNode at the index
	 */
	public TrieNode getNode(int index) {
		//if the TrieNode at index is null, initialize it and then return it
		if(list[index] == null)
			list[index] = new TrieNode(depth);
		return list[index];
	}
	/**
	 * Accesses the value for endOfWord
	 * @return endOfWord
	 */
	public boolean getEndOfWord() {return endOfWord;}
	/**
	 * Sets endOfWord to true;
	 */
	public void setEndOfWord() {endOfWord = true;}
	/**
	 * Checks if the value exists
	 * @param value
	 * @param index
	 * @return if the value exists
	 */
	public boolean exists(String value, int index) {
		char character = value.charAt(index);
		//checks if index is within the word or Trie
		if(index + 1 <= depth && index < value.length()) {
			if(getNode(character - 'a').data != character) return false;
			else if(index + 1 < value.length())
				return list[character - 'a'].exists(value, index + 1); //recursively calls the next letter in value
		}
		//if index is not within the Trie, but in the word, call LeafNode's exists method
		else if(index < value.length()){
			if(!getNode(character - 'a').exists(value.substring(index), 0)) return false;
		}
		return true;
	}
	/**
	 * Inserts the value if not found in the Trie
	 * @param value
	 * @param insert
	 * @return if value could be inserted
	 */
	public boolean insert(String value, int index) {
		if(exists(value, 0)) return false;
		char character = value.charAt(index);
		//checks if index is within the word or Trie
		if(index + 1 <= depth && index < value.length()) {
			if(getNode(character - 'a').data != character) list[character - 'a'].setData(character);
			if(index + 1 < value.length())
				list[character - 'a'].insert(value, index + 1); //recursively call the next letter in value
		}
		//if index is not within the Trie, but in the word, call LeafNode's insert method
		else if(index < value.length()){
			getNode(character - 'a').insert(value.substring(index), 0);
		}
		setEndOfWord(); //sets the end of the word at this point in the Trie
		return true;
	}
}
