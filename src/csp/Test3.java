package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import csp.ImprovedBacktrackingStrategy.Inference;

public class Test3 extends CSP{

	public static final Variable a = new Variable("A");
	public static final Variable b = new Variable("B");
	public static final Variable c = new Variable("C");
	
	private static List<Variable> collectVariables() {
        List<Variable> variables = new ArrayList<Variable>();
        variables.add(a);
        variables.add(b);
        variables.add(c);
        return variables;
}
	
	public Test3() {
        super(collectVariables());

        List<Integer> values = new ArrayList<Integer>();
	    values.add(0);
	    values.add(1);

        for (Variable var : getVariables())
                setDomain(var, values);
        
        /** primo vincolo*/
        Assignment ass1t = new Assignment();
        Assignment ass1h = new Assignment();
		ass1t.setAssignment(a, 0);
		ass1h.setAssignment(c, 1);
		Implies i1 = new Implies(a, Arrays.asList(c), Arrays.asList(ass1t, ass1h));
		addConstraint(i1);
		
		 Assignment ass2t = new Assignment();
		 Assignment ass2h = new Assignment();
		 ass2t.setAssignment(a, 0);
		 ass2h.setAssignment(c, 1);
		 Implies i2 = new Implies(a, Arrays.asList(c), Arrays.asList(ass2t, ass2h));
		 addConstraint(i2);
		 
		 Assignment ass3h = new Assignment();
		 Assignment ass3t = new Assignment();
		 ass3h.setAssignment(b, 0);
		 ass3t.setAssignment(c, 1);
		 Implies i3 = new Implies(c, Arrays.asList(b), Arrays.asList(ass3t, ass3h));
		 addConstraint(i3);
		 
		 Assignment ass4h = new Assignment();
		 Assignment ass4t = new Assignment();
		 ass4h.setAssignment(b, 1);
		 ass4t.setAssignment(c, 0);
		 Implies i4 = new Implies(c, Arrays.asList(b), Arrays.asList(ass4t, ass4h));
		 addConstraint(i4);
		 
		 Assignment ass5t = new Assignment();
		 Assignment ass5h1 = new Assignment();

	
		 ass5h1.setAssignment(c, 1);
		 ass5t.setAssignment(a, 0);

		 Implies i5 = new Implies(a, Arrays.asList(a,c), Arrays.asList(ass5t, ass5h1));
		 addConstraint(i5);
		
		 
		
		 Assignment ass7t = new Assignment();
		 Assignment ass7h1 = new Assignment();
	
		
		 ass7h1.setAssignment(c, 0);
		 ass7t.setAssignment(a, 1);

		 Implies i7 = new Implies(a, Arrays.asList(a,c), Arrays.asList(ass7t, ass7h1));	
		addConstraint(i7);
		
		 
	}
	
	public static void main(String[] args) {
		ImprovedBacktrackingStrategy s = new ImprovedBacktrackingStrategy();
		s.setInference(Inference.FORWARD_CHECKING);
		
		Test3 t = new Test3();
		List<Assignment> res = s.solve(t, true);
	
		if(res.isEmpty()) System.out.println("Nessuna soluzione ottima");
		for(Assignment a : res)
			System.out.println("SOL=" + a);
	}

}
