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

public static int[] getBinArray(int number) {
    int length = 0;
    while (number != 0) {
        number >>>= 1;
        length++;
    }
    return new int[length];
}

public static List<Integer> decToBin(int number) {
    int[] array = getBinArray(number);
    int k = array.length-1;
    while (number != 0)
    {
        array[k--] = number & 1;
        number >>>= 1;
    }
    return fromArrayToIntList(array);
}

public static List<Integer> fromArrayToIntList(int[] ints){
List<Integer> intList = new ArrayList<Integer>();
for (int index = 0; index < ints.length; index++)
{
    intList.add(ints[index]);
}return intList;
}

	}
