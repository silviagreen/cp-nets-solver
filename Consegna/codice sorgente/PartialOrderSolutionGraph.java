import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csp.ListUtils;


public class PartialOrderSolutionGraph {

	private int nNodes;
	CPNet cpnet;
	private List<Solution> solutions = new ArrayList<Solution>();
	public static enum Color { WHITE, GREY, BLACK };
	
	HashMap<String, Color> colors = new HashMap<String, Color>();
	
	
	public PartialOrderSolutionGraph(CPNet cpnet) {
		nNodes = cpnet.getAdjList().size();
		this.cpnet = cpnet;
		setSolutions(nNodes);
	}
	
	public List<Solution> getSolutions(){
		return solutions;
	}
	
	private static String extendBinaryString(String s, int lenght){
		while(s.length()<lenght) s = "0"+s;
		return s;
	}
	
	private void setSolutions(int nNodes){
		Integer nSol = (int)(Math.pow(2, nNodes));
		for(int i = 0; i < nSol; i++){
			String name = extendBinaryString(Integer.toBinaryString(i), nNodes);
			colors.put((name), Color.WHITE);
			solutions.add(new Solution(name));
		
		}
	}
		
		
	//TODO: DFS visit
		
	public boolean isBetterThan(Solution s1, Solution s2){//s1 e' migliore di s2????
		
		int i = 1;
		boolean found = false, result = false;
		for(i = 0; !found && i < s1.getValue().length(); i++){
			if( (s1.getValue()).charAt(i) != (s2.getValue()).charAt(i) ) found = true;
		}
		i--;
		//System.out.println("cambia in pos: "+i);
		Vertex v = cpnet.getAdjList().get(i);
		if(v.getParents().isEmpty()){
			result = (v.getPreferences().get(0).getIsAffirmedValue()) ? s1.getValue().charAt(i) == '1' : s1.getValue().charAt(i) == '0';
		}
		else{ System.out.println("----------------" + s1.getValue() + "e meglio di " +s2.getValue() +"????");
			//controlla preferenza padre
			String parentsPref = "";
			
			//System.out.println("i parents di " + v.getID() + "sono: ");
			//for(Integer ii : v.getParents()) System.out.println(ii.toString());
			
//			for(int k = 1; k < s1.getValue().length();  k++){
//				System.out.println("guardo se " + cpnet.getAdjList().get(k).getID().toString() + " e parent");
//				if(v.getParents().contains(cpnet.getAdjList().get(k).getID())){
//					parentsPref += s1.getValue().charAt(k);
//					System.out.println("new parent pref " + parentsPref);
//				}
//		    }
			
			for(Integer k : v.getParents()){
				//System.out.println("parent "+k);
				parentsPref += s1.getValue().charAt(k);
			}
			System.out.println("per " + v.getID() + " ottengo parentPref=" + parentsPref);
			
			for(Preference p: v.getPreferences()){
				//System.out.println("preference considerata: " + p.toString());
				if(p.getBinaryValue() == ListUtils.fromBinToInt(parentsPref)){
					//System.out.println(p.getBinaryValue() + " e' uguale a " + ListUtils.fromBinToInt(parentsPref));
					System.out.println(p.toString());
					result = (p.getIsAffirmedValue()) ? s1.getValue().charAt(i) == '1' : s1.getValue().charAt(i) == '0';
					//System.out.println("trovata e i="+i);
					//System.out.println(s1.getValue().charAt(i) == '1');
					//System.out.println(s1.getValue().charAt(i) == '0');
					System.out.println(result);
					
				}
					
			}
	}
		//System.out.println(result);
	return result;	
		
	}
	
	private int getIndexByValue(String s){
		for(int i = 0; i < solutions.size(); i++){
			if(solutions.get(i).getValue().equals(s)) return i;
		}
		return -1;
	}
	
	public void DFSvisit(Solution s) {		
		if (colors.get(s.getValue()) != Color.WHITE)
			return;

		colors.put(s.getValue(), Color.GREY);
		List<String> neighbours = getNeighbours(s.getValue());
		for (int i = 0; i < neighbours.size(); i++) {
			if (colors.get(neighbours.get(i)) != Color.BLACK) {
				// se non sono ancora stati confrontati
				int index_s = getIndexByValue(s.getValue());
				int index_n = getIndexByValue(neighbours.get(i));
				if (!solutions.get(index_s).containSubSol(neighbours.get(i)) && !solutions.get(index_n).containSubSol(s.getValue()))
					if (isBetterThan(s, new Solution(neighbours.get(i)))){
						System.out.println(s.getValue() + " e' meglio di " + neighbours.get(i));
						solutions.get(index_s).addSubSol(new Solution(neighbours.get(i)));
					}
					else{
						System.out.println(neighbours.get(i) + " e' meglio di " + s.getValue());
						solutions.get(index_n).addSubSol(s);
						
					}
				DFSvisit(new Solution(neighbours.get(i)));

			}
		}

		colors.put(s.getValue(), Color.BLACK);
	}
	
	/**
	 * Ritorna le solutioni distanti un flip peggiorativo da sol
	 * @param sol
	 * @return
	 */
	private List<String> getNeighbours(String sol){
		List<String> neighbours = new ArrayList<String>();
		StringBuilder sb = new StringBuilder(sol);
		for(int i = 0; i < sol.length(); i++){
			if(sol.charAt(i) == '0'){
				sb.setCharAt(i, '1');
				neighbours.add(sb.toString());
				sb.setCharAt(i, '0');
			}
			else{
				sb.setCharAt(i, '0');
				neighbours.add(sb.toString());
				sb.setCharAt(i, '1');
			}
		}
		return neighbours;
	}
	
	public void setPartialOrderSolutions(/*Solution optimal*/){
		String s = "";
		for(int i = 0; i < nNodes ; i++) s += "0";
		Solution optimal = new Solution(s);
		System.out.println("Generating Solution Partial Order...");
		DFSvisit(optimal);
		System.out.println("....done");
	}
	
	@Override
	public String toString() {
		String ris ="LIST ADJ:\n";
		for(Solution s : getSolutions()){
			String sol = s.getValue() + ": ";
			for(Solution subs : s.getSubSols()){
				sol += subs.getValue() + " ";
			}
			ris += sol + "\n";
		}
		return ris;
	}
}
