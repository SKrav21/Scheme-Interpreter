;; tree: A binary tree, implemented as a "closure"
;;
;; The tree should support the following methods:
;; - 'ins x      - Insert the value x into the tree
;; - 'clear      - Reset the tree to empty
;; - 'inslist l  - Insert all the elements from list `l` into the tree
;; - 'display    - Use `display` to print the tree
;; - 'inorder f  - Traverse the tree using an in-order traversal, applying
;;                 function `f` to the value in each non-null position
;; - 'preorder f - Traverse the tree using a pre-order traversal, applying
;;                 function `f` to the value in each non-null position
;;
;; Note: every method should take two arguments (the method name and a
;; parameter).  If a method is defined as not using any parameters, you
;; should still require a parameter, but your code can ignore it.
;;
;; Note: You should implement the tree as a closure.  One of the simplest
;; examples of a closure that acts like an object is the following:
;;
;; (define (make-my-ds)
;;   (let ((x '())) (lambda (msg arg)
;;       (cond ((eq? msg 'set) (set! x arg) 'ok) ((eq? msg 'get) x) (else 'error)))))
;;
;; In that example, I have intentionally *not* commented anything.  You will
;; need to figure out what is going on there.  If it helps, consider the
;; following sequence:
;;
;; (define ds (make-my-ds)) ; returns nothing
;; (ds 'get 'empty)         ; returns '()
;; (ds 'set 0)              ; returns 'ok
;; (ds 'get 'empty)         ; returns 0
;; (ds 'do 3)               ; returns 'error
;;
;; For full points, your implementation should be *clean*.  That is, the only
;; global symbol exported by this file should be the `make-bst` function.

;; Questions:
;;   - How do you feel about closures versus objects?  Why?
;;   - How do you feel about defining a tree node as a generic triple?
;;   - Contrast your experience solving this problem in Java, Python, and
;;     Scheme.

(define (make-bst)
  (let ((root '()))
    (lambda (msg arg) ; msg is a symbol, arg is a value or list of values (depending on msg) 
      (cond ((eq? msg 'ins) (set! root (ins root arg)) 'ok) ; insert a value into the tree
            ((eq? msg 'clear) (set! root '()) 'ok) ; clear the tree
            ((eq? msg 'inslist) (set! root (inslist root arg)) 'ok) ; insert a list of values into the tree
            ((eq? msg 'display) (print root) 'ok) ; display the tree
            ((eq? msg 'inorder) (inorder root arg) 'ok) ; traverse the tree in-order
            ((eq? msg 'preorder) (preorder root arg) 'ok) ; traverse the tree pre-order
            (else 'error)))))

(define (ins root val)
  (cond ((null? root) (list val '() '())) ; if the root is null, insert the value
        ((< val (car root)) (list (car root) (ins (cadr root) val) (caddr root))) ; if the value is less than the root, insert it into the left subtree
        ((> val (car root)) (list (car root) (cadr root) (ins (caddr root) val))) ; if the value is greater than the root, insert it into the right subtree
        (else root))) ; if the value is equal to the root, do nothing

(define (inslist root l)
  (cond ((null? l) root) ; if the list is empty, return the root
        (else (inslist (ins root (car l)) (cdr l))))) ; otherwise, insert the first element of the list into the tree and recurse on the rest of the list

(define (inorder root f)
  (cond ((null? root) '()) ; if the root is null, do nothing
        (else (inorder (cadr root) f) ; otherwise, traverse the left subtree
              (f (car root)) ; apply the function to the root
              (inorder (caddr root) f)))) ; traverse the right subtree
      
(define (preorder root f)
  (cond ((null? root) '()) ; if the root is null, do nothing
        (else (f (car root)) ; apply the function to the root
              (preorder (cadr root) f) ; traverse the left subtree
              (preorder (caddr root) f)))) ; traverse the right subtree

(define (print root)
  (cond ((null? root) '()) ; if the root is null, do nothing
        (else (display (cadr root)) ; otherwise, display the left subtree
              (display (car root)) ; display the root
              (display (caddr root))))) ; display the right subtree

(define (clear root)
  (cond ((null? root) '()) ; if the root is null, do nothing
        (else (clear (cadr root)) ; otherwise, clear the left subtree
              (clear (caddr root)) ; clear the right subtree
              (set! root '())))) ; set the root to null
;Tests
;(define myTree(make-bst))
;(myTree 'ins 6)
;(myTree 'display '())
;(myTree 'inslist '(12 46 3 8 9 0 145))
;(myTree 'inorder <function of choosing>)
;(myTree 'preorder <function of choosing>)
;(myTree 'display '())
;(myTree 'clear '())
;(myTree 'display '())
