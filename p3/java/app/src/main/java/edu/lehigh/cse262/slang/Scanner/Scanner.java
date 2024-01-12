package edu.lehigh.cse262.slang.Scanner;

import java.util.ArrayList;

//import edu.lehigh.cse262.slang.Slang;
/**
 * Scanner is responsible for taking a string that is the source code of a
 * program, and transforming it into a stream of tokens.
 *
 * [CSE 262] It is tempting to think "if my code doesn't crash when I give it
 * good input, then I have done a good job". However, a good scanner needs to be
 * able to handle incorrect programs. The bare minimum is that the scanner
 * should not crash if the input is invalid. Even better is if the scanner can
 * print a useful diagnostic message about the point in the source code that was
 * incorrect. Best, of course, is if the scanner can somehow "recover" and keep
 * on scanning, so that it can report additional syntax errors.
 *
 * [CSE 262] **In this class, "even better" is good enough for full credit**
 *
 * [CSE 262] With that said, if you make a scanner that can report multiple
 * errors, I'll give you extra credit. But you'll have to let me know that
 * you're doing this... I won't check for it on my own.
 *
 * [CSE 262] This is the only java file that you need to edit in p1. The
 * reference solution has ~240 lines of code (plus 43 blank lines and ~210 lines
 * of comments). Your code may be longer or shorter... the line-of-code count is
 * just a reference.
 *
 * [CSE 262] You are allowed to add private methods and fields to this class.
 * You may also add imports.
 */
public class Scanner {
    private static int currIndex;
    private static ArrayList<Tokens.BaseToken> tokens;

    /** Construct a scanner */
    public Scanner() {

    }

    /**
     * scanTokens works through the `source` and transforms it into a list of
     * tokens. It adds an EOF token at the end, unless there is an error.
     *
     * @param source The source code of the program, as one big string, or a
     *               line of code from the REPL.
     *
     * @return A list of tokens
     */
    public TokenStream scanTokens(String source) {
        currIndex = 0; //variable to track the current index of the source string
        tokens = new ArrayList<Tokens.BaseToken>();
        while(currIndex < source.length()){ //while loop until the current index reaches the end of the source
            /*
             * the first couple of if and else if statements in this ladder pertain to the states allowed directly after START
             * all other states not directly reachable after start or after cleanbreak are reached within the methods called for each post-start state type
             */
            if(source.charAt(currIndex) == '\''){ //if there is an abbreviation char: '
                inAbbrev(source); //method to handle abbreviaton
                continue; //break out of this iteration of the loop (token consumed)
            }
            else if((String.valueOf((source.charAt(currIndex))).matches("[[!$%&*/:<=>?~_^][a-z][A-Z]]+"))){ //IDENTIFIER TOKEN(s)
                inIdentifier(source); //method to handle identifiers 
                cleanBreakChecker(source); //see method body for further explanation, basically just checks to make sure we are properly in a clean break
                continue;
            }
            else if(source.charAt(currIndex) == '#'){
                vecCharBoolToken(source); //method to handle the vec/char/bool states
                continue;
            }
            else if(source.charAt(currIndex) == '\"'){
                strToken(source); //method to handle string state
                continue;
            }
            else if(((String.valueOf((source.charAt(currIndex))).matches("[+-]")))){ //"LEADING" + OR -
                inPM(source); //method to handle leading + or -
                cleanBreakChecker(source);
                continue;
            }
            else if((String.valueOf((source.charAt(currIndex))).matches("[0-9]"))){ //INTEGER TOKENS, CAN LEAD TO DBLTOKENS
                int[] lineAndCol = getLineAndCol(source, currIndex); //building a small array to hold the current line and column numbers, will explain method getLineAndCol at its body
                StringBuilder intLiteral = new StringBuilder(); //creating a string builder to build/hold the integer we consume
                inInteger(intLiteral, source, lineAndCol, currIndex); //passing our builder, our source, our line and column array, and our current index
                cleanBreakChecker(source);
                continue;
                
            }
            //the following parts of the else-if ladder pertain to those tokens that are permitted to immediately proceed a cleanbreak
            else if(source.charAt(currIndex) == '('){//LEFT PAREN TOKEN
                lParenToken(source);
                continue;
            }
            else if(source.charAt(currIndex) == ')'){//RIGHT PAREN TOKEN
                rParenToken(source);
                continue;
            }
            else if(source.charAt(currIndex) == ';'){//COMMENT
                inComment(source);
                continue;
            }
            else if(source.charAt(currIndex) == '.'){ //checking for the case where there is a leading point, or really just a point anywhere it's not supposed to be. 
                errorToken("Cannot start identifier or double with \".\"",getLineAndCol(source, currIndex)); //error if we consume a point without leading 0s or a point trying to lead an identifier
                currIndex++;
                continue;
            }
            //This method checks for all whitespace in the source NOT contained in strings and completely ignores it, as is the behavior of a scanner
            else if((source.charAt(currIndex) == ' ') || (source.charAt(currIndex) == '\n')||(source.charAt(currIndex) == '\t')||(source.charAt(currIndex) == '\r')){ 
                currIndex++;
                continue;
            }
            else{
                //checking basically only for invalid identifiers at this point in the flow
                errorToken("Invalid input: "+source.charAt(currIndex), getLineAndCol(source, currIndex));
                currIndex++;
                continue;
            }
            
        }
        int[] lineAndCol = getLineAndCol(source, currIndex - 1); //when we break from the loop, meaning we have reached EOF, calculate our current line and column
        lineAndCol[1] += 1; //incrementing the column number by 1
        eofToken(lineAndCol); //we have finished the scan, we can pass the eofToken(see method body below)
        return new TokenStream(tokens); //returning our token stream
    }

