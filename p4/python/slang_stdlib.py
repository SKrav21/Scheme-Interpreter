import sys
import slang_parser
import operator
import math


def addMathFuncs(env):
    def add(args):
        typeChecker('+', args)
        result = 0
        for arg in args:
            result += arg['val']
        if isinstance(result, int):
            return slang_parser.IntNode(result)
        else:
            return slang_parser.DblNode(result)

    def sub(args):
        typeChecker('-', args)
        result = args[0]['val']
        if len(args) == 1:
            result = 0 - args[0]['val']
        else:
            for arg in args[1:]:
                result -= arg['val']
        if isinstance(result, int):
            return slang_parser.IntNode(result)
        else:
            return slang_parser.DblNode(result)

    def multiply(args):
        typeChecker('*', args)
        result = args[0]['val']
        for arg in args[1:]:
            result *= arg['val']
        if isinstance(result, int):
            return slang_parser.IntNode(result)
        else:
            return slang_parser.DblNode(result)
    
    def divide(args):
        dblCount = typeChecker('/', args)
        result = args[0]['val']
        if len(args) == 1:
            result = 1 / arg['val']
        else:
            for arg in args[1:]:
                if arg['val'] == 0:
                    return env.get('nan')
                result /= arg['val']
        return slang_parser.DblNode(result)

    def modulo(args):
        typeChecker('%', args)
        result = args[0]['val']
        for arg in args[1:]:
            if arg['val'] == 0:
                return env.get('nan')
            result %= arg['val']
        return slang_parser.IntNode(result)

    def isEqual(args):
        typeChecker('==', args)
        result = args[0]['val']
        for arg in args[1:]:
            if arg['val'] != result:
                return env.poundF
        return env.poundT
    
    def isGreater(args):
        typeChecker('>', args)
        result = args[0]['val']
        for arg in args[1:]:
            if result <= arg['val']:
                return env.poundF
        return env.poundT

    def isGreaterOrEqual(args):
        typeChecker('>=', args)
        result = args[0]['val']
        for arg in args[1:]:
            if result < arg['val']:
                return env.poundF
        return env.poundT
    
    def isLessThan(args):
        typeChecker('<', args)
        result = args[0]['val']
        for arg in args[1:]:
            if result >= arg['val']:
                return env.poundF
        return env.poundT

    def isLessThanOrEqual(args):
        typeChecker('<=', args)
        result = args[0]['val']
        for arg in args[1:]:
            if result > arg['val']:
                return env.poundF
        return env.poundT

    #issues with naming a method 'abs'
    def absoluteValue(args):
        isInt = numArgTypeChecker('abs', args)
        result = abs(args[0]['val'])
        if isInt:
            return slang_parser.IntNode(result)
        else:
            return slang_parser.DblNode(result)

    def sqrt(args):
        numArgTypeChecker('sqrt', args)
        if args[0]['val'] < 0:
            return env.get('nan')
        else:
            return slang_parser.DblNode(math.sqrt(args[0]['val']))

    def acos(args):
        numArgTypeChecker('acos', args)
        if args[0]['val'] < -1 or args[0]['val'] > 1:
            return env.get('nan')
        else:
            return slang_parser.DblNode(math.acos(args[0]['val']))

    def asin(args):
        numArgTypeChecker('asin', args)
        if args[0]['val'] < -1 or args[0]['val'] > 1:
            return env.get('nan')
        else:
            return slang_parser.DblNode(math.asin(args[0]['val']))

    def atan(args):
        numArgTypeChecker('asin', args)
        if args[0]['val'] < -math.pi/2 or args[0]['val'] > math.pi/2:
            return env.get('nan')
        else:
            return slang_parser.DblNode(math.asin(args[0]['val']))
    
    def cos(args):
        numArgTypeChecker('cos', args)
        return slang_parser.DblNode(math.cos(args[0]['val']))

    def cosh(args):
        numArgTypeChecker('cosh', args)
        return slang_parser.DblNode(math.cosh(args[0]['val']))

    def sin(args):
        numArgTypeChecker('sin', args)
        return slang_parser.DblNode(math.sin(args[0]['val']))

    def sinh(args):
        numArgTypeChecker('sinh', args)
        return slang_parser.DblNode(math.sinh(args[0]['val']))

    def tan(args):
        numArgTypeChecker('tan', args)
        return slang_parser.DblNode(math.tan(args[0]['val']))

    def tanh(args):
        numArgTypeChecker('tanh', args)
        return slang_parser.DblNode(math.tanh(args[0]['val']))    

    def isInteger(args):
        oneArgCheck('integer?', args)
        if args[0]['type'] == slang_parser.INT:
            return env.poundT
        else:
            return env.poundF
    
    def isDouble(args):
        oneArgCheck('double?', args)
        if args[0]['type'] == slang_parser.DBL:
            return env.poundT
        else:
            return env.poundF

    def isNumber(args):
        oneArgCheck('number?', args)
        if args[0]['type'] == slang_parser.DBL or args[0]['type'] == slang_parser.INT:
            return env.poundT
        else:
            return env.poundF

    def isSymbol(args):
        oneArgCheck('symbol?', args)
        if args[0]['type'] == slang_parser.SYMBOL:
            return env.poundT
        else:
            return env.poundF

    def isProcedure(args):
        oneArgCheck('procedure?', args)
        if args[0]['type'] == slang_parser.BUILTIN or args[0]['type'] == slang_parser.LAMBDAVAL:
            return env.poundT
        else:
            return env.poundF
    
    def log10(args):
        numArgTypeChecker('log10', args)
        if args[0]['val'] < 0:
            return env.get('nan')
        elif args[0]['val'] == 0:
            return env.get('inf-')
        else:
            return slang_parser.DblNode(math.log10(args[0]['val']))

    def loge(args):
        numArgTypeChecker('loge', args)
        if args[0]['val'] < 0:
            return env.get('nan')
        elif args[0]['val'] == 0:
            return env.get('inf-')
        else:
            return slang_parser.DblNode(math.log(args[0]['val']))

    def pow(args):
        typeChecker('pow', args)
        if(len(args) != 2):
            print('pow takes two args: base and exponent')
            sys.exit(0)
        result = args[0]['val'] ** args[1]['val']
        return slang_parser.DblNode(result)
    
    #issues with naming a method 'not'    
    def negation(args):
        oneArgCheck('not', args)
        if args[0]['val'] == env.poundT:
            return env.poundF
        else:
            return env.poundT

    def intToDbl(args):
        oneArgCheck('integer->double', args)
        if not args[0]['type'] == slang_parser.INT:
            print('Argument for integer->double must be of type integer')
            sys.exit(0)
            
        return slang_parser.DblNode(float(args[0]['val']))

    def dblToInt(args):
        oneArgCheck('double->integer', args)
        if not args[0]['type'] == slang_parser.DBL:
            print('Argument for integer->double must be of type integer')
            sys.exit(0)
        return slang_parser.IntNode(int(args[0]['val']))
    
    def isNull(args):
        oneArgCheck('null?', args)
        if args[0]['type'] == slang_parser.CONS:
            if args[0]['car'] is None:
                return env.poundT
            return env.poundF
        return env.poundF

    #adding functions to the library
    env.put('+', slang_parser.BuiltInNode('+', add))
    env.put('-', slang_parser.BuiltInNode('-', sub))
    env.put('*', slang_parser.BuiltInNode('*', multiply))
    env.put('/', slang_parser.BuiltInNode('/', divide))
    env.put('%', slang_parser.BuiltInNode('%', modulo))
    env.put('==', slang_parser.BuiltInNode('==', isEqual))
    env.put('>', slang_parser.BuiltInNode('>', isGreater))
    env.put('>=', slang_parser.BuiltInNode('>=', isGreaterOrEqual))
    env.put('<', slang_parser.BuiltInNode('<', isLessThan))
    env.put('<=', slang_parser.BuiltInNode('<=', isLessThanOrEqual))
    env.put('abs', slang_parser.BuiltInNode('abs', absoluteValue))
    env.put('sqrt', slang_parser.BuiltInNode('sqrt', sqrt))
    env.put('acos', slang_parser.BuiltInNode('acos', acos))
    env.put('asin', slang_parser.BuiltInNode('asin', asin))
    env.put('atan', slang_parser.BuiltInNode('atan', atan))
    env.put('cos', slang_parser.BuiltInNode('cos', cos))
    env.put('cosh', slang_parser.BuiltInNode('cosh', cosh))
    env.put('sin', slang_parser.BuiltInNode('sin', sin))
    env.put('sinh', slang_parser.BuiltInNode('sinh', sinh))
    env.put('tan', slang_parser.BuiltInNode('tan', tan))
    env.put('tanh', slang_parser.BuiltInNode('tanh', sinh))
    env.put('integer?', slang_parser.BuiltInNode('integer?', isInteger))
    env.put('double?', slang_parser.BuiltInNode('double?', isDouble))
    env.put('number?', slang_parser.BuiltInNode('number?', isNumber))
    env.put('symbol?', slang_parser.BuiltInNode('symbol?', isSymbol))
    env.put('procedure?', slang_parser.BuiltInNode('procedure?', isProcedure))
    env.put('log10', slang_parser.BuiltInNode('log10', log10))
    env.put('loge', slang_parser.BuiltInNode('loge', loge))
    env.put('pow', slang_parser.BuiltInNode('pow', pow))
    env.put('not', slang_parser.BuiltInNode('not', negation))
    env.put('integer->double', slang_parser.BuiltInNode('integer->double', intToDbl))
    env.put('double->integer', slang_parser.BuiltInNode('double->integer', dblToInt))
    env.put('none?', slang_parser.BuiltInNode('none?', isNull))

    #constants
    env.put('pi', slang_parser.DblNode(math.pi))
    env.put('e', slang_parser.DblNode(math.e))
    env.put('tau', slang_parser.DblNode(math.tau))
    env.put('inf+', slang_parser.DblNode(math.inf))
    env.put('inf-', slang_parser.DblNode(-math.inf))
    env.put('nan', slang_parser.DblNode(math.nan))

    #auxilary helper functions
    def oneArgCheck(name, args):
        if len(args) != 1:
            print("function '" + name + "' takes exactly one argument")
            sys.exit(0)
        
    def numArgTypeChecker(name, args):
        oneArgCheck(name, args)
        if args[0]['type'] == slang_parser.INT:
            return True
        if args[0]['type'] == slang_parser.DBL:
            return False
        print(name + " handles only arguments of type int or double")
        sys.exit(0)

    def typeChecker(name, args):
        intCount = 0
        dblCount = 0
        for arg in args:
            if arg['type'] == slang_parser.INT:
                intCount += 1
            if arg['type'] == slang_parser.DBL:
                dblCount += 1
        if len(args) > intCount + dblCount:
            print(name + " can only handle Int and Dbl arguments")
            sys.exit(0)
        if len(args) == 0:
            print(name + " expects at least one argument")
            sys.exit(0)
        return dblCount

