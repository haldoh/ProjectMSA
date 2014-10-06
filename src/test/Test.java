package test;

import io.InputStream;

import java.io.FileNotFoundException;
import java.io.IOException;

import exception.OutOfRangeException;
import exception.WrongTypeException;


public class Test {

	public static void main(String[] args) throws WrongTypeException, OutOfRangeException, FileNotFoundException, IOException {
		//Open input stream
		InputStream in = new InputStream();
		System.out.println("O" + in.getSeparator() + "O");
		//Create statistics container
		/*NodeStatistics ns = new NodeStatistics(in.getAttributes(), in.getClassAtt());
		//Fill statistics
		for(int i = 0; i < 20; i++)
			ns.saveSample(in.getTrainingSample());
		
		for(Attribute a : in.getAttributes()){
			System.out.println(a.getLabel());
			for(DataValue cv : ns.getACVStats(a).keySet()){
				System.out.println("	" + cv);
				for(Entry<DataValue, Integer> e : ns.getACVStats(a).get(cv).entrySet()){
					System.out.println("		" + e.getKey().toString() + " - " + e.getValue());
				}
			}
		}
		System.out.println("Number of processed samples: " + ns.getTotalSamplesNumber());
		System.out.println("Number of processed samples per class:");
		for(DataValue cv : ns.getClassAttribute().getValues()){
			System.out.print("	" + cv + ": ");
			System.out.println(ns.getClassOccurrence(cv));
		}
		System.out.println("Node entropy: " + Heuristics.entropy(ns.getClassOccurrence(), ns.getTotalSamplesNumber()));*/
	}
}
