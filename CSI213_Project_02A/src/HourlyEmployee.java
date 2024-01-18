/**
 * Class describing an hourly employee
 * @author Eli_Pardo
 *
 */
public class HourlyEmployee extends Employee{
	private float hoursWorked; //hours the employee worked
	private float payRate; //hourly pay
	/**
	 * Creates an HourlyEmployee object
	 * @param firstName
	 * @param lastName
	 * @param pay
	 */
	public HourlyEmployee(String firstName, String lastName, float pay) {
		super(firstName, lastName);
		payRate = pay;
	}
	/**
	 * Accesses the hours this employee worked
	 * @return hoursWorked
	 */
	public float getHoursWorked() {
		return hoursWorked;
	}
	/**
	 * Mutates the hours this employee worked
	 * @param hours
	 */
	public void setHoursWorked(float hours) {
		hoursWorked = hours;
	}
	/**
	 * Accesses the hourly wage for this employee
	 * @return payRate
	 */
	public float getPayRate() {
		return payRate;
	}
	/**
	 * Mutates the hourly wage for this employee
	 * @param pay
	 */
	public void setPayRate(float pay) {
		payRate = pay;
	}
	/**
	 * Returns a single pay check for this employee
	 * @return payCheck
	 */
	float getPayCheck() {
		float payCheck = payRate * hoursWorked;
		return payCheck;
	}
	/**
	 * @Override Overrides the toString() method
	 * @return employeeInfo
	 */
	public String toString() {
		String employeeInfo = "";
		employeeInfo = employeeInfo.format("Hourly: $%.2f; %s", getPayRate(), super.toString());
		return employeeInfo;
	}
}
