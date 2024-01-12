package edu.lehigh.cse262.slang.Parser;

import java.util.List;
import java.util.ArrayList;

//import edu.lehigh.cse262.slang.Parser.Nodes.Identifier;

import edu.lehigh.cse262.slang.Scanner.TokenStream;
import edu.lehigh.cse262.slang.Scanner.Tokens;


/**
 * Parser is the second step in our interpreter. It is responsible for turning a
 * sequence of tokens into an abstract syntax tree.
 */
public class Parser {
    private final Nodes.Bool _true;
    private final Nodes.Bool _false;
    private final Nodes.Cons _empty;
    private static List<Nodes.BaseNode> nodesList;

    public Parser(Nodes.Bool _true, Nodes.Bool _false, Nodes.Cons _empty) {
        this._true = _true;
        this._false = _false;
        this._empty = _empty;
    }

    /**
     * Transform a stream of tokens into an AST
     *
     * @param tokens a stream of tokens
     *
     * @return A list of AstNodes, because a Scheme program may have multiple
     *         top-level expressions.
     * @throws Exception
     */
    public List<Nodes.BaseNode> parse(TokenStream tokens) throws Exception{ //need to throw exceptions, this is included in all func headers
        nodesList = new ArrayList<Nodes.BaseNode>(); 
        while(tokens.hasNext()){ //for <program> -> <form>*
            if(tokens.nextNextToken() instanceof Tokens.Define){ //first check if we have form -> define
                if(tokens.nextToken() instanceof Tokens.LeftParen){ //to ensure we have a leading lparen
                    //tokens.popToken();
                    var define = DefineNode(tokens, _true, _false, _empty); //call define function 
                    nodesList.add(define); //add new define node when we return 
                }
                else{
                    throw new Exception("Left Paren must preceed define"); //if no lparen before define
                }
            }
            else{ // | <expression>
                var exp = ExpressionNode(tokens, _true, _false, _empty); //make an expression node
                nodesList.add(exp); //add whatever nodes we find to the list
                tokens.popToken(); //remove EOF
            }
        } 
        return nodesList;
    }
    //function for creating define nodes
    public static Nodes.BaseNode DefineNode(TokenStream tokens, Nodes.Bool _true, Nodes.Bool _false, Nodes.BaseNode _empty) throws Exception{
        Nodes.BaseNode define = _empty; //start with an "empty" node
        tokens.popToken();//pop off lparen
        tokens.popToken(); //popping off the define 
        if(tokens.nextToken() instanceof Tokens.Identifier){ //if we have an ident next
            Nodes.Identifier id = new Nodes.Identifier(tokens.nextToken().tokenText); //make our identifier
            tokens.popToken(); //pop it 
            var exp = ExpressionNode(tokens,_true,_false, _empty); //making our expression. any further parse errors handled in expression func
            define = new Nodes.Define(id,exp); //creating our new define node
            tokens.popToken(); //rparen
            tokens.popToken(); //eof
            return define; //return our define node 
        }   
        else{ //if no identifier following define
            throw new Exception("Parsing Error: Must proceed define with identifier");
        }
    }
     //function that handles all nodes on RHS of <expression>. Recursive in any case where a RHS production of expression brings us back to expression
    public static Nodes.BaseNode ExpressionNode(TokenStream tokens, Nodes.Bool _true, Nodes.Bool _false, Nodes.BaseNode _empty) throws Exception{
        
        Nodes.BaseNode expNode = _empty; //start with an "empty" expression, whatever we create in expression, we set to expNode and return it
        if(tokens.nextToken() instanceof Tokens.LeftParen){ //for any of the expression types that begin with LParen (including application)
            if(tokens.nextNextToken() instanceof Tokens.Quote){ //if we have a quote 
                tokens.popToken(); //get rid of lparen
                tokens.popToken();//get rid of quote
                var qDatum = Datum(expNode,tokens,_true,_false, _empty); //fetch our datum 
                if(qDatum == _empty){ //datum must include at least something 
                    throw new Exception("Unexpected token while parsing (quote)"); 
                }
                expNode = new Nodes.Quote((IValue)qDatum); //create our quote node 
                tokens.popToken();//pop off rparen
            }
            else if(tokens.nextNextToken() instanceof Tokens.Lambda){ //we have a lambda 
                tokens.popToken();//get rid of lparen
                tokens.popToken(); //get rid of lambda
                var formals = Formals(tokens);  //fetch our formals in a separate function 
                var body = Body(tokens, _true, _false, _empty); //fetch our body in a separate function
                expNode = new Nodes.LambdaDef(formals,body); //create our new LambdaDef node
                tokens.popToken(); //pop off rparen
            }
            else if(tokens.nextNextToken() instanceof Tokens.If){
                tokens.popToken(); //get rid of lparen 
                tokens.popToken(); //get rid of if
                //we know that if must have three expressions, handle all errors within expression
                Nodes.BaseNode cond = ExpressionNode(tokens,_true,_false,_empty);
                Nodes.BaseNode ifTrue = ExpressionNode(tokens,_true,_false, _empty);
                Nodes.BaseNode ifFalse = ExpressionNode(tokens,_true,_false,_empty);
                expNode = new Nodes.If(cond,ifTrue,ifFalse); //create our new if node 
                tokens.popToken();//get rid of rparen
            }
            else if(tokens.nextNextToken() instanceof Tokens.Set){ //we have a set
                tokens.popToken(); //get rid of lparen
                tokens.popToken(); //get rid of set
                if(!tokens.hasNext()){ //base case where there is nothing following set
                    throw new Exception("Must include identifier after set!");
                }
                if(tokens.nextToken() instanceof Tokens.Identifier){ //if we have an identifier next
                    var ident = new Nodes.Identifier(tokens.nextToken().tokenText); //grab our identifier
                    tokens.popToken(); //pop it 
                    var exp = ExpressionNode(tokens,_true,_false,_empty); //grab our expression, handle all exp errors there 
                    if(exp == _empty){ //if we did not make an expression
                        throw new Exception("Must include expression after identifier for set!");
                    }
                    expNode = new Nodes.Set(ident,exp); //make our new set node
                    tokens.popToken(); //pop rparen
                }
                else{ //some token next that is not an identifier
                    throw new Exception("must proceed set! with an identifier");
                }
            }
            else if(tokens.nextNextToken() instanceof Tokens.And){ //we have an and
                tokens.popToken();//get rid of lparen
                tokens.popToken(); //get rid of and 
                if(tokens.nextNextToken() instanceof Tokens.Eof){ //base case of nothing following and 
                    throw new Exception("Must include at least one expression following And");
                }
                List<Nodes.BaseNode> exps = new ArrayList<Nodes.BaseNode>(); //declare a list of basenodes for our 1+ expressions
                while(!(tokens.nextToken() instanceof Tokens.RightParen)){ //until we hit a rparen
                    exps.add(ExpressionNode(tokens,_true,_false,_empty)); //add each expression to the list
                }
                if(exps.size() == 0){ //if we added no expressions
                    throw new Exception("Parsing Error: Must include at least one expression following And");
                }
                expNode = new Nodes.And(exps);
                tokens.popToken(); 
                tokens.popToken(); //take off right paren
            }
            else if(tokens.nextNextToken() instanceof Tokens.Or){ //if we have an or
                tokens.popToken(); //pop lparen
                tokens.popToken(); //pop or 
                List<Nodes.BaseNode> exps = new ArrayList<Nodes.BaseNode>(); //new array of expressions
                while(!(tokens.nextToken() instanceof Tokens.RightParen)){ //until we hit rparen
                    exps.add(ExpressionNode(tokens,_true,_false,_empty)); //add expressions to the list
                }
                if(exps.size() == 0){
                    throw new Exception("Parsing Error: zero arguments to (Or)");
                }
                expNode = new Nodes.Or(exps); //create our new expression list
                tokens.popToken(); //pop rparen
            }
            else if(tokens.nextNextToken() instanceof Tokens.Begin){
                tokens.popToken(); //pop off lparen
                if(tokens.nextNextToken() instanceof Tokens.RightParen){ //if no arguments in begin 
                    throw new Exception("Parsing Error: zero arguments to (begin)"); 
                }
                tokens.popToken(); //pop off begin
                List<Nodes.BaseNode> exps = new ArrayList<Nodes.BaseNode>(); //new array of expressions for begin
                while(!(tokens.nextToken() instanceof Tokens.RightParen) || !tokens.hasNext()){
                    exps.add(ExpressionNode(tokens,_true,_false,_empty)); //add expressions to the list
                }
                if(exps.size() == 0){
                    throw new Exception("Parsing Error: zero arguments to (begin)");
                }
                expNode = new Nodes.Begin(exps);
                tokens.popToken();//pop off rparen
            }
            else if(tokens.nextNextToken() instanceof Tokens.Cond){ //we have a cond
                tokens.popToken(); //get rid of lparen
                tokens.popToken(); //get rid of cond
                List<Nodes.Cond.Condition> conditions = new ArrayList<Nodes.Cond.Condition>(); //new array of conditions
                while(!(tokens.nextToken() instanceof Tokens.RightParen || !tokens.hasNext())){
                    conditions.add(Conditions(tokens,_true,_false, _empty)); //keep adding until we hit a rparen
                }
                if(conditions.size() == 0){
                    throw new Exception("Parsing error: cond must have test");
                }
                expNode = new Nodes.Cond(conditions);
                if(!(tokens.nextToken() instanceof Tokens.RightParen)){ 
                    throw new Exception("Parsing Error: must include closing right paren for cond");
                }
                tokens.popToken(); //pop rparen
            }else{ //we are in application. 
                tokens.popToken(); ///get rid of lparen leading application
                List <Nodes.BaseNode> expressions = new ArrayList<Nodes.BaseNode>(); //new array of expressions
                expressions.add((ExpressionNode(tokens,_true,_false,_empty))); //add one expression to start, in case the following while condition is still not met even if there are no exps
                //tokens.popToken();
                while(!(tokens.nextToken() instanceof Tokens.RightParen || !tokens.hasNext())){
                        expressions.add(ExpressionNode(tokens, _true, _false,_empty)); //keep adding until we can't
                }
                if(expressions.size() == 0){ //need 1+ expression 
                    throw new Exception("Parsing Error: must include at least one expression when applying function");
                }
                expNode = new Nodes.Apply(expressions); //make our node
                if(!(tokens.nextToken() instanceof Tokens.RightParen)){
                    throw new Exception("Parsing Error: must include closing right paren for expression");
                }
                tokens.popToken(); //pop rparen
            }
        }else{ //any expression that does not start with a left paren
            if(tokens.nextToken() instanceof Tokens.Abbrev){ //if we have a tick
                tokens.popToken();//pop it
                var data = Datum(expNode,tokens, _true, _false, _empty);//grab our datum
                expNode= new Nodes.Tick((IValue)data); //create the new node
            }
            else if(tokens.nextToken() instanceof Tokens.Identifier){//if we have a plain identifier in expression
                expNode = new Nodes.Identifier(tokens.nextToken().tokenText); //create it
                tokens.popToken(); //pop it
            }
            else {
                expNode = Const(expNode, tokens, _true, _false, _empty);//we must have a const if we have gotten here, or an error. Grab it (or try)
                if(expNode == _empty){ //if there was no const
                    throw new Exception("Unexpected end of expression while parsing expression"); //main error point for expression. Means we could not form one 
                }
            }            
        }
        return expNode; //return whatever expression node we created
    }

