
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import javax.swing.JPanel;


import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;



public class ViewGraph2 extends JPanel{

	private PartialOrderSolutionGraph _model = null;
	private mxGraphComponent graphComponent;
	
    public mxGraphComponent getGraphComponent() {
		return graphComponent;
	}

	public ViewGraph2(PartialOrderSolutionGraph p) {
      
        _model = p;
        final mxGraph graph = new mxGraph();
        graphComponent = new mxGraphComponent(graph);
        
        graphComponent.setPreferredSize(new Dimension(600,300));
   

        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {            
            List<Solution> vert=_model.getSolutions();
            List<Object> viewvert=new ArrayList<>();
            HashMap<String, Integer> indexes = new HashMap<String, Integer>();
            int i = 0;
            for(Solution v : vert){          	
                viewvert.add(graph.insertVertex(parent,null,v.getValue(),30,30,30,30));
                indexes.put(v.getValue(), i);
                i++;
            }
            System.out.println("AGGIUNGO ARCHI");
            for(Solution v: vert){
            	System.out.println("ARCHI X VERT " + v.getValue() + " con " + v.getSubSols().size());
                List<Solution> children=v.getSubSols();
                for(Solution c: children){
                    graph.insertEdge(parent, null, "", viewvert.get(indexes.get(v.getValue())), viewvert.get(indexes.get(c.getValue())));
                    //System.out.println("vertice tra: " + indexes.get(v.getValue()) + " " +indexes.get(c.getValue()) );
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }

        // define layout
        //mxIGraphLayout layout = new mxFastOrganicLayout(graph);
        mxIGraphLayout layout = new mxCircleLayout(graph);

        // layout using morphing
        graph.getModel().beginUpdate();
        try {
            layout.execute(graph.getDefaultParent());
        } finally {
            mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);

            morph.addListener(mxEvent.DONE, new mxIEventListener() {

                @Override
                public void invoke(Object arg0, mxEventObject arg1) {
                    graph.getModel().endUpdate();
                    // fitViewport();
                }

            });

            morph.startAnimation();
        }
    }

}
