# my_reverse: reverse a list without using the python `reverse` function

# Recursive method for reversing a list in python
def my_reverse(l):
    if len(l) == 1: # handling edge case of list length = 1, we just returning the list itself
        return l
    return my_reverse(l[1:]) + l[0:1] #recursive method of reversing the list using slicing notation
    # we take the last node of the list and add it in the front of the new list and repeat the function on 
    #nodes List[1...n-1] until the if statement will handle the edge case and we will print the last node of the list


# Testing 
#test = [3, 2, 1]
#print(my_reverse(test)) #should print out [1, 2, 3]
#print('\n')
#testStr  = ["this","is","a","test"]
#print(my_reverse(testStr))