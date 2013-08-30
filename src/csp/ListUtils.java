package csp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ListUtils {

	  private ListUtils() {}

	  @SuppressWarnings("unchecked")
	  public static <T> void removeDuplicate(List <T> list) {
	    Set <T> set = new HashSet <T>();
	    List <T> newList = new ArrayList <T>();
	    for (Iterator <T>iter = list.iterator();    iter.hasNext(); ) {
	       Object element = iter.next();
	       if (set.add((T) element))
	          newList.add((T) element);
	       }
	       list.clear();
	       list.addAll(newList);
	    }

public static Variable getVariable(List<Variable> list, String name){
	for(Variable v : list){
		if(v.getName().equals(name)) return v;
	}
	return null;
}
	}
