/*
 * Class to test the ListArray, LinkedList, and Iteration classes
 * E. Pardo
 * April 4, 2023
 */
import static org.junit.Assert.*;

import java.lang.reflect.Array;

import org.junit.Assert;
import org.junit.Test;
public class ListArrayUnitTests {
	
	/**
	 * Tests an empty constructor
	 */
	@Test
	public void containsWithEmptyConstructor() {
		var list = new ListArray<String>();
		Assert.assertFalse(list.contains("Hello"));
	}
	
	/**
	 * Tests a constructor with two parameters
	 */
	@Test
	public void lengthWithFullContrsuctor() {
		var list = new ListArray<String>(10,46);
		Assert.assertTrue(list.length() == 50);
	}
	
	/**
	 * Tests a constructor with one parameter
	 */
	@Test
	public void lengthWithSomeConstructor() {
		var list = new ListArray<String>(19);
		Assert.assertTrue(list.length() == 19);
	}
	/**
	 * Tests that adding and getting elements works
	 */
	@Test
	public void addAndGetElements() {
		var list = new ListArray<String>();
		list.add("Hello");
		list.add("There");
		list.add("General");
		list.add("Kenobi");
		list.add("What's");
		list.add("Up");
		Assert.assertTrue(list.get(0).equals("Hello"));
		Assert.assertTrue(list.get(1).equals("There"));
		Assert.assertTrue(list.get(2).equals("General"));
		Assert.assertTrue(list.get(5).equals("Up"));
		Assert.assertTrue(list.contains("What's"));
	}
	
	/**
	 * Tests that setting elements works
	 */
	@Test
	public void setElements() {
		var list = new ListArray<String>();
		list.set(2, "Hello There Buddy-O");
		Assert.assertFalse(list.get(2).equals("Hello"));
		Assert.assertTrue(list.get(2).equals("Hello There Buddy-O"));
	}
	
	/**
	 * Tests that removing elements using remove(index) and remove(object) works
	 */
	@Test
	public void removeElement() {
		var list = new ListArray<String>();
		list.add("Hello");
		list.add("Are");
		list.add("You");
		list.add("going");
		list.add("To");
		list.add("the");
		list.add("Party");
		list.add("tonight?");
		Assert.assertTrue(list.contains("You"));
		list.remove("You");
		Assert.assertFalse(list.contains("You"));
		Assert.assertTrue(list.get(2).equals("going"));
		list.remove(2);
		Assert.assertTrue(list.get(2).equals("To"));
	}
	
	/**
	 * Tests that addAll works
	 */
	@Test
	public void addMutlipleElements() {
		var list = new ListArray<String>();
		list.add("Hello");
		list.add("Are");
		list.add("You");
		list.add("going");
		list.add("To");
		list.add("the");
		list.add("Party");
		list.add("tonight?");
		Assert.assertTrue(list.get(list.size()-1).equals("tonight?"));
		var toAdd = new ListArray<String>();
		toAdd.add("Hello");
		toAdd.add("There");
		toAdd.add("General");
		toAdd.add("Kenobi");
		list.addAll(toAdd);
		Assert.assertTrue(list.get(list.size()-1).equals("Kenobi"));
	}
	
	/**
	 * Tests that addAll() works at a specified index
	 */
	@Test 
	public void addMultipleElementsAtIndex() {
		var list = new ListArray<String>();
		var toAdd = new ListArray<String>();
		list.add("It's");
		list.add("My");
		list.add("Friend!");
		Assert.assertTrue(list.get(list.size() - 1).equals("Friend!"));
		toAdd.add("Banana");
		toAdd.add("Split");
		toAdd.add("We");
		toAdd.add("Rolled");
		toAdd.add("Out");
		toAdd.add("To");
		toAdd.add("Finario");
		list.addAll(4, toAdd);
		Assert.assertTrue(list.get(4).equals("Banana"));
		Assert.assertTrue(list.get(10).equals("Finario"));
	}
	
	/**
	 * Test that sorting the elements in the ListArray works
	 */
	@Test
	public void sortElementsInArray() {
		var list = new ListArray<String>();
		list.add("Banana");
		list.add("Split");
		list.add("We");
		list.add("Rolled");
		list.add("Out");
		list.add("To");
		list.add(8, "Finario");	
		list.sort(null);
		for(int i = 0; i < list.size() - 1; i++)
			Assert.assertTrue(list.compare(list.get(i), list.get(i + 1)) <= 0);
	}
	
	/**
	 * Tests that toArray() works
	 */
	@Test
	public void createAnArray() {
		var list = new ListArray<String>();
		list.add("Banana");
		list.add("Split");
		list.add("We");
		list.add("Rolled");
		list.add("Out");
		list.add("To");
		list.add("Finario");
		Object[] array1 = {"Banana","Split","We","Rolled","Out","To","Finario"};
		Object[] array2 = list.toArray();
		for(int i = 0; i < array1.length; i++)
			Assert.assertTrue(array1[i].equals(array2[i]));
	}
	
	/**
	 * Tests that clear() works
	 */
	@Test
	public void clearingList() {
		var list = new ListArray<String>();
		list.add("Banana");
		list.add("Split");
		list.add("We");
		list.add("Rolled");
		list.add("Out");
		list.add("To");
		list.add("Finario");
		list.clear();
		Assert.assertTrue(list.isEmpty());
	}
	
	/**
	 * Tests that the Iteration class works
	 */
	@Test
	public void testIterator() {
		var list = new ListArray<String>();
		list.add("Banana");
		list.add("Split");
		list.add("We");
		list.add("Rolled");
		list.add("Out");
		list.add("To");
		list.add("Finario");
		list.add("Champagne");
		list.add("Dragon");
		list.add("Pomegranate");
		list.add("Carriage");
		list.add("Blueberry");
		list.add("Sugar");
		list.add("Mountains");
		list.add("Bicycle");
		list.add("Beach");
		list.add("Welcome");
		Iteration<String> iterator = list.iterator();
		for(int i = 0; i < list.length(); i++)
			if(list.get(i) == null) Assert.assertTrue(iterator.next() == null);
			else Assert.assertTrue(list.get(i).equals(iterator.next()));
		iterator.setElement(8);
		Assert.assertTrue("Pomegranate".equals(iterator.next()));
	}
	
	/**
	 * Tests that toString() for LinkedList and ListArray both work
	 */
	@Test 
	public void whatToPrint() {
		var list = new ListArray<String>();
		list.add("Banana");
		list.add("Split");
		list.add("We");
		list.add("Rolled");
		list.add("Out");
		list.add("To");
		list.add("Finario");
		list.add("Champagne");
		list.add("Dragon");
		list.add("Pomegranate");
		list.add("Carriage");
		list.add("Blueberry");
		list.add("Sugar");
		list.add("Mountains");
		list.add("Bicycle");
		list.add("Beach");
		list.add("Welcome");
		String toTest = "[Banana, Split, We, Rolled, Out] ---> [To, Finario, Champagne, Dragon, Pomegranate] ---> "
				+ "[Carriage, Blueberry, Sugar, Mountains, Bicycle] ---> [Beach, Welcome, NULL, NULL, NULL]";
		Assert.assertTrue(list.toString().equals(toTest));
	}
}