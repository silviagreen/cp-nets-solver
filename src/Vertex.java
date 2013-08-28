import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Vertex {
	
	private Integer vertexID;
	private List<Integer> parents;
	private List<Integer> children;
	private List<Preference> preferences;
	
	public Vertex(Integer vertexID) {
		this.vertexID = vertexID;
		this.parents = new ArrayList<Integer>();
		this.children = new ArrayList<Integer>();
		this.preferences = new ArrayList<Preference>();
	}
	
	public Integer getID() {
		return this.vertexID;
	}
	
	public List<Integer> getParents() {
		return this.parents;
	}
	
	public List<Integer> getChildren() {
		return this.children;
	}
	
	public void addParent(Integer parentID) {
		this.parents.add(parentID);
		Collections.sort(this.parents);
	}
	
	public void addChild(Integer child) {
		this.children.add(child);
		Collections.sort(this.children);
	}
	
	public boolean isParentOf(Integer child) {
		return this.children.contains(child);
	}
	
	public void generatePreferences() {
		int nPreferences = (int) Math.pow(2, this.parents.size());
		for(int i = 0; i < nPreferences; i++) {
			this.preferences.add(new Preference(this.vertexID, i));
		}
		//System.out.println("--Preferenze:" +this.preferences.size());
		
		/*
		 * Imposto metà delle preferenze a true (A > !A) 
		 */
		int setPreferencesCounter = 0;
		Random randomizer = new Random(System.nanoTime());
		while(setPreferencesCounter < (nPreferences / 2)) {
			Integer randomPref = Integer.valueOf(randomizer.nextInt(nPreferences));
			//System.out.println("----Ho scelto la preferenza " + Integer.toBinaryString(randomPref) +"b");
			if(this.preferences.get(randomPref).setIsAffirmedVariable(true)) {
				setPreferencesCounter++;
			}
		}
		
		/*
		 * Imposto l'altra metà delle preferenze a false (!A > A), solo se la variabile ha predecessori
		 */
		if(this.parents.size() > 0) {
			//System.out.println("----Ora imposto quelle che mancano per il vertice " + this.vertexID + ":");
			for(Preference p : this.preferences) {
				p.setIsAffirmedVariable(false);
			}
		} else {
			/*
			 * Lancio una moneta, se esce testa (0) imposto a true, altrimenti a false
			 */
			boolean isAV = (Integer.valueOf(randomizer.nextInt(2)) == 0);
			this.preferences.get(0).setIsAffirmedVariable(isAV);
		}
	}
	
	/*
	 * toString di test, si può modificare
	 */
	public String toString() {
		String s = "";
		s += "Vertex " + this.vertexID + ":\n";
		s += "Parents:" + this.parents + "\n";
		s += "Children: " + this.children + "\n";
		s += "Preferences: " + this.preferences + "\n";
		return s;
	}

}
