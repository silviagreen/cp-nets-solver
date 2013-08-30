package csp;

import java.util.ArrayList;
import java.util.List;

/**
 * Artificial Intelligence A Modern Approach (3rd Ed.): Figure 6.5, Page 220.
 * 
 * <pre>
 * <code>
 * function BACKTRACKING-SEARCH(csp) returns a solution, or failure
 *    return BACKTRACK({ }, csp)
 * 
 * function BACKTRACK(assignment, csp) returns a solution, or failure
 *    if assignment is complete then return assignment
 *    var = SELECT-UNASSIGNED-VARIABLE(csp)
 *    for each value in ORDER-DOMAIN-VALUES(var, assignment, csp) do
 *       if value is consistent with assignment then
 *          add {var = value} to assignment
 *          inferences = INFERENCE(csp, var, value)
 *          if inferences != failure then
 *             add inferences to assignment
 *             result = BACKTRACK(assignment, csp)
 *             if result != failure then
 *                return result
 *          remove {var = value} and inferences from assignment
 *    return failure
 * </code>
 * </pre>
 * 
 * @author Ruediger Lunde
 */

public class BacktrackingStrategy extends SolutionStrategy {

	List<Assignment> result = new ArrayList<Assignment>();
	

	public List<Assignment> solve(CSP csp, boolean findAll) {
		return recursiveBackTrackingSearch(csp, new Assignment(), findAll);
	}

	/**
	 * Template method, which can be configured by overriding the three
	 * primitive operations below.
	 */
	/*
	 * private List<Assignment> recursiveBackTrackingSearch(CSP csp, Assignment
	 * assignment, boolean findAll) {
	 * 
	 * for (Variable v : csp.getVariables())
	 * System.out.println("CSP di LAVORO: " + v.getName());
	 * 
	 * if (assignment.isComplete(csp.getVariables())) { result.add(assignment);
	 * System.out.println("completo"); if (findAll == true) {
	 * assignment.removeAssignment(var);
	 * result.addAll(recursiveBackTrackingSearch(csp, assignment,findAll)); }
	 * 
	 * }
	 * 
	 * var = selectUnassignedVariable(csp, assignment); for (Object value :
	 * orderDomainValues(var, assignment, csp)) { assignment.setAssignment(var,
	 * value); System.out.println("in for " + assignment.toString());
	 * 
	 * if (assignment.isConsistent(csp.getConstraints(var))) { csp =
	 * inference(var, assignment, csp);
	 * result.addAll(recursiveBackTrackingSearch(csp, assignment, findAll));
	 * break; } //CSP savedCSP = csp; // csp = inference(var, assignment, csp);
	 * assignment.removeAssignment(var); System.out.println("rimosso"); return
	 * result;
	 * 
	 * // }
	 * 
	 * }
	 * 
	 * return result;
	 * 
	 * }
	 */
	Variable lastAssignedVar = null;
	private List<Assignment> recursiveBackTrackingSearch(CSP csp, Assignment assignment, boolean findAll) {
		System.out.println("findAll = " + findAll);
		boolean isComplete = assignment.isComplete(csp.getVariables());
		if (isComplete) {
			
			result.add(assignment.copy());
			
			assignment.removeAssignment(lastAssignedVar);

			 return result;
			
		} 
			
		System.out.println(assignment);
			Variable var = selectUnassignedVariable(csp, assignment);
			lastAssignedVar = var;
			
			for (Object value : orderDomainValues(var, assignment, csp)) {
				System.out.println("var: " + var.getName() + " = " + value.toString());
				assignment.setAssignment(var, value);
				if (assignment.isConsistent(csp.getConstraints(var))) {
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


	/**
	 * Primitive operation, selecting a not yet assigned variable. This default
	 * implementation just selects the first in the ordered list of variables
	 * provided by the CSP.
	 */
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

	/**
	 * Primitive operation, ordering the domain values of the specified
	 * variable. This default implementation just takes the default order
	 * provided by the CSP.
	 */
	protected Iterable<?> orderDomainValues(Variable var,
			Assignment assignment, CSP csp) {
		return csp.getDomain(var);
	}

	/**
	 * Primitive operation, which tries to prune out values from the CSP which
	 * are not possible anymore when extending the given assignment to a
	 * solution. This default implementation just returns the original CSP.
	 * 
	 * @return A reduced copy of the original CSP or null denoting failure
	 *         (assignment cannot be extended to a solution).
	 
	protected CSP inference(Variable var, Assignment assignment, CSP csp) {
		CSP newcsp = csp.copyForPropagation();
		newcsp.removeValueFromDomain(var, assignment.getAssignment(var));
		return newcsp;
	}*/
	
	/**
     * Primitive operation, which tries to prune out values from the CSP which
     * are not possible anymore when extending the given assignment to a
     * solution. This default implementation just leaves the original CSP as it
     * is.
     * 
     * @return An object which provides informations about (1) whether changes
     *         have been performed, (2) possibly inferred empty domains , and
     *         (3) how to restore the domains.
     */
    protected DomainRestoreInfo inference(Variable var, Assignment assignment,
                    CSP csp) {
            return new DomainRestoreInfo().compactify();
    }
}
