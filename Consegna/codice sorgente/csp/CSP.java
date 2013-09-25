package csp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class CSP {

	private Variable[] variables;
	private Domain[] domains;
	private List<Constraint> constraints;
	public int index= 0;
	
	private Hashtable<Variable, Integer> varIndexHash;
	
	private Hashtable<Variable, List<Constraint>> cnet;

	private CSP() {
	}
	
	public CSP(List<Variable> vars) {
		variables = new Variable[vars.size()];
		domains = new Domain[vars.size()];
		constraints = new ArrayList<Constraint>();
		varIndexHash = new Hashtable<Variable, Integer>();
		cnet = new Hashtable<Variable, List<Constraint>>();
		Domain emptyDomain = new Domain(new ArrayList<Object>(0));
		int index = 0;
		for (Variable var : vars) {
			variables[index] = var;
			domains[index] = emptyDomain;
			varIndexHash.put(var, index);
			cnet.put(var, new ArrayList<Constraint>());
			++index;
		}
	}

	public Variable[] getVariables() {
		return variables;
	}

	public int indexOf(Variable var) {
		return varIndexHash.get(var);
	}
	
	public Domain getDomain(Variable var) {
		return domains[varIndexHash.get(var)];
	}

	public void setDomain(Variable var, List<?> values) {
		domains[varIndexHash.get(var)] = new Domain(values);
	}
	
	public void restoreDomains(DomainRestoreInfo info) {
        for (Pair<Variable, Domain> pair : info.getSavedDomains()){
        		List<Object> list = new ArrayList<Object>();	
                setDomain(pair.getFirst(), pair.getSecond().fromDomainToList());
        }
}

	
	public void removeValueFromDomain(Variable var, Object value) {
		Domain currDomain = getDomain(var);
		List<Object> values = new ArrayList<Object>(currDomain.size());
		for (Object v : currDomain)
			if (!v.equals(value))
				values.add(v);
		setDomain(var, values);
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	
	public List<Constraint> getConstraints(Variable var) {
		return cnet.get(var);
	}

	public void addConstraint(Constraint constraint) {
		constraints.add(constraint);
		for (Variable var : constraint.getScope())
			cnet.get(var).add(constraint);
	}
	

    public Variable getNeighbor(Variable var, Constraint constraint) {
            List<Variable> scope = constraint.getScope();
            if (scope.size() == 2) {
                    if (var == scope.get(0))
                            return scope.get(1);
                    else if (var == scope.get(1))
                            return scope.get(0);
            }
            return null;
    }

	
	public CSP copyForPropagation() {
		CSP result = new CSP();
		result.variables = variables;
		result.domains = new Domain[domains.length];
		for (int i = 0; i < domains.length; i++)
			result.domains[i] = domains[i];
		result.constraints = constraints;
		result.varIndexHash = varIndexHash;
		result.cnet = cnet;
		return result;
	}
}