    private static void lParenToken(String source){ //method for consuming left parens
        int[] lineAndCol = getLineAndCol(source, currIndex); //calculating our current line number and column number 
        var lParen = new Tokens.LeftParen("(", lineAndCol[0], lineAndCol[1]); //creating a lparen token to add to the token stream
        tokens.add(lParen);
        currIndex++; //incrementing the index here rather than in the main while loop so that we are sure this is only done when the token is consumed without error
    }

    private static void rParenToken(String source){ //same logic as left paren for right paren
        int[] lineAndCol = getLineAndCol(source, currIndex);
        var rParen = new Tokens.RightParen(")", lineAndCol[0], lineAndCol[1]);
        tokens.add(rParen);
        currIndex++;
    }

    private static void inAbbrev(String source){ //method for consuming abbreviation tokens
        int[] lineAndCol = getLineAndCol(source, currIndex); 
        var abbrev = new Tokens.Abbrev("'", lineAndCol[0], lineAndCol[1]); //passing our token text, line and column nums 
        tokens.add(abbrev);
        currIndex++; //same logic for incrementing currIndex here as before, logic will be persisted throughout rest of program
    }
    //method that iterates through comments until we encounter a newline. Logic for encountering EOF is innate in the while loop condition
    private static void inComment(String source){
        int[] lineAndCol = getLineAndCol(source, currIndex);
        int startLine = lineAndCol[0];
        while(currIndex < source.length()){
            if(getLineAndCol(source, currIndex)[0] == startLine){ //if the line we started on is the same as the line we calculate on each iteration of the loop 
                currIndex++;
            }
            else{
                break; //newline encountered, subsequently ignored in main method body
            }
        }
    }

