package edu.lehigh.cse262.p1;

import java.util.List;
import java.util.function.Function;

/**
 * A binary tree, implemented from scratch
 */
public class MyTree<T extends Comparable<T>> {
    Node root; // the root of the tree
    /**
     * A node in the tree
     */
    class Node {
        T data; // the data in the node
        Node left; // the left child
        Node right; // the right child

        public Node(T data) { // constructor
            this.data = data; // set the data
        }
    }

    boolean isEmpty() { // is the tree empty?
        return root == null; // return true if the root is null
    }
    /**
     * Insert a value into the tree
     * 
     * @param value The value to insert
     */
    void insert(T value) { // insert a value into the tree
        if(isEmpty()) // if the tree is empty
            root = new Node(value); // make a new node with the value
        else
            insert(root, value); // insert the value into the tree
    } 
    /**
     * Recursive insert method
     * 
     * @param node The node to insert into
     * @param value The value to insert
     */
    void insert(Node node, T value) { // insert a value into the tree

        if(value.compareTo(node.data) < 0) { // if the value is less than the data in the node
            if(node.left == null) // if the left child is null
                node.left = new Node(value); // make a new node with the value
            else
                insert(node.left, value); // insert the value into the left child
        }
        else {
            if(node.right == null) // if the right child is null
                node.right = new Node(value); // make a new node with the value
            else
                insert(node.right, value); // insert the value into the right child
        }
    }

    /** Clear the tree */
    void clear() {
        root = null; // set the root to null
    }

    /**
     * Insert all of the elements from some list `l` into the tree
     *
     * @param l The list of elements to insert into the tree
     */
    void inslist(List<T> l) {
        for(T value : l) // for each value in the list
            insert(value); // insert the value into the tree
    }

    /**
     * Perform an in-order traversal, applying `func` to every element that is
     * visited
     * 
     * @param func A function to apply to each item
     */
    void inorder(Function<T, T> func) {
        if(isEmpty()) // if the tree is empty
            return; // do nothing
        inorder(root, func); // apply the function to the root
    }

    /**
     * Recursive in-order traversal
     * 
     * @param node The node to visit
     * @param func A function to apply to each item
     */
    void inorder(Node node, Function<T, T> func) {
        if(node.left != null)
            inorder(node.left, func); // visit the left child
        func.apply(node.data); // apply the function to the data in the node
        if(node.right != null)
            inorder(node.right, func); // visit the right child
    }

    /**
     * Perform a pre-order traversal, applying `func` to every element that is
     * visited
     * 
     * @param func A function to apply to each item
     */
    void preorder(Function<T, T> func) {
        if(isEmpty())
            return;
        preorder(root, func); // apply the function to the root
    }

    /**
     * Recursive pre-order traversal
     * 
     * @param node The node to visit
     * @param func A function to apply to each item
     */
    void preorder(Node node, Function<T, T> func) {
        func.apply(node.data); // apply the function to the data in the node
        if(node.left != null)
            preorder(node.left, func); // visit the left child
        if(node.right != null) 
            preorder(node.right, func); // visit the right child
    }
}