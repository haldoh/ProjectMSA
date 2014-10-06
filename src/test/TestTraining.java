package test;

import java.io.FileNotFoundException;
import java.io.IOException;

import algorithm.Heuristics;
import algorithm.HoeffdingTreeAlgorithm;

import exception.WrongTypeException;
import io.InputStream;
import tree.HT;

public class TestTraining {

	public static void main(String[] args) throws FileNotFoundException, IOException, WrongTypeException {
		//Open input stream
		InputStream in = new InputStream();
		//Create tree
		HT tree = new HT(in.getAttributes(), in.getClassAtt());
		HoeffdingTreeAlgorithm.train(tree, in, 20000, 0.1, 0.2, 1, Heuristics.ENTROPY, 100000);
		printTree("\t", tree);
	}
	public static void printTree(String tab, HT tree){
		if(tree.isTerminal())
			System.out.println(tab + "leaf");
		else{
			System.out.println(tab + "node");
			for(HT child : tree.getChildren())
				printTree("\t" + tab, child);
		}
	}

}
