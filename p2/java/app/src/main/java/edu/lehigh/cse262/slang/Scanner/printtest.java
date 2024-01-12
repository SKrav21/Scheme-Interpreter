package edu.lehigh.cse262.slang.Scanner;

public class printtest {
    public static void main(String[] args){
        String source = "(#\\0)";
        int currIndex = 1;
        String sourceSub = source.substring(currIndex+1, currIndex+3);
        System.out.println(sourceSub);
        System.out.println(sourceSub.matches("\\\\n|\\\\t|\\\\r|\\\\0"));

    }
}
