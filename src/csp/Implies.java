package csp;

import java.util.ArrayList;
import java.util.List;

public class Implies implements Constraint{

	  private Variable thesis;
      private List<Variable> hypothesis;
      private List<Variable> scope;
      private Assignment assignments;

      public Implies(Variable thesis, List<Variable> hypothesis, Assignment assignments) {
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
    	  //hass quello provvisorio
    	  //Object last = Hassignment.AssignmentForLast();
    	  if(Hassignment.hasAssignmentFor(thesis)){
    		  Object ass = Hassignment.getAssignment(thesis);
         	  Object Hass = assignments.getAssignment(thesis);
         	  if (ass.equals(Hass)){
         		  //controllo ipotesi
         		  for(Variable v : hypothesis){
         			  if (Hassignment.hasAssignmentFor(v) && !( (Hassignment.getAssignment(v)).equals(assignments.getAssignment(v)) ) ) return false;
         		  }
         	  }
         	  else return true; //altrimenti vado avanti
    	  } return true;
    	 
    	      
      }
      
      public String toString() {
    	  Object ass = null;
    	  String ris = "";
    	  for (Variable v: hypothesis){
    	      ass = assignments.getAssignment(v);
    	      ris += v.getName() + " = " + ass.toString() + ", ";
    	  }
    	  ass = assignments.getAssignment(thesis);
    	  ris += " => " + thesis.getName() + " = " + ass.toString();
    	  return ris;
    	  
    	 
      }

}
