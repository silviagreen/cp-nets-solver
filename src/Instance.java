
import java.util.ArrayList;


public class Instance {
    private ArrayList<Boolean> _values=null;
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
