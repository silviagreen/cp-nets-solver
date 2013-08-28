import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	public String toString() {
		String s = "------------------------------------------\n";
		for(Vertex v : this.adjList) {
			s += v + "------------------------------------------\n";
		}
		return s;
	}
	
	public static void main(String[] args) {
		CPNet c = new CPNet(10, 3);
		System.out.println(c);
	}
	
}
