;; my_map: apply a function to every element in a list, and return a list
;; that holds the results.
;;
;; Your implementation of this function is not allowed to use the built-in
;; `map` function.

(define (my-map func l) ; define the function my map to take the function to be applied and the list as arguments 
  (if (null? l) ; if the inputted list is null, return the null list
      '()
      (cons (func (car l)) ;calling the function on the head of the list 'car', and then appending it to the rest of the list 'cdr' (the list is empty initially)
            (my-map func (cdr l))))) ;recursive call to my-map that takes as arguments the function to be applied, and the rest of the list after the head using cdr 

;Test 
;(my-map (lambda (x) (+ x 1)) '(1 2 3 4 5))
