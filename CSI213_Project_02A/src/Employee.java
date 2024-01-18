/**
 * Class describing a generic employee
 * @author Eli_Pardo
 *
 */
public abstract class Employee {
	private String firstName; //the employee's first name
	private String lastName; //the employee's last name
	public static int employeeCount = 0; //the total number of employees so far
	private int employeeNumber; //the employee's ID number
	/**
	 * Creates an Employee object
	 * @param fN
	 * @param lN
	 */
	public Employee(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		employeeCount++;
		employeeNumber = employeeCount;
	}
	/**
	 * Accesses the current employee's ID number
	 * @return employeeNumber
	 */
	public int getEmployeeNumber() {
		return employeeNumber;
	}
	/**
	 * Accesses the total number of employees
	 * @return employeeCount
	 */
	public static int getEmployeeCount() {
		return employeeCount;
	}
	/**
	 * Accesses this employee's last name
	 * @return lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * Accesses this employee's first name
	 * @return firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * Mutates this employee's last name
	 * @param lN
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * Mutates this employee's first name
	 * @param fN
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return employeeInfo
	 */
	@Override
	public String toString() {
		String employeeInfo = "Id:" + getEmployeeNumber() + " - " + getLastName() + ", " + getFirstName();
		return employeeInfo;
	}
	/**
	 * Will be used to calculate the pay check for every individual type of employee
	 */
	abstract float getPayCheck();
}