    //pretty basic function for finding const nodes. Goes through all of the cases that could be const. 
    public static Nodes.BaseNode Const(Nodes.BaseNode expNode, TokenStream tokens, Nodes.Bool _true, Nodes.Bool _false, Nodes.BaseNode _empty) throws Exception{
        if(tokens.nextToken() instanceof Tokens.Bool){
            if(tokens.nextToken().tokenText.equals("#t")){ //probably should have checked literal here but ¯\_(ツ)_/¯
                tokens.popToken();
                return _true; //returning our _true Nodes.bool
            }
            else{ //or else we have a false
                tokens.popToken();
                return _false;
            }
        }
        else if(tokens.nextToken() instanceof Tokens.Int){ //create an int node
           expNode = new Nodes.Int(((Tokens.Int)tokens.nextToken()).literal);  
           tokens.popToken();    
           return expNode;
        }
        else if(tokens.nextToken() instanceof Tokens.Dbl){ //create a double node
            expNode = new Nodes.Dbl(((Tokens.Dbl)tokens.nextToken()).literal);
            tokens.popToken();
            return expNode;
        }
        else if(tokens.nextToken() instanceof Tokens.Char){ //create a char node
            expNode = new Nodes.Char(((Tokens.Char)tokens.nextToken()).literal);
            tokens.popToken();
            return expNode;
        }
        else if(tokens.nextToken() instanceof Tokens.Str){ //create a string node
            expNode = new Nodes.Str(((Tokens.Str)tokens.nextToken()).literal);
            tokens.popToken();
            return expNode;
        }
        else{
            expNode = _empty; //means we did not find a const.. useful in datum
        }
        return expNode; //return what we found
    }

