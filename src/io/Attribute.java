package io;

import java.util.Set;
import java.util.TreeSet;

import exception.WrongTypeException;

public class Attribute implements Comparable<Attribute>{
	private String label;
	private boolean isNumerical;
	private DataValue maxRange;
	private DataValue minRange;
	private Set<DataValue> values;
	/**
	 * Public constructor for the class
	 * @param lab Label of the attribute
	 * @param isNum True if this is a numeric attribute
	 */
	public Attribute(String lab, boolean isNum){
		this.label = lab;
		this.isNumerical = isNum;
		//Init defaults
		try {
			this.minRange = new DataValue(true, Double.MAX_VALUE * -1);
			this.maxRange = new DataValue(true, Double.MAX_VALUE);
		} catch (WrongTypeException e) {
			//Should never happen
			e.printStackTrace();
		}
		this.values = new TreeSet<DataValue>();
	}
	/**
	 * Add a new possible value for the attribute
	 * @param value The new possible value
	 * @throws WrongTypeException 
	 */
	public void addValue(String value) throws WrongTypeException{
		this.values.add(new DataValue(false, value));
	}
	/**
	 * Returns the list of possible values for the attribute
	 * @return The list of possible values, or null if this is a numerical attribute
	 */
	public Set<DataValue> getValues(){
		if(this.isNumerical())
			return null;
		return this.values;
	}
	/**
	 * Set the minimum range for the attribute (if numerical)
	 * @param minR The new minimum range
	 */
	public void setMinRange(double minR){
		if(this.isNumerical())
			try {
				this.minRange.setValue(minR);
			} catch (WrongTypeException e) {
				//Should never happen
				e.printStackTrace();
			}
		else
			System.out.println("The attribute " + this.label + " is not numerical.");
	}
	/**
	 * Get the minimum range for the attribute (if not numerical, the default will be Double.MIN_VALUE)
	 */
	public DataValue getMinRange(){
		return this.minRange;
	}
	/**
	 * Set the maximum range for the attribute (if numerical)
	 * @param maxR The new maximum range
	 */
	public void setMaxRange(double maxR){
		if(this.isNumerical())
			try {
				this.maxRange.setValue(maxR);
			} catch (WrongTypeException e) {
				//Should never happen
				e.printStackTrace();
			}
		else
			System.out.println("The attribute " + this.label + " is not numerical.");
	}
	/**
	 * Get the maximum range for the attribute (if not numerical, the default will be Double.MAX_VALUE)
	 */
	public DataValue getMaxRange(){
		return this.maxRange;
	}
	/**
	 * 
	 * @return True if the attribute is numerical
	 */
	public boolean isNumerical(){
		return this.isNumerical;
	}
	/**
	 * Getter for label
	 * @return The label of this attribute
	 */
	public String getLabel(){
		return this.label;
	}
	@Override
	public int compareTo(Attribute att) {
		return this.getLabel().compareTo(att.getLabel());
	}
}
