package algorithm;

import io.Attribute;
import io.DataValue;

import java.util.Map;

import tree.HT;

public class Heuristics {
	public static final String ENTROPY = "entropy";
	public static final String SQUARE = "square";
	
	public static double informationGain(HT node, Attribute a, DataValue[] thresholds, String heuristic){
		switch(heuristic){
		case ENTROPY:
			return informationGainEntropy(node, a, thresholds);
		case SQUARE:
			return informationGainSquare(node, a, thresholds);
		default:
			return 0;
		}
	}
	/*
	 * Methods for square
	 */
	protected static double nodeSquare(HT node){
		return square(node.getNodeStatistics().getMostFrequentClassOccurrence(), node.getNodeStatistics().getTotalSamplesNumber());
	}
	protected static double square(double mostFrequentClassOccurrence, double totNum){
		double result = 0;
		if(totNum != 0)
			result = Math.sqrt((mostFrequentClassOccurrence / totNum) * (1 - (mostFrequentClassOccurrence / totNum)));
		return result;
	}
	
	protected static double informationGainSquare(HT node, Attribute a, DataValue[] thresholds){
		double result = 0;
		//This should only be called on a leaf
		if(!node.isTerminal())
			return result;
		//If attribute is null, information gain for not splitting is zero
		if(a == null)
			return result;
		//Save data to compute statistics later
		result = nodeSquare(node);
		double origSamples = node.getNodeStatistics().getTotalSamplesNumber();
		//Split the node on attribute a
		HoeffdingTreeAlgorithm.splitNode(node, a, thresholds);
		//Compute new square for each child, and subtract weighted square from result
		for(HT child : node.getChildren()){
			double childSquare = nodeSquare(child);
			double weight = child.getNodeStatistics().getTotalSamplesNumber() / origSamples;
			/*System.out.println();
			System.out.println(childSquare);
			System.out.println(weight);
			System.out.println();*/
			result -= weight * childSquare;
		}
		//Reset split
		node.resetChildren();
		//Return computed information gain
		return result;
	}
	/*
	 * Methods for entropy
	 */
	protected static double nodeEntropy(HT node){
		return entropy(node.getNodeStatistics().getClassOccurrence(), node.getNodeStatistics().getTotalSamplesNumber());
	}	
	protected static double entropy(Map<DataValue, Integer> occs, int totNum){
		double result = 0;
		//Compute entropy for each class value and sum
			for(DataValue cv : occs.keySet()){
				//Number of occurrences of this class value
				double clOcc = occs.get(cv);
				//If clOcc is zero, this value does not influence entropy
				if(clOcc != 0){
					//Probability on total number
					double clProb = clOcc / totNum;
					//Compute entropy
					result -= clProb * (Math.log(clProb) / Math.log(2));
				}
			}
		return result;
	}
	protected static double informationGainEntropy(HT node, Attribute a, DataValue[] thresholds){
		double result = 0;
		//This should only be called on a leaf
		if(!node.isTerminal())
			return result;
		//If attribute is null, information gain for not splitting is zero
		if(a == null)
			return result;
		//Save data to compute statistics later
		result = nodeEntropy(node);
		double origSamples = node.getNodeStatistics().getTotalSamplesNumber();
		//Split the node on attribute a
		HoeffdingTreeAlgorithm.splitNode(node, a, thresholds);
		//Compute new entropy for each child, and subtract weighted entropy from result
		for(HT child : node.getChildren()){
			double childEntropy = nodeEntropy(child);
			double weight = child.getNodeStatistics().getTotalSamplesNumber() / origSamples;
			result -= weight * childEntropy;
		}
		//Reset split
		node.resetChildren();
		//Return computed information gain
		return result;
	}
	public static double HoeffdingBound(double r, double delta, int n){
		return Math.sqrt((r * r * Math.log(1 / delta)) / (2 * n));
	}
}
