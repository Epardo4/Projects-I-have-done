/* 
 * Class to create and implement a list array
 * E. Pardo
 * April 4, 2023
 */
import java.util.*;
public class ListArray<T> implements List<T> {
	private LinkedList<T>[] list; //A list of LinkedLists to implement
	/**
	 * Constructor For ListArray
	 * @param length
	 * @param num
	 */
	public ListArray(int lengthOfArrays, int numOfElements) {
		int numOfArrays = numOfElements/lengthOfArrays;
		//If numOfElements is not a multiple of lengthOfArrays, make one more array for the list
		if(numOfArrays % lengthOfArrays == 0) {
			list = new LinkedList[numOfArrays];
			for(int i = 0; i < numOfArrays; i++) 
				list[i] = new LinkedList(lengthOfArrays);
		}
		else {
			list = new LinkedList[numOfArrays + 1];
			for(int i = 0; i <= numOfArrays; i++)
				list[i] = new LinkedList(lengthOfArrays);
		}
	}
	
	/**
	 * Constructor for ListArray that only takes the length of one LinkedList
	 * @param lengthOfArray
	 */
	public ListArray(int lengthOfArray) {
		list = new LinkedList[1];
		list[0] = new LinkedList(lengthOfArray);
	}
	
	/**
	 * Empty Constructor For ListArray
	 */
	public ListArray() {
		list = new LinkedList[1];
		list[0] = new LinkedList(5);
	}
	
	/**
	 * Finds the array and index in that array for the given index
	 * @param index
	 * @return an integer array containing the array and element of index
	 */
	public int[] findIndex(int index) {
		 int[] indexInfo = new int[2];
		 int total = 0;
	   	 int numberOfArrays = 0;
	   	 //Find which array the LinkedList is in
	   	 while(list[numberOfArrays].length() + total <= index) {
	        	total += list[numberOfArrays].length();
	        	numberOfArrays++;
	     }
	   	 //The two pieces of info to get the index from the ListArray are the 
	   	 //LinkedList it's in and the element in that LinkedList
	     indexInfo[0] = numberOfArrays;
	     indexInfo[1] = index - total;
	     return indexInfo;
	}
	
	/**
	 * Gets the element at the given index
	 * @param index
	 * @return the element at the given index
	 */
    @Override
    public T get(int index) {
    	int[] indexInfo = findIndex(index);
    	//if the element is null, just return null. otherwise return the element
        if(list[indexInfo[0]].getNode(indexInfo[1]) != null)
        return list[indexInfo[0]].get(indexInfo[1]);
        return null;
    }
    
    /**
     * Sets the given element at the given index
     * @param index
     * @param element
     * @return element
     */
    @Override
    public T set(int index, T element) {
    	int[] indexInfo = findIndex(index);
        if(element == null) {
        	list[indexInfo[0]].setToNull(indexInfo[1]);
        	return null;
        }
        list[indexInfo[0]].setT(indexInfo[1], element);
        return element;
    }
    
    /**
     * Adds the given element at the given index
     * @param index
     * @param element
     */
    @Override
    public void add(int index, T element) {
    	//Make the ListArray one LinkedList bigger so that way it can hold the new element if 
    	//element is bigger than the number of elements
    	 if(index > length() - 1) {
    		 LinkedList[] largerList = new LinkedList[list.length + 1];
    		 for(int i = 0; i < list.length; i++)
    			 largerList[i] = list[i];
    		 largerList[largerList.length - 1] = new LinkedList(largerList[0].length());
    		 list = largerList;
    		 add((list.length - 1) * list[0].length(), element);
    		 return;
    	 }
    	 int[] indexInfo = findIndex(index);
    	 //if the last element is null, continue to swap it out with the previous element until it
    	 //reaches the specified index, then set that index which is now null to be the specified element
         if(list[list.length-1].getNode(list[list.length-1].length()-1) == null) {
        		for(int i = list.length-1; i > indexInfo[0]; i--) {
        			for(int j = list[i].length() - 1; j > 0; j--)
        				list[i].swap(j, j - 1);
        			list[i].set(0, list[i-1].getNode(list[i-1].length()-1));
        			list[i-1].set(list[i-1].length()-1, null);
        		}
        		for(int i = list[indexInfo[0]].length() - 1; i > indexInfo[1]; i--)
        			list[indexInfo[0]].swap(i, i - 1);
         }
         //otherwise do the same process, but add a new LinkedList to the end of ListArray first
         //and use the first element which is automatically null as the base element to swap out
         else {
        	 LinkedList[] otherList = new LinkedList[list.length + 1];
        	 for(int i = 0; i < list.length; i++) {
        		 otherList[i] = list[i];
        	 }
        	 otherList[list.length] = new LinkedList(list[0].length());
        	 list = otherList;
        	 for(int i = list.length-1; i > indexInfo[0]; i--) {
     			for(int j = list[i].length()-1; j > 0; j--)
     				list[i].swap(j,j - 1);
     			list[i].set(0, list[i-1].getNode(list[i-1].length()-1));
     			list[i-1].set(list[i-1].length()-1, null);
     		}
     		for(int i = list[indexInfo[0]].length() - 1; i > indexInfo[1]; i--)
     			list[indexInfo[0]].swap(i, i-1);
         }
         list[indexInfo[0]].setT(indexInfo[1], element);
    }
    
