package csp;

import java.util.ArrayList;
import java.util.List;

import csp.ImprovedBacktrackingStrategy.Inference;

public class CpNetCSP extends CSP{

	static List<Variable> variables;
	static List<Implies> contraints;
	List<Integer> values = new ArrayList<Integer>();
    
	
	public CpNetCSP(List<Variable> variables, List<Implies> contraints){
		super(variables);
		this.contraints = contraints;
		this.variables = variables;
		
		
		values.add(0);
	    values.add(1);
	    
	    for (Variable var : getVariables())
            setDomain(var, values);
	    
	    for(Implies i : contraints)
	    	addConstraint(i);
	}
	
	public List<Assignment> solve(Inference iStrategy, CpNetCSP csp, boolean findAll){
		ImprovedBacktrackingStrategy resolution = new ImprovedBacktrackingStrategy();	
		resolution.setInference(iStrategy);
		return resolution.solve(csp, findAll);
	}
	
	

}
