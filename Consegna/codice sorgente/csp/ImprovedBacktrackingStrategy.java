package csp;


import java.util.List;



public class ImprovedBacktrackingStrategy extends BacktrackingStrategy {
	protected Inference inferenceStrategy = Inference.NONE;



	/** Selects the algorithm for INFERENCE. */
	public void setInference(Inference iStrategy) {
		inferenceStrategy = iStrategy;
	}

	

	public List<Assignment> solve(CSP csp, boolean findAll) {
		if (inferenceStrategy == Inference.AC3) {
			System.out.println("AC3 selected");
			DomainRestoreInfo info = new AC3Strategy().reduceDomains(csp);
			if (!info.isEmpty()) {
				//fireStateChanged(csp);
				if (info.isEmptyDomainFound())
					return null;
			}
		}
		return super.solve(csp, findAll);
	}


	@Override
	protected DomainRestoreInfo inference(Variable var, Assignment assignment,
			CSP csp) {
		switch (inferenceStrategy) {
		case FORWARD_CHECKING:
			return doForwardChecking(var, assignment, csp);
		case AC3:
			return new AC3Strategy().reduceDomains(var, assignment.getAssignment(var), csp);
		default:
			return new DomainRestoreInfo().compactify();
		}
	}





	// //////////////////////////////////////////////////////////////
	// inference algorithms

	/** forward checking. */
	private DomainRestoreInfo doForwardChecking(Variable var, Assignment assignment, CSP csp) {
		System.out.println("Forward checking su " + var.getName());
		DomainRestoreInfo result = new DomainRestoreInfo();
		for (Constraint constraint : csp.getConstraints(var)) {
			List<Variable> scope = constraint.getScope();
			if (scope.size() == 2) {
				for (Variable neighbor : constraint.getScope()) {
					if (!assignment.hasAssignmentFor(neighbor)) {
						if (revise(neighbor, constraint, assignment, csp,
								result)) {
							if (csp.getDomain(neighbor).isEmpty()) {
								result.setEmptyDomainFound(true);
								return result;
							}
						}
					}
				}
			}
		}
		return result;
	}

	private boolean revise(Variable var, Constraint constraint,
			Assignment assignment, CSP csp, DomainRestoreInfo info) {

		boolean revised = false;
		for (Object value : csp.getDomain(var)) {
			assignment.setAssignment(var, value);
			if (!constraint.isSatisfiedWith(assignment)) {
				info.storeDomainFor(var, csp.getDomain(var));
				csp.removeValueFromDomain(var, value);
				revised = true;
			}
			assignment.removeAssignment(var);
		}
		return revised;
	}





	public enum Inference {
		NONE, FORWARD_CHECKING, AC3
	}
	
	
}