    /**
     * Gets the the number of all the elements in the ListArray
     * @return length
     */
    public int length() {
    	int length = 0;
    	//get each length of each LinkedList and add them all to get the length
    	//of the entire ListArray
    	for(LinkedList ll : list)
    		length += ll.length();
    	return length;
    }
    
    /**
     * Adds the given element into the ListArray
     * @return whether or not adding the element was possible
     */
    @Override
    public boolean add(T t) {
    	//find the first null element and add the specified element there
        for(int i = 0; i < list.length; i++) {
        	for(int j = 0; j < list[i].length(); j++)
        		if(list[i].getNode(j) == null) {
        			list[i].add(t);
        			return true;
        		}
        }
        //if there is no null elements in the ListArray, do the same process
        //after adding a new LinkedList first
        LinkedList[] otherList = new LinkedList[list.length + 1];
        for(int i = 0; i < list.length; i++)
        	otherList[i] = list[i];
        otherList[otherList.length - 1] = new LinkedList(list[0].length());
        otherList[otherList.length - 1].add(t);
        list = otherList;
        return true;
    }
    
    /**
     * Removes the element at the given index
     * @return the element which was removed
     */
    @Override
    public T remove(int index) {
        int[] indexInfo = findIndex(index);
        T answer = list[indexInfo[0]].get(indexInfo[1]);
        //find the specified element and set it to null, then continue to swap it out until
        //it is swapped out with the last element of this LinkedList
        list[indexInfo[0]].set(indexInfo[1], null);
        for(int i = indexInfo[1]; i < list[indexInfo[0]].length() - 1; i++)
        	list[indexInfo[0]].swap(i, i + 1);
        //swap this null element with the first element in the next LinkedList if there is one
        //then swap with each element in that ListArray
        //rinse and repeat until there is no next LinkedList
        for(int i = indexInfo[0] + 1; i < list.length; i++) {
        	list[i-1].set(list[i-1].length() - 1, list[i].getNode(0));
        	list[i].setToNull(0);
        	for(int j = 0; j < list[i].length() - 1; j++) 
        		list[i].swap(j, j + 1);
        }
        //delete any new LinkedLists that are now completely empty
        int lengthOfNext = list.length;
        while(list[lengthOfNext - 1].isEmpty()) lengthOfNext--;
        if(lengthOfNext != list.length) {
        	LinkedList[] otherList = new LinkedList[lengthOfNext];
        	for(int i = 0; i < otherList.length; i++)
        		otherList[i] = list[i];        	
        	list = otherList;
        }
        return answer;
    }
    
    /**
     * Gets the index of the given element
     * @param o
     * @return -1 if not in ListArray - or - the index of the given element
     */
    @Override
    public int indexOf(Object o) {
    	T value = ((T)o); //Make o an instance of T
    	int index;
    	//find which LinkedList value is in and then where in that LinkedList value is
        for(int i = 0; i < list.length; i++) 
        	if(list[i].contains(value)) return list[i].indexOf(value) + list[i].length() * i;
        return -1; //return -1 if not found
    }
    
    /**
     * Gets the last index of the given element
     * @param o
     * @return -1 if not in ListArray - or - the last index of the given element
     */
    @Override
    public int lastIndexOf(Object o) {
    	T value = (T)o; //Make o an instance of T
    	//find which LinekdList value is in from last to first then where in that LinkedList value is last seen
       for(int i = list.length - 1; i >= 0; i--)
    	   if(list[i].contains(value))
    		   for(int j = list[i].length() - 1; i >= 0; i--)
    			   if(list[i].getNode(i) != null && list[i].get(j).equals(value))
    				   return j + list[i].length() * i;
       return -1; //return -1 if not found
    }
    
