;; Compute the exclusive or of two values, using only and, or, and not
;;
;; xor should always return a boolean value
(define (xor a b) 
       (and (or a b) (not (and a b)))) ;found a way to combine and, 
                                       ;or, and not logic on paper 
                                       ;using some truth tables and
                                       ;then just implemented it in 
                                       ;scheme with valid syntax and 
                                       ;grammar
