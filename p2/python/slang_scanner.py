# [CSE 262] This file is a minimal skeleton for a Scheme scanner in Python.  It
# provides a transliteration of the TokenStream class, and the shell of a
# Scanner class.  Please see the README.md file for more discussion.

import re


#token class is used to represent all tokens
#some parts of the token might be null such as msgOrLiteral which is only used for select token types
#name is the type of token
#line is the line number
#column is the column number
#text is the text directly from the input
#msg or literal is used for tokens that have values
class token:
    def __init__(self, name, line, column, text, msgOrLiteral):
        self.name = name
        self.line = line
        self.column = column
        self.text = text
        self.msgOrLiteral = msgOrLiteral

#function for getting the line and column number of a string
#returns an array [line, column] for the index of the source
def getLineAndCol(source, index):
    if not source or len(source) <= index:
        return None
    lineAndCol = source[:index+1].splitlines(True)
    return [len(lineAndCol), len(lineAndCol[-1])]


class TokenStream:
    def __init__(self, tokens):
        self.__tokens = tokens
        self.__next = 0

    def reset(self): self.__next = 0

    def nextToken(self):
        return None if not self.hasNext() else self.__tokens[self.__next]

    def nextNextToken(self):
        return None if not self.hasNextNext() else self.__tokens[self.__next + 1]

    def popToken(self): self.__next += 1

    def hasNext(self): return self.__next < len(self.__tokens)

    def hasNextNext(self): return (self.__next + 1) < len(self.__tokens)


