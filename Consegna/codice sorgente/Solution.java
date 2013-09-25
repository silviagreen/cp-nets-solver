import java.util.ArrayList;
import java.util.List;


public class Solution {

	private String value;
	private List<Solution> subSols = new ArrayList<Solution>();
	
	public Solution(String value) {
		this.value = value;
	}
	
	public List<Solution> getSubSols(){
		return subSols;
	}
	
	public void addSubSol(Solution subSol){
		subSols.add(subSol);
	}
	
	public String getValue(){
		return value;
	}
	
	public boolean containSubSol(String s){
		for(Solution sol : subSols){
			if (sol.getValue().equals(s)) return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
