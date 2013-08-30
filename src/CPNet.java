import java.util.ArrayList;

import java.util.List;
import java.util.Random;
import java.util.Stack;

import com.sun.istack.internal.Pool.Impl;

import csp.Assignment;
import csp.CpNetCSP;
import csp.Implies;
import csp.ListUtils;
import csp.ImprovedBacktrackingStrategy.Inference;
import csp.Variable;

public class CPNet {
	
	private List<Vertex> adjList;
	private int nVertex;
	private int nEdges;

	public CPNet(int nVertex, int nEdges) throws IllegalArgumentException{
		/*
		 * Il numero totale di archi può essere al massimo N * (N-1)
		 */
		if(nEdges > nVertex * (nVertex - 1)) {
			throw new IllegalArgumentException("Il numero di archi non è appropriato rispetto al numero di vertici inseriti.");
		}
		this.nVertex = nVertex;
		this.nEdges = nEdges;
		this.adjList = new ArrayList<Vertex>(this.nVertex);
		for(int i = 0; i < this.nVertex; i++) {
			adjList.add(new Vertex(i));
		}
		generateRandomCPNet();
	}
        
        public CPNet(CPNet origin){
            this.nEdges=origin.nEdges;
            this.nVertex=origin.nVertex;
            this.adjList=new ArrayList<>();
            for(Vertex v: origin.adjList){
                this.adjList.add(new Vertex(v));
            }
        }
	
    public List<Vertex> getAdjList(){
    	return adjList;
    }
        
