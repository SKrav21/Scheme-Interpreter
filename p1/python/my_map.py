# my_map: apply a function to every element in a list, and return a list
# that holds the results.
#
# Your implementation of this function is not allowed to use the built-in
# `map` function.

# My function my_map does have to create new lists with new sizes after each appending, which makes it not the most
# efficient in terms of memory allocation, but this is Python :)

def my_map(func, l):
    my_list = [] # creating an empty list
    for i in l: #looping through each element in the list
        my_list.append(func(i)) # applying function to each element and inserting at the end of the new list
    return my_list # returning the result

#Our Testing

#def custom_func(number):
#   return number/2
#def custom_func(string):
#    return string.upper()

#test = ["this","is","a","test"]
#print(my_map(custom_func, test)) #expected result: ['THIS', 'IS', 'A', 'TEST']
#test = [2, 4, 6, 8]
#print(my_map(custom_func, test))  #expected result: [1.0, 2.0, 3.0, 4.0]
