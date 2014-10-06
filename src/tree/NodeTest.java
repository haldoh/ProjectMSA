package tree;

import io.Attribute;
import io.DataValue;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
/**
 * 
 * @author wolf
 * <p>
 * This class models tests for a given node. It maps values in the form of DataValue objects to subtrees of the decision tree.
 * For categorical attributes, the test is obvious: to each value corresponds a child tree.
 * For numerical attributes, the DataValue represent the upper bound for a test value to be associated to the given subtree (meaning that the tested value should be lesser than the value stored in the test).
 *
 */
public class NodeTest {
	private Attribute testAttribute;
	private Map<DataValue, HT> testMap;
	/**
	 * Initialize internal data structure and save reference to test attribute
	 * @param tAtt The tested attribute
	 */
	public NodeTest(Attribute tAtt){
		this.testAttribute = tAtt;
		this.testMap = new TreeMap<DataValue, HT>();
	}
	/**
	 * Add a test to this NodeTest, consisting of a couple value-subtree
	 * For numerical data, the value is an upper bound, meaning that the linked subtree will be returned while testing if the tested value is < dv and greater or equal than other tests < dv
	 * @param dv The value for the test
	 * @param child The subtree linked to this value
	 */
	public void addTest(DataValue dv, HT child){
		this.testMap.put(dv, child);
	}
	/**
	 * Implementation of the test on a value
	 * @param dv The value to be tested
	 * @return The subtree corresponding to the key that satisfies the test. For numerical attribute, the subtree with the greatest value is returned if no match is found. For categorical attribute, null is returned if no subtree has a matching key.
	 */
	public HT test(DataValue dv){
		if(this.testAttribute.isNumerical()){
			//The test contains the upper bound, so when dv < the current entry value, return the current entry subtree
			for(Entry<DataValue, HT> t : this.testMap.entrySet()){
				if(dv.compareTo(t.getKey()) < 0)
					return t.getValue();
			}
			//If nothing is returned, return the last subtree
			return ((TreeMap<DataValue, HT>) this.testMap).lastEntry().getValue();
		}else{
			//For categorical data, just return the entry corresponding to the given value
			return this.testMap.get(dv);
		}
	}
	public Attribute getTestAttribute(){
		return this.testAttribute;
	}
}
