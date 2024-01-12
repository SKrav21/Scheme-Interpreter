import sys
import slang_parser
import slang_env


def evaluate(expr, env):
    visitor = Visitor()
    return visitor.visit(expr, env)

#i really hate this code but i greatly prefer it to naming all my functions "visit1", "visit2", "visit3"... ect
def getType(num):
    match num:
        case 0: return 'And'
        case 1: return 'Apply'
        case 2: return 'Begin'
        case 3: return 'Bool'
        case 4: return 'BuiltIn'
        case 5: return 'Char'
        case 6: return 'Cond'
        case 7: return 'Cons'
        case 8: return 'Dbl'
        case 9: return 'Define'
        case 10: return 'Identifier'
        case 11: return 'If'
        case 12: return 'Int'
        case 13: return 'LambdaDef'
        case 14: return 'LambdaVal'
        case 15: return 'Or'
        case 16: return 'Quote'
        case 17: return 'Set'
        case 18: return 'Str'
        case 19: return 'Symbol'
        case 20: return 'Tick'
        case 21: return 'Vec'

#basic visiting functionality using the getattr method. Similar to code in P3.
class Visitor:

    def __init__(self):
        pass

    def visit(self, expr, env):
        method = 'visit' + getType(expr['type'])
        visit = getattr(self, method, self.defaultVisit)
        return visit(expr, env)

    def defaultVisit(self, expr, env):
        raise Exception('No visit method for node of type ' + expr['type'])

    def visitIdentifier(self, expr, env):
        expression = env.get(expr['name'])
        return expression
    
    def visitDefine(self, expr, env):
        env.put(expr['id']['name'], self.visit(expr['expr'], env))
        
    def visitBool(self, expr, env):
        if expr['val'] == True:
            return env.poundT
        else:
            return env.poundF
    
    def visitInt(self, expr, env):
        return expr
    
    def visitDbl(self, expr, env):
        return expr
    
    def visitLambdaVal(self, expr, env):
        return expr
    
    def visitLambdaDef(self, expr, env):
        return slang_parser.LambdaValNode(slang_env.makeInnerEnv(env), expr) 
    
    def visitIf(self, expr, env):
        if expr['cond'] == env.poundF:
            return expr['false']
        else:
            return expr['true']
        
    #def visitSet(self, expr, env):
        #env.g

    def visitAnd(self, expr, env):
        i = 0
        while i < len(expr['exprs']):
            if self.visit(expr['exprs'][i], env) == env.poundF:
                return env.poundF
            i += 1
        return env.poundT
    
    def visitOr(self, expr, env):
        numExpr = len(expr['exprs'])
        falseCount = 0
        for i in range(numExpr):
            if self.visit(expr['exprs'][i], env) == env.poundF:
                falseCount += 1
        if falseCount != numExpr:
            return env.poundT
        else:
            return env.poundF

    def visitBegin(self, expr, env):
        numExpr = len(expr['exprs'])
        for i in range(numExpr):
            self.visit(expr['exprs'][i], env)
        return self.visit(expr['exprs'][numExpr-1], env)

    def visitApply(self, expr, env):
        numExpr = len(expr['exprs'])
        if self.visit(expr['exprs'][0], env)['type'] == slang_parser.BUILTIN: #BUILTIN
            f = self.visit(expr['exprs'][0], env)
            funcs = []
            for i in range(1, numExpr):
                funcs.append(self.visit(expr['exprs'][i], env))
            return f['func'](funcs)
        elif self.visit(expr['exprs'][0], env)['type'] == slang_parser.LAMBDAVAL: #LAMBDAVAL
            l = self.visit(expr['exprs'][0], env)
            if len(l['lambda']['formals']) != numExpr - 1:
                print('Incorrect number of arguments')
                sys.exit(0)
            innerEnv = slang_env.makeInnerEnv(l['env'])
            for i in range(1, numExpr):
                innerEnv.put(l['lambda']['formals'][i-1]['name'], self.visit(expr['exprs'][i], env)['val'])
            for i in range(len(l['lambda']['exprs'])):
                self.visit(l['lambda']['exprs'][i], innerEnv)
            return self.visit(l['lambda']['exprs'][len(l['lambda']['exprs']) - 1], innerEnv)
        else:
            print('Operator is not a Procedure')
            sys.exit(0)

    def visitCons(self, expr, env):
        if expr['car'] == env.empty:
            return env.empty
        return expr['car']

    def visitVec(self, expr, env):
        return expr
    
    def visitSymbol(self, expr, env):
        return expr
    
    def visitQuote(self, expr, env):
        return expr['datum']
    
    def visitTick(self, expr, env):
        return expr['datum']

    def visitChar(self, expr, env):
        return expr
    
    def visitStr(self, expr, env):
        return expr
    
    def visitBuiltIn(self, expr, env):
        return expr
    
    def visitCond(self, expr, env):
        numConditions = len(expr['conditions'])
        for i in range(numConditions):
            if self.visit(expr['conditions'][i]['test'], env) == env.poundT:
                numExpr = len(expr['conditions'][i]['exprs'])
                for j in range(numExpr - 1):
                    self.visit(expr['conditions'][i]['exprs'][j], env)
                return self.visit(expr['conditions'][i]['exprs'][numExpr - 1], env)
        return None #if all are false, return nothing