package test;

import io.DataValue;
import io.InputStream;
import io.Sample;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import algorithm.HoeffdingTreeAlgorithm;

import tree.HT;
import exception.OutOfRangeException;
import exception.WrongTypeException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException, WrongTypeException, OutOfRangeException{
		//Open data input stream
		InputStream dataIn = new InputStream();
		//Create tree
		HT tree = new HT(dataIn.getAttributes(), dataIn.getClassAtt());
		//Input from console
	    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	    //Initialize choice variable
		int choice = -1;
		while(choice != 0){
			//Print options menu
			printMainMenu();
			//Read input
			String temp = input.readLine();
			try{
				choice = Integer.parseInt(temp);
			}catch(NumberFormatException e){
				//If the input isn't an int, return message and go on
				choice = -2;
			}
			//Decide what to do
			switch(choice){
			//Exit
			case 0:
				System.out.println();
				System.out.println("Closing...");
				break;
			//Train on whole training set
			case 1:
				train(tree, dataIn, input);
				break;
			//Train on subset of training set
			case 2:
				trainLimit(tree, dataIn, input);
				break;
			//Test
			case 3:
				test(tree, dataIn, input);
				break;
			//Stats
			case 4:
				stats(tree);
				break;
			case -2:
				System.out.println("Choice entered was not a number");
				break;
			default:
				System.out.println("Warning - Option \"" + choice + "\" not contemplated.");
			}
		}
	}
	//Print the main options menu
	private static void printMainMenu(){
		System.out.println();
		System.out.println("Select an option:");
		System.out.println("1. Train on the whole training set");
		System.out.println("2. Train on a subset of the training set");
		System.out.println("3. Test trained tree on test set");
		System.out.println("4. Get tree statistics");
		System.out.println();
		System.out.println("0. Exit");
		System.out.println();
	}
	//Train on whole training set
	private static void train(HT tree, InputStream dataIn, BufferedReader input) throws IOException{
		//Define number of cycles
		int cycles = 1;
		System.out.println();
		System.out.println("How many cycles through the whole training set? [" + cycles + "]: ");
		//Read number of cycles
		String temp = input.readLine();
		try{
			cycles = Integer.parseInt(temp);
		}catch(NumberFormatException e){
			//If the input isn't an int, keep default value
		}
		//Get data from configuration file
		FileInputStream propFis = new FileInputStream("config/input.properties");
		Properties prop = new Properties();
		prop.load(propFis);
		double delta = getDelta(prop);
		double tau = getTau(prop);
		int grace = getGrace(prop);
		String heuristic = getHeuristic(prop);
		int maxNodes = getMaxNodes(prop);
		//Close properties file
		propFis.close();
		//Confirm data
		System.out.println();
		System.out.println("Starting training Hoeffding Tree on dataset located in " + dataIn.getPath());
		System.out.println("Heuristic selected: " + heuristic + " - delta: " + delta + " - tau: " + tau + " - grace: " + grace + " - training set cycles: " + cycles + " - max number of nodes: " + maxNodes);
		System.out.println("Continue (y/n)? [y]");
		double tErr = 0;
		temp = input.readLine();
		//If OK, run training algorithm
		if(temp.isEmpty() || temp.matches("y")){
			long start = System.currentTimeMillis();
			tErr = HoeffdingTreeAlgorithm.train(tree, dataIn, cycles, delta, tau, grace, heuristic, maxNodes);
			long end = System.currentTimeMillis();
			System.out.println();
			System.out.println("Elapsed time (ms): " + (end - start));
			System.out.println("Training error: " + tErr);
		}
	}
	//Train on subset of training set
	private static void trainLimit(HT tree, InputStream dataIn, BufferedReader input) throws IOException{
		//Define dimension of training subset
		int subset = 1000;
		System.out.println();
		System.out.println("Dimension of the training subset? [" + subset + "]: ");
		//Read number of cycles
		String temp = input.readLine();
		try{
			subset = Integer.parseInt(temp);
		}catch(NumberFormatException e){
			//If the input isn't an int, keep default value
		}
		//Get data from configuration file
		FileInputStream propFis = new FileInputStream("config/input.properties");
		Properties prop = new Properties();
		prop.load(propFis);
		double delta = getDelta(prop);
		double tau = getTau(prop);
		int grace = getGrace(prop); 
		String heuristic = getHeuristic(prop);
		int maxNodes = getMaxNodes(prop);
		//Confirm data
		System.out.println();
		System.out.println("Starting training Hoeffding Tree on dataset located in " + dataIn.getPath());
		System.out.println("Heuristic selected: " + heuristic + " - delta: " + delta + " - tau: " + tau + " - grace: " + grace + " - training subset dimension: " + subset + " - max number of nodes: " + maxNodes);
		System.out.println("Continue (y/n)? [y]");
		double tErr = 0;
		temp = input.readLine();
		//If OK, run training algorithm
		if(temp.isEmpty() || temp.matches("y")){
			long start = System.currentTimeMillis();
			tErr = HoeffdingTreeAlgorithm.trainLimit(tree, dataIn, subset, delta, tau, grace, heuristic, maxNodes);
			long end = System.currentTimeMillis();
			System.out.println();
			System.out.println("Elapsed time (ms): " + (end - start));
			System.out.println("Training error: " + tErr);
		}
	}
	//Test
	private static void test(HT tree, InputStream dataIn, BufferedReader input) throws IOException, WrongTypeException, OutOfRangeException{
		//Test dimension
		int totTests = 1000;
		System.out.println();
		System.out.println("Dimension of the test set? [" + totTests + "]: ");
		//Read number of cycles
		String temp = input.readLine();
		try{
			totTests = Integer.parseInt(temp);
		}catch(NumberFormatException e){
			//If the input isn't an int, keep default value
		}
		//Try and classify some data
		double success = 0;
		for(int i = 0; i < totTests; i++){
			//System.out.println("Test step: " + i + "/" + totTests);
			//Get sample
			Sample s = dataIn.getTestSample();
			if(s == null){
				dataIn.resetTestingInput();
				s = dataIn.getTestSample();
			}
			//Classify it
			HT leaf = tree.classify(s);
			DataValue corrLabel = leaf.getClassLabel();
			//Check if correct
			//s.print();
			//System.out.println("Predicted Class: " + corrLabel.toString());
			if(s.getValue(dataIn.getClassAtt()).compareTo(corrLabel) == 0)
				success += 1;
		}
		//Print results
		double error = 1 - (success / totTests);
		System.out.println("Test error: " + error);
	}
	//Tree stats
	private static void stats(HT tree){
		System.out.println();
		System.out.println("Number of internal nodes: " + tree.treeNodes());
		System.out.println("Number of leaves: " + tree.treeLeaves());
		System.out.println("Tree depth: " + tree.treeDepth());
	}
	private static double getDelta(Properties prop) throws IOException{
		String delta = prop.getProperty("delta");
		try{
			return Double.parseDouble(delta);
		}catch(NumberFormatException e){
			e.printStackTrace();
			return 0;
		}
	}
	private static double getTau(Properties prop) throws IOException{
		String tau = prop.getProperty("tau");
		try{
			return Double.parseDouble(tau);
		}catch(NumberFormatException e){
			e.printStackTrace();
			return 0;
		}
	}
	private static int getGrace(Properties prop) throws IOException{
		String grace = prop.getProperty("grace");
		try{
			return Integer.parseInt(grace);
		}catch(NumberFormatException e){
			e.printStackTrace();
			return 0;
		}
	}
	private static String getHeuristic(Properties prop) throws IOException{
		String heuristic = prop.getProperty("heuristic");
		return heuristic;
	}
	private static int getMaxNodes(Properties prop) throws IOException{
		String maxNodes = prop.getProperty("max_nodes");
		try{
			return Integer.parseInt(maxNodes);
		}catch(NumberFormatException e){
			e.printStackTrace();
			return 0;
		}
	}
}
