package csp;

import java.util.ArrayList;
import java.util.List;

public class Implies implements Constraint {

	private Variable thesis;
	private List<Variable> hypothesis;
	private List<Variable> scope;
	// primo per la tesi, poi gruppi per l'ipotesi
	private List<Assignment> assignments;

	public Implies(Variable thesis, List<Variable> hypothesis,
			List<Assignment> assignments) {
		this.assignments = assignments;
		this.thesis = thesis;
		this.hypothesis = hypothesis;
		scope = new ArrayList<Variable>(hypothesis);
		scope.add(thesis);

	}

	@Override
	public List<Variable> getScope() {
		return scope;
	}

	@Override
	public boolean isSatisfiedWith(Assignment Hassignment) {
		// hass quello provvisorio
		// Object last = Hassignment.AssignmentForLast();
		if (Hassignment.hasAssignmentFor(thesis)) {
			Object ass = Hassignment.getAssignment(thesis);
			Object Hass = assignments.get(0).getAssignment(thesis);
			if (ass.equals(Hass) && hypothesis.isEmpty() == false) {
				// controllo ipotesi
				boolean satisfied = true;
				for (Assignment a : assignments.subList(1, assignments.size())) {
					satisfied = true;
					//System.out.println(a.size());
					for (Variable v : hypothesis) {
						//System.out.println("provo" + a + "con var " + v.getName());
						if (Hassignment.hasAssignmentFor(v)	&& !((Hassignment.getAssignment(v)).equals(a.getAssignment(v)))){
							//System.out.println("var " + v.getName() + "non ok");
							satisfied = false;
							break;}
					}
					if(satisfied) return true; 

				}
				//System.out.println(satisfied);
				if(satisfied) return true;
				else {System.out.println("VINCOLO VIOLATO: " + this.toString()); return false;}
			} else if(ass.equals(Hass) && hypothesis.isEmpty()  == true) return true;
			else if(!ass.equals(Hass) && hypothesis.isEmpty() == true){System.out.println("VINCOLO VIOLATO: " + this.toString()); return false;}
				return true; // altrimenti vado avanti
		}
		return true;

	}

	public String toString() {
		Object ass = null;
		String ris = "";
		for(Assignment a : assignments.subList(1, assignments.size())){
			ris += "<";
			for (Variable v : hypothesis) {
			ass = a.getAssignment(v);
			
			ris += v.getName() + " = " + ass.toString() + ", ";
		}
			ris += ">";
		}
		
		ass = assignments.get(0).getAssignment(thesis);
		ris += " => " + thesis.getName() + " = " + ass;
		return ris;

	}

}