    //function that deals with all datum
    public static Nodes.BaseNode Datum(Nodes.BaseNode expNode, TokenStream tokens, Nodes.Bool _true, Nodes.Bool _false, Nodes.BaseNode _empty) throws Exception{
        expNode = Const(expNode, tokens, _true, _false, _empty); //since the first five cases of datum are the same as const, we reuse them. 
        if(expNode == _empty){ //if we did not find a token shared between datum and const, must be an vec, symbol, or list. 
            if(tokens.nextToken() instanceof Tokens.Identifier){ //basically, is a symbol b/c <symbol> -> IDENTIFIER
                expNode = new Nodes.Symbol(tokens.nextToken().tokenText); //^^
                tokens.popToken();
                return expNode;
            }
            else{ //either a list or a vector
                if(tokens.nextToken() instanceof Tokens.LeftParen){ //this means we have a list
                    if(tokens.nextNextToken() instanceof Tokens.RightParen){ //this means we have an EMPTY list
                        tokens.popToken(); //get rid of lparen
                        expNode = new Nodes.Cons((IValue)null,null);  //send two nulls to cons constructor so it knows what to do for empty
                        tokens.popToken(); //pop off rparen
                    }
                    else{//nonempty list
                        tokens.popToken(); //pop the lparen
                        Nodes.Cons cons = null; //initialize a cons node to pass to our list function 
                        expNode = List(cons, expNode, tokens, _true, _false, _empty);//function to construct pairs
                        tokens.popToken(); //pop the rparen
                    }
                }
                else if(tokens.nextToken() instanceof Tokens.Vec){ //we're in a vector
                    List<IValue> items = new ArrayList<>(); //create a new array list for the items in the vector
                    if(tokens.nextNextToken() instanceof Tokens.RightParen){ //we have an empty vector
                        tokens.popToken(); //pop vector token
                        items = Vec(items, tokens, expNode, _true, _false, _empty);//grab our (nonexistent) items from a Vec function that I made
                        expNode = new Nodes.Vec(items); //create the vector node
                    }
                    else{ //I realize this else here makes no sense whatsoever since it contains the same stuff as the if but I'm too afraid to change the code and potentially break it ¯\_(ツ)_/¯
                        tokens.popToken(); //get rid of vec
                        items = Vec(items, tokens, expNode, _true, _false, _empty); //grab our items
                        expNode = new Nodes.Vec(items); //create our new vector node
                    }
                    tokens.popToken(); //pop rparen
                }
            }
        }
        return expNode; //return our datum 
    } 

