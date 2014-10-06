package io;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import exception.OutOfRangeException;
import exception.WrongTypeException;

public class Sample {
	private Map<Attribute, DataValue> values;
	
	public Sample(){
		this.values = new HashMap<Attribute, DataValue>();
	}
	/**
	 * Get the values of the sample
	 * @return The map of attributes and values for this sample
	 */
	public Set<Entry<Attribute, DataValue>> getValues(){
		return this.values.entrySet();
	}
	/**
	 * Add a value to the sample
	 * @param a The attribute to be added to the sample
	 * @param d The value to be added to the sample
	 * @throws WrongTypeException If there is a mismatch between the attribute and the value types
	 * @throws OutOfRangeException If the value is out of the range of the attribute
	 */
	public void addValues(Attribute a, DataValue d) throws WrongTypeException, OutOfRangeException{
		//Check if types match
		if(a.isNumerical() != d.isNumerical())
				throw new WrongTypeException("Value " + d.toString() + " is of the wrong type.");
		//Check if within ranges
		if(d.isNumerical() && (d.compareTo(a.getMinRange()) < 0 || d.compareTo(a.getMaxRange()) > 0))
			throw new OutOfRangeException("Value " + d.toString() + " not within attribute range.");
		//If everything ok, save data
		this.values.put(a, d);
	}
	/**
	 * Get the value of the attribute of the sample
	 * @param a The attribute
	 * @return The value corresponding to the given attribute for this sample
	 */
	public DataValue getValue(Attribute a){
		return this.values.get(a);
	}
	public void print(){
		System.out.println();
		for(Entry<Attribute, DataValue> e : this.values.entrySet())
			System.out.println(e.getKey().getLabel() + " - " + e.getValue().toString());
	}
}
