package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import algorithm.HoeffdingTreeAlgorithm;

import tree.HT;

import exception.OutOfRangeException;
import exception.WrongTypeException;
import io.DataValue;
import io.InputStream;

public class TestSplit {

	public static void main(String[] args) throws FileNotFoundException, IOException, WrongTypeException, OutOfRangeException {
		//Open input stream
		InputStream in = new InputStream();
		//Create tree
		HT tree = new HT(in.getAttributes(), in.getClassAtt());
		//Index of attribute for split
		int attrNum = 3;
		
		System.out.println("Attribute: " + in.getAttributes().get(attrNum).getLabel());
		
		//Get some samples and save stats in the tree
		for(int i = 0; i < 30; i++)
			tree.getNodeStatistics().saveSample(in.getTrainingSample());
		System.out.println("Samples collected: " + tree.getNodeStatistics().getTotalSamplesNumber());
		
		//Split on attribute
		HoeffdingTreeAlgorithm.splitNode(tree, in.getAttributes().get(attrNum), null);
		
		//Print some data
		for(HT child : tree.getChildren()){
			System.out.println();
			//Total samples for each child
			System.out.println("Total samples for this child: " + child.getNodeStatistics().getTotalSamplesNumber());
			//Class stats
			for(Entry<DataValue, Map<DataValue, Integer>> e : child.getNodeStatistics().getAVCStats(in.getAttributes().get(attrNum)).entrySet()){
				System.out.println("	Value of attr " + attrNum + ": " + e.getKey());
				for(Entry<DataValue, Integer> e1 : e.getValue().entrySet()){
					System.out.println("		Class and class counter: " + e1.getKey() + " - " + e1.getValue());
				}
			}
		}
	}
}
