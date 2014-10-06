package tree;

import io.Attribute;
import io.DataValue;
import io.Sample;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NodeStatistics {
	//Data ordered by attribute, class, value
	private Map<Attribute, Map<DataValue, Map<DataValue, Integer>>> dataACV;
	//Data ordered by attribute, value, class
	private Map<Attribute, Map<DataValue, Map<DataValue, Integer>>> dataAVC;
	private Map<DataValue, Integer> classOccurrence;
	private DataValue mostFrequentClass;
	private int mostFrequentClassOccurrence;
	private boolean diffClasses;
	private Attribute classAtt;
	private int totSampleNum;
	/**
	 * The constructor initializes the data structures used to maintain statistics about the samples that passed through a leaf.
	 * To do so, it needs both the set of attributes of the dataset, and the set of possible values of the attibute used to classify the samples.
	 * 
	 * @param attributes A set representing all the attributes of the dataset
	 * @param cAtt The attribute used as class
	 */
	public NodeStatistics(List<Attribute> attributes, Attribute cAtt){
		//Initialize main Map
		this.dataACV = new TreeMap<Attribute, Map<DataValue, Map<DataValue, Integer>>>();
		this.dataAVC = new TreeMap<Attribute, Map<DataValue, Map<DataValue, Integer>>>();
		//Initialize submaps for each attribute
		for(Attribute a : attributes){
			dataACV.put(a, new TreeMap<DataValue, Map<DataValue, Integer>>());
			dataAVC.put(a, new TreeMap<DataValue, Map<DataValue, Integer>>());
			//Initialize lists of values for each possible class value
			for(DataValue cv : cAtt.getValues()){
				dataACV.get(a).put(cv, new TreeMap<DataValue, Integer>());
			}
		}
		//Initialize counters for class occurrence
		this.classOccurrence = new TreeMap<DataValue, Integer>();
		for(DataValue cv : cAtt.getValues()){
			this.classOccurrence.put(cv, Integer.valueOf(0));
		}
		this.mostFrequentClass = null;
		this.mostFrequentClassOccurrence = 0;
		this.diffClasses = false;
		//Reference to class attribute
		this.classAtt = cAtt;
		//Initialize total number of samples to zero
		this.totSampleNum = 0;
	}
	/**
	 * Save data about the given sample
	 * @param s A sample of the dataset
	 */
	public void saveSample(Sample s){
		//Temp reference to class value
		DataValue classVal = s.getValue(this.classAtt);
		//Increment sample number counter
		this.incrementTotalSamplesNumber();
		//Increment correct class counter
		this.incrementClassOccurrence(s.getValue(this.classAtt));
		//Save attributes values
		for(Attribute a : this.dataACV.keySet()){
			//Get temp reference to value of the current attribute
			DataValue val = s.getValue(a);
			//Save value
			this.addValue(a, val, classVal);
		}
	}
	public void addValue(Attribute a, DataValue val, DataValue classVal){
		//Get temp reference to correct map of value-counters
		Map<DataValue, Integer> listACV = this.dataACV.get(a).get(classVal);
		//Check if the value was already used
		if(listACV.containsKey(val)){
			//If the map already contains the key, increment counter
			listACV.put(val, (listACV.get(val).intValue() + 1));
		}else{
			//If the map does not contain the key, set to one for this value (this is the first occurrence of the value)
			listACV.put(val, Integer.valueOf(1));
		}
		//Get temp reference to correct map of value-class-counters
		Map<DataValue, Map<DataValue, Integer>> listAVC = this.dataAVC.get(a);
		//Check if the value was already used
		if(listAVC.containsKey(val)){
			//Check if this class was already used with this value
			if(listAVC.get(val).containsKey(classVal)){
				//Increment counter
				listAVC.get(val).put(classVal, (listAVC.get(val).get(classVal).intValue() + 1));
			}else{
				//Create new entry for this class value
				listAVC.get(val).put(classVal, Integer.valueOf(1));
			}
		}else{
			//Create new map for this value, with the correct entry
			listAVC.put(val, new TreeMap<DataValue,Integer>());
			listAVC.get(val).put(classVal, Integer.valueOf(1));
		}
	}
	/**
	 * 
	 * @param a An attribute of the dataset
	 * @return A map containing the statistics for the attribute, ordered by class value and attribute value
	 */
	public Map<DataValue, Map<DataValue, Integer>> getACVStats(Attribute a){
		return this.dataACV.get(a);
	}
	/**
	 * 
	 * @param a An attribute of the dataset
	 * @return A map containing the statistics for the attribute, ordered by attribute value and class value
	 */
	public Map<DataValue, Map<DataValue, Integer>> getAVCStats(Attribute a){
		return this.dataAVC.get(a);
	}
	/**
	 * 
	 * @param a An attribute of the dataset
	 * @param cv A possible value for the class attribute
	 * @return A map containing the statistics for the given attribute and class 
	 */
	public Map<DataValue, Integer> getAttributeStatsByClass(Attribute a, DataValue cv){
		return this.dataACV.get(a).get(cv);
	}
	/**
	 * 
	 * @return The number of samples that got classified to this node
	 */
	public int getTotalSamplesNumber(){
		return this.totSampleNum;
	}
	/**
	 * Increment the samples counter
	 */
	public void incrementTotalSamplesNumber(){
		this.incrementTotalSamplesNumber(1);
	}
	/**
	 * Increment the samples counter
	 * @param n The number by which the counter should be incremented
	 */
	public void incrementTotalSamplesNumber(int n){
		this.totSampleNum += n;
	}
	/**
	 * 
	 * @return The attribute used to classify data
	 */
	public Attribute getClassAttribute(){
		return this.classAtt;
	}
	/**
	 * 
	 * @param value A possible value of the class attribute
	 * @return The number of samples classified as the parameter class
	 */
	public int getClassOccurrence(DataValue value){
		return this.classOccurrence.get(value).intValue();
	}
	/**
	 * Increment the class counter associated with the given value
	 * @param classVal The value for which the counter should be incremented
	 */
	public void incrementClassOccurrence(DataValue classVal){
		this.incrementClassOccurrence(classVal, 1);
	}
	/**
	 * Increment the class counter associated with the given value
	 * @param classVal The value for which the counter should be incremented
	 * @param n The number by which the counter should be incremented
	 */
	public void incrementClassOccurrence(DataValue classVal, int n){
		//Check if different classes where observed at this node
		if(this.mostFrequentClass != null && this.mostFrequentClass.compareTo(classVal) != 0)
			this.diffClasses = true;
		//Compute new counter
		int counter = this.classOccurrence.get(classVal).intValue() + n;
		//Save new counter
		this.classOccurrence.put(classVal, Integer.valueOf(counter));
		//Check if this class is the new most frequent class at this node
		if(counter > this.mostFrequentClassOccurrence){
			this.mostFrequentClass = classVal;
			this.mostFrequentClassOccurrence = counter;
		}
	}
	public DataValue getMostFrequentClass(){
		return this.mostFrequentClass;
	}
	public int getMostFrequentClassOccurrence(){
		return this.mostFrequentClassOccurrence;
	}
	public boolean hasDiffClasses(){
		return this.diffClasses;
	}
	/**
	 * 
	 * @return A map containing the number of samples for each possible value of the class attribute
	 */
	public Map<DataValue, Integer> getClassOccurrence(){
		return this.classOccurrence;
	}
	
}