	private void generateRandomCPNet() {
		int edgesCounter = 0;
		Random randomizer = new Random(System.nanoTime());
        while (edgesCounter < this.nEdges) {
            Integer startVertex = Integer.valueOf(randomizer.nextInt(this.nVertex));
            Integer endVertex = Integer.valueOf(randomizer.nextInt(this.nVertex));
            /*
             * Se i due nodi sono diversi e l'arco non esiste già, allora lo inserisco
             */
            if(!startVertex.equals(endVertex) && !this.adjList.get(startVertex).isParentOf(endVertex)) {
            	//System.out.println(startVertex + "->" + endVertex);
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
			//System.out.println("Vertice: " + v.getID());
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
		for(Vertex v : this.adjList) {
			s += v + "------------------------------------------\n";
		}
		return s;
	}
        
        public boolean isCyclic(){
            CPNet workingcopy=new CPNet(this);
            Stack<Vertex> vertexnoparents=new Stack<>();
            for(Vertex v : workingcopy.adjList){
                if(v.getParents().isEmpty()){
                    vertexnoparents.add(v);
                }
            }
            while (!vertexnoparents.isEmpty()){
                Vertex n=vertexnoparents.pop();
                while(!n.getChildren().isEmpty()){
                    Integer m =n.getChildren().remove(n.getChildren().size()-1);
                    Integer i=new Integer(0);
                    boolean trovato=false;
                    while(!trovato && i<workingcopy.adjList.get(m).getParents().size()){
                        if(workingcopy.adjList.get(m).getParents().get(i).intValue()==n.getID().intValue()){
                            workingcopy.adjList.get(m).getParents().remove(i.intValue());
                            trovato=true;
                        }
                        i++;
                    }
                    if(workingcopy.adjList.get(m).getParents().isEmpty()){
                        vertexnoparents.add(workingcopy.adjList.get(m));
                    }
                }
            }
            boolean existedge=false;
            Integer i=new Integer(0);
            while(!existedge && i<workingcopy.adjList.size()){
                if(workingcopy.adjList.get(i).getChildren().size()>0 || workingcopy.adjList.get(i).getParents().size()>0){
                    existedge=true;
                }
                i++;
            }
            return existedge;
        }
	
	public static void main(String[] args) {
		
		Inference strategy = Inference.NONE;
		CPNet c = new CPNet(30, 60);
		List<Variable> variables = new ArrayList<Variable>();
		List<Implies> constraints = new ArrayList<Implies>();
		
		
		for(Vertex v : c.getAdjList()){
			Variable var = new Variable(Integer.toString(v.getID()));
			variables.add(var);
		}
		
		System.out.println(variables);
		
		for(Vertex v : c.getAdjList()){
			
			System.out.println("vertice: " + v.getID());
			//la var che sto considerando � la tesi del vincolo.
			//con isAffirmedValue la tesi vale 1, altrimento vale 0
			//le ipotesi sono il binary value
			
			v.setAffirmedLists();
			System.out.println("fatte liste");
			
			Variable var = ListUtils.getVariable(variables, String.valueOf(v.getID()));
			
			//lista positivi
			List<Assignment> pos = new ArrayList<Assignment>();
			Assignment thesis1 = new Assignment();
			List<Variable> hp1 = new ArrayList<Variable>();
			
			thesis1.setAssignment(var, 1);
			pos.add(thesis1);
			
			char[] binaryValue = new char[v.getParents().size()];//aggiungere zeri davanti se ci sono spazi vuoti
			System.out.println("num pos: " + v.affirmed.size());
			for(Preference p : v.affirmed){ System.out.println("preferenze positive");
				String bv = Integer.toBinaryString(p.getBinaryValue());
				Assignment a = new Assignment();
				int diff = (v.getParents().size()) - (binaryValue.length);
				while (diff == 0){ System.out.println("sistemo zeri");
					bv = (new StringBuffer(bv)).insert(0, "0").toString();
					diff--;
				}
				binaryValue = bv.toCharArray();
				int i = 0;
				int k = 0;
				Integer pref = new Integer(Character.getNumericValue(binaryValue[i]));
				while (i == binaryValue.length){
					Variable varHp = ListUtils.getVariable(variables, String.valueOf(v.getParents().get(i)));
					a.setAssignment(varHp, pref);
					if(k == 0){
						hp1.add(varHp);
					}
					i++;
					//variabili in hp1
				}//Character.getNumericValue(binaryValue[i])
				pos.add(a);
				if (k == 0) k++;
			}
			
			Implies i = new Implies(var, hp1, pos);
			constraints.add(i);
			
			
			//lista negativi
			List<Assignment> neg = new ArrayList<Assignment>();
			Assignment thesis0 = new Assignment();
			List<Variable> hp0 = new ArrayList<Variable>();
			
			thesis1.setAssignment(var, 1);
			neg.add(thesis0);
			
			char[] binaryValue0 = new char[v.getParents().size()];//aggiungere zeri davanti se ci sono spazi vuoti
			
			for(Preference p : v.notAffirmed){ System.out.println("preferenze negative");
				String bv = Integer.toBinaryString(p.getBinaryValue());
				Assignment a = new Assignment();
				int diff = (v.getParents().size()) - (binaryValue.length);
				while (diff == 0){
					bv = (new StringBuffer(bv)).insert(0, "0").toString();
					diff--;
				}
				binaryValue0 = bv.toCharArray();
				int i0 = 0;
				int k0 = 0;
				Integer pref = new Integer(Character.getNumericValue(binaryValue0[i0]));
				while (i0 == binaryValue0.length){
					Variable varHp = ListUtils.getVariable(variables, String.valueOf(v.getParents().get(i0)));
					a.setAssignment(varHp, pref);
					if(k0 == 0){
						hp0.add(varHp);
					}
					i0++;
					//var in hp0
				}//Character.getNumericValue(binaryValue[i])
				neg.add(a);
				if (k0 == 0) k0++;
			}
			
			Implies i0 = new Implies(var, hp1, neg);
			constraints.add(i0);
						
		}
		
		CpNetCSP csp = new CpNetCSP(variables, constraints);
		System.out.println("provo a risolvere");
		List<Assignment> result = csp.solve(Inference.AC3, csp, false);
		for(Assignment a : result)
		 System.out.println("SOL=" + a);      
				
				
				
				ViewGraph view=new ViewGraph(c);
                view.setVisible(true);
	}
	
}
