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

	List<Vertex> indepList = new ArrayList<Vertex>();

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

	public static void main(String[] args) {
		// TODO: trattare var indip e committare
		// Inference strategy = Inference.NONE;
		CPNet c = new CPNet(30, 15);

		List<Variable> variables = new ArrayList<Variable>();
		for (Vertex v : c.getAdjList()) {
			Variable var = new Variable(Integer.toString(v.getID()));
			variables.add(var);
		}

		if (c.isCyclic()) {

			List<Implies> constraints = new ArrayList<Implies>();

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
				// System.out.println("fatte liste");

				Variable var = ListUtils.getVariable(variables,
						String.valueOf(v.getID()));

				// lista positivi
				System.out.println("POSITIVI");
				List<Assignment> pos = new ArrayList<Assignment>();
				Assignment thesis1 = new Assignment();
				List<Variable> hp1 = new ArrayList<Variable>();

				if(!v.getParents().isEmpty() || (v.getParents().isEmpty() && v.getPreferences().get(0).getIsAffirmedValue()) ){
				thesis1.setAssignment(var, 1);
				pos.add(thesis1);}
				
				// char[] binaryValue = new char[v.getParents().size()];

				if (!v.getParents().isEmpty()) {
					for (Preference p : v.affirmed) {

						// int[] binVal =
						// ListUtils.decToBin(p.getBinaryValue());
						List<Integer> binVal = ListUtils.decToBin(p
								.getBinaryValue());

						// String bv = Integer.toString(p.getBinaryValue());
						Assignment a = new Assignment();
						// int diff = (v.getParents().size()) - (bv.length());
						int diff = (v.getParents().size()) - (binVal.size());
						System.out.println("bin val " + binVal.toString()
								+ " has length " + binVal.size()
								+ " con parents " + v.getParents().size());
						System.out.println("diff = " + diff);
						// List<Integer> binvalList =
						// ListUtils.fromArrayToIntList(binVal);
						while (diff != 0) {
							// bv = (new StringBuffer(bv)).insert(0,
							// "0").toString();

							binVal.add(0, 0);
							System.out.println("binval list = "
									+ binVal.toString());
							diff--;
							// if(diff == 0) {
							// for(int ii = 0; ii < binvalList.size(); ii++){
							// int intero =(int) binvalList.get(ii);
							// binVal[ii] = intero;
							// }
							// }
						}
						System.out.println(v.getID() + " ha binary value "
								+ binVal.toString() + " e ha "
								+ v.getParents().size() + " parents");
						// binaryValue = bv.toCharArray();
						int i = 0;

						// Integer pref = new Integer(binVal.get(i));
						// Integer pref = new Integer(
						// Character.getNumericValue(binaryValue[i]));
						// System.out.println("BIN VAL " + binaryValue +
						// " has length " + binaryValue.length);
						// while (i != binaryValue.length) {
						while (i != binVal.size()) {
							Variable varHp = ListUtils.getVariable(variables,
									String.valueOf(v.getParents().get(i)));
							// pref = new
							// Integer(Character.getNumericValue(binaryValue[i]));
							Integer pref = new Integer((binVal.get(i)));
							System.out
									.println("preferenza: " + varHp.toString()
											+ ", " + pref.toString());
							a.setAssignment(varHp, pref);
							if (!hp1.contains(varHp)) {
								hp1.add(varHp);
							}
							i++;
							// variabili in hp1
						}// Character.getNumericValue(binaryValue[i])
						pos.add(a);

						System.out.println("nell'ipotesi ci sono " + hp1.size()
								+ " var d'ipotesi");
					}
				}
				if(!v.getParents().isEmpty() || (v.getParents().isEmpty() && v.getPreferences().get(0).getIsAffirmedValue()) ){
				Implies i = new Implies(var, hp1, pos);
				constraints.add(i);}

				// lista negativi
				System.out.println("NEGATIVI");
				List<Assignment> neg = new ArrayList<Assignment>();
				Assignment thesis0 = new Assignment();
				List<Variable> hp0 = new ArrayList<Variable>();

				if(!v.getParents().isEmpty() || (v.getParents().isEmpty() && !v.getPreferences().get(0).getIsAffirmedValue()) ){
				thesis0.setAssignment(var, 0);
				neg.add(thesis0);}

				// char[] binaryValue0 = new char[v.getParents().size()];//
				// aggiungere
				// zeri
				// davanti
				// se ci
				// sono
				// spazi
				// vuoti
				if (!v.getParents().isEmpty()) {
					for (Preference p : v.notAffirmed) {
						// System.out.println("preferenze negative");
						// String bv = Integer.toString(p.getBinaryValue());
						List<Integer> binVal = ListUtils.decToBin(p
								.getBinaryValue());
						Assignment a = new Assignment();

						int diff = (v.getParents().size()) - (binVal.size());
						System.out.println("bin val " + binVal.toString()
								+ " has length " + binVal.size());
						System.out.println("diff = " + diff);
						while (diff != 0) {
							// bv = (new StringBuffer(bv)).insert(0,
							// "0").toString();
							binVal.add(0, 0);
							diff--;
						}
						// binaryValue0 = bv.toCharArray();
						System.out.println(v.getID() + " ha binary value "
								+ binVal.toString() + " e ha "
								+ v.getParents().size() + " parents");

						int i0 = 0;

						// Integer pref = new
						// Integer(Character.getNumericValue(binaryValue0[i0]));
						while (i0 != binVal.size()) {
							Variable varHp = ListUtils.getVariable(variables,
									String.valueOf(v.getParents().get(i0)));
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
				if(!v.getParents().isEmpty() || (v.getParents().isEmpty() && !v.getPreferences().get(0).getIsAffirmedValue()) ){
				Implies i0 = new Implies(var, hp0, neg);
				constraints.add(i0);}

			}

			CpNetCSP csp = new CpNetCSP(variables, constraints);
			System.out.println("provo a risolvere");
			List<Assignment> result = csp.solve(Inference.NONE, csp, true);
			if(result.isEmpty()) System.out.println("NESSUNA SOLUZIONE  OTTIMA");
			for (Assignment a : result)
				System.out.println("SOL=" + a);

			for (Constraint i : csp.getConstraints())
				System.out.println(i);
		} else {
			Assignment a = c.solveAcyclicCPNet(new Assignment(), variables);
			System.out.println("La cp net aciclica ha come unica soluzione "
					+ a.toString());
			System.out.println("");
		}

		ViewGraph view = new ViewGraph(c);
		view.setVisible(true);
	}

}
