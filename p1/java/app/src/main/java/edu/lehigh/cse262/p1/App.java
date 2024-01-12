package edu.lehigh.cse262.p1;
import java.util.*;
/**
 * App is the entry point into our program. You are allowed to add fields and
 * methods to App. You may also add `import` statements.
 */
public class App {
    public static void main(String[] args) {
        //main method that calls the various test functions
        System.out.println("CSE 262 Project 1\n");
        System.out.println("MyMap Test:");
        myMapTest();
        System.out.println("\nMyRevese Test: ");
        myReverseTest();
        System.out.println("\nMyTree Test: ");
        myTreeTest();
        System.out.println("\nPrimeDivisor Test: ");
        primeDivisorTest();
        System.out.println("\nReadList Test:");
        readListTest();
    }

    public static void myReverseTest(){
        List<Integer> revList = Arrays.asList(1,2,3,4,5); //setting our input list
        MyReverse<Integer> myReverse = new MyReverse<Integer>(); //creating an instance of the MyReverse class
        List<Integer> output = myReverse.reverse(revList); //creating an output list from the list, calling reverse on the MyReverse instance with the list to be reversed
        System.out.println(output); //printing output: expected output: [5,4,3,2,1] real result: [5,4,3,2,1]
        List<String> revStringList = Arrays.asList("this","is","a","test"); //the following lines are the same test as above, just 
        MyReverse<String> myReverseString = new MyReverse<String>();             //with a list of strings to prove the function works on 
        List<String> outputStr = myReverseString.reverse(revStringList);        //several types. 
        System.out.println(outputStr);
    }

    public static void primeDivisorTest(){
        int value = 350; //the value that we will compute the prime divisor of
        PrimeDivisors primeDivisors = new PrimeDivisors(); //creating an instance of the prime divisor class
        List<Integer> primeDivisorList = primeDivisors.computeDivisors(value); //calling computeDivisors on the PrimeDivisor instance and our value
        System.out.println(primeDivisorList); //print the result
        //expected output: [2,5,5,7] real result: [2,5,5,7]
    }

    public static void myMapTest(){
        //Test for MyMap. Using arbitrary "Test Division" and "Test To Upper" function to test MyMap
        List<Integer> intList = Arrays.asList(2, 4, 6, 8, 10); //list of ints to send to MyMap
        List<String> stringList = Arrays.asList("this", "is", "a test"); //list of strings to MyMap
        MyMap<Integer> myMapInt = new MyMap<Integer>(); //instance of MyMap with Integer type (wrapped non-primitive int)
        MyMap<String> myMapString = new MyMap<String>(); //instance of MyMap with String type
        List<Integer> outputInt = myMapInt.map(intList, (x) -> testDivision(x)); //passing the intlist and stringListto myMap instance, 
        List<String> outputString = myMapString.map(stringList, (x) -> testToUpper(x)); //as well as a LAMBDA defined below (the func to apply)
        System.out.println(outputInt);
        //expected output: [1,2,3,4,5] real result: [1,2,3,4,5]
        System.out.println(outputString);
        //expected output [THIS, IS, A TEST] real result: [THIS, IS, A TEST] 
    }
    //MyMap testing method for ints
    public static int testDivision(int number) {
        return number / 2;
    }
    //another testing method for MyMap to show that the function itself works on all types
    public static String testToUpper(String string){
        return string.toUpperCase();
    }

    public static void readListTest(){
        System.out.println("Enter to STDIN to reverse your input. Hit enter when done, then press Ctrl+Z for EOF (ctrl+D on mac/linux): "); //simple message for the user
        ReadList readList = new ReadList();//readlist instance
        List<String> outputList = readList.read(); //sending readlist output to a new output list
        System.out.println(outputList); 
    }

    public static void myTreeTest(){
        MyTree tree = new MyTree<>(); //creating a new tree instance
        if(tree.isEmpty()) //testing isEmpty method
            System.out.println("The tree is empty"); //testing isEmpty(), as the tree should be empty at this point. 
        tree.insert(1); //inserting using the insert method
        List<Integer> numList = Arrays.asList(24,18,0,8,75,1234); //list of int to insert with inslist
        tree.inslist(numList);
        tree.inorder((x)->myTreePrintFunc(x)); //expected output order: 0,1,8,18,24,75,1234 (non-decreasing order)
        System.out.println("\n");
        tree.preorder((x)->myTreePrintFunc(x)); //expected output order: 1,0,24,18,8,75,1234 (returns copy of tree)
        tree.clear(); //clearing the tree 
        if(tree.isEmpty())
            System.out.println("The tree is empty"); //testing isEmpty(), as the tree should be empty after being cleared. 
        tree.insert("fido");
        List<String> strList = Arrays.asList("this", "is", "my","dog"); //list of strings to insert with inslist
        tree.inslist(strList); //the SAME TREE is used with two different types, proves generic typing works 
        tree.inorder((x)->myTreePrintFunc(x)); //expected output order: dog fido is my this (alphabetical)
        System.out.println("\n");
        tree.preorder((x)->myTreePrintFunc(x)); //expected output order: fido dog this is my (copy of tree) 
        tree.clear();
        if(tree.isEmpty())
            System.out.println("The tree is empty"); //testing isEmpty(), as the tree should be empty after being cleared. 
    }
    public static Object myTreePrintFunc(Object x){ //tester func for inorder and postorder, just prints out the contents of the node using generic object type
            System.out.println(x);
            return x;
    }
}
