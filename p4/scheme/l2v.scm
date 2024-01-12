;; list2vector takes a list and returns a vector, without using `list->vector`

;;pretty much a completely refined implementation from the p2 implementation. 
;; NOTE: This code produces a segmentation fault on cxxscheme.exe, but works with my interpreter. 

(define list2vector (lambda (vec) ;;declare outermost function as a lambda
    (define length (lambda (vec) (if (null? vec) 0 (+ 1 (length (cdr vec)))))) ;;helper function to calculate the list's length
        (define listLen (length vec)) ;; store the length of the list in a variable 
          (define retVec (make-vector listLen)) ;; create an empty vector of the same length as the list
            (define l2vhelper (lambda (vec i) (if (null? vec) retVec ;;define helper function. if the list is null, return the "vectorized" list
            (begin (vector-set! retVec i (car vec))(l2vhelper (cdr vec) (+ i 1)))))) ;; if it is not null, set the ith element of the vector to the car of the list, recursively call the helper on the cdr and i+1
            (l2vhelper vec 0))) ;; first call to the helper
(list2vector '(1 2 3 4 5)) ;;test for the function
