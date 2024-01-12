# read_list: Read from the keyboard and put the results into a list.  The code
# should keep reading until EOF (control-d) is input by the user.
#
# The order of elements in the list returned by read_list should the reverse of
# the order in which they were entered.

def read_list():
    lst = [] # declaring a list
    print("Enter to STDIN to reverse your input. Hit enter when done, then press Ctrl+Z for EOF: ")
    while True: # the loop runs until EOF(control-z)
        try: # using try and except clause to handle the EOF(control-z)
            x = input() # x stores the input from the user
            lst.append(x) # adding the input at the end of the list
        except EOFError: # if EOF(control-z) is input, break the loop
            break 
    return my_reverse(lst) # return the reversed list

# Taking the function from the my_reverse.py to reverse the list
def my_reverse(l):
    if len(l) == 1: 
        return l
    return my_reverse(l[1:]) + l[0:1]

# Test case: 
print(read_list())
