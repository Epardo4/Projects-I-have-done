/* 
 * Class to iterate over a ListArray
 * E. Pardo
 * April 4, 2023
 */
import java.util.Iterator;
public class Iteration<T> implements Iterator<T>{
		private ListArray<T> toIterate; //The ListArray that will be iterated over
		private int element = -1; //the current element in the ListArray that the user is viewing
								  //start right before the ListArray
		
		/**
		 * Constructor for the Iteration class
		 * @param list
		 */
		public Iteration(ListArray<T> list) {
			toIterate = list;
		}
		
		/**
		 * Sets element to the desired index
		 * @param index
		 */
		public void setElement(int index) {
			element = index;
		}
		
		/**
		 * Checks if there is something in the array after element
		 * @return whether there is something in the array
		 */
		public boolean hasNext() {
			if(element < toIterate.length() - 1) return true;
			return false;
		}
		
		/**
		 * Gets the next element in toIterate and sets element to that index
		 * @return next element in toIterate
		 */
		public T next() {
			if(hasNext() == false) { 
				System.out.println("End of list \nreturning null and ending"); //indicate that there is no next
				return null;
			}
			element++;
			return toIterate.get(element);
		}
	}