    //a rather large method for consuming vector tokens, character tokens, and booleans. Basically, it deals with anything that can proceed '#'
    private static void vecCharBoolToken(String source){
        
        //this guarantees that there is actually, at a minimum, some potentially correct characters after the #.  
        //(invalid input can enter here, but this is checked against within the subsequent logic)
        //there is an error if this if statement is not entered. 
        if (currIndex + 2 <=source.length()){ 
            int[] lineAndCol = getLineAndCol(source, currIndex); //calculating our current line and column once again
            if(source.charAt(currIndex+1) == '('){ //checking if the character following the # is a (, meaning we have a vector token
                var vectorStart = new Tokens.Vec("#(", lineAndCol[0], lineAndCol[1]);
                tokens.add(vectorStart);
                currIndex +=2; //doing this here to set current index to the character following the vector 
            }
            else if(source.charAt(currIndex+1) == 't'){ //character after # is boolean true
                var boolVal = new Tokens.Bool("#t", lineAndCol[0], lineAndCol[1], true);
                tokens.add(boolVal);
                currIndex+=2; //to get to the index following the t 
                cleanBreakChecker(source); //checking to make sure we are properly cleanbroken
            }
            else if(source.charAt(currIndex+1) == 'f'){ //boolean false
                var boolVal = new Tokens.Bool("#f", lineAndCol[0], lineAndCol[1], false);
                tokens.add(boolVal);
                currIndex+=2; //to get to the index following the f
                cleanBreakChecker(source); //checking to make sure we are properly cleanbroken
            }
            //this else first checks if currIndex+9 (from the # to after the potential "\newline") even exists
            //if it does not, we short circuit and avoid an index out of bounds error
            //if it exists, we check the substring from the character following the # to 9 after the # to see if we have "\newline"
            else if((currIndex+9<=source.length()) && ((source.substring(currIndex+1,currIndex+9)).equals("\\newline"))){ 
                var newlineVal = new Tokens.Char(source.substring(currIndex,currIndex+9), lineAndCol[0], lineAndCol[1], '\n' );
                tokens.add(newlineVal);
                currIndex = currIndex + 9; //setting the index to the character after "\newline"
                cleanBreakChecker(source); //checking if we are cleanbroken
                
            }
            //same logic as above for space
            else if((currIndex+7<=source.length()) &&((source.substring(currIndex+1,currIndex+7)).equals("\\space"))){
                var spaceVal = new Tokens.Char(source.substring(currIndex,currIndex+7),lineAndCol[0], lineAndCol[1], ' ');
                tokens.add(spaceVal);
                currIndex = currIndex+7;
                cleanBreakChecker(source);
            }
            //same logic as above for tab
            else if((currIndex+5<=source.length()) &&((source.substring(currIndex+1,currIndex+5)).equals("\\tab"))){
                var tabVal = new Tokens.Char(source.substring(currIndex,currIndex+5), lineAndCol[0], lineAndCol[1], '\t');
                tokens.add(tabVal);
                currIndex = currIndex+5;
                cleanBreakChecker(source);
            }
            else if(currIndex+2<source.length() && source.charAt(currIndex+1) == '\\'){ //if the character following the # is a \
                //here we make sure that the LITERALS "\t, \r, \n, or \0" do not follow #\ (ex: #\\t AS A VISIBLE LITERAL is not valid.)                 
                if(currIndex+4 <= source.length() && (source.substring(currIndex+2,currIndex+4).matches("\\\\t|\\\\r|\\\\n|\\\\0"))){
                    errorToken("Error: improper use of escape characters", getLineAndCol(source, currIndex));
                    currIndex+=4; //setting currIndex to after the potential literal escapes
                }
                 
                else if(currIndex+2 <=source.length() && (source.charAt(currIndex+2)) ==' '){ //checking for '#\ '
                    errorToken("Error: improper use of whitespace character", getLineAndCol(source, currIndex));
                    currIndex+=3;
                }
        
                else if((String.valueOf(source.charAt(currIndex+2))).matches("[\\s]")){ //checking for all other whitespace escape characters (\\s in regex)
                    errorToken("Error: no whitespace characters allowed after #\\",getLineAndCol(source, currIndex));
                    currIndex+=3;
                }
                else{ //if we reach here, we have found a valid character token. 
                    var charVal = new Tokens.Char(source.substring(currIndex,currIndex+3), lineAndCol[0], lineAndCol[1], source.charAt(currIndex+2));
                    tokens.add(charVal);
                    currIndex+=3;
                    cleanBreakChecker(source);
                }
            }
            else{ //if a character besides / or ( follows a # 
                errorToken("Must follow #\\ with valid character(s)",getLineAndCol(source, currIndex));
                currIndex+=2;
            }
        }
        else{ //this is only reached if we encounter a lone #
            errorToken("Error: improper use of #", getLineAndCol(source, currIndex));
            currIndex++;
        } 
    }
    
