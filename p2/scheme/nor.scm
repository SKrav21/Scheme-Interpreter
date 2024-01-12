;; Compute the nor of two values, using only the `and` and `not` functions
;;
;; nor should always return a boolean value
(define (nor a b) 
    (and (not a)(not b))) 
                          ;I realized nor is the "opposite" of 
                          ;or, in other words "not or", 
                          ;so by doing some DeMorgan's law 
                          ;on true and false conditions, I 
                          ;was able to turn ors into ands, 
                          ;and then just implemented the logic
                          ;in scheme