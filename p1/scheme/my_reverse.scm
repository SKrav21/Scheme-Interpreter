;; my_reverse: reverse a list without using the scheme `reverse` function
;;
;; Your implementation of this function can use special forms and standard
;; functions, such as `car`, `cdr`, `list`, `append`, and `if`, but it cannot
;; use the built-in `reverse` function.
;;
;; Your implementation should be recursive.


;; my_reverse: reverse a list without using the scheme `reverse` function
;; parameters: l - a list
;; if the list is empty, return the empty list, becasue reverse is also empty


(define my_reverse ;defining the function
  (lambda (l) ;defining a lambda to reverse the given list 
    (if (null? l) ;if the list is null, return an empty list 
        '()
        (append (my_reverse (cdr l)) (list (car l)))))) ;recursively calling my_reverse on the rest of the list besides the head and appending the head to the end of the list. 
                                                        ;the head is returned using the list function, which returns its arguments as a linked list so that append can work correctly
                                                        ;(append needs two lists in order to work)
; test a function my_reverse 
;(display (my_reverse '(1 2 3 4 5))))
;(display(my_reverse '(this is a test))))



