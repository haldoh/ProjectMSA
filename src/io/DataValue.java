package io;

import exception.WrongTypeException;

public class DataValue implements Comparable<DataValue>{
	private boolean isNumerical;
	private String strValue;
	private double dValue;
	
	/**
	 * Constructor for String type
	 * @param isNum True if the value is numerical
	 * @param s The string value to be stored
	 * @throws WrongTypeException If isNum is true and the value should be a number, use the other constructor
	 */
	public DataValue(boolean isNum, String s) throws WrongTypeException{
		this.isNumerical = isNum;
		if(!this.isNumerical)
			this.strValue = s;
		else
			throw new WrongTypeException();
	}
	/**
	 * Constructor for numerical type
	 * @param isNum True if the value is numerical
	 * @param d The numerical value to be stored
	 * @throws WrongTypeException If isNum is false and the value should be a string, use the other constructor
	 */
	public DataValue(boolean isNum, double d) throws WrongTypeException{
		this.isNumerical = isNum;
		if(this.isNumerical)
			this.dValue = d;
		else
			throw new WrongTypeException();
	}
	/**
	 * 
	 * @return True if data is numerical
	 */
	public boolean isNumerical(){
		return this.isNumerical;
	}
	/**
	 * Get value
	 * @return The value of the data
	 * @throws WrongTypeException If the data is numerical, use the other getter method
	 */
	public String getStrValue() throws WrongTypeException{
		if(!this.isNumerical())
			return this.strValue;
		else
			throw new WrongTypeException();
	}
	/**
	 * Get value
	 * @return The value of the data
	 * @throws WrongTypeException If the data is not numerical, use the other getter method
	 */
	public double getNumValue() throws WrongTypeException{
		if(this.isNumerical())
			return this.dValue;
		else
			throw new WrongTypeException();
	}
	/**
	 * Set value
	 * @param newS The new value
	 * @throws WrongTypeException If the data is numerical, use the other setter method
	 */
	public void setValue(String newS) throws WrongTypeException{
		if(!this.isNumerical())
			this.strValue = newS;
		else
			throw new WrongTypeException();
	}
	/**
	 * Set value
	 * @param newD The new value
	 * @throws WrongTypeException If the data is not numerical, use the other setter method
	 */
	public void setValue(double newD) throws WrongTypeException{
		if(this.isNumerical())
			this.dValue = newD;
		else
			throw new WrongTypeException();
	}
	@Override
	public String toString(){
		if(this.isNumerical())
			try {
				return Double.toString(this.getNumValue());
			} catch (WrongTypeException e) {/*should never happen*/}
		else
			try {
				return this.getStrValue();
			} catch (WrongTypeException e) {/*should never happen*/}
		return null;
	}
	@Override
	public int compareTo(DataValue arg){
		//Must throw NullPointerException if argument is null
		if(arg == null)
			throw new NullPointerException("Null argument");
		//Both objects must be of the same type to be comparable
		if(this.isNumerical() != arg.isNumerical())
			throw new ClassCastException("Incompatible types.");
		//If values are numerical, return difference
		if(this.isNumerical())
			return Double.compare(this.uncheckedGetNumValue(), arg.uncheckedGetNumValue());
		else
			return this.uncheckedGetStrValue().compareTo(arg.uncheckedGetStrValue());
	}
	private double uncheckedGetNumValue(){
		return this.dValue;
	}
	private String uncheckedGetStrValue(){
		return this.strValue;
	}
}
