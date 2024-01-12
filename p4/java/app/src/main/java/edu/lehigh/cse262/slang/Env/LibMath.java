package edu.lehigh.cse262.slang.Env;

import java.util.HashMap;
import java.util.List;

import edu.lehigh.cse262.slang.Parser.IValue;
import edu.lehigh.cse262.slang.Parser.Nodes;

/**
 * The purpose of LibMath is to implement all of the standard library functions
 * that we can do on numbers (Integer or Double)
 */
public class LibMath {
    /**
     * Populate the provided `map` with a standard set of mathematical functions
     * 
     * 
     * A NOTE ABOUT THIS CLASS: I definitely could have done a lot better in terms of shared functionality being stored in separate functions
     * for LibMath, but this was the first library that I approached so I was still getting used to how to do things.
     * If I had more time to refactor, I could probably reduce the amount of code in this file by ~100 lines. 
     */
    public static void populate(HashMap<String, IValue> map, Nodes.Bool poundT, Nodes.Bool poundF) {
        // As a starting point, let's go ahead and put the addition function
        // into the map. This will make it **much** easier to test `apply`, and
        // should provide some useful guidance for making other functions.
        //
        // Note that this code is **very** tedious. Making some helper
        // functions would probably be wise, but it's up to you to figure out
        // how.
        var add = new Nodes.BuiltInFunc("+", (List<IValue> args) -> {
            // Type checking: make sure we only have int and dbl arguments. We also will use
            // this to know if we should be returning an Int or a Dbl
           
            // Compute, making sure to know the return type
            int dblCount = typeChecker("+",args); //moved some given code into a helper
            if (dblCount > 0) {
                double result = 0;
                for (var arg : args) {
                    if (arg instanceof Nodes.Int)
                        result += ((Nodes.Int) arg).val;
                    else
                        result += ((Nodes.Dbl) arg).val;
                }
                return new Nodes.Dbl(result);
            } else {
                int result = 0;
                for (var arg : args) {
                    result += ((Nodes.Int) arg).val;
                }
                return new Nodes.Int(result);
            }
        });

        var subtract = new Nodes.BuiltInFunc("-", (List<IValue> args) -> {
            int dblCount = typeChecker("-",args); //check types, return number of doubles 
            if(dblCount > 0){ //if there are doubles in the arg list
                double result = 0;
                if(args.get(0) instanceof Nodes.Int){ //set the result to the first argument, but must check type for proper casting
                    result = ((Nodes.Int)args.get(0)).val;
                }else{
                    result = ((Nodes.Dbl)args.get(0)).val;
                }
                for(int i = 1; i < args.size(); i++){ //decrement first argument by each subsequent arg, must check type for proper casting
                    if(args.get(i) instanceof Nodes.Int)
                        result -= ((Nodes.Int)args.get(i)).val;
                    else
                        result -= ((Nodes.Dbl)args.get(i)).val;
                }
                if(result % 1 == 0)
                    return new Nodes.Int((int)result); //if result % 1 = 0; the result is actually an int
                return new Nodes.Dbl(result); //return the result as a double
            }else{ //there are no doubles, makes casting easier 
                int result = ((Nodes.Int)args.get(0)).val;
                for(int j = 1; j < args.size(); j++){
                    result -= ((Nodes.Int)args.get(j)).val;
                }
                    return new Nodes.Int(result);
            }              
        });
        /**
         * essentially the same code as subtraction, with * instead
         */
        var multiply = new Nodes.BuiltInFunc("*", (List<IValue> args) -> {
            int dblCount = typeChecker("*",args);
            if(dblCount > 0){
                double result = 0;
                if(args.get(0) instanceof Nodes.Int){
                    result = ((Nodes.Int)args.get(0)).val;
                }else{
                    result = ((Nodes.Dbl)args.get(0)).val;
                }
                    for(int i = 1; i < args.size(); i++){
                        if(args.get(i) instanceof Nodes.Int)
                            result *= ((Nodes.Int)args.get(i)).val;
                        else
                            result *= ((Nodes.Dbl)args.get(i)).val;
                    }
                    return new Nodes.Dbl(result);
            }else{
                int result = ((Nodes.Int)args.get(0)).val;
                for(int j = 1; j < args.size(); j++){
                    result *= ((Nodes.Int)args.get(j)).val;
                }
                    return new Nodes.Int(result);
            }              
        });        

        /**
         * again, essentially the same code as * and - 
         */
        var divide = new Nodes.BuiltInFunc("/", (List<IValue> args) -> {
            int dblCount = typeChecker("/",args);
            double result = 0;
            if(dblCount > 0){
                if(args.get(0) instanceof Nodes.Int){
                    result = ((Nodes.Int)args.get(0)).val;
                }else{
                    result = ((Nodes.Dbl)args.get(0)).val;
                }
                    for(int i = 1; i < args.size(); i++){
                        if(args.get(i) instanceof Nodes.Int)
                            result /= ((Nodes.Int)args.get(i)).val;
                        else
                            result /= ((Nodes.Dbl)args.get(i)).val;
                    }
            }else{
                result = ((Nodes.Int)args.get(0)).val;
                for(int j = 1; j < args.size(); j++){
                    result /= ((Nodes.Int)args.get(j)).val;
                }
            }  
            if(result % 1 == 0){
                return new Nodes.Int((int)result);
            }
            return new Nodes.Dbl(result);
        });

        /**
         * again, very similar code, but at this point I kind of realized that it makes more sense to % by 1 at the end to determine return type
         * rather than determine this by counting the number of doubles, I figured this out when I started doing both in the above functions 
         */
        var modulo = new Nodes.BuiltInFunc("%", (List<IValue> args) -> {
            typeChecker("%",args);
            double result = 0; 
            if(args.get(0) instanceof Nodes.Int){
                result = ((Nodes.Int)args.get(0)).val;
            }else{
                result = ((Nodes.Dbl)args.get(0)).val;
            }
            for(int i = 1; i < args.size(); i++){
                if(args.get(i) instanceof Nodes.Int)
                    result %= ((Nodes.Int)args.get(i)).val;
                else
                    result %= ((Nodes.Dbl)args.get(i)).val;
            }
            if(result % 1 == 0){
                return new Nodes.Int((int)result);
            }
            return new Nodes.Dbl(result);
        });

        var isEqual = new Nodes.BuiltInFunc("==", (List<IValue> args) -> {
            typeChecker("==",args);
            double result = 0;
            if(args.get(0) instanceof Nodes.Int){
                result = ((Nodes.Int)args.get(0)).val;
            }else{
                result = ((Nodes.Dbl)args.get(0)).val;
            }
            for(int i = 1; i < args.size(); i++){ //if any argument is not equal to the first (can handle several)
                if(args.get(i) instanceof Nodes.Int){ 
                    if(((Nodes.Int)args.get(i)).val != result){
                        return poundF;
                    }
                }
                if(args.get(i) instanceof Nodes.Dbl){
                    if(((Nodes.Dbl)args.get(i)).val != result){
                        return poundF;
                    }
                }
            }
            return poundT; //if we make it here, all arguments were equal
        });
        
        var isGreater = new Nodes.BuiltInFunc(">", (List<IValue> args) -> {
            int dblCount = typeChecker(">",args);
            double currArg = 0;
            if(dblCount > 0){ //set our "current argument" to the first, check types if there are doubles. 
                if(args.get(0) instanceof Nodes.Int)
                    currArg = ((Nodes.Int)args.get(0)).val;
                else
                    currArg = ((Nodes.Dbl)args.get(0)).val;
                for(int i = 1; i < args.size(); i++){
                    if(args.get(i) instanceof Nodes.Int){ //if any argument is less than or equal to the current argument 
                        if(currArg <= ((Nodes.Int)args.get(i)).val){
                            return poundF;
                        }
                    }
                    if(args.get(i) instanceof Nodes.Dbl){
                        if(currArg <= ((Nodes.Dbl)args.get(i)).val){
                            return poundF;
                        }
                    }
                if(args.get(0) instanceof Nodes.Int) //need to set the next argument to currArg to check it against the next next argument. 
                    currArg = ((Nodes.Int)args.get(i)).val;
                else
                    currArg = ((Nodes.Dbl)args.get(i)).val;
                }
                return poundT; //if we reach here, we had a strictly decreasing list of arguments meaning no argument was <= to the following argument, as is the essence of (>) in scheme.
            }else{
                currArg = ((Nodes.Int)args.get(0)).val; //same code but only checking for integers
                for(int i = 1; i < args.size(); i++){
                    if(currArg <= ((Nodes.Int)args.get(i)).val)
                        return poundF;
                    currArg = ((Nodes.Int)args.get(i)).val;
                }
                return poundT;
            }
        });

        /**
         * same code as >, but allowing the case where currArg can be = to following arg. 
         */
        var isGreaterOrEqual = new Nodes.BuiltInFunc(">=", (List<IValue> args) -> {
            int dblCount = typeChecker(">=",args);
            double currArg = 0;
            if(dblCount > 0){
                if(args.get(0) instanceof Nodes.Int)
                    currArg = ((Nodes.Int)args.get(0)).val;
                else
                    currArg = ((Nodes.Dbl)args.get(0)).val;
                for(int i = 1; i < args.size(); i++){
                    if(args.get(i) instanceof Nodes.Int){
                        if(currArg < ((Nodes.Int)args.get(i)).val){
                            return poundF;
                        }
                    }
                    if(args.get(i) instanceof Nodes.Dbl){
                        if(currArg < ((Nodes.Dbl)args.get(i)).val){
                            return poundF;
                        }
                    }
                if(args.get(i) instanceof Nodes.Int)
                    currArg = ((Nodes.Int)args.get(i)).val;
                else
                    currArg = ((Nodes.Dbl)args.get(i)).val;
                }
                return poundT;
            }else{
                currArg = ((Nodes.Int)args.get(0)).val;
                for(int i = 1; i < args.size(); i++){
                    if(currArg < ((Nodes.Int)args.get(i)).val)
                        return poundF;
                    currArg = ((Nodes.Int)args.get(i)).val;
                }
                return poundT;
            }
        });

        /**
         * same code as isGreaterThan, just opposite condition where each currArg must be strictly less than the following. 
         * This is where I would certainly start to implement some shared functionality, given some more time to refactor. 
         */
        var isLessThan = new Nodes.BuiltInFunc("<", (List<IValue> args) -> {
            int dblCount = typeChecker("<",args);
            double currArg = 0;
            if(dblCount > 0){
                if(args.get(0) instanceof Nodes.Int)
                    currArg = ((Nodes.Int)args.get(0)).val;
                else
                    currArg = ((Nodes.Dbl)args.get(0)).val;
                for(int i = 1; i < args.size(); i++){
                    if(args.get(i) instanceof Nodes.Int){
                        if(currArg >= ((Nodes.Int)args.get(i)).val){
                            return poundF;
                        }
                    }
                    if(args.get(i) instanceof Nodes.Dbl){
                        if(currArg >= ((Nodes.Dbl)args.get(i)).val){
                            return poundF;
                        }
                    }
                    if(args.get(0) instanceof Nodes.Int)
                        currArg = ((Nodes.Int)args.get(i)).val;
                    else
                        currArg = ((Nodes.Dbl)args.get(i)).val;
                }
                return poundT;
            }else{
                currArg = ((Nodes.Int)args.get(0)).val;
                for(int i = 1; i < args.size(); i++){
                    if(currArg >= ((Nodes.Int)args.get(i)).val)
                        return poundF;
                    currArg = ((Nodes.Int)args.get(i)).val;
                }
                return poundT;
            }
        });

        /**
         * same code as isLessThan, but relaxes condition to where currArg can be = to following
         */
        var isLessOrEqual = new Nodes.BuiltInFunc("<=", (List<IValue> args) -> {
            int dblCount = typeChecker("<=",args);
            double currArg = 0;
            if(dblCount > 0){
                if(args.get(0) instanceof Nodes.Int)
                    currArg = ((Nodes.Int)args.get(0)).val;
                else
                    currArg = ((Nodes.Dbl)args.get(0)).val;
                for(int i = 1; i < args.size(); i++){
                    if(args.get(i) instanceof Nodes.Int){
                        if(currArg > ((Nodes.Int)args.get(i)).val){
                            return poundF;
                        }
                    }
                    if(args.get(i) instanceof Nodes.Dbl){
                        if(currArg > ((Nodes.Dbl)args.get(i)).val){
                            return poundF;
                        }
                    }
                if(args.get(i) instanceof Nodes.Int)
                    currArg = ((Nodes.Int)args.get(i)).val;
                else
                    currArg = ((Nodes.Dbl)args.get(i)).val;
                }
                return poundT;
            }else{
                currArg = ((Nodes.Int)args.get(0)).val;
                for(int i = 1; i < args.size(); i++){
                    if(currArg > ((Nodes.Int)args.get(i)).val)
                        return poundF;
                    currArg = ((Nodes.Int)args.get(i)).val;
                }
                return poundT;
            }
        });

        /**
         * simple function that uses Math.abs on either an int or double, depending on the return value of numArgTypeChecker,
         * a type-checking function that also checks the list of arguments to see if it is comprised of integers only. 
         */
        var abs= new Nodes.BuiltInFunc("abs", (List<IValue> args) -> {
                boolean isInt = numArgTypeChecker("abs", args);
                if(isInt)
                    return new Nodes.Int(Math.abs(((Nodes.Int)args.get(0)).val));
                return new Nodes.Dbl(Math.abs(((Nodes.Dbl)args.get(0)).val));
                
        }); 
        /**
         * similar code to abs, uses Math.sqrt, also determines return type using % 1
         */
        var sqrt = new Nodes.BuiltInFunc("sqrt", (List<IValue> args) -> {
                boolean isInt = numArgTypeChecker("sqrt", args);
                double result = 0;
                if(isInt)
                    result = Math.sqrt(((Nodes.Int)args.get(0)).val);
                else
                    result = Math.sqrt(((Nodes.Dbl)args.get(0)).val);
                if(result % 1 == 0)
                    return new Nodes.Int((int)result);
                return new Nodes.Dbl(result);
        });

        /**
         * the next several functions are all trig functions that use the Math.java class' trig implementations. 
         * These functions are all identical besides the actual trig function applied. 
         * Another place that I really wish I had time to refactor and implement shared functionality, would save tons of lines 
         */
        var acos = new Nodes.BuiltInFunc("acos", (List<IValue> args) -> {
            boolean isInt = numArgTypeChecker("acos", args);
            double result = 0;
            if(isInt)
                result = Math.acos(((Nodes.Int)args.get(0)).val);
            else
                result = Math.acos(((Nodes.Dbl)args.get(0)).val);
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });

        var asin = new Nodes.BuiltInFunc("asin", (List<IValue> args) -> {
            boolean isInt = numArgTypeChecker("asin", args);
            double result = 0;
            if(isInt)
                result = Math.asin(((Nodes.Int)args.get(0)).val);
            else
                result = Math.asin(((Nodes.Dbl)args.get(0)).val);
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });

        var atan = new Nodes.BuiltInFunc("atan", (List<IValue> args) -> {
            boolean isInt = numArgTypeChecker("atan", args);
            double result = 0;
            if(isInt)
                result = Math.atan(((Nodes.Int)args.get(0)).val);
            else
                result = Math.atan(((Nodes.Dbl)args.get(0)).val);
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });

        var cos = new Nodes.BuiltInFunc("cos", (List<IValue> args) -> {
            boolean isInt = numArgTypeChecker("cos", args);
            double result = 0;
            if(isInt)
                result = Math.cos(((Nodes.Int)args.get(0)).val);
            else
                result = Math.cos(((Nodes.Dbl)args.get(0)).val);
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });

        var cosh = new Nodes.BuiltInFunc("cosh", (List<IValue> args) -> {
            boolean isInt = numArgTypeChecker("cosh", args);
            double result = 0;
            if(isInt)
                result = Math.cosh(((Nodes.Int)args.get(0)).val);
            else
                result = Math.cosh(((Nodes.Dbl)args.get(0)).val);
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });

        var sin = new Nodes.BuiltInFunc("sin", (List<IValue> args) -> {
            boolean isInt = numArgTypeChecker("sin", args);
            double result = 0;
            if(isInt)
                result = Math.sin(((Nodes.Int)args.get(0)).val);
            else
                result = Math.sin(((Nodes.Dbl)args.get(0)).val);
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });

        var sinh = new Nodes.BuiltInFunc("sinh", (List<IValue> args) -> {
            boolean isInt = numArgTypeChecker("sinh", args);
            double result = 0;
            if(isInt)
                result = Math.sinh(((Nodes.Int)args.get(0)).val);
            else
                result = Math.sinh(((Nodes.Dbl)args.get(0)).val);
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });

        var tan = new Nodes.BuiltInFunc("tan", (List<IValue> args) -> {
            boolean isInt = numArgTypeChecker("tan", args);
            double result = 0;
            if(isInt)
                result = Math.tan(((Nodes.Int)args.get(0)).val);
            else
                result = Math.tan(((Nodes.Dbl)args.get(0)).val);
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });

        var tanh = new Nodes.BuiltInFunc("tanh", (List<IValue> args) -> {
            boolean isInt = numArgTypeChecker("tanh", args);
            double result = 0;
            if(isInt)
                result = Math.tanh(((Nodes.Int)args.get(0)).val);
            else
                result = Math.tanh(((Nodes.Dbl)args.get(0)).val);
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });
        

        var isInteger = new Nodes.BuiltInFunc("integer?", (List<IValue> args) -> {
            oneArgCheck("integer?", args); //checks function to make sure there is only one argument provided. 
            if(args.get(0) instanceof Nodes.Int){ //if it is an integer
                return poundT;
            }
            return poundF;
        });

        /**
         * same as above, just for doubles 
         */
        var isDouble = new Nodes.BuiltInFunc("double?", (List<IValue> args) -> {
            oneArgCheck("double?", args);
            if(args.get(0) instanceof Nodes.Dbl){
                return poundT;
            }
            return poundF;
        });

        /**
         * same as above but checks for either an int or double using or. 
         */
        var isNumber = new Nodes.BuiltInFunc("number?", (List<IValue> args) -> {
            oneArgCheck("number?", args);
            if(args.get(0) instanceof Nodes.Dbl || args.get(0) instanceof Nodes.Int){
                return poundT;
            }
            return poundF;
        });

        /**
         * same as above, but checks for symbol nodes
         */
        var isSymbol = new Nodes.BuiltInFunc("symbol?", (List<IValue> args) -> {
            oneArgCheck("symbol?", args);
            if(args.get(0) instanceof Nodes.Symbol){
                return poundT;
            }
            return poundF;
        });

        /**
         * same as above, but checks for builtinfunc nodes or lambdavals
         */
        var isProcedure = new Nodes.BuiltInFunc("procedure?", (List<IValue> args) -> {
            oneArgCheck("procedure?", args);
            if(args.get(0) instanceof Nodes.BuiltInFunc || args.get(0) instanceof Nodes.LambdaVal)
                return poundT;
            return poundF;
        });

        /**
         * uses Math.log10 to calculate the log10, also checks for argument type to get value as well as return type
         */
        var log10 = new Nodes.BuiltInFunc("log10", (List<IValue> args) ->{
            boolean isInt = numArgTypeChecker("log10", args); 
            double result = 0;
            if(isInt)
                result = Math.log10(((Nodes.Int)args.get(0)).val);
            else
                result = Math.log10(((Nodes.Dbl)args.get(0)).val);
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });

        /**
         * same as above but uses standard Math.log function (represents log e)
         */
        var loge = new Nodes.BuiltInFunc("loge", (List<IValue> args) ->{
            boolean isInt = numArgTypeChecker("loge", args); 
            double result = 0;
            if(isInt)
                result = Math.log(((Nodes.Int)args.get(0)).val);
            else
                result = Math.log(((Nodes.Dbl)args.get(0)).val);
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });

        var pow = new Nodes.BuiltInFunc("pow", (List<IValue> args) -> {
            if(args.size() != 2){ //check for strictly 2 arguments, base and exp
                throw new Exception("pow takes two args: base and exponent");
            }
            double result = 0; //instantiate each part of the expression
            double base = 0; 
            double exp = 0;
            if(args.get(0) instanceof Nodes.Int) //set the base to either int or double
                base = ((Nodes.Int)args.get(0)).val;
            else
                base = ((Nodes.Dbl)args.get(0)).val;
            if(args.get(1) instanceof Nodes.Int) //set the exponent to either int or double
                exp = ((Nodes.Int)args.get(1)).val;
            else
                exp = ((Nodes.Dbl)args.get(1)).val;
            result = Math.pow(base,exp); //calculate the result, check the type for return
            if(result % 1 == 0)
                return new Nodes.Int((int)result);
            return new Nodes.Dbl(result);
        });

        var not = new Nodes.BuiltInFunc("not", (List<IValue> args) -> {
            oneArgCheck("not",args); //check for one argument
            if(!(args.get(0) == poundF)) //the only case that not returns true is when given a #f
                return poundF;
            return poundT;
        });

        var intToDbl = new Nodes.BuiltInFunc("integer->double", (List<IValue> args) -> {
            oneArgCheck("integer->double", args);
            if(!(args.get(0) instanceof Nodes.Int)) //need to input an int
                throw new Exception("Argument for integer->double must be of type integer");
            return new Nodes.Dbl(((double)((Nodes.Int)args.get(0)).val)); //cast the double to an int, floors the double. 
        });

        var dblToInt = new Nodes.BuiltInFunc("double->integer", (List<IValue> args) -> {
            oneArgCheck("double->integer", args);
            if(!(args.get(0) instanceof Nodes.Dbl)) //need to input a double
                throw new Exception("Argument for double->integer must be of type double");
            return new Nodes.Int(((int)((Nodes.Dbl)args.get(0)).val)); //cast the int to a double (simply adds a .0)
        });

        var isNull = new Nodes.BuiltInFunc("null?", (List<IValue> args) -> {
            oneArgCheck("null?", args); //as per GSI this function needs one argument at least but that is all you need regardless to determine if null (?)
            if(args.get(0) instanceof Nodes.Cons){ //only real case of nullness is an empty list
                if(((Nodes.Cons)args.get(0)).car == null) //if the car is null/empty
                    return poundT; //the list is null
                return poundF; 
            }
            return poundF;
        }); 

        //adding math funcs to the env 
        map.put(add.name, add);
        map.put(subtract.name, subtract);
        map.put(multiply.name,multiply);
        map.put(divide.name,divide);
        map.put(modulo.name, modulo);
        map.put(isEqual.name, isEqual);
        map.put(isGreater.name, isGreater);
        map.put(isGreaterOrEqual.name, isGreaterOrEqual);
        map.put(isLessThan.name, isLessThan);
        map.put(isLessOrEqual.name, isLessOrEqual);
        map.put(abs.name, abs);
        map.put(sqrt.name, sqrt);
        map.put(acos.name, acos);
        map.put(asin.name, asin);
        map.put(atan.name, atan);
        map.put(cos.name, cos);
        map.put(cosh.name, cosh);
        map.put(sin.name, sin);
        map.put(sinh.name, sinh);
        map.put(tan.name,tan);
        map.put(tanh.name, tanh);
        map.put(isInteger.name,isInteger);
        map.put(isDouble.name, isDouble);
        map.put(isNumber.name, isNumber);
        map.put(isSymbol.name, isSymbol);
        map.put(isProcedure.name, isProcedure);
        map.put(log10.name, log10);
        map.put(loge.name, loge);
        map.put(pow.name, pow);
        map.put(not.name, not);
        map.put(intToDbl.name, intToDbl);
        map.put(dblToInt.name, dblToInt);
        map.put(isNull.name, isNull);

        //Math constants
        map.put("pi",new Nodes.Dbl(Math.PI));
        map.put("e", new Nodes.Dbl(Math.E));
        map.put("tau", new Nodes.Dbl(2 * Math.PI));
        map.put("inf+", new Nodes.Dbl(Double.POSITIVE_INFINITY));
        map.put("inf-", new Nodes.Dbl(Double.NEGATIVE_INFINITY));
        map.put("nan", new Nodes.Dbl(Double.NaN));

    }

