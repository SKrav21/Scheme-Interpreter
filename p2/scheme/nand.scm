;; Compute the nand of two values, using only the `or` and `not` functions
;;
;; nand should always return a boolean value
(define (nand a b) 
    (not(and a b))) ;nand is pretty simple, just looked at its 
                    ;logic table representation and realized its 
                    ;the opposite of an and, so I used a not to
                    ;negate an and. 