class Scanner:
    currIndex = 0#variable used to keep tract of where in the source the iterator is
    tokStream = []#a list of all tokens which are used to create the TokenStream

    def __init__(self):
        pass

    def scanTokens(self, source):
        while self.currIndex < len(source):#while the currIndex is still within the source
            if source[self.currIndex] == '\'':#ABBREV TOKEN
                self.abbrevToken(source)
                continue
            elif re.match("[!$%&*/:<=>?~_^a-zA-Z]+", source[self.currIndex]):#IDENTIFIERTOKEN
                self.inIdentifier(source)
                self.cleanBreakChecker(source)
                continue
            elif source[self.currIndex] == "#":#VEC, CHAR, OR BOOL TOKEN
                self.vecCharBoolToken(source)
                continue
            elif source[self.currIndex] == '(':#LEFT PAREN TOKEN
                self.lParenToken(source)
                continue
            elif source[self.currIndex] == ')':#RIGHT PAREN TOKEN
                self.rParenToken(source)
                continue
            elif source[self.currIndex] == "\"":#STRING TOKEN
                self.strToken(source)#cleanbreak checking done within strToken
                continue
            elif re.match("[+-]", source[self.currIndex]):#PM TOKEN (MIGHT BE INT OR DOUBLE)
                self.inPM(source)
                self.cleanBreakChecker(source)
                continue
            elif re.match("[0-9]", source[self.currIndex]):#INT TOKEN (MIGHT BE DOUBLE)
                self.inInteger(source, getLineAndCol(source, self.currIndex), self.currIndex, "")
                self.cleanBreakChecker(source)
                continue
            elif source[self.currIndex] == ";":#IN COMMENT TOKEN
                self.inComment(source)
                continue
            elif source[self.currIndex] == ".":#ERROR FOR BEGINING WITH ".""
                self.errorToken("Cannot start identifier or double with \".\"", getLineAndCol(source, self.currIndex))
                self.currIndex += 1
                continue
            elif source[self.currIndex] == " " or source[self.currIndex] == "\n" or source[self.currIndex] == "\t" or source[self.currIndex] == "\r":#WHITESPACE GETS SKIPPED
                self.currIndex += 1
                continue
            else:#ERROR IF UNKNOWN TOKEN
                self.errorToken("Unknown Behavior", getLineAndCol(source, self.currIndex))
                self.currIndex += 1
        self.eofToken(source)#ADDS EOF TOKEN
        return TokenStream(self.tokStream)
    
    #Adds a token for left parenthesis
    def lParenToken(self, source):
        lineAndCol = getLineAndCol(source, self.currIndex)
        self.tokStream.append(token("LParenToken", lineAndCol[0], lineAndCol[1], "(", ""))
        self.currIndex += 1

    #Adds a token for right parenthesis
    def rParenToken(self, source):
        lineAndCol = getLineAndCol(source, self.currIndex)
        self.tokStream.append(token("RParenToken", lineAndCol[0], lineAndCol[1], ")", ""))
        self.currIndex += 1

    #Adds a token for the Abbrev symbol
    def abbrevToken(self, source):
        lineAndCol = getLineAndCol(source, self.currIndex)
        self.tokStream.append(token("AbbrevToken", lineAndCol[0], lineAndCol[1], "'", ""))
        self.currIndex += 1
    
    #increments currIndex until the the next line, thus skipping over the remainder of the current line
    def inComment(self, source):
        startLine = getLineAndCol(source, self.currIndex)[0]
        while self.currIndex < len(source):
            if getLineAndCol(source, self.currIndex)[0] == startLine:
                self.currIndex += 1
            else:
                break
    
    #function for handling anything that begins with #
    def vecCharBoolToken(self, source):
        lineAndCol = getLineAndCol(source, self.currIndex)
        if self.currIndex + 2 <= len(source):#makes sure that there is something after the #
            if source[self.currIndex + 1] == "(":#if this is a vector token
                self.tokStream.append(token("VectorToken", lineAndCol[0], lineAndCol[1], "#(", ""))
                self.currIndex += 2
            elif source[self.currIndex + 1] == "t":#if this is a true boolean token
                self.tokStream.append(token("BoolToken", lineAndCol[0], lineAndCol[1], "#t", True))
                self.currIndex += 2
                self.cleanBreakChecker(source)
            elif source[self.currIndex + 1] == "f":#if this is a false boolean token
                self.tokStream.append(token("BoolToken", lineAndCol[0], lineAndCol[1], "#f", False))
                self.currIndex += 2
                self.cleanBreakChecker(source)
            #the following three elif blocks handle newline, space, and tab
            #they first check the length is valid and then see if the token matches value for newline, space, or tab
            elif self.currIndex + 9 <= len(source) and source[self.currIndex + 1: self.currIndex + 9] == "\\newline":#checks for newline
                self.tokStream.append(token("CharToken", lineAndCol[0], lineAndCol[1], source[self.currIndex + 1: self.currIndex + 9], "\n"))
                self.currIndex += 9
                self.cleanBreakChecker(source)
            elif self.currIndex + 7 <= len(source) and source[self.currIndex + 1: self.currIndex + 7] == "\\space":#checks for space
                self.tokStream.append(token("CharToken", lineAndCol[0], lineAndCol[1], source[self.currIndex + 1: self.currIndex + 7], " "))
                self.currIndex += 7
                self.cleanBreakChecker(source)
            elif self.currIndex + 5 <= len(source) and source[self.currIndex + 1: self.currIndex + 5] == "\\tab":#checks for tab
                self.tokStream.append(token("CharToken", lineAndCol[0], lineAndCol[1], source[self.currIndex + 1: self.currIndex + 5], "\t"))
                self.currIndex += 5
                self.cleanBreakChecker(source)
            #the following block and nested blocks check for all other chars
            elif self.currIndex + 2 < len(source) and source[self.currIndex + 1] == "\\":#all ohter chars must have a \ after the #
                if self.currIndex + 4 < len(source) and re.match("\\t|\\n|\\r|\\0",source[self.currIndex + 2: self.currIndex + 4]):#checks if there is a \t, \n, \r, or \0 after the #\
                    self.errorToken("Improper use of escape characters", lineAndCol)
                    self.currIndex += 4
                elif self.currIndex + 2 < len(source) and (source[self.currIndex + 2] == " " or source[self.currIndex + 2] == "\n" or source[self.currIndex + 2] == "\t" or source[self.currIndex + 2] == "\r"):#checks for whitespace
                    self.errorToken("Improper use of whitespace characters", lineAndCol)
                    self.currIndex += 3
                else:#proper chars
                    self.tokStream.append(token("CharToken", lineAndCol[0], lineAndCol[1], source[self.currIndex: self.currIndex + 3], source[self.currIndex + 2]))
                    self.currIndex += 3
                    self.cleanBreakChecker(source)
            else:#error for not having a valid character after the #\
                self.errorToken("Must follow #\\ with valid character(s)", lineAndCol)
                self.currIndex += 2
        else:#error for having a trailing # at the end of input
            self.errorToken("Improper use of #", lineAndCol)

    #method for dealing with all string tokens
    def strToken(self, source):
        stringEnded = False#checks to see if the string ended with a " before the eof
        startIndex = self.currIndex
        literal = ""
        lineAndCol = getLineAndCol(source, self.currIndex)
        while not stringEnded and self.currIndex + 1 < len(source):#continuously scans chars of input until either eof or closing quotes
            self.currIndex += 1
            if source[self.currIndex] == "\\":#if block handles escape sequences
                self.currIndex += 1
                match source[self.currIndex]:#Python has Switch statements as of 3.10 which are used here for the escape characters
                    case "\"":
                        literal += "\""
                    case "\\":
                        literal += "\\"
                    case "n":
                        literal += "\n"
                    case "r":
                        literal += "\r"
                    case "t":
                        literal += "\t"
                    case _:
                        self.errorToken("Improper use of escape character sequence", lineAndCol)#only \", \\, \n, \r, and \t are valid escape sequences. All others are invalid
            elif source[self.currIndex] == "\"":#sets Stringended to indicate that a closing quote has been found 
                stringEnded = True
            else:#adds the character to the literal
                literal += source[self.currIndex]
        if stringEnded:#if stringended was found, append the string to the tokStream
            tokenText = source[startIndex + 1: self.currIndex]
            self.tokStream.append(token("StrToken", lineAndCol[0], lineAndCol[1], tokenText, literal))
            self.currIndex += 1
            self.cleanBreakChecker(source)
        else:#if eof reached before closing quotes, add an error token
            self.errorToken("Missing end quotes for String", lineAndCol)
            self.currIndex += 1

    #method for handling all identifiers
    def inIdentifier(self, source):
        idSaver = ""#string for storing the name of the identifier
        lineAndCol = getLineAndCol(source, self.currIndex)
        while self.currIndex < len(source):#loop through chars of source until eof or break condition met
            if re.match("[!$%&*/:<=>?~_^0-9a-zA-Z.+-]+", source[self.currIndex]):#valid identifier character
                idSaver += source[self.currIndex]#adds the next part of the identifier to the string
                self.currIndex += 1
            else:#if not a valid identifier character, break the loop. Cleanbreak is checked seprately 
                break
        if re.match("and|begin|cond|define|if|lambda|or|quote|set!", idSaver.lower()):#if regex matches a keyword to the input, go to keyword function
            self.inKeyword(idSaver, lineAndCol)
        else:#if not a keyword, add the identifier to the tokStream
            self.tokStream.append(token("IdentifierToken", lineAndCol[0], lineAndCol[1], idSaver, idSaver))

    #method for handling keywords
    def inKeyword(self, idSaver, lineAndCol):
        keywordName = re.sub("[!]", '', idSaver.capitalize()) + "Token"#removes ! symbols if present, capitalizes only the first letter, and then appends "Token". ex: sEt! -> SetToken
        self.tokStream.append(token(keywordName, lineAndCol[0], lineAndCol[1], idSaver, ""))

    #method for handling plus or minus symbol
    def inPM(self, source):
        lineAndCol = getLineAndCol(source, self.currIndex)
        startIndex = self.currIndex
        if self.currIndex + 1 < len(source):#if the PM isn't the last character
            if re.match("[0-9]", source[self.currIndex + 1]):#if there is a number, then this is an int/float
                intLiteral = source[self.currIndex]
                self.currIndex += 1
                self.inInteger(source, lineAndCol, startIndex, intLiteral)
                return         
            elif re.match("[^\s)]", source[self.currIndex + 1]):#if a character that isn't space follows, throw error token
                self.errorToken("Cannot follow leading + or - with non-numeric character", lineAndCol)
                self.currIndex += 1
                return
        #if PM is last character before eof or if there is a whitespace afterwards, adds the PM as an identifier
        self.tokStream.append(token("IdentifierToken", lineAndCol[0], lineAndCol[1], source[startIndex], ""))
        self.currIndex += 1
        print(self.currIndex)

    #method for handling integers
    def inInteger(self, source, lineAndCol, startIndex, intLiteral):
        isDouble = False#flag for if a decimal point has been found
        while self.currIndex < len(source):
            if re.match("[0-9]", source[self.currIndex]):#if a number has been found, add it to the literal and continue iterating
                intLiteral += source[self.currIndex]
                self.currIndex += 1
            elif source[self.currIndex] == ".":#if decimal found, set the flag to true and stop iterating
                isDouble = True
                break
            else:#if the character is not a digit or a decimal, will stop iterating. The cleanbreak status gets checked above
                break
        if isDouble:#if the double flag was set to true, goes to the double method
            self.inDouble(source, lineAndCol, startIndex, intLiteral)
        else:#if the double flag was not set, adds the number as an integer token
            tokenText = source[startIndex: self.currIndex]
            self.tokStream.append(token("IntToken", lineAndCol[0], lineAndCol[1], tokenText, int(intLiteral)))

    #method for handling floating point numbers    
    def inDouble(self, source, lineAndCol, startIndex, intLiteral):
        intLiteral += source[self.currIndex]
        self.currIndex += 1
        validDouble = False#flag will be set to true if there is at least one digit following the decimal point
        while self.currIndex < len(source):
            if re.match("[0-9]", source[self.currIndex]):#if a digit is found, adds it to the literal and continues iterating
                intLiteral += source[self.currIndex]
                self.currIndex += 1
                validDouble = True
            else:#if the character is not a digit, will break and then check if cleanbreak above
                break
        if validDouble:#if the flag was set to True, adds the token as a double
            tokenText = source[startIndex: self.currIndex]
            self.tokStream.append(token("DblToken", lineAndCol[0], lineAndCol[1], tokenText, float(intLiteral)))
        else:#if there were no digits following the decimal point, will create an error token
            self.errorToken("Must have trailing zeroes or numbers after decimal point", lineAndCol)

    #method for creating error tokens
    def errorToken(self, message, lineAndCol):
        self.tokStream.append(token("ErrorToken", lineAndCol[0], lineAndCol[1], "", message))
    
    #method for throwing the eof token
    def eofToken(self, source):
        lineAndCol = getLineAndCol(source, self.currIndex)
        self.tokStream.append(token("EofToken", None, None, "", ""))
    
    #method that checks there is a valid whitespace character or parenthesis token to ensure that there is a valid cleanbreak
    def cleanBreakChecker(self, source):
        if (self.currIndex < len(source)) and (not (re.match(" |\t|\n|\r|\0'", source[self.currIndex]))) and (not ((source[self.currIndex] == "(") or (source[self.currIndex] == ")"))):
            self.errorToken("All valid tokens must be proceeded by either a space, newline, tab, carriage return, EOF, ), (, or ;", getLineAndCol(source, self.currIndex))

#method for turning the token into valid XML
def tokenToXml(token: token):
    if token.name == "EofToken":
        return "<EofToken />"
    elif token.msgOrLiteral == "":#does not print a val= if there is no value such as with a parenToken
        return "<" + token.name + " line=" + str(token.line) + " col=" + str(token.column) + " />"
    else:#will print a value if there is a val such as with strings or integers
        return "<" + token.name + " line=" + str(token.line) + " col=" + str(token.column) + " val='" + escape(str(token.msgOrLiteral)) + "' />"

#method for properly printing escape characters for values of messages or literals
def escape(s) -> str:
    return s.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("'", "\\'")
    
