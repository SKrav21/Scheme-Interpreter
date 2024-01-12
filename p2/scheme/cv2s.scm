;; charvec2string takes a vector of characters and returns a string
(define (charvec2string cv)
  (define cvAsList (vector->list cv)) ;;using vector->list on cv to convert cv to a list (no restrictions on functions for this part of the assignment
            (list->string cvAsList)) ;;using list->string on cvAslist to convert it to a string.

;;test with:
;;(charvec2String '#(<YOUR VECTOR HERE>))
