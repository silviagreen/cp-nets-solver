
import java.awt.Font;
import java.awt.Label;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;


public class ViewPreferences extends JPanel{
    private CPNet _model=null;
    public ViewPreferences(CPNet model){
        _model=model;
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        List<Vertex> vert=_model.getCPNet();
        Label l;
        if (_model.isCyclic()){
            l=new Label("Grafo cicliclo");
        }
        else{
            l=new Label("Grafo aciclico");
        }
        this.add(l);
        for(Vertex v: vert){
            l=new Label(v.getID().toString()+": ");
            l.setFont(new Font("Arial",Font.BOLD,12));
            this.add(l);
            List<Preference> prefs=v.getPreferences();
            for(Preference p: prefs){
            	List<Integer> binaryValue = p.getBinaryValueInList(v.getParents().size());
            	String hypothesis = "";
            	for (int k = 0; k < binaryValue.size(); k++){
            		if(binaryValue.get(k).equals(1)) hypothesis += v.getParents().get(k).toString();
            		else if(binaryValue.get(k).equals(0)) hypothesis += "!" +  v.getParents().get(k).toString();
            	}
            	String implies =  " -> ";
            	String thesis =  (p.getIsAffirmedValue() ? p.getVariableName() + " > !" + p.getVariableName()  : "!" + p.getVariableName() + " > " + p.getVariableName());
                if(v.getParents().isEmpty()) l = new Label(thesis);
                else l=new Label(hypothesis+implies+thesis);
            	//l=new Label(p.toString());
                this.add(l);
            }
        }
    }
}
