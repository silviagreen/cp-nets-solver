package csp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Assignment {
	
	List<Variable> variables;
	Hashtable<Variable, Object> variableToValue;

	public Assignment() {
		variables = new ArrayList<Variable>();
		variableToValue = new Hashtable<Variable, Object>();
	}
	
	public Object AssignmentForLast() {
		return variableToValue. get(variableToValue.size());
	}

	public int size(){
		return variableToValue.size();
	}
	
	public Object getAssignment(Variable var) {
		return variableToValue.get(var);
	}

	public void setAssignment(Variable var, Object value) {
		if (!variableToValue.containsKey(var))
			variables.add(var);
		variableToValue.put(var, value);
	}

	public void removeAssignment(Variable var) {
		if (hasAssignmentFor(var)) {
			variables.remove(var);
			variableToValue.remove(var);
		}
	}

	public boolean hasAssignmentFor(Variable var) {
		//System.out.println(variableToValue.get(var));
		return variableToValue.get(var) != null;
	}


	public boolean isConsistent(List<Constraint> constraints) {
		//per ogni vincolo con la variabile scelta
		for (Constraint cons : constraints)
			if (!cons.isSatisfiedWith(this)) //è consistente con quella assegnata provvisoriamente????
				return false;
		return true;
	}


	public boolean isComplete(List<Variable> vars) {
		for (Variable var : vars) {
			if (!hasAssignmentFor(var))
				return false;
		}
		return true;
	}
	

	public boolean isComplete(Variable[] vars) {
		for (Variable var : vars) {
			if (!hasAssignmentFor(var))
				return false;
		}
		return true;
	}


	public boolean isSolution(CSP csp) {
		return isConsistent(csp.getConstraints())
				&& isComplete(csp.getVariables());
	}

	public Assignment copy() {
		Assignment copy = new Assignment();
		for (Variable var : variables) {
			copy.setAssignment(var, variableToValue.get(var));
		}
		return copy;
	}

	@Override
	public String toString() {
		boolean comma = false;
		StringBuffer result = new StringBuffer("{");
		for (Variable var : variables) {
			if (comma)
				result.append(", ");
			result.append(var + "=" + variableToValue.get(var));
			comma = true;
		}
		result.append("}");
		return result.toString();
	}
	
	public String toValueString(){
		String val = "";
		for(Variable v :variables)
			val += (getAssignment(v)).toString();
		return val;
	}
}