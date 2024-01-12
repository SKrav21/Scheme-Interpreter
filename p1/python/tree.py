# tree: A binary tree, implemented as a class
#
# The tree should support the following methods:
# - ins(x)      - Insert the value x into the tree
# - clear()     - Reset the tree to empty
# - inslist(l)  - Insert all the elements from list `l` into the tree
# - display()   - Use `display` to print the tree
# - inorder(f)  - Traverse the tree using an in-order traversal, applying
#                 function `f` to the value in each non-null position
# - preorder(f) - Traverse the tree using a pre-order traversal, applying
#                 function `f` to the value in each non-null position

#from typing_extensions import Self


class tree:

     
    # Constructor
    def __init__(self,value):
        self.value = value
        self.left = None    
        self.right = None
    class tree:
            def __init__(self):
                self.root = None
        
    # - ins(x)      - Insert the value x into the tree
    def ins(self,x):
        if self.value is None:
            self.value = x
            return
        if x < self.value:
            if self.left:
                self.left.ins(x)
            else:
                self.left = tree(x)
        else:
            if self.right:
                self.right.ins(x)
            else:
                self.right = tree(x)
    
    # - clear()     - Reset the tree to empty
    def clear(self):
        self.value = None
        self.left = None
        self.right = None
    
    # - inslist(l)  - Insert all the elements from list `l` into the tree
    def inslist(self,l):
        for x in l:
            self.ins(x)
    
    # - display()   - Use `display` to print the tree
    def display(self):
        if self.value == None:
            return
        else:
            self.left.display()
            print(self.value)
            self.right.display()
    
    # - inorder(f)  - Traverse the tree using an in-order traversal, applying
    #                 function `f` to the value in each non-null position
    def inorder(self,f):
        if self.value == None:
            return
        else:
            self.left.inorder(f)
            f(self.value)
            self.right.inorder(f)
    
    # - preorder(f) - Traverse the tree using a pre-order traversal, applying
    #                 function `f` to the value in each non-null position
    def preorder(self,f):
        if self.value == None:
            return
        else:
            f(self.value)
            self.left.preorder(f)
            self.right.preorder(f)


