import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import com.sun.istack.internal.Pool.Impl;

import csp.Assignment;
import csp.Constraint;
import csp.CpNetCSP;
import csp.Implies;
import csp.ListUtils;
import csp.ImprovedBacktrackingStrategy.Inference;
import csp.Variable;

public class CPNet {

	private List<Vertex> adjList;
	private int nVertex;
	private int nEdges;

	public CPNet(int nVertex, int nEdges) throws IllegalArgumentException {
		/*
		 * Il numero totale di archi può essere al massimo N * (N-1)
		 */
		if (nEdges > nVertex * (nVertex - 1)) {
			throw new IllegalArgumentException(
					"Il numero di archi non è appropriato rispetto al numero di vertici inseriti.");
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

	private void generateRandomCPNet() {
		int edgesCounter = 0;
		Random randomizer = new Random(System.nanoTime());
		while (edgesCounter < this.nEdges) {
			Integer startVertex = Integer.valueOf(randomizer
					.nextInt(this.nVertex));
			Integer endVertex = Integer.valueOf(randomizer
					.nextInt(this.nVertex));
			/*
			 * Se i due nodi sono diversi e l'arco non esiste già, allora lo
			 * inserisco
			 */
			if (!startVertex.equals(endVertex)
					&& !this.adjList.get(startVertex).isParentOf(endVertex)) {
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
				while (!trovato
						&& i < workingcopy.adjList.get(m).getParents().size()) {
					if (workingcopy.adjList.get(m).getParents().get(i)
							.intValue() == n.getID().intValue()) {
						workingcopy.adjList.get(m).getParents()
								.remove(i.intValue());
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
			if (workingcopy.adjList.get(i).getChildren().size() > 0
					|| workingcopy.adjList.get(i).getParents().size() > 0) {
				existedge = true;
			}
			i++;
		}
		return existedge;
	}

	public static void main(String[] args) {
//TODO: trattare var indip e committare
		// Inference strategy = Inference.NONE;
		CPNet c = new CPNet(15, 5);
		List<Variable> variables = new ArrayList<Variable>();
		List<Implies> constraints = new ArrayList<Implies>();

		for (Vertex v : c.getAdjList()) {
			Variable var = new Variable(Integer.toString(v.getID()));
			variables.add(var);
		}
		System.out.println(variables);
		// for(Vertex v : c.getAdjList()){
		// String ris = v.getID() + " ha parens ";
		// for(Integer p : v.getParents()){
		// ris += String.valueOf(p);
		// }
		// System.out.println(ris);
		// }

		for (Vertex v : c.getAdjList()) {

			System.out.println("vertice: " + v.getID());
			// la var che sto considerando � la tesi del vincolo.
			// con isAffirmedValue la tesi vale 1, altrimento vale 0
			// le ipotesi sono il binary value

			v.setAffirmedLists();
			//System.out.println("fatte liste");

			Variable var = ListUtils.getVariable(variables,
					String.valueOf(v.getID()));

			// lista positivi
			System.out.println("POSITIVI");
			List<Assignment> pos = new ArrayList<Assignment>();
			Assignment thesis1 = new Assignment();
			List<Variable> hp1 = new ArrayList<Variable>();

			thesis1.setAssignment(var, 1);
			pos.add(thesis1);

			//char[] binaryValue = new char[v.getParents().size()];
			
			if (!v.getParents().isEmpty()) {
				for (Preference p : v.affirmed) {
					
					//int[] binVal = ListUtils.decToBin(p.getBinaryValue());
					List<Integer> binVal = ListUtils.decToBin(p.getBinaryValue());
					
					
					//String bv = Integer.toString(p.getBinaryValue());
					Assignment a = new Assignment();
					//int diff = (v.getParents().size()) - (bv.length());
					int diff = (v.getParents().size()) - (binVal.size());
					System.out.println("bin val " + binVal.toString() +" has length " + binVal.size() + " con parents " + v.getParents().size());
					System.out.println("diff = " + diff);
					//List<Integer> binvalList = ListUtils.fromArrayToIntList(binVal);
					while (diff != 0) {
						//bv = (new StringBuffer(bv)).insert(0, "0").toString();
						
						binVal.add(0, 0);
						System.out.println("binval list = " + binVal.toString());
						diff--;
//						if(diff == 0) {
//							for(int ii = 0; ii < binvalList.size(); ii++){
//								int intero =(int) binvalList.get(ii);
//								binVal[ii] = intero;
//							}
//						}
					}
					System.out.println(v.getID() + " ha binary value " + binVal.toString() + " e ha " + v.getParents().size() + " parents");
					//binaryValue = bv.toCharArray();
					int i = 0;
				
					
					//Integer pref = new Integer(binVal.get(i));
					//Integer pref = new Integer(	Character.getNumericValue(binaryValue[i]));
					//System.out.println("BIN VAL " + binaryValue + " has length " + binaryValue.length);
					//while (i != binaryValue.length) {
					while (i != binVal.size()) {
						Variable varHp = ListUtils.getVariable(variables, String.valueOf(v.getParents().get(i)));
						//pref = new Integer(Character.getNumericValue(binaryValue[i]));
						Integer pref = new Integer((binVal.get(i)));
						System.out.println("preferenza: " + varHp.toString() + ", " + pref.toString());
						a.setAssignment(varHp, pref);
						if (!hp1.contains(varHp)) {
							hp1.add(varHp);
						}
						i++;
						// variabili in hp1
					}// Character.getNumericValue(binaryValue[i])
					pos.add(a);
					
					System.out.println("nell'ipotesi ci sono " + hp1.size() + " var d'ipotesi");
				}
			}
			Implies i = new Implies(var, hp1, pos);
			constraints.add(i);
			
			// lista negativi
			System.out.println("NEGATIVI");
			List<Assignment> neg = new ArrayList<Assignment>();
			Assignment thesis0 = new Assignment();
			List<Variable> hp0 = new ArrayList<Variable>();

			thesis0.setAssignment(var, 0);
			neg.add(thesis0);

			//char[] binaryValue0 = new char[v.getParents().size()];// aggiungere
																	// zeri
																	// davanti
																	// se ci
																	// sono
																	// spazi
																	// vuoti
			if(!v.getParents().isEmpty()){
			for (Preference p : v.notAffirmed) {
				// System.out.println("preferenze negative");
				//String bv = Integer.toString(p.getBinaryValue());
				List<Integer> binVal = ListUtils.decToBin(p.getBinaryValue());
				Assignment a = new Assignment();
				
				int diff = (v.getParents().size()) - (binVal.size());
				System.out.println("bin val " + binVal.toString() +" has length " + binVal.size());
				System.out.println("diff = " + diff);
				while (diff != 0) {
					//bv = (new StringBuffer(bv)).insert(0, "0").toString();
					binVal.add(0, 0);
					diff--;
				}
				//binaryValue0 = bv.toCharArray();
				System.out.println(v.getID() + " ha binary value " + binVal.toString() + " e ha " + v.getParents().size() + " parents");

				int i0 = 0;
				
				//Integer pref = new Integer(Character.getNumericValue(binaryValue0[i0]));
				while (i0 != binVal.size()) {
					Variable varHp = ListUtils.getVariable(variables, String.valueOf(v.getParents().get(i0)));
					Integer pref = new Integer((binVal.get(i0)));
					a.setAssignment(varHp, pref);
					if (!hp0.contains(varHp)) {
						hp0.add(varHp);
					}
					i0++;
					// var in hp0
				}// Character.getNumericValue(binaryValue[i])
				neg.add(a);
				
			}
		}	
			Implies i0 = new Implies(var, hp0, neg);
			constraints.add(i0);

		}

		CpNetCSP csp = new CpNetCSP(variables, constraints);
		 System.out.println("provo a risolvere");
		 List<Assignment> result = csp.solve(Inference.AC3, csp, true);
		 for(Assignment a : result)
		 System.out.println("SOL=" + a);

		for (Constraint i : csp.getConstraints())
			System.out.println(i);

		ViewGraph view = new ViewGraph(c);
		view.setVisible(true);
	}

}
