/**
 * Class describing a commissioned employee
 * @author Eli_Pardo
 *
 */
public class CommissionedEmployee extends SalariedEmployee{
	private float[][] commissionSchedule; //specifies the ratio of units sold/pay per unit
	private float unitsSold; //the units this employee has sold
	/**
	 * Accesses the commission schedule for this employee
	 * @return commissionSchedule
	 */
	public float[][] getCommissionSchedule() {
		return commissionSchedule;
	}
	/**
	 * Accesses the units that this employee has sold
	 * @return unitsSold
	 */
	public float getUnitsSold() {
		return unitsSold;
	}
	/**
	 * Mutates the commission schedule for this employee
	 * @param commission
	 */
	public void setCommissionSchedule(float[][] commission) {
		commissionSchedule = commission;
	}
	/**
	 * Mutates the units that this employee has sold
	 * @param units
	 */
	public void setUnitsSold(float units) {
		unitsSold = units; 
	}
	/**
	 * Gets a singular pay check for this employee
	 * @return payCheck
	 */
	public float getPayCheck() {
		int indexOfUnitsSold = 0; //will hold the index for the correct amount of units sold and how much money will be earned
		//Check where the units sold falls in the commission schedule and retain that index of the array
		for(int i = 0; i < commissionSchedule[0].length; i++) {
			if(unitsSold >= commissionSchedule[0][i])
				indexOfUnitsSold = i;
		}
		//calculate the pay check using the salary portion and the information from the commission schedule
		float payCheck = (commissionSchedule[1][indexOfUnitsSold] * unitsSold) + super.getPayCheck();
		return payCheck;
	}
	/**
	 * Creates a Commissioned Employee object
	 * @param firstName
	 * @param lastName
	 * @param salary
	 * @param commission
	 */
	public CommissionedEmployee(String firstName, String lastName, float salary, float[][] commission) {
		super(firstName, lastName, salary);
		commissionSchedule = commission;
	}
	/**
	 * @Override Overrides the toString() method
	 * @return employeeInfo
	 */
	public String toString() {
		String employeeInfo = super.toString();
		employeeInfo = employeeInfo.replace("Salaried", "Commission");
		return employeeInfo;
	}
}
