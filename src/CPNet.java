import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import com.sun.istack.internal.Pool.Impl;

import csp.Assignment;
import csp.CSP;
import csp.Constraint;
import csp.CpNetCSP;
import csp.Implies;
import csp.ListUtils;
import csp.ImprovedBacktrackingStrategy.Inference;
import csp.Variable;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class CPNet {

	private List<Vertex> adjList;
	private int nVertex;
	private int nEdges;
	private boolean isCyclic;
	private PartialOrderSolutionGraph p;
	
	boolean getIsCyclic(){
		return isCyclic;
	}

	PartialOrderSolutionGraph getPartialOrderSol(){
		return p;
	}
	List<Vertex> indepList = new ArrayList<Vertex>();
        private int MAX_ITERATION_LS=10000;

	public CPNet(int nVertex, int nEdges) throws IllegalArgumentException {
		/*
		 * Il numero totale di archi può essere al massimo N * (N-1)
		 */
		if (nEdges > nVertex * (nVertex - 1)) {
			throw new IllegalArgumentException("Il numero di archi non è appropriato rispetto al numero di vertici inseriti.");
		}
		this.nVertex = nVertex;
		this.nEdges = nEdges;
		this.adjList = new ArrayList<Vertex>(this.nVertex);
		for (int i = 0; i < this.nVertex; i++) {
			adjList.add(new Vertex(i));
		}
		generateRandomCPNet();
	}

	public CPNet(CPNet origin) {
		this.nEdges = origin.nEdges;
		this.nVertex = origin.nVertex;
		this.adjList = new ArrayList<>();
		for (Vertex v : origin.adjList) {
			this.adjList.add(new Vertex(v));
		}
	}

	public List<Vertex> getAdjList() {
		return adjList;
	}

	public void setIndepList() {
		for (Vertex v : adjList) {
			if (v.getParents().isEmpty())
				indepList.add(v);
		}
	}

	private void generateRandomCPNet() {
		int edgesCounter = 0;
		Random randomizer = new Random(System.nanoTime());
		while (edgesCounter < this.nEdges) {
			Integer startVertex = Integer.valueOf(randomizer.nextInt(this.nVertex));
			Integer endVertex = Integer.valueOf(randomizer.nextInt(this.nVertex));
			/*
			 * Se i due nodi sono diversi e l'arco non esiste già, allora lo
			 * inserisco
			 */
			if (!startVertex.equals(endVertex) && !this.adjList.get(startVertex).isParentOf(endVertex)) {
				// System.out.println(startVertex + "->" + endVertex);
				this.adjList.get(startVertex).addChild(endVertex);
				this.adjList.get(endVertex).addParent(startVertex);
				edgesCounter++;
			}
			/*
			 * Altrimenti non faccio nulla, aspetto la prossima iterazione
			 */
		}
		generatePreferences();
	}

	private void generatePreferences() {
		for (Vertex v : this.adjList) {
			// System.out.println("Vertice: " + v.getID());
			v.generatePreferences();
		}
	}

	public List<Vertex> getCPNet() {
		return this.adjList;
	}

	/*
	 * toString di test, si può modificare
	 */
	@Override
	public String toString() {
		String s = "------------------------------------------\n";
		for (Vertex v : this.adjList) {
			s += v + "------------------------------------------\n";
		}
		return s;
	}

	public boolean isCyclic() {
		CPNet workingcopy = new CPNet(this);
		Stack<Vertex> vertexnoparents = new Stack<>();
		for (Vertex v : workingcopy.adjList) {
			if (v.getParents().isEmpty()) {
				vertexnoparents.add(v);
			}
		}
		while (!vertexnoparents.isEmpty()) {
			Vertex n = vertexnoparents.pop();
			while (!n.getChildren().isEmpty()) {
				Integer m = n.getChildren().remove(n.getChildren().size() - 1);
				Integer i = new Integer(0);
				boolean trovato = false;
				while (!trovato && i < workingcopy.adjList.get(m).getParents().size()) {
					if (workingcopy.adjList.get(m).getParents().get(i).intValue() == n.getID().intValue()) {
						workingcopy.adjList.get(m).getParents().remove(i.intValue());
						trovato = true;
					}
					i++;
				}
				if (workingcopy.adjList.get(m).getParents().isEmpty()) {
					vertexnoparents.add(workingcopy.adjList.get(m));
				}
			}
		}
		boolean existedge = false;
		Integer i = new Integer(0);
		while (!existedge && i < workingcopy.adjList.size()) {
			if (workingcopy.adjList.get(i).getChildren().size() > 0 || workingcopy.adjList.get(i).getParents().size() > 0) {
				existedge = true;
			}
			i++;
		}
		isCyclic = existedge;
		return existedge;
	}

	public Assignment solveAcyclicCPNet(Assignment result, List<Variable> vars) {

		setIndepList();
		for (Vertex v : indepList) {
			Variable var = ListUtils.getVariable(vars, v.getID().toString());
			// v � una variabile indipendente
			// v ha una sola preferenza
			Preference p = v.getPreferences().get(0);

			if (p.getIsAffirmedValue())
				result.setAssignment(var, 1);
			else
				result.setAssignment(var, 0);

		}

		System.out.println(result);

		List<Vertex> nonInped = ListUtils.subtract(adjList, indepList);
		int index = 0;
		while (!nonInped.isEmpty()) {

			boolean notAssigned = false;
			Vertex v = nonInped.get(index);
			// System.out.println("Considero vertice " + v.getID().toString() +
			// " con " + v.getParents().size() + " parents.");
			String binaryValue = "";
			Variable var = ListUtils.getVariable(vars, v.getID().toString());

			for (Integer i : v.getParents()) {
				Variable temp = ListUtils.getVariable(vars, i.toString());
				System.out.println("cerco assegnamento per " + i.toString() + " in " + result + " : " + result.getAssignment(temp));
				if (!result.hasAssignmentFor(temp)) {
					// System.out.println("parent " + i.toString() +
					// "non assegnato");
					index++;
					if (index == nonInped.size())
						index = 0;
					notAssigned = true;
					break;
				} else {
					binaryValue += result.getAssignment(temp).toString();
				}
			}

			if (notAssigned)
				continue;
			int binToChoose = ListUtils.fromBinToInt(binaryValue);
			for (Preference p : v.getPreferences()) {
				if (p.getBinaryValue() == binToChoose) {
					if (p.getIsAffirmedValue()) {
						result.setAssignment(var, 1);
						System.out.println(result);
					} else {
						result.setAssignment(var, 0);
						System.out.println(result);
					}
					System.out.println("rimuovo non indip " + nonInped.get(index) + "restano " + nonInped.size() + "parents");
					nonInped.remove(index);
					index = 0;
				}
			}

		}

		return result;
	}

	private List<Variable> generateVariables() {
		List<Variable> variables = new ArrayList<Variable>();
		for (Vertex v : getAdjList()) {
			Variable var = new Variable(Integer.toString(v.getID()));
			variables.add(var);
		}
		return variables;
	}
	
	private void generateConstraint(Vertex v, List<Implies> constraints, List<Variable> variables, boolean affirmed){
		Variable var = ListUtils.getVariable(variables, String.valueOf(v.getID()));
		List<Assignment> assignments = new ArrayList<Assignment>();
		Assignment thesis1 = new Assignment();
		List<Variable> hp1 = new ArrayList<Variable>();

		if ((!v.getParents().isEmpty() && affirmed)|| (v.getParents().isEmpty() && affirmed && v.getPreferences().get(0).getIsAffirmedValue()) ) {
			thesis1.setAssignment(var, 1);
			assignments.add(thesis1);
		}
		else if ((!v.getParents().isEmpty() && !affirmed) || (v.getParents().isEmpty() && !affirmed && !v.getPreferences().get(0).getIsAffirmedValue())) {
			thesis1.setAssignment(var, 0);
			assignments.add(thesis1);
		}

		List<Preference> preferences = null;
		if(affirmed) preferences = v.affirmed;
		else preferences=v.notAffirmed;

		if (!v.getParents().isEmpty()) {
			for (Preference p :preferences) {
				Assignment a = new Assignment();
				List<Integer> binVal = p.getBinaryValueInList(v.getParents().size());

				int i = 0;

				while (i != binVal.size()) {
					Variable varHp = ListUtils.getVariable(variables, String.valueOf(v.getParents().get(i)));
					Integer pref = new Integer((binVal.get(i)));
					// System.out.println("preferenza: " + varHp.toString()+ ", " + pref.toString());
					a.setAssignment(varHp, pref);
					if (!hp1.contains(varHp)) {
						hp1.add(varHp);
					}
					i++;
					// variabili in hp1
				}
				assignments.add(a);

				System.out.println("nell'ipotesi ci sono " + hp1.size() + " var d'ipotesi");
			}
		}
		if (!v.getParents().isEmpty() || (v.getParents().isEmpty() && affirmed && v.getPreferences().get(0).getIsAffirmedValue()) || (!v.getParents().isEmpty() || (v.getParents().isEmpty() && !affirmed && !v.getPreferences().get(0).getIsAffirmedValue()))) {
			Implies i = new Implies(var, hp1, assignments);
			constraints.add(i);
		}
	}
	
	private List<Implies> generateConstraints(List<Variable> variables){
		List<Implies> constraints = new ArrayList<Implies>();
		for (Vertex v : getAdjList()) {

			// System.out.println("vertice: " + v.getID());
			// la var che sto considerando � la tesi del vincolo.
			// con isAffirmedValue la tesi vale 1, altrimenti vale 0
			// le ipotesi sono il binary value

			v.setAffirmedLists();
			generateConstraint(v, constraints, variables, true);
			generateConstraint(v, constraints, variables, false);

//			Variable var = ListUtils.getVariable(variables, String.valueOf(v.getID()));
//
//			// lista positivi
//			// System.out.println("POSITIVI");
//			List<Assignment> pos = new ArrayList<Assignment>();
//			Assignment thesis1 = new Assignment();
//			List<Variable> hp1 = new ArrayList<Variable>();
//
//			if (!v.getParents().isEmpty() || (v.getParents().isEmpty() && v.getPreferences().get(0).getIsAffirmedValue())) {
//				thesis1.setAssignment(var, 1);
//				pos.add(thesis1);
//			}
//
//			if (!v.getParents().isEmpty()) {
//				for (Preference p : v.affirmed) {
//					Assignment a = new Assignment();
//					List<Integer> binVal = p.getBinaryValueInList(v.getParents().size());
//
//					int i = 0;
//
//					while (i != binVal.size()) {
//						Variable varHp = ListUtils.getVariable(variables, String.valueOf(v.getParents().get(i)));
//						Integer pref = new Integer((binVal.get(i)));
//						// System.out.println("preferenza: " + varHp.toString()+ ", " + pref.toString());
//						a.setAssignment(varHp, pref);
//						if (!hp1.contains(varHp)) {
//							hp1.add(varHp);
//						}
//						i++;
//						// variabili in hp1
//					}
//					pos.add(a);
//
//					System.out.println("nell'ipotesi ci sono " + hp1.size() + " var d'ipotesi");
//				}
//			}
//			if (!v.getParents().isEmpty() || (v.getParents().isEmpty() && v.getPreferences().get(0).getIsAffirmedValue())) {
//				Implies i = new Implies(var, hp1, pos);
//				constraints.add(i);
//			}
//
//			// lista negativi
//			// System.out.println("NEGATIVI");
//			List<Assignment> neg = new ArrayList<Assignment>();
//			Assignment thesis0 = new Assignment();
//			List<Variable> hp0 = new ArrayList<Variable>();
//
//			if (!v.getParents().isEmpty() || (v.getParents().isEmpty() && !v.getPreferences().get(0).getIsAffirmedValue())) {
//				thesis0.setAssignment(var, 0);
//				neg.add(thesis0);
//			}
//
//			if (!v.getParents().isEmpty()) {
//				for (Preference p : v.notAffirmed) {
//					Assignment a = new Assignment();
//					List<Integer> binVal = p.getBinaryValueInList(v.getParents().size());
//
//					int i0 = 0;
//
//					while (i0 != binVal.size()) {
//						Variable varHp = ListUtils.getVariable(variables, String.valueOf(v.getParents().get(i0)));
//						Integer pref = new Integer((binVal.get(i0)));
//						a.setAssignment(varHp, pref);
//						if (!hp0.contains(varHp)) {
//							hp0.add(varHp);
//						}
//						i0++;
//						// var in hp0
//					}
//					neg.add(a);
//
//				}
//			}
//			if (!v.getParents().isEmpty() || (v.getParents().isEmpty() && !v.getPreferences().get(0).getIsAffirmedValue())) {
//				Implies i0 = new Implies(var, hp0, neg);
//				constraints.add(i0);
//			}
//
		}
			
		
		
		return constraints;
	}

	public CpNetCSP generateCSP() {
		List<Variable> variables = generateVariables();
		List<Implies> constraints = generateConstraints(variables);
		
		CpNetCSP csp = new CpNetCSP(variables, constraints);
		return csp;
	}

	public List<Assignment> getOptimalSolution(Inference strategy, boolean findAll) {
		List<Assignment> result = null;
		if (isCyclic()) {
			CpNetCSP csp = generateCSP();
			result = csp.solve(strategy, csp, true);

			for (Constraint i : csp.getConstraints())
				System.out.println(i);
		} else {
			
			List<Variable> variables = generateVariables();
			result = new ArrayList<Assignment>();
			Assignment a = solveAcyclicCPNet(new Assignment(), variables);
			result.add(a);
			System.out.println("La cp net aciclica ha come unica soluzione " + a.toString());
		}
		if(!result.isEmpty()){
			p = new PartialOrderSolutionGraph(this);
			p.setPartialOrderSolutions(new Solution(result.get(0).toValueString()));
		}
		else p = null;
		return result;
	}

        private Instance generateRandomAssignment(){
            Instance ass=new Instance();
            for(int i=0;i<this.adjList.size();i++){
                Random randomizer = new Random(System.nanoTime());
                Boolean b=randomizer.nextBoolean();
                ass.add(b);
            }
            return ass;
        }
        
        private Instance nextNeighbor(Instance currentass){
            ArrayList<Integer> randomarray=new ArrayList<>();
            for(int i=0;i<this.adjList.size();i++){
                randomarray.add(i);
            }
            long seed=System.nanoTime();
            Collections.shuffle(randomarray, new Random(seed));
            boolean betterfound=false;
            int i=0;
            while(!betterfound && i<randomarray.size()){
                int posdifferent=randomarray.remove(randomarray.size()-1);
                Vertex v=this.adjList.get(posdifferent);
                List<Integer> parents=v.getParents();
                int indexrow=0;
                for(int j=0;j<parents.size();j++){
                    if(currentass.get(parents.get(j))){
                        indexrow+=(int) Math.pow(2,parents.size()-j-1);
                    }
                }
                if(v.getPreferences().get(indexrow).getIsAffirmedValue()!=currentass.get(posdifferent)){
                    currentass.set(posdifferent, !currentass.get(posdifferent));
                    betterfound=true;
                }
                i++;
            }
            if(betterfound){
                return currentass;
            }
            else{
                return null;
            }
        }
        
        public Instance solveWithLocalSearch(){
            Instance solution=generateRandomAssignment();
            boolean bestfound=false;
            int i=0;
            while(!bestfound && i<MAX_ITERATION_LS){
                Instance next=nextNeighbor(solution);
                if(next==null){
                    bestfound=true;
                }
                else{
                    solution=next;
                }
                i++;
            }
            if(bestfound){
                System.out.println("SOLUZIONE OTTIMA TROVATA "+i);
            }
            else{
                System.out.println("SOLUZIONE NON OTTIMA TROVATA"+i);
            }
            return solution;
        }
        
	private String setStrategyName(Inference strategy) {
		String str = "";
		if (!isCyclic)
			str = "Sweep Forward";
		else {
			switch (strategy) {
			case NONE:
				str = "Backtracking";
				break;
			case AC3:
				str = "Backtracking + propagazione di vincoli (AC3)";
				break;
			case FORWARD_CHECKING:
				str = "Backtracking + forward checking";
				break;
			}
		}
		
		return str;

	}
        
	public static void main(String[] args) {
		//CPNet c = new CPNet(30, 15);
		CPNet c = new CPNet(4, 8);
		CPNet cc = new CPNet(c);
		
		Inference strategy = Inference.NONE;
		long start = System.currentTimeMillis();
		List<Assignment> list = c.getOptimalSolution(strategy, true);
		long end = System.currentTimeMillis();
		double timeCalc = ((end-start)/1000.0);
		
		//print di test
		if (list.isEmpty())
			System.out.println("NESSUNA SOLUZIONE  OTTIMA");
		for (Assignment a : list)
			System.out.println("SOL=" + a);
		//System.out.println("Tempo di calcolo: " + ((end-start)/1000.0) + " s");
		//System.out.println(c.p.toString());
                
                Instance solution=cc.solveWithLocalSearch();
                System.out.println(solution.toString());
                
       
                ViewGraph view = new ViewGraph(c, list, timeCalc, c.setStrategyName(strategy));
        		view.setVisible(true); 
		
/*        		//test per generare ordinamento parziale delle solutioni
		CPNet cp = new CPNet(3,2);
		PartialOrderSolutionGraph p = new PartialOrderSolutionGraph(cp);
		List<Assignment> list = cp.getOptimalSolution(Inference.NONE, true);
		
		if(list.isEmpty()) System.out.println("NO SOL");
		
		p.setPartialOrderSolutions(new Solution(list.get(0).toValueString()));
		cp.setP(p);
		
		Inference strategy = Inference.NONE;
		ViewGraph view = new ViewGraph(cp, list, 0, cp.setStrategyName(strategy));
		view.setVisible(true); 
		System.out.println(p.toString());*/
		
                
	}

	

	public void setP(PartialOrderSolutionGraph p) {
		this.p = p;
	}

}