    //method for consuming string tokens
    private static void strToken(String source){
        int startIndex = currIndex; //setting a new start index to our current index to keep track of the beginning of the string
        boolean stringEnded = false; //boolean to track if the string has been closed
        StringBuilder literal = new StringBuilder(); //using a stringbuilder to append each member of the string to the entire string 
        while(!stringEnded && currIndex + 1 < source.length()){ //while the string is still being consumed and we are not at/past the end of source
            currIndex++; //moving past each subsequent character after it has been checked in the previous iteration (first iteration just skips ")
            if(source.charAt(currIndex) == '\\'){
                currIndex++; //if we encounter a backslash, we check which escape character follows using a switch statement
                switch (source.charAt(currIndex)){
                    case '\"':
                        literal.append('\"'); //if we find a quote after the backslash, we append a quote to our literal
                        break;
                    case '\\':
                        literal.append('\\'); //append a \ to our literal, etc. 
                        break;
                    case 'n':
                        literal.append('\n'); 
                        break;
                    case 'r':
                        literal.append('\r');
                        break;
                    case 't':
                        literal.append('\t');
                        break;
                    default:
                        errorToken("Improper use of escape character sequence", getLineAndCol(source, currIndex)); //reached if the character following a \ is NOT a valid escape char
                }
            }
            else if(source.charAt(currIndex) == '\"'){ //if we encounter the quote that closes the string 
                stringEnded = true;
            }
            else{ //if we reach here, we can freely add whatever we have found at this index to the string
                literal.append(source.charAt(currIndex));
            }
        }
        if(stringEnded){ //if there was a double quote prior to the end of source
            int[] lineAndCol = getLineAndCol(source, startIndex); //calculate our current line and column 
            String tokenText = source.substring(startIndex + 1, currIndex); //generating our token text
            var str = new Tokens.Str(tokenText, lineAndCol[0], lineAndCol[1], literal.toString());
            tokens.add(str);
            currIndex++;
            cleanBreakChecker(source); //checking if we are clean broken
        }
        else{ //if we reach here, "stringEnded" was false, meaning the end of source was reached because the string was never closed, therefore stringEnded was never set to true
            errorToken("Missing end quotes for String", getLineAndCol(source, startIndex));
            currIndex++;
        }
    }

    private static void inIdentifier(String source){ 
        String idSaver = ""; //a string for collecting the identifier tokens 
        int[] lineAndCol = getLineAndCol(source, currIndex); //calculating our line and column 
        while (currIndex < source.length()){
            if((String.valueOf(source.charAt(currIndex))).matches("[[!$%&*/:<=>?~_^][0-9][a-z][A-Z][.+-]]+")){ //big regex for the self loop on INID
                idSaver += source.charAt(currIndex); //with keywords, we actually have to save subsequent identifier tokens until we break from this loop so that we can check if there are keywords
                currIndex++; 
            }
            else{ //we find a character that is not permitted in identifiers, we break out of the while loop 
                break;
            }
        }   
        if(!(idSaver.toLowerCase().matches("and|begin|cond|define|if|lambda|or|quote|set!"))){  //if the string of identifiers we found is NOT a keyword
            var id = new Tokens.Identifier(idSaver, lineAndCol[0], lineAndCol[1]); //consume the identifier
            tokens.add(id);
        }
        else
            inKeyword(idSaver, lineAndCol); //if we get here, it means we have found a keyword  
    }
    
