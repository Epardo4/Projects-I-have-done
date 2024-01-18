
import java.util.Scanner;
public class Payroll {
		private static Employee[] employees = new Employee[100];
		private static int numberOfEmployees = 0;
		public static void main(String [] args) {
			Scanner keyboard = new Scanner(System.in);	
			String option1 = "1)    Create an employee";
			String option2 = "2)    Search for an employee by last name";
			String option3 = "3)	Display an employee by employee number";
			String option4 = "4)	Run payroll";
			String option5 = "5)	Quit";
			int option;
			String info;
			while(true) {
				System.out.println(option1 + "\n" + option2 + "\n" + option3 + "\n" + option4 + "\n" + option5);
				option = keyboard.nextInt();
				switch(option) {
					case 1:
						createEmployee();
						break;
					case 2:
						System.out.println("Which last name would you like to search for?");
						searchForEmployee(keyboard.nextLine());
						break;
					case 3:
						//Display employee by number
						break;
					case 4:
						//Run
						break;
					case 5:
						System.exit(0);
					default:
						System.out.println("That was not an option, please choose a different option");	
				}
			}
		}
		public static boolean checkWord(String word) {
			char[] checkCharacters = new char[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s',
					't','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S',
					'T','U','V','W','X','Y','Z','-',' '};
			boolean check = false;
			char a;
			for(int i = 0; i < word.length(); i++) {
				a = word.charAt(i);
				check = false;
				for(int j = 0; j < checkCharacters.length; j++) {
					if(checkCharacters[j] == a)
						check = true;
				}
				if(check == false)
					return check;
			}
			return check;
		}
		public static void createEmployee() {
			Scanner keyboard = new Scanner(System.in);
			String firstName = "";
			String lastName = "";
			int option;
			boolean check = false;
			while(check == false) {
				System.out.println("What is the employee's first name?");
				firstName = keyboard.nextLine();
				check = checkWord(firstName);
				if(check == false)
					System.out.println("Please enter a valid name with only letters, \"-\", or \" \"");
			}
			check = false;
			while(check == false) {
					System.out.println("What is the employee's last name?");
					lastName = keyboard.nextLine();
					check = checkWord(lastName);
					if(check == false)
						System.out.println("Please enter a valid name with only letters, \"-\", or \" \"");
			}
			if(firstName.equals("q") || lastName.equals("q"))
				return;
			System.out.println("What is this employee's type?\n1)    Hourly\n2)    Salaried\n3)    Commissioned?");
			option = keyboard.nextInt();
			switch (option){
				case 1:
					System.out.println("What is the employee's hourly pay rate?");
					employees[numberOfEmployees] = new HourlyEmployee(firstName, lastName, keyboard.nextFloat());
					break;
				case 2:
					System.out.println("What is the employee's yearly salary?");
					employees[numberOfEmployees] = new SalariedEmployee(firstName, lastName, keyboard.nextFloat());
					break;
				case 3:
					System.out.println("What is the employee's base pay?");
					float basePay = keyboard.nextFloat();
					employees[numberOfEmployees] = new CommissionedEmployee(firstName, lastName, basePay, commissionSchedule());
					break;
				default:
					System.out.println("That was not a valid choice please try again.");
					break;
			}
			numberOfEmployees++;
		}
		public static float[][] commissionSchedule(){
			Scanner keyboard = new Scanner(System.in);
			String input = "";
			int i = 0;
			float itemsSold;
			System.out.println("How long is the commission schedule?");
			int length = keyboard.nextInt();
			float[][] commissionSchedule = new float[2][length];
			while(true) {
				System.out.println("How many units must be sold for this commission?(to end commission "
						+ "schedule, enter \"q\")");
				input = keyboard.next();
				if(input.charAt(0) != 'q') {
					if(i >= length) {
						System.out.println("Going over the commission schedule length is not allowed, commission schedule is ending");
						return commissionSchedule;
					}
				itemsSold = Float.parseFloat(input);
				commissionSchedule[0][i] = itemsSold;
				}
				else
					return commissionSchedule;
				System.out.println("What is the value for each unit sold?");
				commissionSchedule[1][i] = keyboard.nextFloat();
				i++;
			}
	}
		public static int compareName(String name, String otherName) {
			int length;
			String n1 = name.toUpperCase();
			String n2 = otherName.toUpperCase();
			if(name.length() >= otherName.length())
				length = otherName.length();
			else
				length = name.length();
			for(int i =0; i < length; i++) {
				if(n1.charAt(i) < n2.charAt(i))
					return -1;
				else if(n1.charAt(i) > n2.charAt(i))
					return 1;
			}
			return 0;
		}
		public static void quickSortLastName(Employee[] a, int low, int high) {
			int first = low;
			int last = high;
			Employee temporary;
			while(low < high) {
				while(low < high && compareName(a[low].getLastName(), a[last].getLastName()) <= 0)//a[low] <= a[last]
					low++;
				while(high > low && compareName(a[high].getLastName(), a[last].getLastName()) >= 0)//a[high] >= a[last]
					high--;
				if(low < high) {
					temporary = a[low];
					a[low] = a[high];
					a[high] = temporary;
				}
				else {
					temporary = a[last];
					a[last] = a[low];
					a[low] = temporary;
				}
				quickSortLastName(a, first, low - 1);
				quickSortLastName(a, high + 1, last);
			}
		}
		public static void searchForEmployee(String lastName) {
			int max = 0;
			while(employees[max] != null)
				max++;
			max--;
			quickSortLastName(employees, 0, max);
			for(int i = 0; i <= max; i++) {
				System.out.printf("%s %s Employee %d\n", employees[i].getFirstName(), 
						employees[i].getLastName(), employees[i].getEmployeeNumber());
			}
		}
}