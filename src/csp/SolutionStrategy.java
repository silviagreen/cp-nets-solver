package csp;

import java.util.List;


public abstract class SolutionStrategy {
	public abstract List<Assignment> solve(CSP csp, boolean findAll);
	
	
}