    /**
     * Creates a ListArray within list from the two specified indices
     * @param fromIndex
     * @param toIndex
     * @return the subList of list
     */
    @Override
    public ListArray<T> subList(int fromIndex, int toIndex) {
        //make a sublist with the same LinkedList lengths as list and have as many elements as specified
    	//from fromIndex to toIndex
        ListArray subList = new ListArray(list[0].length(), toIndex - fromIndex + 1);
   	 	int indexOfSubList = 0;
   	 	//add to the subList if it is not null
   	 	//if it is null, don't add to the list, just increment inside the subList by one index
   	 	for(int i = fromIndex; i <= toIndex; i++) {
   	 		int[] indexInfo = findIndex(i);
        	if(list[indexInfo[0]].get(indexInfo[1]) != null)
        		subList.add(indexOfSubList, list[indexInfo[0]].get(indexInfo[1]));
        	indexOfSubList++;
   	 	}
        return subList;
    }
    
    /**
     * Gets the number of elements in the ListArray that are not null
     * @return true size of the ListArray
     */
    @Override
    public int size() {
    	int size = 0;
    	//get the size of each LinkedList and add them all to get all non-null values in the LisrArray
        for(LinkedList smallArray : list)
        	size += smallArray.size();
        return size;
    }
    
    /**
     * Checks if the entire ListArray is empty
     * @return whether the ListArray is or isn't empty
     */
    @Override
    public boolean isEmpty() {
    	//if one of the LinkedLists isn't empty, the ListArray isn't empty
        for(LinkedList smallArray : list)
        	if(smallArray.isEmpty() == false)
        		return false;
        return true;
    }
    
    /**
     * Checks whether the given element is in the ListArray
     * @return whether it is or isn't in the ListArray
     */
    @Override
    public boolean contains(Object o) {
        T value = ((T)o); //make o an instance of T
        //if one of the LinkedLists contains value, the ListArray contains value
        for(LinkedList smallArray : list)
        	if(smallArray.contains(value))return true;
        return false;
    }
    
    /**
     * Creates an Iteration object to iterate over the ListArray
     * @return a new Iteration object
     */
    @Override
    public Iteration<T> iterator() {
    	//make an instance of ListArray with the same information as list and then pass it into
    	//the Iteration constructor
    	ListArray<T> toIterate = new ListArray<T>(list[0].length(), length());
    	for(int i = 0; i < length(); i++)
    		toIterate.set(i, get(i));
    	return new Iteration<T>(toIterate);
    }
    
    /**
     * Creates an array of all the not null objects in the ListArray
     * @return a generic array
     */
    @Override
    public Object[] toArray() {
        Object[] bigArray = new Object[size()];
        int index = 0;
        //if something in the ListArray isn't null, add it to bigArray
        for(int i = 0; i < length(); i++)
        	if(get(i) != null) {
        		bigArray[index] = get(i);
        		index++;
        	}
        return bigArray;
    }
    
    /**
     * Removes a given object from the ListArray if found
     * @param o
     * @return if o was found
     */
    @Override
    public boolean remove(Object o) {
    	//if the ListArray contains the object, find it's index and remove it from the ListArray
        if(contains(o) == false) {
        	return false; //if it wasn't found, it can't be removed, so return false
        }
        int toRemove = indexOf(o);
        remove(toRemove);
        return true;
    }
    
    /**
     * Adds the entire collection to the ListArray
     * @param Collection c
     * @return whether adding was possible
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
    	//loop through the collection and add each value individually
    	for(T value : c)
    		if(value != null)
    			add(value);
    	return true;
    }
    
    /**
     * Adds the entire collection to the ListArray at the given index
     * @param index
     * @param Collection c
     * @return whether adding was possible at the given index
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
    	//loop through the collection and add at the specified index
    	for(T value : c)
    		if(value != null) {
    			add(index, value);
    			index++; //increment the index so that way, value adds to the next index in the list for every
    					 //non-null value
    		}
    	return true;
    }
    
    /**
     * Sorts the ListArray by calling the sort() method
     * @param Comparator c
     */
    @Override
    public void sort(Comparator<? super T> c) {
        // Use quicksort here
    	Object[] toSort = toArray(); //get the non-null values in the array
    	quickSort(c, toSort, 0, toSort.length - 1); //quick sort all of those values
    	clear(); //clear the ListArray and add all the new values to it
    	for(Object value : toSort)
    		add((T)value);
    }
    
