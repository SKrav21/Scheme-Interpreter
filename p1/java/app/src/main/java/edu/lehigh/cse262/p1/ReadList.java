package edu.lehigh.cse262.p1;
import java.util.*;


/**
 * ReadList is a wrapper class around the function `read`
 */
public class ReadList<T>{
  /**
   * Read from stdin until EOF is encountered, and put all of the values into a
   * list. The order in the list should be the reverse of the order in which the
   * elements were added.
   * 
   * @return A list with the values that were read
   */
  List<T> read(){
    //Read from stdin until EOF is encountered, and put all of the values into a list. The order in the list should be the reverse of the order in which the elements were added 
    List<T> list = new ArrayList<T>(); //creating a new list to store the values that are read from stdin
   Scanner in = new Scanner(System.in);
   T temp = null;
   temp = (T) in.nextLine();
   list.add(temp);
    while(in.hasNextLine()){ //loop until EOF is encountered
       temp = (T) in.nextLine(); //reading a line from stdin and storing it in a temporary variable
      if(temp == null) 
        break; //if the temporary variable is null, break out of the loop
      list.add(temp); //adding the temporary variable to the list
    }
    Collections.reverse(list); //reversing the order of the list
    return list; //returning the list
  }
}