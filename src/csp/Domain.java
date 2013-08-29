package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import sun.org.mozilla.javascript.internal.ObjArray;


/**
 * A domain Di consists of a set of allowable values {v1, ... , vk} for
 * the corresponding variable Xi and defines a default order on those values.
 * This implementation guarantees, that domains are never changed after they
 * have been created. Domain reduction is implemented by replacement instead
 * of modification. So previous states can easily be restored.
 * 
 * @author Ruediger Lunde
 */
public class Domain implements Iterable<Object> {
	private static final long serialVersionUID = 1L;

	Object[] values;
	
	public Domain(List<?> values) {
		this.values = new Object[values.size()];
		for (int i = 0; i < values.size(); i++)
			this.values[i] = values.get(i);
	}
	
	public int size() {
		return values.length;
	}
	
	public void removeFirst(){
		Object[] newvals = new Object[values.length-1];
		
		for(int i = 0; i < values.length; i++){
			if (i == 0) break;
			else{
				newvals[i-1] = values[i];
			}
		}
		values = newvals;
		
	}
	
	public Object get(int index) {
		return values[index];
	}

	public boolean isEmpty() {
		return values.length == 0;
	}
	
	public boolean contains(Object value) {
		for (Object v : values)
			if (v.equals(value))
				return true;
		return false;
	}
	
	@Override
	public Iterator<Object> iterator() {
		return new ArrayIterator<Object>(values);
	}
	
	/** Not very efficient... */
	public List<Object> asList() {
		List<Object> result = new ArrayList<Object>();
		for (Object value : values)
			result.add(value);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Domain) {
			Domain d = (Domain) obj;
			if (d.size() != values.length)
				return false;
			else
				for (int i = 0; i < values.length; i++)
					if (!values[i].equals(d.values[i]))
						return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 9; // arbitrary seed value
		int multiplier = 13; // arbitrary multiplier value
		for (int i = 0; i < values.length; i++)
			hash = hash * multiplier + values[i].hashCode();
		return hash;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("{");
		boolean comma = false;
		for (Object value : values) {
			if (comma)
				result.append(", ");
			result.append(value.toString());
			comma = true;
		}
		result.append("}");
		return result.toString();
	}
}