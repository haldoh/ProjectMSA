package tree;

import io.Attribute;
import io.DataValue;
import io.Sample;

import java.util.ArrayList;
import java.util.List;

public class HT {
	//List of children of this node
	private List<HT> children;
	//Father of this node
	private HT father;
	//Class label for this node
	private DataValue classLabel;
	//Statistics for this node
	private NodeStatistics nodeStats;
	//Test for this node
	private NodeTest nTest;
	//Reference to the list of attributes for the current dataset
	private List<Attribute> dsAttributes;
	//Reference to the class attribute for the current dataset
	private Attribute classAttribute;
	/**
	 * Create a new tree
	 */
	public HT(List<Attribute> attributes, Attribute cAtt){
		//Initialize data structures
		this.children = new ArrayList<HT>();
		this.father = null;
		this.nodeStats = new NodeStatistics(attributes, cAtt);
		this.dsAttributes = attributes;
		this.classAttribute = cAtt;
		this.nTest = null;
	}
	/**
	 * Use the tree to classify a sample
	 * @param s The sample to be classified
	 * @return The leaf reached after exploring this tree with the given sample
	 */
	public HT classify(Sample s){
		if(this.isTerminal())
			return this;
		else
			return this.nTest.test(s.getValue(this.nTest.getTestAttribute())).classify(s);
	}
	/**
	 * Wrapper for the corresponding method in NodeTest
	 * @param dv The test value
	 * @param child The subtree linked to this test
	 */
	public void addTest(Attribute a, DataValue dv, HT child){
		if(this.nTest == null)
			this.nTest = new NodeTest(a);
		if(this.children.contains(child))
			this.nTest.addTest(dv, child);
	}
	/**
	 * Add a new child to this tree
	 * @return The new child
	 */
	public HT addNewChild(){
		HT child = new HT(this.dsAttributes, this.classAttribute);
		this.addChild(child);
		return child;
	}
	/**
	 * Add the given child to this tree
	 * @param child The new child to be added to the tree
	 */
	public void addChild(HT child){
		child.setFather(this);
		this.children.add(child);
	}
	/**
	 * Remove the given child from the tree
	 * @param child The child to be removed
	 */
	public void removeChild(HT child){
		this.children.remove(child);
	}
	/**
	 * reset children for this node
	 */
	public void resetChildren(){
		this.children = new ArrayList<HT>();
		this.nTest = null;
	}
	/**
	 * 
	 * @return The number of internal nodes in this tree
	 */
	public int treeNodes(){
		int result = 0;
		if(this.isTerminal())
			return result;
		else {
			result++;
			for(HT child : this.getChildren())
				result += child.treeNodes();
			return result;
		}
	}
	/**
	 * 
	 * @return The number of leaves in this tree
	 */
	public int treeLeaves(){
		int result = 0;
		if(this.isTerminal())
			return 1;
		else {
			for(HT child : this.getChildren())
				result += child.treeLeaves();
			return result;
		}
	}
	/**
	 * 
	 * @return The depth of this tree
	 */
	public int treeDepth(){
		int result = 0;
		if(this.isTerminal())
			return result;
		else{
			result++;
			for(HT child : this.getChildren())
				if(child.treeDepth() >= result)
					result = child.treeDepth()+1;
			return result;
		}
	}
	/**
	 * 
	 * @param val The value for the test
	 * @return The child that corresponds to the parameter value, found using the test at this node
	 */
	public HT getChild(DataValue val){
		if(val.isNumerical() != this.getTestAtt().isNumerical())
			return null;
		else
			return this.nTest.test(val);
	}
	/**
	 * 
	 * @return Attribute used for tests at this node, null if it's a leaf
	 */
	public Attribute getTestAtt(){
		if(this.nTest == null)
			return null;
		else
			return this.nTest.getTestAttribute();
	}
	/**
	 * Change this tree's father
	 * @param newFather The new father of this tree
	 */
	public void changeFather(HT newFather){
		this.father.removeChild(this);
		this.father = newFather;
		this.father.addChild(this);
	}
	/**
	 * @return the children
	 */
	public List<HT> getChildren() {
		return children;
	}
	/**
	 * @return the father
	 */
	public HT getFather() {
		return father;
	}
	/**
	 * @param father the father to set
	 */
	public void setFather(HT father) {
		this.father = father;
	}
	/**
	 * @return the classLabel
	 */
	public DataValue getClassLabel() {
		return classLabel;
	}
	/**
	 * @param classLabel the classLabel to set
	 */
	public void setClassLabel(DataValue classLabel) {
		this.classLabel = classLabel;
	}
	/**
	 * 
	 * @return NodeStatistics for this node
	 */
	public NodeStatistics getNodeStatistics(){
		return this.nodeStats;
	}
	/**
	 * reset stats for this node
	 */
	public void resetNodeStatistics(){
		this.nodeStats = new NodeStatistics(this.dsAttributes, this.classAttribute);
	}
	/**
	 * 
	 * @return True if this tree is a leaf (meaning that it has no children)
	 */
	public boolean isTerminal(){
		return this.children.isEmpty();
	}
	/**
	 * 
	 * @return True if this tree is a root (meaning that it has no father
	 */
	public boolean isRoot(){
		return (this.father == null);
	}
	
}
