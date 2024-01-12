package edu.lehigh.cse262.slang.Env;

import java.util.HashMap;
import java.util.List;

import edu.lehigh.cse262.slang.Parser.IValue;
import edu.lehigh.cse262.slang.Parser.Nodes;

/**
 * The purpose of LibString is to implement all of the standard library
 * functions that we can do on Strings
 */
public class LibString {
    /**
     * Populate the provided `map` with a standard set of string functions
     */
    public static void populate(HashMap<String, IValue> map, Nodes.Bool poundT, Nodes.Bool poundF) {

        var stringAppend = new Nodes.BuiltInFunc("string-append",(List<IValue> args)  -> { 
            argsCheckerString("string-append", args, 2, true);
            return new Nodes.Str((((Nodes.Str)args.get(0)).val) + (((Nodes.Str)args.get(1)).val)); //simply return a new string node by concatenating strings using + (thanks java!)
        });

        var stringLength = new Nodes.BuiltInFunc("string-length",(List<IValue> args)  -> {
            argsCheckerString("string-length",args,1, true);
            return new Nodes.Int(((Nodes.Str)args.get(0)).val.length()); //calculate length for the given string arg, return as a node int
        });

        var subString = new Nodes.BuiltInFunc("substring",(List<IValue> args)  -> {
            argsCheckerString("substring",args,3, false);
            if(!(args.get(0) instanceof Nodes.Str)) //make sure first argument is a string
                throw new Exception("First argument to substring must be of type string");
            if(!(args.get(1) instanceof Nodes.Int) || !(args.get(2) instanceof Nodes.Int)) //make sure both bounds are ints 
                throw new Exception("substring bounds must be of type integer");
            int fromInc = ((Nodes.Int)args.get(1)).val; //declare each as an integer
            int toExc = ((Nodes.Int)args.get(2)).val;
            String str = ((Nodes.Str)args.get(0)).val;
            indexOOB(fromInc,str); //check fromInc for out of bounds 
            indexOOB(toExc-1, str); //check toExc -1 for out of bounds, because toExc index is allowed to be ==  string's length
            if(fromInc > toExc) //make sure that the from index is not greater than the to index
                throw new Exception("Starting index greater than ending index for substring");
            return new Nodes.Str(str.substring(fromInc,toExc)); //use java's substring to return the substring as a string node
        });

        var isString = new Nodes.BuiltInFunc("string?",(List<IValue> args)  -> {
            argsCheckerString("string?",args,1,false);
            if(!(args.get(0) instanceof Nodes.Str)) //if the argument is not a string node
                return poundF; //return false
            return poundT; //return true
        });

        var strRef = new Nodes.BuiltInFunc("string-ref", (List<IValue> args) -> {
            argsCheckerString("string-ref",args,2,false);
            if(!(args.get(0) instanceof Nodes.Str)) 
                throw new Exception("First argument to string-ref must be of type string");
            if(!(args.get(1) instanceof Nodes.Int))
                throw new Exception("string-ref index must be of type integer");
            String str = ((Nodes.Str)args.get(0)).val; //declare string node as a String variable
            int index = ((Nodes.Int)args.get(1)).val; //declare index as a string variable
            indexOOB(index, str); //check the index for Outofbounds
            return new Nodes.Char(str.charAt(index)); //return the single character as a char node
        });

        var strEqual = new Nodes.BuiltInFunc("string-equal?", (List<IValue> args) -> {
            argsCheckerString("string-equal?",args,2,true);
            String str1 = ((Nodes.Str)args.get(0)).val; //declare each string as a string variable 
            String str2 = ((Nodes.Str)args.get(1)).val;
            if(str1.equals(str2)) //use java's equals method 
                return poundT;
            return poundF;
        });

        var string = new Nodes.BuiltInFunc("string", (List<IValue> args) -> {
            String result = ""; //declare result as empty to start 
            for(var arg: args){ //enhanced loop through each character argument to make sure they are all characters
                if(!(arg instanceof Nodes.Char))
                    throw new Exception("Arguments to string must be all characters of form #\\x");
                result += ((Nodes.Char)arg).val; //build the string with each character
            }     
            return new Nodes.Str(result); //return a new node of type string with the result
        
        });   

        //map each function into the environment
        map.put(string.name, string);
        map.put(strEqual.name, strEqual);
        map.put(strRef.name, strRef);
        map.put(isString.name,isString);
        map.put(subString.name, subString);
        map.put(stringLength.name, stringLength);
        map.put(stringAppend.name, stringAppend);
    }
    /**
     * method that ensures an index is within bounds for a string
     * @param index
     * @param arg
     * @throws Exception
     */
    public static void indexOOB(int index, String arg) throws Exception{
        if(index >= arg.length())
            throw new Exception("Index out of bounds error: index " + index + " out of bounds for length " + arg.length());
    }
    /**
     * checks to make sure each argument to some function is a string, if a function requires it.
     * @param name
     * @param args
     * @param numArgs
     * @throws Exception
     */
    public static void checkArgsForStrings(String name, List<IValue> args, int numArgs) throws Exception{
        for(var arg: args){
            if(!(arg instanceof Nodes.Str))
                throw new Exception("Function " + name + " requires " + numArgs + " arguments of type string");
        }
    }
    /**
     * function that checks for the number of arguments required by some function, optionally calls checkArgsForStrings if passed "true" for checkStr
     * @param name
     * @param args
     * @param numArgs
     * @param checkStr
     * @throws Exception
     */
    public static void argsCheckerString(String name, List<IValue> args, int numArgs, boolean checkStr) throws Exception{
        if(args.size() != numArgs)
            throw new Exception("Incorrect Number of Arguments found for function " + name + ": " + args.size() + " found, " + numArgs + " expected");
        if(checkStr)
            checkArgsForStrings(name, args, numArgs);
    }
}
