import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class CPNet {
	
	private Map<Integer, List<Integer>> adjList;
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
		this.adjList = new HashMap<Integer, List<Integer>>(this.nVertex);
		for(int i = 0; i < this.nVertex; i++) {
			adjList.put(new Integer(i), new ArrayList<Integer>());
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
            //System.out.println(startVertex + "->" + endVertex);
            if(!startVertex.equals(endVertex) && !this.adjList.get(startVertex).contains(endVertex)) {
            	this.adjList.get(startVertex).add(endVertex);
            	edgesCounter++;
            }
            /*
             * Altrimenti non faccio nulla, aspetto la prossima iterazione
             */
        }
        
	}
	
	/* prova commit */
	
	public Map<Integer, List<Integer>> getCPNet() {
		return adjList;
	}
	
	public static void main(String[] args) {
		CPNet c = new CPNet(3, 2);
		System.out.println(c.getCPNet());
	}
	
	
}
