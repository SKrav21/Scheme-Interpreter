;; substring-wildcard is like contains-substring, but it understands
;; single-character wildcards in the pattern string.  Wildcards are represented
;; by the ? character.  Note that this is a slightly broken way of doing
;; wildcards: the '?' character cannot be matched exactly.
;;
;; Here's an example execution: 
;; (substring-wildcard "hello" "e?lo") <-- returns true
;; (substring-wildcard "hello" "yell") <-- returns false
;; (substring-wildcard "The quick brown fox jumps over lazy dogs" "q?ick") <-- returns true
;;
;; You should implement this on your own, by comparing one character at a time,
;; and should not use any string comparison functions that are provided by gsi.

;; TODO: implement this function

;;the only major changes that I made to this scheme program from its original form in p3 were that I changed things like 
;; '=' to '==', as well as using string-equal? instead of eq?, which also meant that I had to turn my characters into 
;;strings using the 'string' method. I also had to turn my outermost define into a lambda so that I could have "inner" 
;;defines (slang does not allow purely nested defines) and I also changed my helper function to a lambda so that it could call 
;;itself. Also, for ease of scanning/parsing/interpreting the code as well as a test case, I put (substring-wildcard "string1" "substring")
;; at the bottom. Feel free to change the arguments to any test cases you'd like! 

(define substring-wildcard (lambda (source pattern)
     (define sourceLen (string-length source)) ;;set the length of the source to a variable (NOT called in each recursive call)
          (define patternLen (string-length pattern)) ;;set the length of the pattern to a variable (NOT called in each recursive call)
              (define substring-wildcard-helper (lambda (source pattern i j) ;;define a helper function
                (if (== j patternLen) #t ;;if this condition is satisfied, (1. continued below)
                    (if (== i sourceLen) #f ;;means we have traversed the entire source and have not found the full pattern, (2. cont. below)
                        (if (or (string-equal? (string (string-ref source i)) (string (string-ref pattern j))) (string-equal? (string (string-ref pattern j)) (string #\?))) ;;if char at i in source matches char j in pattern (6. cont below)
                            (substring-wildcard-helper source pattern (+ i 1) (+ j 1)) ;;we recursively check the next charcter in each
                             (if (== i 0) (substring-wildcard-helper source pattern (+ i 1) 0) ;;if above is false, we check next character of source (3. cont. below)
                             (if (== j 0) (substring-wildcard-helper source pattern (+ i 1) 0) ;;if i is not 0, (4. cont. below)
                                         (substring-wildcard-helper source pattern i 0))))))));;if j is NOT 0, (5. cont. below)
                                         (substring-wildcard-helper source pattern 0 0))) 
(substring-wildcard "campfire" "a?pf")

;; 1. it means we were able to match every character in the pattern. 
;; j is only incremented when the current char in the pattern matches the current 
;; char in the main string, meaning if we have incremented through the whole pattern, it is contained in the source. 

;; 2. works even if the given strings are the same since patternLen is checked first

;; 3. with the first character of pattern. We reset j to 0 (which it technically is anyway at this point in the code) 

;; 4. we must check if j is 0. This is because we do not want to keep checking the first character of source against the first character in pattern.
;; this makes more sense after reading the description of the false condition of that if. 

;; 5. we need to reset it to 0 to continue the search. Because even if we have found a partial match, as soon as we come across
;; a character in source that differs from pattern, we must reset our search in pattern. HOWEVER, we want to still check the 
;; current character in source against the first character in pattern after we reset the counter, so we pass i instead of i+1 
;; to the recursive call. The reason I split this into two separate conditions is because we do not want this call being done
;; when the first character of both does not match or else infinite recursion will occur. A good example where this helps: 
;; source: "remember", pattern "ember" (chars 2 and 3 of source match 1 and 2 of pattern, but 4 of source doesnt match 3 of 
;; pattern. however if we do not check this failing character of source against the beginning of pattern, we will get a false
;; negative.)

;; 6. this line contains the only change from contains-substring to substring-wildcard. We check either if the two current characters 
;; match one another, OR if the current character in pattern matches '#\?', the character representation of '?', using proper or syntax. 



