;; list2vector takes a list and returns a vector, without using `list->vector`
(define (list2vector l)
    (let ((retVector (make-vector (length l))))
        (define (list2vectorHelper l i)
            (if (< i (vector-length vec)) 
            ((vector-set! retVector i (car l))
            (list2vectorHelper (cdr l)(+ i 1)))retVector))
    (list2vectorHelper l 0)))
