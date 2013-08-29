package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            Assignment ass = new Assignment();
    		ass.setAssignment(a, 1);
    		ass.setAssignment(c, 1);
    		Implies one = new Implies(a, Arrays.asList(c), ass);
    		
    		/** secondo vincolo*/
    		Assignment ass1 = new Assignment();
    		ass1.setAssignment(a, 0);
    		ass1.setAssignment(c, 0);
    		Implies two = new Implies(a, Arrays.asList(c), ass1);
    		
    		/** terzo vincolo*/
    		Assignment ass2 = new Assignment();
    		ass2.setAssignment(a, 1);
    		ass2.setAssignment(b, 1);
    		Implies three = new Implies(b, Arrays.asList(a), ass2);
    		
    		/** quarto vincolo*/
    		Assignment ass3 = new Assignment();
    		ass3.setAssignment(a, 0);
    		ass3.setAssignment(b, 0);
    		Implies four = new Implies(b, Arrays.asList(a), ass3);
    		
    		/** quinto vincolo*/
    		Assignment ass4 = new Assignment();
    		ass4.setAssignment(b, 1);
    		ass4.setAssignment(c, 1);
    		Implies five = new Implies(c, Arrays.asList(b), ass4);
    		
    		/** sesto vincolo*/
    		Assignment ass5 = new Assignment();
    		ass5.setAssignment(b, 0);
    		ass5.setAssignment(c, 0);
    		Implies six = new Implies(c, Arrays.asList(b), ass5);
            
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
    	
		BacktrackingStrategy resolution = new BacktrackingStrategy();
		List<Assignment> result = resolution.solve(t, true);
		for(Assignment a : result)
		 System.out.println("SOL=" + a);
	}
	
		
		
		
		
		
		
		
		
		

	

}
