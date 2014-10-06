package test;

import io.DataValue;
import io.InputStream;
import io.Sample;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import tree.HT;
import algorithm.HoeffdingTreeAlgorithm;
import exception.OutOfRangeException;
import exception.WrongTypeException;

public class MassiveDataTest {
	private static final int maxCycles = 3;
	private static final int grace = 1;
	private static final int maxNodes = 10000;

	public static void main(String[] args) throws FileNotFoundException, IOException, WrongTypeException, OutOfRangeException {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String formattedDate = sdf.format(date);
		String outFile = formattedDate + "-mass-test-" + grace + "-" + maxNodes + ".txt";
		/*
		 * Adult
		 */
		String path = "dataset/adult/";
		double delta = 0.0001;
		double tau = 0.3;
		int deltaTauRange = 3;
		massTest(outFile, path, "entropy", delta, tau, grace, maxNodes, deltaTauRange);
		massTest(outFile, path, "square", delta, tau, grace, maxNodes, deltaTauRange);
		/*
		 * Poker
		 */
		path = "dataset/poker/";
		massTest(outFile, path, "entropy", delta, tau, grace, maxNodes, deltaTauRange);
		massTest(outFile, path, "square", delta, tau, grace, maxNodes, deltaTauRange);
		/*
		 * Shuttle
		 */
		path = "dataset/shuttle/";
		massTest(outFile, path, "entropy", delta, tau, grace, maxNodes, deltaTauRange);
		massTest(outFile, path, "square", delta, tau, grace, maxNodes, deltaTauRange);
	}
	private static void massTest(String outFile, String path, String heuristic, double delta, double tau, int grace, int maxNodes, int deltaTauRange) throws FileNotFoundException, IOException, WrongTypeException, OutOfRangeException{
		for(int i = deltaTauRange; i > 0; i--){
			//Adjusted parameters
			double adjDelta = delta / Math.pow(10, i);
			double adjTau = tau - (0.05 * i);
			runTest(outFile, path, heuristic, adjDelta, adjTau, grace, maxNodes);
		}
		runTest(outFile, path, heuristic, delta, tau, grace, maxNodes);
		for(int i = 1; i <= deltaTauRange; i++){
			//Adjusted parameters
			double adjDelta = delta * Math.pow(10, i);
			double adjTau = tau + (0.05 * i);
			runTest(outFile, path, heuristic, adjDelta, adjTau, grace, maxNodes);
		}
		
	}
	private static void runTest(String outFile, String path, String heuristic, double delta, double tau, int grace, int maxNodes) throws FileNotFoundException, IOException, WrongTypeException, OutOfRangeException{
		//Open data input stream
		InputStream dataIn = new InputStream(path, "?", ",");
		//Create tree
		HT tree = new HT(dataIn.getAttributes(), dataIn.getClassAtt());
		//Define number of cycles
		for(int cycles = 1; cycles <= maxCycles; cycles++){
			int samples = cycles * dataIn.getTrainingDim();
			double trainErr = 0;
			//If OK, run training algorithm
			long start = System.currentTimeMillis();
			trainErr = HoeffdingTreeAlgorithm.train(tree, dataIn, cycles, delta, tau, grace, heuristic, maxNodes);
			long time = System.currentTimeMillis() - start;
			//Test dimension
			int totTests = dataIn.getTestingDim();
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
			double testErr = 1 - (success / totTests);
			writeResults(outFile, path, heuristic, delta, tau, samples, time, trainErr, testErr, tree.treeNodes(), tree.treeLeaves(), tree.treeDepth());
		}
	}
	private static void writeResults(String outFile, String path, String heuristic, double delta, double tau, int samples, long time, double trainErr, double testErr, int nodes, int leafs, int depth){
		Writer writer = null;
		String output = path + "," + heuristic + "," + delta + "," + tau + "," + samples + "," + time + "," + trainErr + "," + testErr + "," + nodes + "," + leafs + "," + depth + "\n";
		try {
		    writer = new BufferedWriter(new FileWriter(outFile, true));
		    writer.write(output);
		} catch (IOException ex){
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
}
