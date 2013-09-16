package csp;

import java.util.ArrayList;
import java.util.List;



public class BacktrackingStrategy extends SolutionStrategy {

	List<Assignment> result = new ArrayList<Assignment>();
	

	public List<Assignment> solve(CSP csp, boolean findAll) {
		return recursiveBackTrackingSearch(csp, new Assignment(), findAll);
	}


	Variable lastAssignedVar = null;
	private List<Assignment> recursiveBackTrackingSearch(CSP csp, Assignment assignment, boolean findAll) {
		//System.out.println("findAll = " + findAll);
		boolean isComplete = assignment.isComplete(csp.getVariables());
		if (isComplete) {
			System.out.println("trovata sol = " + assignment);
			result.add(assignment.copy());
			
			assignment.removeAssignment(lastAssignedVar);

			 return result;
			
		} 
			
		
			Variable var = selectUnassignedVariable(csp, assignment);
			lastAssignedVar = var;
			
			for (Object value : orderDomainValues(var, assignment, csp)) {
				//System.out.println("var: " + var.getName() + " = " + value.toString());
				assignment.setAssignment(var, value);
				System.out.println("assegnamento corrente:" + assignment);
				if (assignment.isConsistent(csp.getConstraints(var))) { System.out.println("è consistente");
					//csp = inference(var, assignment, csp);
					DomainRestoreInfo info = inference(var, assignment, csp);
					//if(csp !=null)
					if (!info.isEmptyDomainFound()) 
					{ // null denotes failure
                        result.addAll(recursiveBackTrackingSearch(csp, assignment,findAll));
                        if(!findAll){
                        	if(result != null) break;
                        }
					}
					csp.restoreDomains(info);
				}
				assignment.removeAssignment(var);
			}
		ListUtils.removeDuplicate(result);
		return result;
	}



	protected Variable selectUnassignedVariable(CSP csp, Assignment assignment) {
		for (Variable var : csp.getVariables()) {
			if (!(assignment.hasAssignmentFor(var))
					&& csp.getDomain(var).isEmpty() == false) {
				// System.out.println(var.toString());
				return var;
			}
		}
		return null;
	}


	protected Iterable<?> orderDomainValues(Variable var,
			Assignment assignment, CSP csp) {
		return csp.getDomain(var);
	}

	
    protected DomainRestoreInfo inference(Variable var, Assignment assignment,
                    CSP csp) {
            return new DomainRestoreInfo().compactify();
    }
}
