package edu.lehigh.cse262.p1;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;

/** MyMap is a wrapper class around the function `map` */
public class MyMap<T> {
  /**
   * Apply `func` to every element in `list`, and return a list containing the
   * results
   * 
   * @param list The list of elements that should be passed to func
   * @param func The function to apply to each element in the list
   * @return A list of the results
   */
  List<T> map(List<T> list, Function<T, T> func) {
    // [CSE 262] Implement Me!
    List<T> returnList = new ArrayList<T>(); //creating the ArrayList that stores the results 
    for(int i = 0; i < list.size(); i++){ //iterating over the length of the list of elements to be passed to func
      returnList.add(func.apply(list.get(i))); //using the .add() function to append each result of applying func to the input list. The .apply() function for Function intefaces is used. This method will apply the specified function to the ith element of the list. 
    }
    return returnList;//returning our outputted list
  }
}