    //a function that recursively defines a list as pairs of values
    public static Nodes.Cons List(Nodes.Cons cons, Nodes.BaseNode expNode, TokenStream tokens, Nodes.Bool _true, Nodes.Bool _false, Nodes.BaseNode _empty) throws Exception{
        if(!(tokens.nextToken() instanceof Tokens.RightParen)){ //we're still in the list
            var datum = Datum(expNode, tokens, _true, _false, _empty); //get car of the list 
            cons = new Nodes.Cons((IValue)datum,List(cons,expNode,tokens,_true,_false, _empty)); //create a new cons node with car and a recursive call to list with the cdr of the list. Notice that this follows the recursive, linked-list nature of lists in scheme
        }else{
            cons = new Nodes.Cons((IValue)null,null); //we have reached the end of the list. no more car or cdr but still need it in AST
        }
        return cons;
    }  

    //function to create vector
    public static List<IValue> Vec(List<IValue> items, TokenStream tokens, Nodes.BaseNode expNode, Nodes.Bool _true, Nodes.Bool _false, Nodes.BaseNode _empty) throws Exception{
        while(!(tokens.nextToken() instanceof Tokens.RightParen)){ //until we hit the end
            items.add((IValue)(Datum(expNode, tokens, _true, _false, _empty))); //add each item in the vector to the list
        }
        return items;
    }