    /**
     * check for one arg only
     * @param name
     * @param args
     * @throws Exception
     */
    public static void oneArgCheck(String name, List<IValue> args) throws Exception{
        if(args.size() != 1)
            throw new Exception("function '" + name + "' takes exactly 1 argument");
    }

    /**
     * check for number of arguments = 1 and that argument is either an int or double. Return true if int, false if double, throw if neither. 
     * @param name
     * @param args
     * @return
     * @throws Exception
     */
    public static boolean numArgTypeChecker(String name, List<IValue> args) throws Exception{
        oneArgCheck(name, args);
        if(args.get(0) instanceof Nodes.Int)
            return true;
        if(args.get(0) instanceof Nodes.Dbl)
            return false;
        throw new Exception(name + " handles only arguments of type int or double");
    }
    /**
     * originally code that was in given + function, moved to a helper. 
     * @param name
     * @param args
     * @return
     * @throws Exception
     */
    public static int typeChecker(String name, List<IValue> args) throws Exception{
        int intCount = 0;
        int dblCount = 0;
        for (var arg : args) {
            if (arg instanceof Nodes.Int)
                intCount++;
            if (arg instanceof Nodes.Dbl)
                dblCount++;
        }
        if (args.size() > (intCount + dblCount))
            throw new Exception(name + " can only handle Int and Dbl arguments");
        // Semantic analysis: make sure there are arguments!
        if (args.size() == 0)
            throw new Exception(name + " expects at least one argument");
        return dblCount;
    }
}
