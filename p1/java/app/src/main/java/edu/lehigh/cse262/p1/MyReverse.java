package edu.lehigh.cse262.p1;

import java.util.List;
/** MyReverse is a wrapper class around the function `reverse` */
public class MyReverse<T> {
  /**
   * Return a list that has all of the elements of `in`, but in reverse order
   * 
   * @param in The list to reverse
   * @return A list that is the reverse of `in`
   */
  List<T> reverse(List<T> in) { //this method returns the same list that it was given, but reversed. No new list is created. This saves memory as well as costs less iterations of the for loop
    int lastIndexIn = in.size() -1; //precalculating the last index of the list to optimize loop (the loop runs faster when it does not have to repeat this calculation on each iteration)
    int halfInLen = in.size()/2; //precalculating half of the length of the list to optimize loop (the loop runs faster when it does not have to repeat this calculation on each iteration)
    for(int i = 0; i < halfInLen; i++){ //only iterating through half of the list. By switching the positions of items on opposite ends of the list in each iteration, the whole list becomes reversed by only traversing through half of the list (in lists that have an odd number of entries, the middle entry does not change in a reversal)
      T temp = in.get(lastIndexIn - i);//setting the last element - i of the list to a temporary value so that its corresponding entry in the first half of the list can be put in the second half. 
      in.set(lastIndexIn - i, in.get(i)); //setting the last element -i of the list to its corresponding value in the beginning of the list (ex: when i=0, the first value in the list is placed at the last index)
      in.set(i, temp);//placing the last element -i of the list at the ith spot in the list
    }
    
    return in;


  }
}
