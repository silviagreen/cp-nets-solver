package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import csp.ImprovedBacktrackingStrategy.Inference;

public class test extends CSP{

	
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

    public test() {
            super(collectVariables());

            List<Integer> values = new ArrayList<Integer>();
		    values.add(0);
		    values.add(1);

            for (Variable var : getVariables())
                    setDomain(var, values);

            
    		
    		
    		/** primo vincolo*/
            Assignment asst = new Assignment();
            Assignment assh = new Assignment();
    		asst.setAssignment(a, 1);
    		assh.setAssignment(c, 1);
    		Implies one = new Implies(a, Arrays.asList(c), Arrays.asList(asst, assh));
    		
    		/** secondo vincolo*/
    		Assignment ass1t = new Assignment();
    		Assignment ass1h = new Assignment();
    		ass1t.setAssignment(a, 0);
    		ass1h.setAssignment(c, 0);
    		Implies two = new Implies(a, Arrays.asList(c), Arrays.asList(ass1t, ass1h));
    		
    		/** terzo vincolo*/
    		Assignment ass2t = new Assignment();
    		Assignment ass2h = new Assignment();
    		ass2h.setAssignment(a, 1);
    		ass2t.setAssignment(b, 1);
    		Implies three = new Implies(b, Arrays.asList(a), Arrays.asList(ass2t, ass2h));
    		
    		/** quarto vincolo*/
    		Assignment ass3t = new Assignment();
    		Assignment ass3h = new Assignment();
    		ass3h.setAssignment(a, 0);
    		ass3t.setAssignment(b, 0);
    		Implies four = new Implies(b, Arrays.asList(a), Arrays.asList(ass3t, ass3h));
    		
    		/** quinto vincolo*/
    		Assignment ass4t = new Assignment();
    		Assignment ass4h = new Assignment();
    		ass4h.setAssignment(b, 1);
    		ass4t.setAssignment(c, 1);
    		Implies five = new Implies(c, Arrays.asList(b), Arrays.asList(ass4t, ass4h));
    		
    		/** sesto vincolo*/
    		Assignment ass5t = new Assignment();
    		Assignment ass5h = new Assignment();
    		ass5h.setAssignment(b, 0);
    		ass5t.setAssignment(c, 0);
    		Implies six = new Implies(c, Arrays.asList(b), Arrays.asList(ass5t, ass5h));
            
            addConstraint(one);
            addConstraint(two);
            addConstraint(three);
            addConstraint(four);
            addConstraint(five);
            addConstraint(six);
           
    }
		
	
		
		
    public static void main(String[] args){
    	test t = new test();
//    	for(Variable v : t.getVariables()){
//    		System.out.println( v.getName() + ": " + t.getConstraints(v));
//    	}
    	
/*		BacktrackingStrategy resolution = new BacktrackingStrategy();
		List<Assignment> result = resolution.solve(t, true);
		for(Assignment a : result)
		 System.out.println("SOL=" + a);*/
    	
		ImprovedBacktrackingStrategy resolution = new ImprovedBacktrackingStrategy();
		
		resolution.setInference(Inference.FORWARD_CHECKING);
		List<Assignment> result = resolution.solve(t, true);
		for(Assignment a : result)
		 System.out.println("SOL=" + a);
	}
	
		
		
		
		
		
		
		
		
		

	

}