    /**
     * Sets the hash code for a given object
     * @param toHash
     * @return hashCode of toHash
     * 
     */
    public int hashCode(Object toHash) {
    	//find what type the object is and return an integer based on an algorithm for that object that puts a value
    	//to the object
		if(toHash instanceof Integer)
			return (int)toHash;
		if(toHash instanceof Double || toHash instanceof Float || toHash instanceof Short || toHash instanceof Long) {
			double total = (double)toHash * 71829.5567 / 7 + ((int)((double)toHash / 4));
			int amount = (int)total;
			return amount;
		}
		if(toHash instanceof String) {
			int total = 0;
			String check = ((String)toHash).toLowerCase();
			for(int i = 0; i < check.length(); i++)
				if(check.charAt(i) % 2 == 0)
					total = (total + check.charAt(i)) * check.charAt(i);
				else
					total = (total + check.charAt(i)) / check.charAt(i);
			return total;
		}
		return 0;
	}
    
    /**
     * Compares two objects based on their hash codes
     * @param value1
     * @param value 2
     * @return an integer showing which object has a greater value
     */
    public int compare(Object value1, Object value2) {
    	int hashFirst = hashCode(value1);
    	int hashSecond = hashCode(value2);
    	//subtract the two objects if they aren't integers
    	if(value1 instanceof Double || value1 instanceof Float || value1 instanceof Short || 
    			value1 instanceof Long || value1 instanceof Integer) return hashFirst - hashSecond;
    	//if a String, just call compareTo to the lower case versions of themselves
    	if(value1 instanceof String) {
    		String first = (String)value1;
    		first = first.toLowerCase();
    		String second = (String)value2;
    		second = second.toLowerCase();
    		return first.compareTo(second);
    	}
    	return 0; //return 0 if no information was returned yet
    }
    
    /**
     * Quick sorts the values in toSort from least to greatest
     * @param Comparator c
     * @param toSort
     * @param low
     * @param high
     */
    public void quickSort(Comparator<? super T> c, Object[] toSort, int low, int high) {
    	int first = low, last = high, pivot = high;
    	Object temporary; //will be used to swap
    	//while not only one object that is being compared, continue to increment or decrement the indicies
    	//if the element at low is lower than the pivot and high is higher than the pivot
    	while(low < high) {
    		while(low < high && compare(toSort[low], toSort[pivot]) <= 0) low++;
    		while(low < high && compare(toSort[high], toSort[pivot]) >= 0) high--;
    		//just swap the two values if low is still less than high
    		if(low < high) {
    			temporary = toSort[low];
    			toSort[low] = toSort[high];
    			toSort[high] = temporary;
    		}
    		//otherwise, swap the middle index with pivot
    		else {
    			temporary = toSort[low];
    			toSort[low] = toSort[pivot];
    			toSort[pivot] = temporary;
    		}
    		//quickSort all values above and below the pivot point
    		quickSort(c, toSort, first, low - 1);
    		quickSort(c, toSort, low, last);
    	}
    }
    
    
    /**
     * Sets all values in the ListArray to null
     */
    @Override
    public void clear() {
    	//set all values in the LinkedList to null
    	for(LinkedList smallArray : list) {
    		for(int i = 0; i < smallArray.length(); i++)
    			smallArray.setToNull(i);
    	}
    }
    
    
    /**
     * Creates a String to display the ListArray using the LinkedList's toString() method
     * @return a String to display the ListArray
     */
    @Override
    public String toString() {
    	String information = ""; //the String that will be returned
    	//add to the string for each LinkedList so it looks like:
    	//"[T(0), T(1), ...] ---> [T(0), T(1), ...] ---> [T(0), T(1), ...] ---> ... "
    	for(int i = 0; i < list.length - 1; i++)
    		information += list[i].toString() + " ---> ";
    	information += list[list.length - 1].toString();
    	return information;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false; // DO NOT IMPLEMENT THIS
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false; // DO NOT IMPLEMENT THIS
    }
    @Override
    public ListIterator<T> listIterator() {
        return null; // DO NOT IMPLEMENT THIS
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return null; // DO NOT IMPLEMENT THIS
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false; // DO NOT IMPLEMENT THIS
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null; // DO NOT IMPLEMENT THIS
    }

}