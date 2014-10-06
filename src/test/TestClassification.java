package test;

import java.io.FileNotFoundException;
import java.io.IOException;

import exception.OutOfRangeException;
import exception.WrongTypeException;
import io.DataValue;
import io.InputStream;
import io.Sample;
import tree.HT;
import algorithm.Heuristics;
import algorithm.HoeffdingTreeAlgorithm;

public class TestClassification {

	public static void main(String[] args) throws FileNotFoundException, IOException, WrongTypeException, OutOfRangeException {
		//Open input stream
		InputStream in = new InputStream();
		//Create tree
		HT tree = new HT(in.getAttributes(), in.getClassAtt());
		//Select heuristic
		String heuristic = Heuristics.ENTROPY;
		//Train tree
		HoeffdingTreeAlgorithm.train(tree, in, 1, 0.1, 0.075, 1, heuristic, 100000);
		
		//Try and classify some data
		double success = 0;
		double notClass = 0;
		int totTests = 10000;
		for(int i = 0; i < totTests; i++){
			System.out.println("Test step: " + i);
			//Get sample
			Sample s = in.getTestSample();
			//Classify it
			HT leaf = tree.classify(s);
			DataValue corrLabel = leaf.getClassLabel();
			//Check if correct
			if(corrLabel == null)
				notClass += 1;
			else{
				//s.print();
				//System.out.println("Predicted Class: " + corrLabel.toString());
				if(s.getValue(in.getClassAtt()).compareTo(corrLabel) == 0)
					success += 1;
			}
		}
		double succP = (success / totTests) * 100;
		double notP = (notClass / totTests) * 100;
		System.out.println("Success: " + succP + "%");
		System.out.println("Not Classified: " + notP + "%");
	}
}
