# CSE 262 Assignment 1

The purpose of this assignment is to ensure that you are familiar with the three
programming languages that we will use in this class: Java, Python, and Scheme.
Among the goals of this assignment are:

* To make sure you have a proper development environment for using these
  languages
* To introduce you to these languages, if you haven't used them before
* To introduce you to some features of these languages that you may not have
  seen before
* To get you thinking about how to program idiomatically

## Parts of the Assignment

This assignment has *four* parts, which are contained in three sub-folders:
`java`, `python`, and `scheme`.  Three tasks are similar: in Java, Python, and
Scheme, you will implement five "programs":

* `read_list` -- Read a list of values from stdin and put them in a list
* `reverse` -- Reverse a list, without using any built-in list functions
* `map` -- Apply a function to all elements of a list, without using any
  built-in map functions
* `tree` -- Implement a binary tree
* `prime_divisors`-- Factor an integer into its prime divisors

The README file in each sub-folder has some more information about programming
in each of these languages.

The *fourth* part of the assignment is to answer the questions at the end of
this file.

## Development Environments

I strongly encourage you to use Visual Studio Code as your development
environment.  It has good plug in support for Java and Python, and reasonable
support for Scheme.  This support is not just syntax highlighting, but also code
formatting, refactoring, code completion, and tooltips.  It will help you to
write better code in less time.

VSCode also has two very important features for this assignment: VSCode Remote
and Live Share.  If you do not want to install Java, Python, and Scheme on your
computer, you can use the sunlab, and with VSCode Remote, you can use VSCode to
connect to sunlab.  It's very nice.  If you choose to work in a team of two,
Live Share will make it much easier to pair program.

## Teaming

You may work in teams of two for this assignment.  If you choose to work in a
team, you should **pair program**.  You should not split the assignment.  You
will not be able to succeed in this class if you do not understand everything in
this assignment.  Furthermore, if you split the work, you and your teammate will
wind up having to solve the same hard problems, which means you'll do 100% of
the work for each step.  In contrast, if you pair program, things you figure out
in Java won't need to be re-learned in Python, so you'll do only about 50% of
the work for Python... that savings adds up!

If you wish to work in a team, you must email Prof Spear <spear@lehigh.edu>.
Your email must follow these rules:

1. You must cc your project partner, so that I know that both team members
are aware of the team request.
2. You must tell me which team member's repository you will be working in.

I will change the permissions on that repository, so that both students can
read and write to it.  You will not need to submit the assignment twice.

## Documentation

You are **required** to follow the documentation instructions
that accompany each part of the assignment.  Correct code that does not have
documentation will not receive full points.

**DO NOT FORGET THE QUESTIONS AT THE END OF THIS FILE**

## Deadlines

This assignment is due by 11:59 PM on Friday, September 9th.  You should have
received this assignment by using `git` to `clone` a repository onto the machine
where you like to work.  You can use `git add`, `git commit`, and `git push` to
submit your work.

You are strongly encouraged to proceed *incrementally*: as you finish parts of
the assignment, `commit` and `push` them.

## Start Early

You should not wait until the last minute to start this assignment.  Start
early, and stop often.  This strategy will maximize your learning and minimize
your stress.  I promise.

## Questions

Please be sure to answer all of the following questions by writing responses in
this document.

### **Read List**

* **Did you run into any trouble using `let`?  Why?**
  
      Yes, specifically in trying to use let in tree.scm. I could not figure out how to equivalently implement lets instead of defines for the various functions to be performed on the tree. I think this is because lets do not really get names that can be called, unlike defines, which could be easily called and passed arguments from the closure at the top of the program. I tried for several hours to implement lets into my code, with not much success. Hopefully, later on in the semester, I will develop a deeper understanding of scheme that makes this kind of coding easy. 

* **What happens if the user enters several values on one line?**

      This is not an issue. The list that is being inputted and subsequently returned accepts entire lines as  its elements, so this would result in an entire line, regardless of how many values that are inputted on each line, being stored as an element in the returned list. In other words, the delimeter for elements in the list is a newline character, inputted by the user  with the enter key. While this is not actually the delimiter, as if the lines were being split into tokens, I like to think of it as one because of how read list splits the input by line. 
      
* **What happens if the user enters non-integer values?**

      The readlist function works for all types in Java Python, and Scheme. This is possible in java due to the generic type used for the list,  and the fact that python and scheme are weakly-typed language. The same behavior occurs, where each line is inputted as an element to the list, regardless of type. This kind of implementation is a bit tricker in java, because the generic type has to be implemented. 
    
* **Contrast your experience solving this problem in Java, Python, and Scheme.**

      Readlist in java was relatively easy, as it made good use of the Scanner class and its associated methods. This made checking for EOF much  simpler. Implementing this method with generic types was slightly challenging, but it became possible using well-placed typecasting.  In python, this was a rather simple implementation as well, as python is dynamically typed, therefore this code had to worry much less about typing than the java version. The scheme implementation was a bit more difficult, as it required the use of some lambdas and let statements, as well as trying to figure out how to use append to concatenate the head of each recursive list and the head of that list. However, casting  the head as its own list made this possible. 

### Reverse