def addListFuncs(env):
    def car(args):
        listFirstArgCheck('car', args)
        numArgs('car', args, 1)
        return args[0]['car']

    def cdr(args):
        listFirstArgCheck('cdr', args)
        numArgs('cdr', args, 1)
        return args[0]['cdr']

    def cons(args):
        numArgs('cons', args, 2)
        checkIsValue('cons', args[0])
        checkIsValue('cons', args[1])
        return slang_parser.ConsNode(args[0], args[1])

    #naming a method 'list' causes problems in python
    def makeList(args):
        for arg in args:
            checkIsValue('list', arg)
        if len(args) != 0:
            return slang_parser.ConsNode(args, env.empty)
        else:
            return slang_parser.ConsNode(env.empty, env.empty)
    
    def isList(args):
        numArgs('list?', args, 1)
        if not args[0]['type'] == slang_parser.CONS:
            return env.poundF
        else:
            return env.poundT
    
    
    def setCar(args):
        numArgs('set-car!', args, 2)
        if not args[0]['type'] == slang_parser.CONS:
            print('First Argument of function set-car! must be a list')
            sys.exit(0)
        checkIsValue('list?', args[1])
        args[0]['car'] = args[1]
        return None

    def setCdr(args):
        numArgs('set-cdr!', args, 2)
        if not args[0]['type'] == slang_parser.CONS:
            print('First Argument of function set-car! must be a list')
            sys.exit(0)
        checkIsValue('list?', args[1])
        args[0]['cdr'] = args[1]
        return None
    

    #adding functions to the library
    env.put('car', slang_parser.BuiltInNode('car', car))
    env.put('cdr', slang_parser.BuiltInNode('cdr', cdr))
    env.put('cons', slang_parser.BuiltInNode('cons', cons))
    env.put('list?', slang_parser.BuiltInNode('list?', makeList))
    env.put('isList', slang_parser.BuiltInNode('isList', isList))
    env.put('set-car!', slang_parser.BuiltInNode('set-car!', setCar))
    env.put('set-cdr!', slang_parser.BuiltInNode('set-cdr!', setCdr))



    def numArgs(name, args, numArgs):
        if len(args) != numArgs:
            print('Incorrect Number of Arguments to function ' + name + ': ' + str(len(args)) + ' found, ' + str(numArgs) + ' expected')  
            sys.exit(0)
    
    def listFirstArgCheck(name, args):
        if not args[0]['type'] == slang_parser.CONS:
            print('First Argument to function ' + name + ' must be a list')
            sys.exit(0)
    
    def checkIsValue(name, arg):
        values = [3, 4, 5, 7, 8, 12, 14, 18, 19, 21] #values are bools, basenodes, char, cons, dbl, int lambdaVal, Str, symbol, vec
        if not arg['type'] in values:
            print(name + ' should have a value for this argument (int,str,char,etc.)')
            sys.exit(0)
        
