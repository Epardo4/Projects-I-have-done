/*
 * Class to create LinkedLists
 * E. Pardo
 * April 4, 2023
 */
public class LinkedList <T>{
	/*
	 * Class to create Nodes for the LinkedList class
	 * E. Pardo
	 * April 4, 2023
	 */
	private class Node<T>{
		private T data; //the data for each Node
		
		/**
		 * Constructor for Nodes
		 * @param data
		 */
		public Node(T data) {this.data = data;}
		
		/**
		 * Gets the data for this Node object
		 * @return data
		 */
		public T data() {return data;}
	}
	private Node<T>[] list; //The array that will hold the information for this LinkedList
	
	/**
	 * Constructor for the LinkedList with a specified length
	 * @param length
	 */
	public LinkedList(int length) {
		list = new Node[length];
	}
	
	/**
	 * Empty Constructor for LinkedList
	 */
	public LinkedList() {
		list = new Node[5];
	}
	
	/**
	 * Gets the index in the LinkedList of the specified value if found
	 * @param value
	 * @return where if found in the LinkedList value is
	 */
	public int indexOf(T value) {
		for(int i = 0; i < list.length; i++)
			if(list[i] != null && list[i].data.equals(value))
				return i;
		return -1; //value was not found
	}
	
	/**
	 * Adds value T to the LinkedList
	 * @param value
	 */
	public void add(T value) {
		Node<T> val = new Node(value); //list is of Node not T
		for(int i = 0; i < list.length; i++)
			//finds the first null in the LinkedList and adds val there
			if(list[i] == null) {
				list[i] = val;
				return;
			}
	}
	
	/**
	 * Checks if the LinkedList has any non-null values in it
	 * @return whether the LinkedList is empty or not
	 */
	public boolean isEmpty() {
		for(Node<T> value : list)
			if(value != null)
				return false;
		return true;
	}
	
	/**
	 * Checks if the specified value is in the LinkedList
	 * @param value
	 * @return if value is in the LinkedList
	 */
	public boolean contains(T value) {
		//if indexOf does not return -1, it was found
		if(indexOf(value) != -1) return true;
		return false;
	}
	
	/**
	 * Swaps two values in the LinkedList
	 * @param index1
	 * @param index2
	 */
	public void swap(int index1, int index2) {
		Node<T> temporary = list[index1];
		list[index1] = list[index2];
		list[index2] = temporary;
	}
	
	/**
	 * The number of non-null values in the LinkedList
	 * @return the true size of the LinkedList
	 */
	public int size() {
		int size = 0;
		for(int i = 0; i < list.length; i++)
			if(list[i] != null)
				size++;
		return size;
				
	}
	
	/**
	 * Sets the given value at the given index to null
	 * @param index
	 */
	public void setToNull(int index) {
		list[index] = null;
	}
	
	/**
	 * The length of LinkedList
	 * @return the length of the LinkedList
	 */
	public int length() {
		return list.length;
	}
	
	/**
	 * Gets the value of the LinkedList at the given index
	 * @param index
	 * @return the value at index
	 */
	public T get(int index) {
		return list[index].data;
	}
	
	/**
	 * Gets the Node at the given index
	 * @param index
	 * @return index of the LinkedList
	 */
	public Node<T> getNode(int index){
		return list[index];
	}
	
	/**
	 * Sets the Node at index with another Node
	 * @param index
	 * @param value
	 */
	public void set(int index, Node<T> value) {
		list[index] = value;
	}
	
	/**
	 * Sets the Node at index with another Node based on the given T
	 * @param index
	 * @param value
	 */
	public void setT(int index, T value) {
		Node<T> val = new Node(value); //The new Node that will be used because LinkedList holds Node not T
		list[index] = val;
	}
	
	/**
	 * Creates the String to display the LinkedList
	 * @return the display String
	 */
	@Override
	public String toString() {
		String information = "["; //the String to be returned
		//adds to the String so it looks like "[T(0), T(1), T(2), ..."
		for(int i = 0; i < list.length - 1; i++) {
			if(list[i] == null)
				information += "NULL";
			else
				information += list[i].data;
			information += ", ";
		}
		//adds to the end of the String so it finishes with "..., T(length-3), T(length-2), T(length-1)]"
		if(list[list.length - 1] == null)
			information += "NULL]";
		else
			information += list[list.length - 1].data + "]";
		return information;
	}
}