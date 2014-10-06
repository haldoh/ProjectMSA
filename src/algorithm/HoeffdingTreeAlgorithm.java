package algorithm;

import io.Attribute;
import io.DataValue;
import io.InputStream;
import io.Sample;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import tree.HT;
import exception.OutOfRangeException;
import exception.WrongTypeException;

public class HoeffdingTreeAlgorithm {
	
	public static double train(HT tree, InputStream in, int cycles, double delta, double tau, int gracePeriod, String heuristic, int maxNodes){
		int limit = cycles * in.getTrainingDim();
		return trainLimit(tree, in, limit, delta, tau, gracePeriod, heuristic, maxNodes);
	}
	public static double trainLimit(HT tree, InputStream in, int samplesLimit, double delta, double tau, int gracePeriod, String heuristic, int maxNodes){
		//Initialize counter
		int counter = 1;
		//Initialize training error
		double tErr = 0;
		//Initialize sample var
		Sample s = null;
		//Get first row from training set
		try {
			s = in.getTrainingSample();
			//s = in.getTestSample();
		} catch (IOException | WrongTypeException | OutOfRangeException e) {
			e.printStackTrace();
		}
		//Grace period counter
		int sampleCounter = 0;
		//Repeat while there are still samples and the counter did not reach the given limit
		while(counter <= samplesLimit && tree.treeNodes() < maxNodes){
			//If it reached the end of the training set, keep going from the beginning
			if(s == null){
				in.resetTrainingInput();
				try {
					s = in.getTrainingSample();
				} catch (IOException | WrongTypeException | OutOfRangeException e) {
					e.printStackTrace();
				}
			}
			//Classify sample using the current tree and update training error
			if(counter > 1 && tree.classify(s).getClassLabel().compareTo(s.getValue(in.getClassAtt())) != 0)
				tErr += 1;
			//System.out.println("Training step: " + counter + "/" + samplesLimit);
			System.out.println("Training step: " + counter + "/" + samplesLimit + " - " + sampleCounter);
			//s.print();
			//Classifiy sample using the current tree
			HT leaf = tree.classify(s);
			//Save stats about the sample
			leaf.getNodeStatistics().saveSample(s);
			//Assign to the leaf the most frequent class observed as class label
			leaf.setClassLabel(leaf.getNodeStatistics().getMostFrequentClass());
			//Increment sample counter
			sampleCounter++;
			//Check if within grace period
			if(sampleCounter >= gracePeriod){
				//Check if at this leaf where classified samples with different classes
				if(leaf.getNodeStatistics().hasDiffClasses()){
					/*
					 * Compute information gain for each attribute
					 * and save first and second best, only if greater than
					 * information gain for not splitting
					 */
					double infGainNoSplit = Heuristics.informationGain(leaf, null, null, heuristic);
					//Vars for best attribute
					Attribute firstAtt = null;
					DataValue[] firstThres = null;
					double firstAttGain = infGainNoSplit;
					//Vars for second best attribute
					double secondAttGain = infGainNoSplit;
					for(Attribute a : in.getAttributes()){
						double infGain = 0;
						DataValue[] thres = null;
						//Inf gain is computed for all attributes, except the one used for tests by the node father
						if(leaf.getFather() == null || !a.equals(leaf.getFather().getTestAtt())){
							//Check if attribute is numerical or categorical
							if(a.isNumerical()){
								//Numerical
								thres = numericThresholds(leaf, a, in);
								infGain = Heuristics.informationGain(leaf, a, thres, heuristic);
							}else{
								//Categorical
								infGain = Heuristics.informationGain(leaf, a, firstThres, heuristic);
							}
							//Check if a is best or second best
							if(infGain > firstAttGain){
								firstAtt = a;
								firstAttGain = infGain;
								firstThres = thres;
							}else if(infGain > secondAttGain){
								secondAttGain = infGain;
							}
						}
					}
					//If the first attribute is not null, use Hoeffding Bound to decide if it should split
					if(firstAtt != null){
						double diff = firstAttGain - secondAttGain;
						double hBound = Heuristics.HoeffdingBound(Math.log(in.getClassAtt().getValues().size())/Math.log(2), delta, leaf.getNodeStatistics().getTotalSamplesNumber());
						//System.out.println(diff + " - " + (Math.log(in.getClassAtt().getValues().size())/Math.log(2)) + " - " + leaf.getNodeStatistics().getTotalSamplesNumber() + " - " + hBound + " - " + tau);
						//Differnce should be bigger than Hoeffding bound, or smaller than a constant used to define ties
						if(diff > hBound || hBound < tau){
							//Split node
							splitNode(leaf, firstAtt, firstThres);
							//Assign classes and reset stats
							for(HT child : leaf.getChildren()){
								DataValue label = child.getNodeStatistics().getMostFrequentClass();
								/*
								 * If label is null, this child corresponds to a value of the split attribute
								 * that was never observed in the training set until now.
								 * By using a pseudo-random label we are not incrementing the training or test error
								 * of the classificator if we consider non-classified samples as errors, and the label
								 * will be changed to a meaningful one at the first observation of this attribute value.
								 * 
								 */
								if(label == null)
									label = in.getClassAtt().getValues().iterator().next();
								child.setClassLabel(label);
								child.resetNodeStatistics();
							}
							leaf.resetNodeStatistics();
							//Reset sample counter for grace
							sampleCounter = 0;
						}
					}
				}
			}
			
			//Increment counter
			counter++;
			//Get next sample
			try {
				s = in.getTrainingSample();
				//s = in.getTestSample();
			} catch (IOException | WrongTypeException | OutOfRangeException e) {
				e.printStackTrace();
			}
		}
		//Return training error
		return (tErr / counter);
	}
	