    private static void inKeyword(String idSaver, int[] lineAndCol){ //giant if else ladder that basically figures out which keyword we have, properly adds as a token.
        if(idSaver.equalsIgnoreCase("and")){
            var and = new Tokens.And("and", lineAndCol[0], lineAndCol[1]);
            tokens.add(and);
        }
        else if(idSaver.equalsIgnoreCase("begin")){
            var begin = new Tokens.Begin("begin", lineAndCol[0], lineAndCol[1]);
            tokens.add(begin);
        }
        else if(idSaver.equalsIgnoreCase("cond")){
            var cond = new Tokens.Cond("cond", lineAndCol[0], lineAndCol[1]);
            tokens.add(cond);
        }
        else if(idSaver.equalsIgnoreCase("define")){
            var define = new Tokens.Define("define", lineAndCol[0], lineAndCol[1]);
            tokens.add(define);
        }
        else if(idSaver.equalsIgnoreCase("if")){
            var ifTok = new Tokens.If("if", lineAndCol[0], lineAndCol[1]);
            tokens.add(ifTok);
        }
        else if(idSaver.equalsIgnoreCase("lambda")){
            var lambda = new Tokens.Lambda("lambda", lineAndCol[0], lineAndCol[1]);
            tokens.add(lambda);
        }
        else if(idSaver.equalsIgnoreCase("or")){
            var or = new Tokens.Or("Or", lineAndCol[0], lineAndCol[1]);
            tokens.add(or);
        }
        else if(idSaver.equalsIgnoreCase("quote")){
            var quote = new Tokens.Quote("Quote", lineAndCol[0], lineAndCol[1]);
            tokens.add(quote);
        }
        else if(idSaver.equalsIgnoreCase("set!")){
            var set = new Tokens.Set("set!", lineAndCol[0], lineAndCol[1]);
            tokens.add(set);
        }
    }

    //we enter this method if we encounter a leading + or - that is not inside of an identifier, meaning we either have a standalone + or -, or a + or - number
    private static void inPM(String source){ 
        int[] lineAndCol = getLineAndCol(source, currIndex);
        int startIndex = currIndex; //set a starting index
        if(currIndex + 1 < source.length()){ //while we are before the end of source
            if(String.valueOf(source.charAt(currIndex + 1)).matches("[0-9]")){ //if we have a valid int
                StringBuilder intLiteral = new StringBuilder(); //creating a string builder to hold our integer, if we have one
                intLiteral.append(source.charAt(currIndex)); //append the + or - sign to the integer
                currIndex++; 
                inInteger(intLiteral, source, lineAndCol, startIndex); //enter the inInteger state with our current literal (just a + or - ATP), source, line/columnn num, and starting index
                return;
            }
            else if(String.valueOf(source.charAt(currIndex + 1)).matches("[^\\s)]")){ //if we are not in a proper cleanbreak from PM->IDENTIFIER 
                errorToken("Cannot follow leading + or - with non-numeric character", lineAndCol);
                currIndex++;
                return;
            }
        }
        var pm = new Tokens.Identifier((String.valueOf(source.charAt(currIndex))), lineAndCol[0], lineAndCol[1]); //we reach here if + or - is to be consumed as a standalone identifier
        tokens.add(pm);
        currIndex++;
    }

