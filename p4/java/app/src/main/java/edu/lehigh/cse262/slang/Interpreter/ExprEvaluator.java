package edu.lehigh.cse262.slang.Interpreter;

import java.util.ArrayList;
import java.util.List;

import edu.lehigh.cse262.slang.Env.Env;
import edu.lehigh.cse262.slang.Parser.IAstVisitor;
import edu.lehigh.cse262.slang.Parser.IValue;
import edu.lehigh.cse262.slang.Parser.Nodes;

/**
 * ExprEvaluator evaluates an AST node. It is the heart of the evaluation
 * portion of our interpreter.
 */
public class ExprEvaluator implements IAstVisitor<IValue> {
    /** The environment in which to do the evaluation */
    private Env env;

    /** Construct an ExprEvaluator by providing an environment */
    public ExprEvaluator(Env env) {
        this.env = env;
    }

    /** Interpret an Identifier */
    @Override
    public IValue visitIdentifier(Nodes.Identifier expr) throws Exception {
        IValue expression = env.get(expr.name); //making sure the identifier actually exists, exceptions in Env will be thrown if needed
        return expression; //return the expression associated with this identifier
    }

    /**
     * Interpret a Define special-form
     *
     * NB: it's OK for this to return null, because definitions aren't
     * expressions
     */
    @Override
    public IValue visitDefine(Nodes.Define expr) throws Exception {    
        env.put(expr.identifier.name, expr.expression.visitValue(this)); //populate the env with what we are defining
        return null; //no need to return anything 
    } 

    /** Interpret a Bool value */
    @Override
    public IValue visitBool(Nodes.Bool expr) throws Exception {
        if(expr.val == true){ //if the value of the bool node is true, return env.poundT, the environment's '#t'
            return env.poundT;
        } 
        return env.poundF; //otherwise return the environment's '#f'
    }

    /** Interpret an Int value */
    @Override
    public IValue visitInt(Nodes.Int expr) throws Exception { 
        return expr;
    }

    /** Interpret a Dbl value */
    @Override
    public IValue visitDbl(Nodes.Dbl expr) throws Exception {
        return expr; 
    }

    /** Interpret a Lambda value */
    @Override
    public IValue visitLambdaVal(Nodes.LambdaVal expr) throws Exception {
        return expr;
    }

    /**
     * Interpret a Lambda definition by creating a Lambda value from it in the
     * current environment
     */
    @Override
    public IValue visitLambdaDef(Nodes.LambdaDef expr) throws Exception {
        return new Nodes.LambdaVal(Env.makeInner(env), expr); //return a new LambdaVal with a new inner environment and expr as the "code to run"
    }

    /** Interpret an If expression */
    @Override
    public IValue visitIf(Nodes.If expr) throws Exception {
        if(expr.cond.visitValue(this)  == env.poundF){ //visit the condition, if it evaluates to false, evaluate and return the false branch
            return expr.ifFalse.visitValue(this); 
        }
        return expr.ifTrue.visitValue(this); //if the condition evaluates to true, evaluate and return the true branch 
    }

    /**
     * Interpret a set! special form. As with Define, this isn't an expression,
     * so it can return null
     */
    @Override
    public IValue visitSet(Nodes.Set expr) throws Exception {
        env.get(expr.identifier.name); //preliminarily make sure that the identifier we are trying to set actually exists 
        if(expr.expression instanceof Nodes.Identifier){ //this means we are setting the value of one identifier to the value of another 
            env.update(expr.identifier.name, env.get(((Nodes.Identifier)expr.expression).name)); //grab the value of the "other" identifier and set it to the one we are changing
            return null; //no need to return anything
        }
        env.update(expr.identifier.name, (IValue)expr.expression); //means we are simply updating the val of an identifier with another value
        return null; 
    }

    /** Interpret an And expression */
    @Override
    public IValue visitAnd(Nodes.And expr) throws Exception {
        int i = 0;
        while(i < expr.expressions.size() && expr.expressions.get(i).visitValue(this) != env.poundF){ //while either i is less than the number of expressions in the And, or we evaluate an expression and it is false
            i++; //simply increment i if we enter. Only take action if i = number of expressions or we evaluate a false expr
        }   
        if(i == expr.expressions.size()) //means we broke out of while loop, either b/c i is = num expressions, or we found a false. if i = num expressions, there was no false expressions.
            return env.poundT; //return true if we get here
        return env.poundF; //if not return false
    }

    /** Interpret an Or expression */
    @Override
    public IValue visitOr(Nodes.Or expr) throws Exception {
        int numExpr = expr.expressions.size(); //calculate the number of expressions
        for(int i = 0; i < numExpr; i++){ 
            if(expr.expressions.get(i).visitValue(this) != env.poundF){ //if any expression evaluates to true (all we need is 1 for Or)
                return env.poundT; //return true
            }
        }
        return env.poundF; //if we get here, it means that no expression was true.
    }

