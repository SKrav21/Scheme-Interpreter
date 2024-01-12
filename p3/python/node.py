

def And(expressions): return {'type': 'And', 'expressions': expressions}
def Apply(expressions): return {'type': 'Apply', 'expressions': expressions}
def Begin(expressions): return {'type': 'Begin', 'expressions': expressions}
def Bool(val): return {'type': 'Bool', 'val': val}
def BuiltInFunc(name, func): return {'type': 'BuiltInFunc', 'name': name, 'func': func}
def Char(val): return {'type': 'Char', 'val': val}
def Cond(conditions): return {'type': 'Cond', 'conditions': conditions}
def Condition(test, expressions): return {'type': 'Condition', 'test': test, 'expressions': expressions}
def Cons(var1, var2):
   if type(var1) is list:
      if len(var1) == 0:
         raise Exception( "Cannon construct Cons from empty list")
      elif len(var1) == 1:
         car = var1[0]
         cdr = None
      else:
         car = var1[0]
         cdr = Cons(var1[1:], None)
   else:
      car = var1
      cdr = var2
   return {'type': 'Cons', 'car': car, 'cdr': cdr}
def Dbl(val): return {'type': 'Dbl', 'val': val}
def Define(identifier, expression): return {'type': 'Define', 'identifier': identifier, 'expression': expression}
def Identifier(name): return {'type': 'Identifier', 'name': name}
def If(cond, ifTrue, ifFalse): return {'type': 'If', 'cond': cond, 'ifTrue': ifTrue, 'ifFalse': ifFalse}
def Int(val): return {'type': 'Int', 'val': val}
def LambdaDef(formals, body): return{'type': 'LambdaDef', 'formals': formals, 'body': body}
def LambdaVal(env, _lambda): return{'type': 'LambdaVal', 'env': env, '_lambda': _lambda}
def Or(expressions): return {'type': 'Or', 'expressions': expressions}
def Quote(datum): return {'type': 'Quote', 'datum': datum}
def Set(identifier, expression): return {'type': 'Set', 'identifier': identifier, 'expression': expression}
def Str(val): return {'type': 'Str', 'val': val}
def Symbol(name): return {'type': 'Symbol', 'name': name}
def Tick(datum): return {'type': 'Tick', 'datum': datum}
def Vec(items): return {'type': 'Vec', 'items': items}