    //method that consumes integers, can be reached from start or from PM state
    private static void inInteger(StringBuilder intLiteral, String source, int[] lineAndCol, int startIndex){
        boolean isDouble = false; //a boolean that checks if the integer has "become" a double through the consummation of a . 
        while(currIndex < source.length()){ //while before the end of our input
            //add the int to the builder
            if((String.valueOf(source.charAt(currIndex))).matches("[0-9]")){//if the character is 0-9
                intLiteral.append(source.charAt(currIndex));
                currIndex++;
            }
            else if(source.charAt(currIndex) == '.'){ //will be reached when the next token is "." meaning were in a double. 
                isDouble = true; //setting this boolean, basically saying we are finished with the rest of the content in this function
                break;
            }
            else{ //if we are still in an int
                break;
            }
        }
        
        if(isDouble == false){ //this means we only read an int, there was no "."
            String tokenText = source.substring(startIndex, currIndex); //getting our tokenText, could also be done by calling .toString() on the literal
            var number = new Tokens.Int(tokenText, lineAndCol[0], lineAndCol[1],Integer.parseInt(intLiteral.toString())); //getting our int to add to tokens. last param could also be done by calling parseInt() on tokenText
            tokens.add(number);
        } 
        else{
            doubleToken(intLiteral, source, lineAndCol, startIndex); //calling the function doubleToken, passing our source and our current string that we build to it. 
        }
    }

    private static void doubleToken(StringBuilder intLiteral, String source, int[] lineAndCol, int startIndex){ // we are now in a double. 
        intLiteral.append(String.valueOf(source.charAt(currIndex))); //appending the "." to the literal that we passed in from inInteger
        currIndex++; //looking @ next char
        boolean validDouble = false; //makes sure there is a some number after the decimal
        while(currIndex < source.length()){ //same logic as inInt, basically the same function, could have even kept this logic in one function, but decided to make a separate double function to keep with the nature of the finite state machine. 
            if((String.valueOf(source.charAt(currIndex))).matches("[0-9]")){
                intLiteral.append(source.charAt(currIndex));
                validDouble = true; //this means that there are integers after the decimal point
                currIndex++;
            }
            else{//we are still in a double
                break;
            }
        }
        if(validDouble){//we are good to go to add the double as a token
            String tokenText = source.substring(startIndex, currIndex);
            var dbl = new Tokens.Dbl(tokenText, lineAndCol[0], lineAndCol[1], Double.parseDouble(intLiteral.toString())); //same logic as before, but calling .parseDouble() on the literal (didnt know this was a thing!)
            tokens.add(dbl);
        }
        else    //if there are no values following the "."
            errorToken("Must have trailing zeroes or numbers after decimal point",getLineAndCol(source, startIndex));
    }

    private static void errorToken(String message, int[] lineAndCol){ //method that helps consume error tokens, takes our custom message and the line/column number
        var error = new Tokens.Error(message, lineAndCol[0], lineAndCol[1]);
        tokens.add(error);
    }
    
    private static void eofToken(int[] lineAndCol){ //method for consuming the EOF token
        var eof = new Tokens.Eof("", lineAndCol[0], lineAndCol[1]);//EOF TOKEN
        tokens.add(eof);
    }
    
    private static int[] getLineAndCol(String source, int index){ //method that calculates the column and line number at a given time
        if (source == null || index >= source.length()){ //if there is nothing in the source or the index is greater than or = to the length of the source
            return null;
        }
        String[] temp = (source.substring(0, index + 1)).split("\\n", -1); //splitting the entire source up to this point by newlines to basically count how many lines we have, each line is inserted into an index in temp[]
        int[] lineAndCol = {temp.length, temp[temp.length-1].length()}; //temp.length is how many lines we have, the second parameter calculates column 
        return lineAndCol;                                             //by taking the length of the last element of temp (the current line), as this is the column we are currently at. 

    }
     //a function that ensures clean break conditions are met and that we are all set to return to start after consuming a token. This only happens when currIndex is less than source length,
     //as the case where we are in REPL mode and currIndex == source length would cause an out of bounds error. 
     //this is possible becauser EOF is not truly a "character", and is handled elsewhere in the code. 
    private static void cleanBreakChecker(String source){
        if(currIndex<source.length() && !((String.valueOf(source.charAt(currIndex))).matches(" |\t|\n|\r|\0'")) && !(source.charAt(currIndex) == '(' || source.charAt(currIndex) == ')')){ 
            errorToken("All valid tokens must be proceeded by either a space, newline, tab, carriage return, EOF, ), (, or ;",getLineAndCol(source,currIndex));
        }
    }

}