	public static void splitNode(HT node, Attribute a, DataValue[] thresholds){
		if(a.isNumerical())
			splitNodeNum(node, a, thresholds);
		else
			splitNodeCat(node, a);
	}

	private static void splitNodeCat(HT node, Attribute a) {
		//Split and create one node for all values of attribute a
		//Get a reference to the node statistics for the attribute, ordered by attr value and class value
		Map<DataValue, Map<DataValue, Integer>> stats = node.getNodeStatistics().getAVCStats(a);
		for(DataValue val : a.getValues()){
			//Add a new child
			HT newChild = node.addNewChild();
			//Add test
			node.addTest(a, val, newChild);
			if(stats.get(val) != null){
				//Update stats for the new child
				for(Entry<DataValue, Integer> e1 : stats.get(val).entrySet()){
					//Increment sample counter
					newChild.getNodeStatistics().incrementTotalSamplesNumber(e1.getValue());
					//Increment class counter
					newChild.getNodeStatistics().incrementClassOccurrence(e1.getKey(), e1.getValue());
					//Register value
					for(int i = 0; i < e1.getValue(); i++)
						newChild.getNodeStatistics().addValue(a, val, e1.getKey());
				}
			}
		}
	}
	/*
	private static void splitNodeNum(HT node, Attribute a, DataValue threshold) {
		//Split and create two nodes, and compute threshold for test
		//Get a reference to the node statistics for the attribute, ordered by attr value and class value
		Map<DataValue, Map<DataValue, Integer>> stats = node.getNodeStatistics().getAVCStats(a);
		//Split on given threshold
		//New children
		HT newChild1 = node.addNewChild();
		HT newChild2 = node.addNewChild();
		//Add tests
		node.addTest(a, threshold, newChild1);
		node.addTest(a, a.getMaxRange(), newChild2);
		//Save stats in children nodes
		for(DataValue val : stats.keySet()){
			HT child = val.compareTo(threshold) < 0 ? newChild1 : newChild2;
			//Update stats for the new child
			for(Entry<DataValue, Integer> e1 : stats.get(val).entrySet()){
				//Increment sample counter
				child.getNodeStatistics().incrementTotalSamplesNumber(e1.getValue());
				//Increment class counter
				child.getNodeStatistics().incrementClassOccurrence(e1.getKey(), e1.getValue());
				//Register value
				for(int i = 0; i < e1.getValue(); i++)
					child.getNodeStatistics().addValue(a, val, e1.getKey());
			}
		}
	}
	*/
	private static void splitNodeNum(HT node, Attribute a, DataValue[] thresholds) {
		//Get a reference to the node statistics for the attribute, ordered by attr value and class value
		Map<DataValue, Map<DataValue, Integer>> stats = node.getNodeStatistics().getAVCStats(a);
		//Split on given thresholds
		for(DataValue thres : thresholds){
			//New child
			HT newChild = node.addNewChild();
			//Add test
			node.addTest(a, thres, newChild);
		}
		//Add last child
		HT newChild = node.addNewChild();
		//Add test
		node.addTest(a, a.getMaxRange(), newChild);
		//Save stats in children nodes
		for(DataValue val : stats.keySet()){
			HT child = node.getChild(val);
			//Update stats for the new child
			for(Entry<DataValue, Integer> e1 : stats.get(val).entrySet()){
				//Increment sample counter
				child.getNodeStatistics().incrementTotalSamplesNumber(e1.getValue());
				//Increment class counter
				child.getNodeStatistics().incrementClassOccurrence(e1.getKey(), e1.getValue());
				//Register value
				for(int i = 0; i < e1.getValue(); i++)
					child.getNodeStatistics().addValue(a, val, e1.getKey());
			}
		}
	}
	private static DataValue[] numericThresholds(HT node, Attribute a, InputStream in){
		//If class attribute has n values, the method will return n-1 thresholds
		DataValue[] result = new DataValue[in.getClassAtt().getValues().size()-1];
		//In this arrays averages will be saved
		double[] avgs = new double[in.getClassAtt().getValues().size()];
		int index = 0;
		for(DataValue cVal : in.getClassAtt().getValues()){
			//Get the values of the attribute corresponding to this class
			int avgCounter = 0;
			for(Entry<DataValue, Integer> attVal : node.getNodeStatistics().getACVStats(a).get(cVal).entrySet()){
				//Get this value
				double val = 0;
				try {
					val = attVal.getKey().getNumValue();
				} catch (WrongTypeException e) {
					e.printStackTrace();
				}
				//Get this value counter
				int valCount = attVal.getValue();
				//Update average
				avgs[index] = ((avgs[index] * avgCounter) + (val * valCount)) / (avgCounter + valCount); 
				//Update counter
				avgCounter += valCount;
			}
			//Done with this class, increment index
			index++;
		}
		//Find points between averages
		for(int i = 0; i < result.length; i++)
			try {
				result[i] = new DataValue(true, (avgs[i] + avgs[i+1]) / 2);
			} catch (WrongTypeException e) {
				e.printStackTrace();
			}
		return result;
	}
}
