;; prime_divisors: compute the prime factorization of a number
;;
;; To test this function, open a new `gsi` instance and then type:
;;  (load "prime_divisors.scm")
;; Then you can issue commands such as:
;;  (prime-divisors 60)
;; And you should see results of the form:
;;  (2 2 3 5)

;; This is a skeleton for the prime-divisors function.  For now, it just
;; returns #f (false)
;;
;; Note that you will almost certainly want to write some helper functions,
;; and also that this will probably need to be a recursive function.  You are
;; not required to use good information hiding.  That is, you may `define`
;; other functions in the global namespace and use them from
;; `prime-divisors`.


(define (prime-divisors n) ;defining the main function
  (define (prime-divisors-helper n i) ;defining the recursive helper function
    (if (= i n) ;if the given value (or value returned from recursive calls) is equal to the current divisor 
        (display i) ;return that divisor. This happens when the value has been completely broken down into its prime divisors and the value / current divisor = 1. 
        (if (= (remainder n i) 0) ; if the remainder of the value / the divisor is 0 
            (begin ;first if condition -> entered only when the divisor is prime. See PrimeDivisor.java line 20 comments for a detailed explanation about why this is the case. 
              (display i) ;print the divisor, as it is assured to be prime if this branch of the if statement is reached (see above comment)
              (display " ") ;adding space in output
              (prime-divisors-helper (/ n i) i)) ;recursively calling prime-divisors-helper with a new value, the previous value divided by the divisor, and the divisor itself.  
                                                 ; this is accomplished by first doing n/i (written as / n i ), whose result is automatically used as an argument to prime-divisors-helper. 
            (prime-divisors-helper n (+ i 1))))) ;this is the other branch of the if statement that is reached if the remainder of n / i is not zero. Here, we recursively call prime-divisor-helper
                                                 ;again, but with the divisor incremented by 1 (written as + i 1)
  (prime-divisors-helper n 2)) ; calling prime-divisor-helper with an initial divisor of 2

; Test cases
;(display "prime-divisors 100: ")
;(prime-divisors 100)
;(newline)
;(display "prime-divisors 1000: ")
;(prime-divisors 1000)
;(newline)