def addStringFuncs(env):
    def stringAppend(args):
        argsCheckerString('string-append', args, 2, True)
        return slang_parser.StrNode(args[0]['val'] + args[1]['val'])

    def stringLength(args):
        argsCheckerString('string-length', args, 1, True)
        return slang_parser.IntNode(len(args[0]['val']))
    
    def subString(args):
        argsCheckerString('substring', args, 3, False)
        if not args[0]['type'] == slang_parser.StrNode:
            print('First argument to substring must be of type string')
            sys.exit(0)
        if args[1]['type'] != slang_parser.IntNode or args[2]['type'] != slang_parser.IntNode:
            print('substring bounds must be of type integer')
        fromInc = args[1]['val']
        toExc = args[2]['val']
        string = args[0]['val']
        indexOOB(fromInc, string)
        indexOOB(toExc, string)
        if fromInc > toExc:
            print('Starting index greater than ending index for substring')
            sys.exit(0)
        return slang_parser.StrNode(string[fromInc:toExc])

    def isString(args):
        argsCheckerString('string?', args, 1, False)
        if args[0]['type'] != slang_parser.STR:
            return env.poundF
        else:
            return env.poundT
    
    def strRef(args):
        argsCheckerString('string-ref', args, 2, False)
        if args[0]['type'] != slang_parser.STR:
            print('First argument to string-ref must be of type string')
            sys.exit(0)
        if args[1]['type'] != slang_parser.INT:
            print('string-ref index must be of type integer')
            sys.exit(0)
        string = args[0]['val']
        index = args[1]['val']
        indexOOB(index, string)
        return slang_parser.CharNode(str[index])
    
    def strEqual(args):
        argsCheckerString('string-equal?', args, 2, True)
        str1 = args[0]['val']
        str2 = args[1]['val']
        if str1 == str2:
            return env.poundT
        else:
            return env.poundF
    
    def string(args):
        result = ''
        for arg in args:
            if arg['type'] != slang_parser.CHAR:
                print('Arguments to string must be all characters of from #\\x')
                sys.exit(0)
            result += arg['val']
        return slang_parser.StrNode(result)
    
        

    env.put('string-append', slang_parser.BuiltInNode('string-append', stringAppend))
    env.put('string-length', slang_parser.BuiltInNode('string-length', stringLength))
    env.put('substring', slang_parser.BuiltInNode('substring', subString))
    env.put('string?', slang_parser.BuiltInNode('string?', isString))
    env.put('string-ref?', slang_parser.BuiltInNode('string-ref?', strRef))
    env.put('string-equal?', slang_parser.BuiltInNode('string-equal?', strEqual))
    env.put('string', slang_parser.BuiltInNode('string', string))
    
    def indexOOB(index, args):
        if index >= len(args):
            print('Index out of bounds error: index ' + str(index) + ' out of bounds for length ' + str(len(arg)))
            sys.exit(0)
    
    def checkArgsForStrings(name, args, numArgs):
        for arg in args:
            if not arg['type'] == slang_parser.STR:
                print('Function ' + name + ' requires ' + str(numArgs) + ' arguments of type string')
                sys.exit(0)
        
    def argsCheckerString(name, args, numArgs, checkStr):
        if len(args) != numArgs:
            print('Incorrect Number of Arguments found for function ' + name + ': ' + str(len(args)) + ' found, ' + str(numArgs) + ' expected')
            sys.exit(0)
        if checkStr:
            checkArgsForStrings(name, args, numArgs)
    

