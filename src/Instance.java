
import java.util.ArrayList;
import java.util.List;

import csp.Assignment;
import csp.Variable;


public class Instance {
    private ArrayList<Boolean> _values=null;
    private int iter;
    private boolean bestfound;
    
	public List<Assignment> fromInstanceToAssignment(List<Vertex> verts) {
		List<Assignment> result = new ArrayList<Assignment>();
		if (bestfound) {
			Assignment a = new Assignment();
			for (int i = 0; i < _values.size(); i++) {
				int val = _values.get(i) ? 1 : 0;
				a.setAssignment(new Variable(verts.get(i).getID().toString()), val);
			}
			result.add(a);
		}
		return result;
	}
    
    public boolean isBestfound() {
		return bestfound;
	}

	public void setBestfound(boolean bestfound) {
		this.bestfound = bestfound;
	}

	public int getIter() {
		return iter;
	}
    
    public void setIter(int iter) {
		this.iter = iter;
	}

	public Instance(){
        _values=new ArrayList<>();
    }
    
    public void add(boolean b){
        this._values.add(b);
    }
    
    public void set(int i,boolean b){
        this._values.set(i, b);
    }
    
    public int size(){
        return this._values.size();
    }
    
    public boolean get(int i){
        return this._values.get(i);
    }
    
    @Override
    public String toString(){
        String out=new String();
        for(int i=0;i<_values.size();i++){
            if(_values.get(i)){
                out=out.concat("1");
            }
            else{
                out=out.concat("0");
            }
        }
        return out;
    }
}
