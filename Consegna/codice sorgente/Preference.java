import java.util.List;

import csp.ListUtils;

public class Preference {
	
	private int variableName;
	
	/*
	 * Numero binario che rappresenta i valori delle variabili coinvolte, per es. "011"
	 * rappresenta la prima variabile negata, e la seconda e la terza affermate.
	 */
	private int binaryValue;
	
	/*
	 * Se true, allora la preferenza è della forma A > !A
	 * altrimenti la preferenza è nella forma !A > A
	 */
	private Boolean isAffirmedVariable;
	
	public Preference(int variableName, int binaryValue) {
		this.variableName = variableName;
		this.binaryValue = binaryValue;
		this.isAffirmedVariable = null;
	}
        
        public Preference(Preference origin){
            this.variableName=origin.variableName;
            this.binaryValue=origin.binaryValue;
            this.isAffirmedVariable=new Boolean(origin.isAffirmedVariable);
        }
	
        boolean getIsAffirmedValue(){
        	return isAffirmedVariable;
        }
        
        int getBinaryValue(){
        	return binaryValue;
        }
        
        public int getVariableName(){
        	return variableName;
        }
        
	public boolean setIsAffirmedVariable(boolean isAV) {
		//System.out.println("--------Stato attuale di " + Integer.toBinaryString(this.binaryValue) + "b : " + this.isAffirmedVariable);
		if(this.isAffirmedVariable == null) {
			this.isAffirmedVariable = isAV;
			//System.out.println("--------La imposto a " + isAV + "->" + this.isAffirmedVariable);
			return true;
		}
		return false;
	}
	
	public List<Integer> getBinaryValueInList(int nParents){
		List<Integer> binVal = ListUtils.decToBin(getBinaryValue());
		int diff = (nParents) - (binVal.size());
		// System.out.println("bin val " + binVal.toString()+
		// " has length " + binVal.size());
		// System.out.println("diff = " + diff);
		while (diff != 0) {
			binVal.add(0, 0);
			diff--;
		}
		// System.out.println(v.getID() + " ha binary value " +
		// binVal.toString() + " e ha " + v.getParents().size() +
		// " parents");
		return binVal;
	}
	
	/*
	 * toString di test, si può modificare
	 */
        @Override
	public String toString() {
		return "\n" + Integer.toBinaryString(this.binaryValue) + " -> " + (this.isAffirmedVariable ? 
				this.variableName + " > !" + this.variableName  : 
					"!" + this.variableName + " > " + this.variableName);
	}

}
