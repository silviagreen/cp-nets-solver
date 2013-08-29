package csp;
/**
 * Interface which allows interested clients to register at a solution
 * strategy an follow their progress step by step.
 * 
 * @author Ruediger Lunde
 */
public interface CSPStateListener {
	void stateChanged(CSP csp);
	void stateChanged(Assignment assignment);
}
