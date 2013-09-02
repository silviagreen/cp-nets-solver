package csp;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * Artificial Intelligence A Modern Approach (3rd Ed.): Figure 6.3, Page 209.
 * 
 * <pre>
 * <code>
 * function AC-3(csp) returns false if an inconsistency is found and true otherwise
 *    inputs: csp, a binary CSP with components (X, D, C)
 *    local variables: queue, a queue of arcs, initially all the arcs in csp
 *    while queue is not empty do
 *       (Xi, Xj) = REMOVE-FIRST(queue)
 *       if REVISE(csp, Xi, Xj) then
 *          if size of Di = 0 then return false
 *             for each Xk in Xi.NEIGHBORS - {Xj} do
 *                add (Xk, Xi) to queue
 *    return true
 * 
 * function REVISE(csp, Xi, Xj) returns true iff we revise the domain of Xi
 *    revised = false
 *    for each x in Di do
 *       if no value y in Dj allows (x ,y) to satisfy the constraint between Xi and Xj then
 *          delete x from Di
 *          revised = true
 *    return revised
 * </code>
 * </pre>
 * 
 * Figure 6.3 The arc-consistency algorithm AC-3. After applying AC-3, either
 * every arc is arc-consistent, or some variable has an empty domain, indicating
 * that the CSP cannot be solved. The name "AC-3" was used by the algorithm's
 * inventor (Mackworth, 1977) because it's the third version developed in the
 * paper.
 * 
 * @author Ruediger Lunde
 */

public class AC3Strategy {

	public DomainRestoreInfo reduceDomains(Variable var, Object value, CSP csp) {
		System.out.println("riduco dominio");
		DomainRestoreInfo result = new DomainRestoreInfo();
		Domain domain = csp.getDomain(var);
		List<Object> valueList = new ArrayList<>();
		valueList.add(value);
		if (domain.contains(value)) {
			if (domain.size() > 1) {
				FIFOQueue<Variable> queue = new FIFOQueue<Variable>();
				queue.add(var);
				result.storeDomainFor(var, domain);
				csp.setDomain(var, valueList);
				reduceDomains(queue, csp, result);
			}
		} else {
			result.setEmptyDomainFound(true);
		}
		return result.compactify();
	}

	public DomainRestoreInfo reduceDomains(CSP csp) {
		DomainRestoreInfo result = new DomainRestoreInfo();
		FIFOQueue<Variable> queue = new FIFOQueue<Variable>();
		for (Variable var : csp.getVariables())
			queue.add(var);
		reduceDomains(queue, csp, result);
		return result.compactify();
	}

	protected void reduceDomains(FIFOQueue<Variable> queue, CSP csp,
			DomainRestoreInfo info) {

		while (!queue.isEmpty()) {
			Variable var = queue.pop();
			for (Constraint constraint : csp.getConstraints(var)) {
				if (constraint.getScope().size() == 2) {
					Variable neighbor = csp.getNeighbor(var, constraint);
					if (revise(neighbor, var, constraint, csp, info)) {
						if (csp.getDomain(neighbor).isEmpty()) {
							info.setEmptyDomainFound(true);
							return;
						}
						queue.push(neighbor);
					}
				}
			}
		}
	}

	private boolean revise(Variable xi, Variable xj, Constraint constraint,
			CSP csp, DomainRestoreInfo info) {
		System.out.println("in revise");
		boolean revised = false;
		Assignment assignment = new Assignment();
		for (Object vValue : csp.getDomain(xi)) {
			assignment.setAssignment(xi, vValue);
			boolean vValueOK = false;
			for (Object nValue : csp.getDomain(xj)) {
				assignment.setAssignment(xj, nValue);
				if (constraint.isSatisfiedWith(assignment)) {
					vValueOK = true;
					break;
				}
			}
			if (!vValueOK) {
				info.storeDomainFor(xi, csp.getDomain(xi));
				csp.removeValueFromDomain(xi, vValue);
				revised = true;
			}
		}
		return revised;
	}
}