    //function to create formals for a lambda 
    public static List<Nodes.Identifier> Formals(TokenStream tokens) throws Exception{
        List<Nodes.Identifier> formals = new ArrayList<Nodes.Identifier>(); //create a new list of formals (identifiers)
        if(tokens.nextToken() instanceof Tokens.LeftParen){ 
            if(tokens.nextNextToken() instanceof Tokens.RightParen){
                tokens.popToken();//pop lparen
                tokens.popToken(); //pop rparen
                return formals; //allowed to have 0 formals
            }
            tokens.popToken(); //pop lparen
            if(!(tokens.nextToken() instanceof Tokens.Identifier)){//the next token is NOT an identifier
                throw new Exception("Error parsing formals");
            }
            while(!(tokens.nextToken() instanceof Tokens.RightParen)){ //until we have found all formals
                if(!(tokens.nextToken() instanceof Tokens.Identifier || tokens.nextNextToken() instanceof Tokens.RightParen)){
                    throw new Exception("Error parsing formals"); //this might be repetitive error checking considering the if condition but again.. ¯\_(ツ)_/¯
                }
                formals.add(new Nodes.Identifier(tokens.nextToken().tokenText)); //add each formal to the list
                tokens.popToken(); //move to the next
            }
            tokens.popToken(); //pop rparen
        }else{ //there is no leading lparen
            throw new Exception("Syntax Error: missing '(' before formals"); //this throws a whole stack trace along with it but it still prints
        }
        return formals; //return our formals
    }
    //function to create the body of a lambda
    public static List<Nodes.BaseNode> Body(TokenStream tokens, Nodes.Bool _true, Nodes.Bool _false, Nodes.BaseNode _empty) throws Exception{
        List <Nodes.BaseNode> body = new ArrayList<Nodes.BaseNode>(); //new array of body elements (expressions or definitons or both )
        if(tokens.nextToken() instanceof Tokens.LeftParen){ //similar conditions to those we first checked when looking for define
             if(tokens.nextNextToken() instanceof Tokens.Define){ 
                while(tokens.nextNextToken() instanceof Tokens.Define){//we keep adding defines until we run out
                    if(tokens.nextToken() instanceof Tokens.LeftParen){ //keep checking for leading lparens like before 
                        body.add(DefineNode(tokens,_true,_false,_empty));
                    }else{
                        throw new Exception("Left Paren must preceed define"); //same error as before
                    }
                }   
                body.add(ExpressionNode(tokens, _true, _false,_empty)); //once we are done adding defines we add our lone body
                return body;
            }
        }
        body.add(ExpressionNode(tokens, _true, _false,_empty)); //if there was no define
        return body;
    }
    
    //a function to generate a condition to add to our list of conditions created above. Consistent with the subclass in the cond node constructor 
    public static Nodes.Cond.Condition Conditions(TokenStream tokens, Nodes.Bool _true, Nodes.Bool _false, Nodes.BaseNode _empty) throws Exception{
        List<Nodes.BaseNode> expressions = new ArrayList<Nodes.BaseNode>(); //an array of expressions
        Nodes.Cond.Condition condition = null; //null our condition to start 
        Nodes.BaseNode test = _empty;
        if(tokens.nextToken() instanceof Tokens.LeftParen){
            tokens.popToken(); //get rid of lparen
            test = ExpressionNode(tokens, _true, _false,_empty); //grab our test
            if(test == _empty){
                throw new Exception("Must have >= 1 test for cond");
            }
            while(!(tokens.nextToken() instanceof Tokens.RightParen)){
                expressions.add(ExpressionNode(tokens,_true,_false, _empty)); //get our expressions
            }
        }
        tokens.popToken(); 
        condition = new Nodes.Cond.Condition(test,expressions); //pass up our new condition to be added to the list
        return condition;
   } 
}