    /** Interpret a Begin expression */
    @Override
    public IValue visitBegin(Nodes.Begin expr) throws Exception {
        int numExpr = expr.expressions.size(); //calculate the number of expressions
        for(int i = 0; i < numExpr - 1; i++){ //loop until before the last expression, evaluate each
            expr.expressions.get(i).visitValue(this);
        }
        return expr.expressions.get(numExpr-1).visitValue(this); //return the last expression in the list, as is practice for "begin"
    }

    /** Interpret a "not special form" expression */
    @Override
    public IValue visitApply(Nodes.Apply expr) throws Exception {
        int numExpr = expr.expressions.size(); //calculate the number of expressions 
        if(expr.expressions.get(0).visitValue(this) instanceof Nodes.BuiltInFunc){ //if a node is recognized as a built in function
            Nodes.BuiltInFunc f = (Nodes.BuiltInFunc)expr.expressions.get(0).visitValue(this); //set f to the visited value of the built in function (we simply return the function, if it does not exist this will be caught elsewhere)
            List<IValue> funcs = new ArrayList<IValue>(); //declare a new list of the "funcs" or arguments to the function
            for(int i = 1; i < numExpr; i++){
                funcs.add(expr.expressions.get(i).visitValue(this)); //add each evaluated argument to the list
            }
            return f.func.execute(funcs); //use the "execute" method of the IExecutable class, which is also a field of the BuiltInFunc class
        }
        else if(expr.expressions.get(0).visitValue(this) instanceof Nodes.LambdaVal){ //if not a built in function, we have a LambdaVal
            Nodes.LambdaVal l = (Nodes.LambdaVal)expr.expressions.get(0).visitValue(this); //set l to the visited value of the lambdaval (just returns it)
            if(l.lambda.formals.size() != numExpr -1){ //if the number of formals for the lambda's "code to run" is not equal to the # of arguments given
                throw new Exception("Incorrect number of arguments");
            }
            ExprEvaluator innerEnv = new ExprEvaluator(Env.makeInner(l.env)); //make an "inner inner" environment with the inner environment associated with the lambdaval by constructing a new ExprEvaluator object
            for(int i = 1; i < numExpr; i++){ //loop to map the arguments to the formals
                innerEnv.env.put(l.lambda.formals.get(i-1).name,expr.expressions.get(i).visitValue(this)); //evaluate each argument, map it using the name of each formal
            }
            for(int i = 0; i < l.lambda.body.size(); i++){ //evaluate each element of the lambda body, using the inner environment (this is why ExprEvaluator implements IAstVisitor)
                l.lambda.body.get(i).visitValue(innerEnv);
            }
            return l.lambda.body.get(l.lambda.body.size()-1).visitValue(innerEnv); //return the last (evaluated) element of the body
        }
        else
            throw new Exception("Operator is not a Procedure"); //if not a builtinfunc or lambdaval, operator is not a procedure
        }

    /** Interpret a Cons value */
    @Override
    public IValue visitCons(Nodes.Cons expr) throws Exception { //visiting each cons of a list individually
        if(expr.car == env.empty){ //allow empty lists
            return env.empty;
        }  
        return expr.car;
    }

    /** Interpret a Vec value */
    @Override
    public IValue visitVec(Nodes.Vec expr) throws Exception {
        return expr; 
    }

    /** Interpret a Symbol value */
    @Override
    public IValue visitSymbol(Nodes.Symbol expr) throws Exception {
        return expr; 
    }

    /** Interpret a Quote expression */
    @Override
    public IValue visitQuote(Nodes.Quote expr) throws Exception {
        return expr.datum; //return the datum
    }

    /** Interpret a quoted datum expression */
    @Override
    public IValue visitTick(Nodes.Tick expr) throws Exception {
        return expr.datum; //return the datum
    }

    /** Interpret a Char value */
    @Override
    public IValue visitChar(Nodes.Char expr) throws Exception {
        return expr; 
    }

    /** Interpret a Str value */
    @Override
    public IValue visitStr(Nodes.Str expr) throws Exception {
        return expr;
    }

    /** Interpret a Built-In Function value */
    @Override
    public IValue visitBuiltInFunc(Nodes.BuiltInFunc expr) throws Exception {
       return expr;
    }

    /** Interpret a Cond expression */
    @Override
    public IValue visitCond(Nodes.Cond expr) throws Exception {
        int numConditions = expr.conditions.size();
        for(int i = 0; i < numConditions; i++){ //for each condition in the cond list 
            if(expr.conditions.get(i).test.visitValue(this) != env.poundF){ //if the test of any evaluates to true
                int numExpr = expr.conditions.get(i).expressions.size(); //count the number of expressions in the "action" list 
                for(int j = 0; j < numExpr-1; j++){ //evaluate each expression in the action list
                    expr.conditions.get(i).expressions.get(j).visitValue(this);
                }
                return expr.conditions.get(i).expressions.get(numExpr - 1).visitValue(this); //return the visited value of the last action of the first true condition. 
            }
        }
        return null; //if all false, return nothing. 
    }
}
