package edu.lehigh.cse262.slang.Env;

import java.util.HashMap;
import java.util.List;

import edu.lehigh.cse262.slang.Parser.IValue;
import edu.lehigh.cse262.slang.Parser.Nodes;

/**
 * The purpose of LibLists is to implement all of the standard library functions
 * that we can do on Cons nodes
 */
public class LibLists {
    /**
     * Populate the provided `map` with a standard set of list functions
     */
    public static void populate(HashMap<String, IValue> map, Nodes.Bool poundT, Nodes.Bool poundF, Nodes.Cons empty) {

        var car = new Nodes.BuiltInFunc("car", (List<IValue> args) -> {
            listFirstArgCheck("car",args); //check if first argument is a list 
            numArgs("car",args,1); //check the number of arguments 
            return ((Nodes.Cons)args.get(0)).car; //return the car of the list by simply using the car function of a cons node
        });

        var cdr = new Nodes.BuiltInFunc("cdr", (List<IValue> args) -> {
            listFirstArgCheck("cdr",args); //check if first arg is list
            numArgs("cdr",args,1); //check number of args
            return ((Nodes.Cons)args.get(0)).cdr; //return the cdr of the list by simply using the cdr function of a cons node
        });

        var cons = new Nodes.BuiltInFunc("cons", (List<IValue> args) -> {
            numArgs("cons",args,2); //check number of args
            if(!(args.get(0) instanceof IValue) || !(args.get(1) instanceof IValue)) //if either argument is not an IValue (this may not be necessary b/c either something evaluates to an IValue or this code is not reached b/c of some other error)
                throw new Exception("cons requires 2 value arguments (int,str,char,etc.)");
            return new Nodes.Cons(args.get(0),args.get(1)); //return a cons node made up of the two arguments 
        });

        var list = new Nodes.BuiltInFunc("list", (List<IValue> args) -> {
            for(var arg: args){ //again, check args to make sure they are IValues, but may not be necessary... kept it to play things safe. 
                if(!(arg instanceof IValue))
                    throw new Exception("list only accepts values as arguments (int,str,char,etc.)");
            }
            if(args.size() != 0) //if there are arguments to make a list with 
                return new Nodes.Cons(args,empty); //arguments are the args, and an empty cons cell
            return new Nodes.Cons(empty,empty); //two empty cons cells (allow '())
        });

        var isList = new Nodes.BuiltInFunc("list?", (List<IValue> args) -> {
            numArgs("list?",args,1); //check the number of arguments
            if(!(args.get(0) instanceof Nodes.Cons)) //if the argument is not a cons cell (list)
                return poundF;
            return poundT;
        });
        
        var setCar = new Nodes.BuiltInFunc("set-car!", (List<IValue> args) -> {
            numArgs("set-car!", args, 2);  //check number of arguments
            if(!(args.get(0) instanceof Nodes.Cons)) 
                throw new Exception("First Argument of function set-car! must be a list");
            if(!(args.get(1) instanceof IValue))
                throw new Exception("Second Argument of function set-car! must be a value");
                ((Nodes.Cons)args.get(0)).car = args.get(1); //set the car of the given list to the given value
                return null; //do not need to necessarily display the new list user can do that on their own if they choose
        });

        var setCdr = new Nodes.BuiltInFunc("set-cdr!", (List<IValue> args) -> {
            numArgs("set-cdr!", args, 2); //check number of arguments
            if(!(args.get(0) instanceof Nodes.Cons))
                throw new Exception("First Argument of function set-cdr! must be a list");
            if(!(args.get(1) instanceof IValue))
                throw new Exception("Second Argument of function set-cdr! must be a value");
                ((Nodes.Cons)args.get(0)).cdr = args.get(1); //set the cdr of the given list to the given value 
                return null; //do not need to necessarily display the new list user can do that on their own if they choose
        });

        //map functions to environment
        map.put(setCdr.name, setCdr);
        map.put(setCar.name, setCar);
        map.put(isList.name,isList);
        map.put(list.name,list);
        map.put(cons.name, cons);
        map.put(cdr.name, cdr);
        map.put(car.name, car);
    }
    /**
     * check that the number of given arguments is == to the number required for the given function
     * @param name
     * @param args
     * @param numArgs
     * @throws Exception
     */
    public static void numArgs(String name, List<IValue> args, int numArgs) throws Exception{
        if(args.size() != numArgs)
                throw new Exception("Incorrect Number of Arguments to function " + name + ": " + args.size() + " found, " + numArgs + " expected");
    }
    /**
     * check that the first argument to a function is a list
     * @param name
     * @param args
     * @throws Exception
     */
    public static void listFirstArgCheck(String name, List<IValue> args) throws Exception{
        if(!(args.get(0) instanceof Nodes.Cons))
            throw new Exception("First Argument to function " + name + " must be a list");
    }
}
