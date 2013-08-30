
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
        for(Vertex v: vert){
            Label l=new Label(v.getID().toString()+": ");
            l.setFont(new Font("Arial",Font.BOLD,12));
            this.add(l);
            List<Preference> prefs=v.getPreferences();
            for(Preference p: prefs){
                l=new Label(p.toString());
                this.add(l);
            }
        }
    }
}