* **What is tail recursion?**

      tail recursion is implemented in recursive functions where the recursive call is the last one made in the function. This avoids having to create a new stack frame, instead calling the function in the current stack frame and returning its result. 

* **Is your code tail recursive?**

      The python and scheme versions of reverse employ tail recursion. In each function, the recursive call is the last one made. 

* **How would you write a test to see if Scheme is applying tail recursion optimizations?**

      This could be achieved by testing if the return value of the entire function is the recursive value of the last call. This can be done by  wrapping an entire function in a call to display, where the inner function would not normally display any values. Then, the call to display should print the return value of the inner function's last recursive call. 

* **Contrast your experience solving this problem in Java, Python, and Scheme.** 

      In java, recursion was not used. I opted to use iteration, however, this iteration saved memory and was time-efficient because it only iterates through half of the entire list it was given. The comments in that function go into a deeper explanation of why this was done. In python,  where tail recursion was used, slicing notation was used in the recursive call to recursively append the head of each list to the end of  the list. Similarly, in scheme, where tail recursion was also used, the append function along with the car and cdr functions (to isolate the head and the rest of the list respectively), were used to recursively constuct a reversed list. 

### Map

* **What kinds of values can be in `l`?**
    
      'l' can hold values of any type, as the map function is meant to apply a function to some list of values, both of which are defined by the programmer. 

* **What are the arguments to the function `func`?**

      In java, the interface Func<T,T> func takes two arguments, the type of the value to be inputted in the function, and the return type  of the function. 'func' itself takes the list that the function will apply as an argument. In python, func similarly accepts the  list that will have the defined function applied to it. The same applies to scheme. 
     
* **Why is this function built into scheme when it's so simple to write?**
      
      This function is built into scheme because it supports passing a lambda on the command line, which is user-defined. This allows the user to place any operation they choose on their inputted list, and the program will handle the application of that function, regardless of the  type of the list and the inputted function. 

* **Contrast your experience solving this problem in Java, Python, and Scheme.**

      All three implementations of this problem were relatively simple, once I fully understood what a 'func' is and how it contrasts from the  traditional idea of a function in programming in that a function can actually be passed into another function as an argument. In java,  all I had to do was support generic types, and I iteratively applied the function to each element in the list. I also created the functions to pass into the method, one to apply to int, and another to apply to String. This was almost identical to how I went about the  process in python. In scheme, I did this recursively, isolating the head of the list using car and calling my passed in 'func' on it, and then  calling my-map again on the rest of the list using cdr l. 

### Tree

* **How do you feel about closures versus objects?  Why?**

      I feel that closures are a pretty intuitive way to apply the idea of an object in a language like scheme. It allows something very similar to an object to be implemented through scheme's innate rules about scope. Additionally, I find it interesting how the state of the program in memory is saved in subsequent calls, which is really what made this idea work for trees. While objects are a lot more cookie-cutter and  defined in object oriented programming, and are CERTAINLY easier to use, it is fascinating that most of the same functionality can be used  in scheme through closures. 

* **How do you feel about defining a tree node as a generic triple?**
    
      I found that this was a pretty nifty trick to get a binary tree to implement any type that the user wants. Doing so was a bit odd at first, but through extending comparable in java, this was a lot more doable and ended up working well. 

* **Contrast your experience solving this problem in Java, Python, and Scheme.**

      In java, the implementation of the tree was pretty straightforward besides doing so with generic types. I used a lot of what I learned in CSE-017 to get the job done, and it worked well. This was a similar case in python, but not in the sense that the code was the same, I just leveraged the general concepts about a tree that I learned in CSE-017 to flesh this out in python. The tree implementation in schem was probably the hardest. I could not figure out how to put all of the tree functions into a let, so they are all made using define, but regardless of this, the implementation works. It also took me a while to figure out how to actually test this program, but after copying my code into sunlab and figuring out how to use gsi and actually define a tree "object" using my make-bst function, this became a lot mor straightforward. I had trouble testing the inorder and preorder functions, as I could not figure out how to pass a lambda to these but I am hoping all works out when the TA/professor grade it. 

### Prime Divisors

* **Why did you choose the Scheme constructs that you chose in order to solve this problem?**

      I used several if statements and a helper function in scheme to solve this problem, as well as tail recursion. I chose to use these  constructs because the if statements made it easy to deal with the several different conditions in this problem, like the value / divisor having a remainder, the value equaling 1, and so forth. The helper function was used because of how the values of each recursive call  need to be saved in order to calculate each subsequent divisor. Rather than saving off each value in a global list, the value and divisor need to also be carried over to the next recursive call to prime-divisor-helper, which is made possible by nesting this helper function  in another global function. Tail recursion also makes this possible.

* **Contrast your experience solving this problem in Java, Python, and Scheme.**

      In java, my first approach to this problem, I studied the rules of how to compute the prime factorization of a number, and realized how  the nature of this mathematical technique is inherently recursive. I left ample comments in the PrimeDivisors.java file that explain how my recursive method alligns with prime factorization. In python, I used iteration, but followed the same general principles of prime  factorization. Once again, in scheme, using if statements and a helper function (which was the only major difference between the scheme implementation and the other two, as it is necessary for this kind of computation in scheme), I applied the recursive nature of finding a prime divisor. 
