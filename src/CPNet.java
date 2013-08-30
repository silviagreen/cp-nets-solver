import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

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
		CPNet c = new CPNet(30, 60);
                ViewGraph view=new ViewGraph(c);
                view.setVisible(true);
	}
	
}