def addVectorFuncs(env):
    def vectorLength(args):
        argsCheck('vector-length', args, 1, True)
        numElements = len(args[0]['items'])
        return slang_parser.IntNode(numElements)
    
    def vectorGet(args):
        argsCheck('vector-get', args, 2, True)
        if args[1]['type'] != slang_parser.INT:
            print('Index into vector must be of type int')
            sys.exit(0)
        outOfBoundsErr(args)
        return args[0]['items'][args[1]['val']]
    
    def vectorSet(args):
        argsCheck('vector-set!', args, 3, True)
        if args[1]['type'] != slang_parser.INT:
            print('Index into vector must be of type int')
            sys.exit(0)
        outOfBoundsErr(args)
        checkIsValue('vector', args[2])
        args[0]['items'][args[1]['val']] = args[2]
        return None
    
    def vector(args):
        for i in range(len(args)):
            checkIsValue('vector', args[i])
        return slang_parser.VecNode(args)
    
    def isVector(args):
        argsCheck('vector?', args, 1, False)
        checkIsValue('vector?', args[0])
        if args[0]['type'] == slang_parser.VEC:
            return env.poundT
        else:
            return env.poundF
        
    def makeVector(args):
        argsCheck('make-vector', args, 1, False)
        if args[0]['type'] != slang_parser.INT:
            print('Vector size must be of type integer')
        items = []
        for i in range(len(args[0]['val'])):
            items.append(env.poundF)
        return slang_parser.VecNode(items)
    
        

    
    env.put('vector-length', slang_parser.BuiltInNode('vector-length', vectorLength))
    env.put('vector-get', slang_parser.BuiltInNode('vector-get', vectorGet))
    env.put('vector-set!', slang_parser.BuiltInNode('vector-set!', vectorSet))
    env.put('vector', slang_parser.BuiltInNode('vector', vector))
    env.put('vector-set!', slang_parser.BuiltInNode('vector-set!', vectorSet))
    env.put('make-vector', slang_parser.BuiltInNode('make-vector', makeVector))

    def outOfBoundsErr(args):
        if args[1]['val'] >= len(args[0]['items']):
            print('Index ' + str(args[1]['val']) + ' out of bounds for length ' + str(len(args[0]['items'])))
            sys.exit(0)

    def vecTypeCheck(name, args, numCorrectArgs):
        if args[0]['type'] != slang_parser.VEC:
            print('function ' + name + ' takes vector argument as its first argument of (' + str(numCorrectArgs) + ')')
            sys.exit(0)
    
    def argsCheck(name, args, numCorrectArgs, checkFirst):
        if checkFirst:
            vecTypeCheck(name, args, numCorrectArgs)
        if len(args) != numCorrectArgs:
            print('Incorrect Number of Arguments found for function ' + name + ': found ' + str(len(args)) + ', expected' + str(numCorrectArgs))
            sys.exit(0)

    def checkIsValue(name, arg):
        values = [3, 4, 5, 7, 8, 12, 14, 18, 19, 21] #values are bools, basenodes, char, cons, dbl, int lambdaVal, Str, symbol, vec
        if not arg['type'] in values:
            print(name + ' should have a value for this argument (int,str,char,etc.)')
            sys.exit(0)
