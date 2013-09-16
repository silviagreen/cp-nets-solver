package csp;

import java.util.ArrayList;
import java.util.List;


public abstract class SolutionStrategy {
	List<CSPStateListener> listeners = new ArrayList<CSPStateListener>();

	public void addCSPStateListener(CSPStateListener listener) {
		listeners.add(listener);
	}

	public void removeCSPStateListener(CSPStateListener listener) {
		listeners.remove(listener);
	}

	protected void fireStateChanged(CSP csp) {
		for (CSPStateListener listener : listeners)
			listener.stateChanged
			(csp != null ? csp.copyForPropagation() : null);
	}

	protected void fireStateChanged(Assignment assignment) {
		for (CSPStateListener listener : listeners)
			listener.stateChanged(assignment.copy());
	}

	public abstract List<Assignment> solve(CSP csp, boolean findAll);
	
	
}
