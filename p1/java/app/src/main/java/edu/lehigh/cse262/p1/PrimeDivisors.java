package edu.lehigh.cse262.p1;

import java.util.List;
import java.util.ArrayList;
/** PrimeDivisors is a wrapper class around the function `computeDivisors` */
public class PrimeDivisors {
  /**
   * Compute the prime divisors of `value` and return them as a list
   *
   * @param value The value whose prime divisors are to be computed
   * @return A list of the prime divisors of `value`
   */
  List<Integer> primeDivisorList = new ArrayList<Integer>(); //instantiating our list of prime divisors that will be returned
  int divisor = 2; //beginning with a divisor of two
  List<Integer> computeDivisors(int value) { //recursive method that computes the prime factorization of a number 
    if(value == 1){ //handling the edge case where the value is 1, which has no prime divisors. OR once recursion is complete and we have broken the value into its prime divisors 
      return primeDivisorList; //returning the list when the value hits 1 
    }
    if(value % divisor == 0){ //checking if the value is evenly divisble by the current divisor using modulus
      primeDivisorList.add(divisor); //because of the inherent nature of prime factorization, this if statement will only be entered when the divisor is prime. ex: even if a number is divisble by 4 (not prime), this means it is also evenly divisble by 2 and will be divided by 2 (by 4) until it is not able to be divided by 2 anymore, meaning it is also not evenly divisible by 4 anymore. 
      value = value/divisor; //dividing the value by the prime divisor
      computeDivisors(value); //calling computeDivisors on the new value to continue to search it for prime divisors using prime factorization 
    }
    else{//the case where the value is not evenly divisible by the current divisor. 
      divisor++; //incrementing the divisor as per prime factorization. Because divisor is global to this function, its value is not affected by the recursion and is retained on subsequent calls to computeDivisors.
      computeDivisors(value); //calling computeDivisors on the current value after incrementing divisor. 
    }
    return primeDivisorList; //returning the list of prime divisors. 
  }
}
