;; read_list: Use the `read` function to read from the keyboard and put the
;; results into a list.  The code should keep reading until EOF (control-d) is
;; input by the user.  It should use recursion, not iterative constructs.
;;
;; The order of elements in the list returned by (read-list) should the reverse
;; of the order in which they were entered.
;;
;; You should *not* define any other functions in the global namespace.  You may
;; need a helper function, but if you do, you should define it so that it is
;; local to `read-list`.

(define read-list ;defining the function 
  (lambda () ;defining the operations of the function as a lambda (a nameless, inline function) that does some operation on a list of values. 
             ;in this case, it reads in each line inputted by a user and then appends this value to the end of the list returned by the next 
             ;recursive call. 
    (let ((x (read))) ;let statement within the lambda. A let statement can create local variables or objects that are local to that function. 
                      ;The let statement evaluates its expression list in order, and returns the return value of the last expression as the entire
                      ;return value of the let statement. 
      (if (eof-object? x) ;checks if x, the object read in, is an eof-object. EOF can be inputted by ctrl+d on linux/mac, and ctrl+z on windows.
          '() ;return an empty list 
          (append (read-list) (list x)))))) ;list x initially contains the head of the list, and is appended at the end of a recursive call that returns the second element 
                                            ;(the head of the new list) which is then appended to the end of another recursive call which does the same, and so on. 
                                            ;the end result is that the list is recursively constructed in an order backwards from the order it was created in. 

;test 
;(display (read-list))



