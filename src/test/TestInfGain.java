package test;

import io.InputStream;

import java.io.FileNotFoundException;
import java.io.IOException;

import tree.HT;
import algorithm.Heuristics;
import exception.OutOfRangeException;
import exception.WrongTypeException;

public class TestInfGain extends Heuristics{

	/**
	 * @param args
	 * @throws WrongTypeException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws OutOfRangeException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, WrongTypeException, OutOfRangeException {
		//Open input stream
		InputStream in = new InputStream();
		//Create tree
		HT tree = new HT(in.getAttributes(), in.getClassAtt());
		//Select type of heuristic
		String heuristic = Heuristics.SQUARE;
		//Index of attribute for split
		int attrNum = 2;
		
		System.out.println("Attribute: " + in.getAttributes().get(attrNum).getLabel());
		
		//Get some samples and save stats in the tree
		for(int i = 0; i < 30; i++)
			tree.getNodeStatistics().saveSample(in.getTrainingSample());
		System.out.println("Samples collected: " + tree.getNodeStatistics().getTotalSamplesNumber());
		
		//Original entropy
		System.out.println("Orginal square: " + Heuristics.nodeSquare(tree));
		
		//Information gain after split on attribute
		System.out.println("Information gain: " + Heuristics.informationGain(tree, in.getAttributes().get(attrNum), null, heuristic));
		
		//Hoeffding Bound
		//System.out.println("Hoeffding Bound: " + Heuristics.HoeffdingBound(Math.log(in.getClassAtt().getValues().size())/Math.log(2), 0.0001, 3000));
	}

}
