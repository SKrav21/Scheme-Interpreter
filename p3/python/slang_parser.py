import slang_scanner
import node
import sys

# [CSE 262] You will probably find it tedious to create a whole class hierarchy
# for your Python parser.  Instead, consider whether each node type could just
# be a hash table.  In that case, you could have a function for "constructing"
# each "type", by putting some values into a hash table.


class Parser:
    """The parser class is responsible for parsing a stream of tokens to produce
    an AST"""

    def __init__(self, true, false, empty):
        """Construct a parser by caching the environmental constants true,
        false, and empty"""
        self.true = true
        self.false = false
        self.empty = empty

    def parse(self, tokens):
        nodesList = []
        while tokens.hasNext():#<program> -> <form>*
            if tokens.nextNextToken().type == 7:#DEFINE
                if tokens.nextToken().type == 13:#LEFTPAREN
                    nodesList.append(self.DefineNode(tokens))
                else:
                    print('Left Paren must preceed define')
                    sys.exit()
            else:
                nodesList.append(self.ExpressionNode(tokens))
                tokens.popToken()#eof
        return nodesList

    def DefineNode(self, tokens):
        define = self.empty
        tokens.popToken()#lparen
        tokens.popToken()#define
        if tokens.nextToken().type == 9:#IDENTIFIER
            id = node.Identifier(tokens.nextToken().tokenText)
            tokens.popToken()#pop identifier
            exp = self.ExpressionNode(tokens)
            define = node.Define(id, exp)
            tokens.popToken()#rparen
            tokens.popToken()#eof
            return define
        return define
    
    def ExpressionNode(self, tokens):
        expNode = self.empty
        if tokens.nextToken().type == 13:#LEFTPAREN
            if tokens.nextNextToken().type == 15:#QUOTE
                tokens.popToken()#lparen
                tokens.popToken()#quote
                qDatum = self.Datum(expNode, tokens)
                if qDatum == self.empty:
                    print('Unexpected token while parsing (quote)')
                    sys.exit()
                expNode = node.Quote(qDatum)
                tokens.popToken()#rparen
            elif tokens.nextNextToken().type == 12:#LAMBDA
                tokens.popToken()#lparen
                tokens.popToken()#lambda
                formals = self.Formals(tokens)
                body = self.Body(tokens)
                expNode = node.LambdaDef(formals, body)
                tokens.popToken()#rparen
            elif tokens.nextNextToken().type == 10:#IF
                tokens.popToken()#lparen
                tokens.popToken()#lambda
                cond = self.ExpressionNode(tokens)
                ifTrue = self.ExpressionNode(tokens)
                ifFalse = self.ExpressionNode(tokens)    
                expNode = node.If(cond, ifTrue, ifFalse)
                tokens.popToken()#rparen
            elif tokens.nextNextToken().type == 17:#SET
                tokens.popToken()#lparen
                tokens.popToken()#rparen
                if not tokens.hasNext():
                    print('Must incldue identifier after set!')
                if tokens.nextToken().type == 9:#IDENTIFIER
                    ident = node.Identifier(tokens.nextToken().tokenText)
                    tokens.popToken()#pop the ident
                    exp = self.ExpressionNode(tokens)
                    if(exp == self.empty):#check expression was made
                        print('Must include expression after identifier for set!')
                        sys.exit()
                    expNode = node.Set(ident, exp)
                    tokens.popToken()#rparen
                else:#token that isn't ident
                    print('First argument to set! is not an identifier')
                    sys.exit()
            elif tokens.nextNextToken().type == 1:#AND
                tokens.popToken()#lparen
                tokens.popToken()#and
                if tokens.hasNextNext() and tokens.nextNextToken().type == 8:#EOF
                    print('Must include at least one expression following And')             
                    sys.exit()
                exps = []#list of expressions
                while not tokens.nextToken().type == 16:#RIGHTPAREN
                    exps.append(self.ExpressionNode(tokens))
                if len(exps) == 0:
                    print('Parsing Error: Must include at least one expression following And')       
                    sys.exit()
                expNode = node.And(exps)
                tokens.popToken()
                tokens.popToken()#rightParen
            elif tokens.nextNextToken().type == 14:#OR
                tokens.popToken()#lparen
                tokens.popToken()#or
                exps = []
                while not tokens.nextToken().type == 16:#RIGHTPAREN
                    exps.append(self.ExpressionNode(tokens))
                if len(exps) == 0:
                    print('Parsing Error: zero arguments to (Or)')
                    sys.exit()
                expNode = node.Or(exps)
                tokens.popToken()#rparen
            elif tokens.nextNextToken().type == 2:#BEGIN
                tokens.popToken()#lparen
                if tokens.nextNextToken().type == 16:#RIGHTPAREN
                    print('Parsing Error: zero arguments to (begin)')
                    sys.exit()
                tokens.popToken()#begin
                exps = []
                while (tokens.hasNext() and tokens.nextToken().type != 16):#RIGHTPAREN
                    exps.append(self.ExpressionNode(tokens))
                if len(exps) == 0:
                    print('Parsing Error: zero arguments to (begin)')
                    sys.exit()
                expNode = node.Begin(exps)
                tokens.popToken()#rparen
            elif tokens.nextNextToken().type == 5:#COND
                tokens.popToken()#lparen
                tokens.popToken()#cond
                conditions = []
                while (tokens.hasNext() and tokens.nextToken().type != 16):#RIGHTPAREN
                    conditions.append(self.Conditions(tokens))
                if len(conditions) == 0:
                    print('Parsing error: cond must have test')
                    sys.exit()
                expNode = node.Cond(conditions)
                if not tokens.hasNext() or (tokens.hasNext() and tokens.nextToken().type != 16):
                    print('Parsing Error: must include closing right paren for cond')
                    sys.exit()
                tokens.popToken()#paren
            else:#application
                tokens.popToken()#remove lparen
                expressions = []
                expressions.append(self.ExpressionNode(tokens))
                while (tokens.hasNext() and tokens.nextToken().type != 16):#RIGHTPAREN
                    expressions.append(self.ExpressionNode(tokens))
                if len(expressions) == 0:
                    print('Parsing Error: must include at least one expression when applying function')
                    sys.exit()
                expNode = node.Apply(expressions)
                if not tokens.nextToken().type == 16:
                    print('Prasing Error: must include closing right paren for expression')
                    sys.exit()
                tokens.popToken()#rparen
        else:
            if tokens.nextToken().type == 0:#ABBREV
                tokens.popToken()#pop the abbrev
                data = self.Datum(expNode, tokens)
                expNode = node.Tick(data)
            elif tokens.nextToken().type == 9:#IDENTIFIER
                expNode = node.Identifier(tokens.nextToken().tokenText)    
                tokens.popToken()#pop the identifier
            else:#Const
                expNode = self.Const(expNode, tokens)
                if expNode == self.empty:
                    print('Unexpected end of expression while parsing expression')
                    sys.exit()
        return expNode
                
    
    def Const(self, expNode, tokens):
        if tokens.nextToken().type == 3:#BOOL
            if tokens.nextToken().tokenText == '#t':
                tokens.popToken()
                expNode = self.true
            else:
                tokens.popToken()
                expNode = self.false
        elif tokens.nextToken().type == 11:#INT
            expNode = node.Int(tokens.nextToken().literal)
            tokens.popToken()
        elif tokens.nextToken().type == 6:#DBL
            expNode = node.Dbl(tokens.nextToken().literal)
            tokens.popToken()
        elif tokens.nextToken().type == 4:#CHAR
            expNode = node.Char(tokens.nextToken().literal)
            tokens.popToken()
        elif tokens.nextToken().type == 18:#STR
            expNode = node.Str(tokens.nextToken().literal)
            tokens.popToken()
        else:
            expNode = self.empty#did not find a const
        return expNode
    
    def Datum(self, expNode, tokens):
        expNode = self.Const(expNode, tokens)
        if expNode == self.empty:#if nothing was found in const
            if tokens.nextToken().type == 9:#IDENTIFIER (means it is a symbol)
                expNode = node.Symbol(tokens.nextToken().tokenText)
                tokens.popToken()
                return expNode
            else:#if not symbol it must be list or vector
                if tokens.nextToken().type == 13:#LEFTPAREN
                    if tokens.nextToken().type == 16:#RIGHTPAREN
                        tokens.popToken()#lparen
                        expNode = self.empty
                        tokens.popToken()#rparen
                    else:#nonemptylist
                        tokens.popToken()#lparen
                        expNode = self.List(self.empty, expNode, tokens)
                        tokens.popToken()#paren
                elif tokens.nextToken().type == 19:#VECTOR
                    items = []
                    tokens.popToken()#vector
                    items = self.Vec(items, tokens, expNode)
                    expNode = node.Vec(items)
                    tokens.popToken()#RIGHTPAREN
        return expNode
    
    def List(self, cons, expNode, tokens):
        if not tokens.nextToken().type == 16:#RIGHTPAREN
            datum = self.Datum(expNode, tokens)
            cons = node.Cons(datum, self.List(cons, expNode, tokens))
        else:#end of the list
            cons = self.empty
        return cons

    def Vec(self, items, tokens, expNode):
        while not tokens.nextToken().type == 16:#RIGHTPAREN
            items.append(self.Datum(expNode, tokens))
        return items
    
    def Formals(self, tokens):
        formals = []
        if tokens.nextToken().type == 13:#LEFTPAREN
            if tokens.nextNextToken().type == 16:#RIGHTPAREN (if no formals)
                tokens.popToken()#lparen
                tokens.popToken()#rparen
                return formals
            tokens.popToken()#lparen
            if not tokens.nextToken().type == 9:#IDENTIFIER
                print('Error parsing formals')
                sys.exit()
            while not tokens.nextToken().type == 16:#RIGHTPAREN
                if not (tokens.nextToken().type == 9 or tokens.nextNextToken().type == 16):#next IDENTIFIER or nextnext RIGHTPAREN
                    print('Error parsing formals')
                    sys.exit()
                formals.append(node.Identifier(tokens.nextToken().tokenText))
                tokens.popToken()#next Token
            tokens.popToken()#rparen
        else:#no leading lparen
            print('Syntax Error: missing \'(\' before formals')
            sys.exit()
        return formals

    def Body(self, tokens):
        body  = []
        if tokens.nextToken() == 13:#LEFTPAREN
            if tokens.nextNextToken().type == 7:#DEFINE
                while tokens.nextNextToken().type == 7:#DEFINE
                    if tokens.nextToken().type == 13:#LEFTPAREN
                        body.append(self.DefineNode(tokens))
                    else:
                        print('Left Paren must preceed define')
                        sys.exit()
                body.append(self.ExpressionNode(tokens))
                return body
        body.append(self.ExpressionNode(tokens))
        return body
    
    def Conditions(self, tokens):
        expressions = []
        condition = self.empty
        test = self.empty
        if tokens.nextToken().type == 13:#LEFTPAREN
            tokens.popToken()#lparen
            test = self.ExpressionNode(tokens)
            if test == self.empty:
                print('Must have >= 1 test for cond')
                sys.exit()
            while not tokens.nextToken().type == 16:#RIGHTPAREN
                expressions.append(self.ExpressionNode(tokens))
        tokens.popToken()
        condition = node.Condition(test, expressions)
        return condition
