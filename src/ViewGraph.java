import com.mxgraph.layout.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

import csp.Assignment;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class ViewGraph extends JFrame {

    private CPNet _model = null;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;
    
    public ViewGraph(CPNet cpnet, List<Assignment> solutions1, double time, String strategy) {
        super("View CPNet");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 500);
        this.setLocation(300, 200);
        
      //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("CPNet Options");
        menuItem = new JMenuItem("Genera Ordinamento Parziale Soluzioni", KeyEvent.VK_S);
       
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	_model.generatePartialOrderSolution(new Solution(_model.getSolutions().get(0).toValueString()));
            	if (_model.getPartialOrderSol() != null) {
            		ViewGraph2 sols = new ViewGraph2(_model.getPartialOrderSol());
        			getContentPane().add(BorderLayout.CENTER, sols.getGraphComponent());
       			//sols.getGraphComponent().setPreferredSize(new Dimension(500, 500));
        			sols.getGraphComponent().setPreferredSize(sols.getGraphComponent().getPreferredSize());
        			getContentPane().revalidate();
        			getContentPane().repaint();
        		}
            }
        });
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
        _model = cpnet;
        final mxGraph graph = new mxGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(BorderLayout.LINE_START,graphComponent);
        graphComponent.setPreferredSize(new Dimension(300,300));
        //graphComponent.setPreferredSize(graphComponent.getPreferredSize());
        ViewPreferences pref=new ViewPreferences(_model);
        JScrollPane scroll=new JScrollPane(pref);
        
        String textSol1 = "";
        if(solutions1.isEmpty()) textSol1 += "Nessuna soluzione ottima\n";
        else{
        	for(Assignment a : solutions1){
        		textSol1 += a.toString();
        		textSol1 += "\n";
        	}
        }
        String timeCalc = "Tempo di calcolo: " + time + " s";
        
        JTextArea ta = new JTextArea("Algoritmo usato: " + strategy + "\n" + "SOLUZIONI OTTIME\n" + textSol1 + "\n" + timeCalc);
        ta.setEditable(false);
        JScrollPane scroll2=new JScrollPane(ta);
        
        scroll.setPreferredSize(new Dimension(150,100));
        //scroll2.setPreferredSize(new Dimension(150,100));
        
        //scroll.setPreferredSize(scroll.getPreferredSize());
        scroll2.setPreferredSize(scroll2.getPreferredSize());
        getContentPane().add(BorderLayout.EAST,scroll);
        getContentPane().add(BorderLayout.SOUTH,scroll2);
        
//		if (_model.getPartialOrderSol() != null) {
//			ViewGraph2 sols = new ViewGraph2(_model.getPartialOrderSol());
//			getContentPane().add(BorderLayout.CENTER, sols.getGraphComponent());
//			//sols.getGraphComponent().setPreferredSize(new Dimension(500, 500));
//			sols.getGraphComponent().setPreferredSize(sols.getGraphComponent().getPreferredSize());
//		}
        
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {            
            List<Vertex> vert=_model.getCPNet();
            List<Object> viewvert=new ArrayList<>();
            for(Vertex v : vert){
                viewvert.add(graph.insertVertex(parent,null,v.getID(),20,20,20,20));
            }
            for(Vertex v: vert){
                List<Integer> children=v.getChildren();
                for(Integer c: children){
                    graph.insertEdge(parent, null, "", viewvert.get(v.getID()), viewvert.get(c));
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
