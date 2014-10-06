package io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import exception.OutOfRangeException;
import exception.WrongTypeException;

/**
 * 
 * @author wolf
 * <p>
 * This class represents a stream of input from a file
 * 
 * Using the settings in input.properties this class opens 3 files:
 * 
 * attributes.data
 * training.data
 * testing.data
 * 
 * The file attributes.data should contain a property named "attribute_number" giving the number of attributes of the dataset.
 * Then, for each attribute, there should be an entry named "attribute_name_n" (where n is a progressive number to differentiate the various attributes)
 * giving the label of the attribute, and an entry named "attribute_type_n" giving the type of attribute (categorical or numerical).
 * For numerical attributes, the entries "attribute_min_range_n" and "attribute_max_range_n" give the range of the attribute (use "infinite" in one or both to leave no boundary).
 * For categorical attributes, the entry "attribute_values_n" gives the list of possible values (in the form {val1, val2, ..., valn}).
 * 
 * The files training.data and testing.data should contain one sample per row, with data separated using the separator specified in the file input.properties.
 * 
 * The last attribute in the list will always be considered the class to be used in the classification problem, and should always be "categorical"
 *
 */
public class InputStream {
	private String path;
	private BufferedReader training;
	private int trainingDim;
	private BufferedReader testing;
	private int testingDim;
	private List<Attribute> attributes;
	private Attribute classAtt;
	private String separator;
	private String unknown;
	/**
	 * 
	 * @throws FileNotFoundException If properties file or one of the input files is not found
	 * @throws IOExcpetion If properties file can't be loaded
	 */
	public InputStream() throws FileNotFoundException, IOException, WrongTypeException{
		//Open properties file for input informations
		FileInputStream propFis = new FileInputStream("config/input.properties");
		Properties prop = new Properties();
		prop.load(propFis);
		//Get dataset path from properties
		this.path = prop.getProperty("input");
		//Check format
		if(!this.path.endsWith("/"))
			this.path += "/";
		//Open attributes list file
		FileInputStream attFis = new FileInputStream(this.path + "attributes.data");
		Properties attProp = new Properties();
		attProp.load(attFis);
		//Save unknown symbol from properties file
		this.unknown = prop.getProperty("unknown", "?");
		//Build list of attributes
		this.buildAttributesList(attProp);
		//Close attributes file
		attFis.close();
		//Save separators from porperties file
		this.separator = prop.getProperty("separator", " ");
		//Close properties file
		propFis.close();
		//Save dimensions of training and testing files
		LineNumberReader  lnr1 = new LineNumberReader(new FileReader(this.path + "training.data"));
		lnr1.skip(Long.MAX_VALUE);
		this.trainingDim = lnr1.getLineNumber() + 1;
		lnr1.close();
		LineNumberReader  lnr2 = new LineNumberReader(new FileReader(this.path + "testing.data"));
		lnr2.skip(Long.MAX_VALUE);
		this.testingDim = lnr2.getLineNumber() + 1;
		lnr2.close();
		//Open streams to training and test files
		this.training = new BufferedReader(new FileReader(this.path + "training.data"));
		this.testing = new BufferedReader(new FileReader(this.path + "testing.data"));
	}
	public InputStream(String path, String unknown, String separator) throws FileNotFoundException, IOException, WrongTypeException{
		//Set dataset path
		this.path = path;
		//Check format
		if(!this.path.endsWith("/"))
			this.path += "/";
		//Open attributes list file
		FileInputStream attFis = new FileInputStream(this.path + "attributes.data");
		Properties attProp = new Properties();
		attProp.load(attFis);
		//Set unknown symbol
		this.unknown = unknown;
		//Build list of attributes
		this.buildAttributesList(attProp);
		//Close attributes file
		attFis.close();
		//Set separators
		this.separator = separator;
		//Save dimensions of training and testing files
		LineNumberReader  lnr1 = new LineNumberReader(new FileReader(this.path + "training.data"));
		lnr1.skip(Long.MAX_VALUE);
		this.trainingDim = lnr1.getLineNumber() + 1;
		lnr1.close();
		LineNumberReader  lnr2 = new LineNumberReader(new FileReader(this.path + "testing.data"));
		lnr2.skip(Long.MAX_VALUE);
		this.testingDim = lnr2.getLineNumber() + 1;
		lnr2.close();
		//Open streams to training and test files
		this.training = new BufferedReader(new FileReader(this.path + "training.data"));
		this.testing = new BufferedReader(new FileReader(this.path + "testing.data"));
	}
	/**
	 * Build the list of attributes for the dataset
	 * @param attProp Properties object linked to the file containing the description of the attributes of the input dataset
	 * @throws WrongTypeException 
	 */
	private void buildAttributesList(Properties attProp) throws WrongTypeException{
		//Get the number of attributes from file
		int attNum = Integer.valueOf(attProp.getProperty("attribute_number"));
		//Initialize attributes list
		this.attributes = new ArrayList<Attribute>(attNum);
		//Acquire attribute data
		for(int i = 0; i < attNum-1; i++){
			boolean isNum = attProp.getProperty("attribute_type_" + i).equals("numerical");
			//Create attribute
			Attribute att = new Attribute(attProp.getProperty("attribute_name_" + i), isNum);
			//If attribute is categorical, get list of possible values
			if(isNum){
				//Save attribute range
				String minRange = attProp.getProperty("attribute_min_range_" + i);
				String maxRange = attProp.getProperty("attribute_max_range_" + i);
				if(!minRange.equals("infinite"))
					att.setMinRange(Double.valueOf(minRange));
				if(!maxRange.equals("infinite"))
					att.setMaxRange(Double.valueOf(maxRange));
			}else{
				//Get values, remove brackets, split on commas
				String[] values = attProp.getProperty("attribute_values_" + i).replace("{", "").replace("}", "").split(",");
				//Save list of possible values
				for(String val : values)
					att.addValue(val.trim());
				att.addValue(this.unknown);
			}
			//Add attribute to the list
			this.attributes.add(att);
		}
		//Acquire class data
		boolean isNum = attProp.getProperty("attribute_type_" + (attNum - 1)).equals("numerical");
		//Create class attribute
		this.classAtt = new Attribute(attProp.getProperty("attribute_name_" + (attNum - 1)), isNum);
		//If class is categorical, get list of possible values
		if(isNum){
			throw new WrongTypeException("Class attribute should be categorical.");
		}else{
			//Get values, remove brackets, split on commas
			String[] values = attProp.getProperty("attribute_values_" + (attNum - 1)).replace("{", "").replace("}", "").split(",");
			//Save list of possible values
			for(String val : values)
				this.classAtt.addValue(val.trim());
		}
	}
	public Sample getTrainingSample() throws IOException, WrongTypeException, OutOfRangeException{
		return this.getSample(this.training);
	}
	public Sample getTestSample() throws IOException, WrongTypeException, OutOfRangeException{
		return this.getSample(this.testing);
	}
	private Sample getSample(BufferedReader input) throws IOException, WrongTypeException, OutOfRangeException{
		//New sample
		Sample result = new Sample();
		//Get data
		String line = input.readLine();
		//If there are no more rows, return null
		if(line == null)
			return null;
		//Split data
		String[] sampleValues = line.trim().split(this.separator);
		for(int i = 0; i < sampleValues.length-1; i++){
			//Check if this attribute should be numerical or categorical, then add values to sample
			boolean isNum = this.attributes.get(i).isNumerical();
			if(isNum)
				result.addValues(this.attributes.get(i), new DataValue(isNum, Double.valueOf(sampleValues[i].trim())));
			else
				result.addValues(this.attributes.get(i), new DataValue(isNum, sampleValues[i].trim()));
		}
		//Save class attribute
		result.addValues(this.classAtt, new DataValue(false, sampleValues[sampleValues.length-1].trim()));
		//Return sample
		return result;
	}
	/**
	 * Close and reopen training file, resetting the pointer to the beginning
	 */
	public void resetTrainingInput(){
		try {
			this.training = new BufferedReader(new FileReader(this.path + "training.data"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Close and reopen testing file, resetting the pointer to the beginning
	 */
	public void resetTestingInput(){
		try {
			this.testing = new BufferedReader(new FileReader(this.path + "testing.data"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Get the attribute list
	 * @return The attribute list for this dataset
	 */
	public List<Attribute> getAttributes(){
		return this.attributes;
	}
	/**
	 * Close input file for testing
	 * @throws IOException
	 */
	public void closeTesting() throws IOException{
		this.testing.close();
	}
	/**
	 * Close input file for training
	 * @throws IOException
	 */
	public void closeTraining() throws IOException{
		this.training.close();
	}
	/**
	 * Getter method for the class attribute
	 * @return The class attribute for this dataset
	 */
	public Attribute getClassAtt(){
		return this.classAtt;
	}
	/**
	 * 
	 * @return The separator used to read the input
	 */
	public String getSeparator(){
		return this.separator;
	}
	/**
	 * 
	 * @return The path of the current dataset
	 */
	public String getPath(){
		return this.path;
	}
	/**
	 * 
	 * @return Number of lines in training set
	 */
	public int getTrainingDim(){
		return this.trainingDim;
	}
	/**
	 * 
	 * @return Number of lines in testing set
	 */
	public int getTestingDim(){
		return this.testingDim;
	}
}
