# prime_divisors: compute the prime factorization of a number
# returns the list of prime numbers
def prime_divisors(n):
    divisor = 2 #initial divisor starts with 2
    primes = [] #initialization of the list
    while(n > 1): #the loop interates until the argument becomes 1
        if(n % divisor == 0): # if statement checks if remainder is 0 after the division by divisor
            n = n / divisor #if yes, the number is divided by the divisor and
            primes.append(divisor) #the divisor is added to the list as a prime number
        else:
            divisor += 1 #otherwise, the divisor is incremented by 1
    return primes #return list of the divisors

# Test case: 
#test = 72 
#print(prime_divisors(test)) # 2, 2, 2, 3, 3 = 2^3 * 3^2
 
    
