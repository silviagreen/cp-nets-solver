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
        
	public boolean setIsAffirmedVariable(boolean isAV) {
		//System.out.println("--------Stato attuale di " + Integer.toBinaryString(this.binaryValue) + "b : " + this.isAffirmedVariable);
		if(this.isAffirmedVariable == null) {
			this.isAffirmedVariable = isAV;
			//System.out.println("--------La imposto a " + isAV + "->" + this.isAffirmedVariable);
			return true;
		}
		return false;
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
