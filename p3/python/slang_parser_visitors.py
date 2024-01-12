import slang_parser
import node

def AstToXml(expr, indent=""):
    visitor = Visitor()
    return visitor.visit(expr, indent)


#identical to escape i used in p2
#method for properly printing escape characters for values of messages or literals
def escape(s) -> str:
    return s.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("'", "\\'")    

class Visitor:
    def __init__(self):
        pass

    def visit(self, expr, indent):
        method = 'visit' + expr['type']
        visit = getattr(self, method, self.defaultVisit)
        return visit(expr, indent)

    def defaultVisit(self, expr, indent):
        raise Exception('No visit method for node of type ' + expr['type'])

    def visitIdentifier(self, expr, indent):
        xml = indent + '<Identifier val=\'' + escape(expr['name']) + '\' />\n'
        return xml

    def visitDefine(self, expr, indent):
        xml = indent + '<Define>\n'
        indent += ' '
        xml += self.visit(expr['identifier'], indent)
        xml += self.visit(expr['expression'], indent)
        indent = indent[1:]
        xml += indent + '</Define>\n'
        return xml

    def visitBool(self, expr, indent):
        xml = indent + '<Bool val=\'' + ('true' if expr['val'] else 'false') + '\' />\n'
        return xml

    def visitInt(self, expr, indent):
        xml = indent + '<Int val=\'' + str(expr['val']) + '\' />\n'
        return xml

    def visitDbl(self, expr, indent):
        xml = indent + '<Db; val=\'' + str(expr['val']) + '\' />\n'
        return xml

    def visitLambdaDef(self, expr, indent):
        xml = indent + '<Lambda>\n'
        indent += ' '
        xml += indent + '<Formals>\n'
        indent += ' '
        for f in expr['formals']:
            xml += self.visit(f, indent)
        indent = indent[1:]
        xml += indent + '</Formals>\n'
        xml += indent + '<Expressions>\n'
        indent += ' '
        for e in expr['body']:
            xml += self.visit(e, indent)
        indent = indent[1:]
        xml += indent + '</Expressions>\n'
        indent = indent[1:]
        xml += indent + '</Lambda>\n'
        return xml

    def visitIf(self, expr, indent):
        xml = indent + '<If>\n'
        indent += ' '
        xml += self.visit(expr['cond'], indent)
        xml += self.visit(expr['ifTrue'], indent)
        xml += self.visit(expr['ifFalse'], indent)
        indent = indent[1:]
        xml += indent + '</If>\n'
        return xml
    
    def visitSet(self, expr, indent):
        xml = indent + '<Set>\n'
        indent += ' '
        xml += self.visit(expr['identifier'], indent)
        xml += self.visit(expr['expression'], indent)
        indent = indent[1:]
        xml += indent + '</Set>\n'
        return xml

    def visitAnd(self, expr, indent):
        xml = indent + '<And>\n'
        indent += ' '
        for e in expr['expressions']:
            xml += self.visit(e, indent)
        indent = indent[1:]
        xml += indent + '</And>\n'
        return xml

    def visitOr(self, expr, indent):
        xml = indent + '<Or>\n'
        indent += ' '
        for e in expr['expressions']:
            xml += self.visit(e, indent)
        indent = indent[1:]
        xml += indent + '</Or>\n'
        return xml

    def visitBegin(self, expr, indent):
        xml = indent + '<Begin>\n'
        indent += ' '
        for e in expr['expressions']:
            xml += self.visit(e, indent)
        indent = indent[1:]
        xml += indent + '</Begin>\n'
        return xml

    def visitApply(self, expr, indent):
        xml = indent + '<Apply>\n'
        indent += ' '
        for e in expr['expressions']:
            xml += self.visit(e, indent)
        indent = indent[1:]
        xml += indent + '</Apply>\n'
        return xml

    def visitCons(self, expr, indent):
        xml = indent + '<Cons>\n'
        indent += ' '
        if expr['car'] is not None:
            xml += self.visit(expr['car'], indent)
        else:
            xml += indent + '<Null />\n'
        if expr['cdr'] is not None:
            xml += self.visit(expr['cdr'], indent)
        else:
            xml += indent + '<Null />\n'       
        indent = indent[1:]
        xml += indent + '</Cons>\n'
        return xml
    
    def visitVec(self, expr, indent):
        xml = indent + '<Vector>\n'
        indent += ' '
        for i in  expr['items']:
            xml += self.visit(i, indent)
        indent = indent[1:]
        xml += indent + '</Vector>\n'
        return xml

    def visitSymbol(self, expr, indent):
        xml = indent + '<Symbol val=\'' + expr['name'] + '\' />\n'
        return xml

    def visitQuote(self, expr, indent):
        xml = indent + '<Quote>\n'
        indent += ' '
        xml += self.visit(expr['datum'], indent)
        indent = indent[1:]
        xml += indent + '</Quote>\n'
        return xml

    def visitTick(self, expr, indent):
        xml = indent + '<Tick>\n'
        indent += ' ' 
        xml += self.visit(expr['datum'], indent)
        indent = indent[1:]
        xml += indent + '</Tick>\n'
        return xml
    
    def visitChar(self, expr, indent):
        xml = indent + '<Char val=\'' + escape(expr['val']) + '\' />\n'
        return xml
    
    def visitStr(self, expr, indent):
        xml = indent + '<Str val=\'' + escape(expr['val']) + '\' />\n'
        return xml
    
    def visitBuiltInFunc(self, expr, indent):
        raise Exception('BuiltInFunc should not be visited during AST printing')

    def visitLambdaVal(self, expr, indent):
        raise Exception('LambdaVal should not be visited during AST printing')

    def visitCond(self, expr, indent):
        xml = indent + '<Cond>\n'
        indent += ' '
        for cond in expr['conditions']:
            xml += indent + '<Condition>\n'
            indent += ' '
            xml += indent + '<Test>\n'
            indent += ' '
            xml += self.visit(cond['test'], indent)
            indent = indent[1:]
            xml += indent + '</Test>\n'
            xml += indent + '<Actions>\n'
            indent += ' '
            for e in cond['expressions']:
                xml += self.visit(e, indent)
            indent = indent[1:]
            xml += indent + '</Actions>\n'
            indent = indent[1:]
            xml += indent + '</Condition>\n'
        indent = indent[1:]
        xml += indent + '</Cond>\n'
        return xml
