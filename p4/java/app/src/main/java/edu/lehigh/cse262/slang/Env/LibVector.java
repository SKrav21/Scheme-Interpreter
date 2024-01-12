package edu.lehigh.cse262.slang.Env;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.lehigh.cse262.slang.Parser.IValue;
import edu.lehigh.cse262.slang.Parser.Nodes;

/**
 * The purpose of LibVector is to implement all of the standard library
 * functions that we can do on vectors
 */
public class LibVector {
    /**
     * Populate the provided `map` with a standard set of vector functions
     */
    public static void populate(HashMap<String, IValue> map, Nodes.Bool poundT, Nodes.Bool poundF) {

        var vectorLength = new Nodes.BuiltInFunc("vector-length", (List<IValue> args) -> {
            argsCheck("vector-length",args,1,true); 
            int numElements = ((Nodes.Vec)args.get(0)).items.length; //calculate the length of the item array assoc. with the vector
            return new Nodes.Int(numElements); //return new int node of the vector's length
        });

        var vectorGet = new Nodes.BuiltInFunc("vector-get", (List<IValue> args) ->{
            argsCheck("vector-length", args, 2,true);
            if(!(args.get(1) instanceof Nodes.Int)) //need to make sure we are trying to index using an integer (can even be from an identifier as long as it evaluates to an int)
                throw new Exception("Index into vector must be of type int");
            outOfBoundsErr(args); //outofbounds error checker
            return ((Nodes.Vec)args.get(0)).items[((Nodes.Int)args.get(1)).val]; //return the element at our index

        });

        var vectorSet = new Nodes.BuiltInFunc("vector-set!", (List<IValue> args) ->{
            argsCheck("vector-set!",args,3, true);
            if(!(args.get(1) instanceof Nodes.Int)) //need to make sure we are trying to index using an integer (can even be from an identifier as long as it evaluates to an int)
                throw new Exception("Index into vector must be of type int");
            outOfBoundsErr(args);
            if(!(args.get(2) instanceof IValue))
                throw new Exception("Can only insert datum into vectors (int, dbl, string, etc)");
            ((Nodes.Vec)args.get(0)).items[((Nodes.Int)args.get(1)).val] = (IValue)args.get(2); //set the index to our new value
            return null; //no need to return
        });

        var vector = new Nodes.BuiltInFunc("vector", (List<IValue> args) ->{
            for(int i = 0; i < args.size(); i++){ //iterate over all arguments to make sure all are valid datum
                if(!(args.get(i) instanceof IValue)){
                    throw new Exception("Can only insert datum into vectors (int, dbl, string, etc.)");
                }
            }
            return new Nodes.Vec(args); //return a new vec node with the list of IValues we are given
        });

        var isVector = new Nodes.BuiltInFunc("vector?", (List<IValue> args) -> {
            argsCheck("vector?",args,1,false);
            if(!(args.get(0) instanceof Nodes.Vec)) //simply check our argument to make sure it is a vec node or not
                return poundF;
            return poundT;
        });

        var makeVector = new Nodes.BuiltInFunc("make-vector", (List<IValue> args) -> {
            argsCheck("make-vector",args,1,false);
            if(!(args.get(0) instanceof Nodes.Int)) //make sure the length is of type int
                throw new Exception("Vector size must be of type integer");
            List<IValue> items = new ArrayList<IValue>(); //create a new list of items for the vector
            for(int i = 0; i < ((Nodes.Int)args.get(0)).val; i++)
                items.add(poundF); //set each item to #f
            return new Nodes.Vec(items); //return new vector
        });

        //map all functions to the environment
        map.put(makeVector.name, makeVector);
        map.put(isVector.name, isVector);
        map.put(vector.name, vector);
        map.put(vectorSet.name,vectorSet);
        map.put(vectorLength.name, vectorLength);
        map.put(vectorGet.name,vectorGet);
    }

    public static void outOfBoundsErr(List <IValue> args) throws Exception{
        if(((Nodes.Int)args.get(1)).val >= ((Nodes.Vec)args.get(0)).items.length) //if the given index is >= to the length of the vector
            throw new Exception("Index " + ((Nodes.Int)args.get(1)).val + " out of bounds for length " + ((Nodes.Vec)args.get(0)).items.length);
    }

    public static void vecTypeCheck(String name, List<IValue> args, int numCorrectArgs) throws Exception{
        if(!(args.get(0) instanceof Nodes.Vec)) //checks to make sure that the first argument to a function is a vector
            throw new Exception("function " + name + " takes vector argument as its first argument of (" + numCorrectArgs + ")");
    }
    
    public static void argsCheck(String name, List<IValue> args, int numCorrectArgs, boolean checkFirst) throws Exception{
        if(checkFirst) //argument that tells argsCheck whether or not to call vecTypeCheck (most functions need to be checked for specific # of args, not all need to have a vec as first arg though)
            vecTypeCheck(name,args,numCorrectArgs);
        if(args.size() != numCorrectArgs)
            throw new Exception("Incorrect Number of Arguments found for function " + name + ": found " + args.size() + ", expected " + numCorrectArgs);
    }

}
