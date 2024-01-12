;; simple-hash creates a basic hash *set* of strings.  It uses the "method
;; receiver" style that you have seen previously: `make-hash` returns a function
;; that closes over some state; that function takes two arguments (a symbol and
;; a string).
;;
;; The argument to make-hash is a number: it should be positive.  It will be the
;; size of the "bucket vector" for your hash table.
;;
;; Your hash table should be a vector of lists.  Three operations can be
;; requested:
;; - 'contains string - Returns true if the string is in the hash set, false
;;   otherwise
;; - 'insert string - Returns true if the string was inserted into the hash set,
;;   false if it was alread there.
;; - 'remove string - Returns true if the string was removed from the hash set,
;;   false if it was not present to begin with.
;;
;; Here's an example execution:
;; (define my-hash (make-hash 32))
;; (my-hash 'insert "hello") <-- returns true
;; (my-hash 'contains "world") <-- returns false
;; (my-hash 'contains "hello") <-- returns true
;; (my-hash 'insert "hello") <-- returns false
;; (my-hash 'remove "world") <-- returns false
;; (my-hash 'remove "hello") <-- returns true
;; (my-hash 'remove "hello") <-- returns false
;; (my-hash 'contains "hello") <-- returns false
;;
;; To "hash" input strings, you should use the (very simple) djb2 function from
;; <http://www.cse.yorku.ca/~oz/hash.html>


;; TODO: implement this function
(define (make-hash size) 
   (define (hashFunc arg) ;;creating the actual hash funciton... still trying to work out how to approach this 
        (define argLen (string-length arg))
        (define (hashHelper argLen arg i)
            (if (not (= i argLen)) (define hash (+ (* hash 33) string-ref source i)
                                        (set! hash (hashHelper argLen arg (+ i 1))))
                                   ('()))
    )

)
    (let ((bucketVector (make-vector size))) ;;creating our vector to hold each list (hash values)
    (lambda (msg arg) ;;lambda to help us with arguments to the hash, users choose between insertion, checking if a record exists, or removal
    (cond ((eq? msg 'insert)) 
          ((eq? msg 'contains))
          ((eq? msg 'remove))
          (else 'error))) ;;if argument does not match what user gives 

 ))