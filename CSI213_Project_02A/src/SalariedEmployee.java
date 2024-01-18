/**
 * Class describing a salaried employee
 * @author Eli_Pardo
 *
 */
public class SalariedEmployee extends Employee{
	protected float salary; //Employee's salary
	/**
	 * Creates a SalariedEmployee object
	 * @param firstName
	 * @param lastName
	 * @param salary
	 */
	public SalariedEmployee(String firstName, String lastName, float salary) {
		super(firstName, lastName);
		this.salary = salary;
	}
	/**
	 * Accesses the salary for this employee
	 * @return salary
	 */
	public float getSalary() {
		return salary;
	}
	/**
	 * Mutates the salary for this employee
	 * @param salary
	 */
	public void setSalary(float salary) {
		this.salary = salary;
	}
	/**
	 * Tells what the pay check is for this employee
	 * @return payCheck
	 */
	float getPayCheck() {
		float payCheck = salary/26;//splits the salary into 26 paychecks given throughout the year
		return payCheck; 
	}
	/**
	 * @Override overrides the super class's toString() method
	 * @return employeeInfo
	 */
	public String toString() {
		String employeeInfo = "";
		//formats the String to the desired String
		employeeInfo = employeeInfo.format("Salaried: Base: $%,.0f; ", getSalary()) + super.toString();
		return employeeInfo;
	}
}
