package com.shpp.p2p.cs.mhorak.assignment10;

/**
 * The class, which represents a single node in a binary tree of a mathematical expression
 */
public class TreeNode {

    /* An information, which is contained in a node. It can be a number, a variable, or mathematical operation */
    public String info;

    /* A reference to the left leaf or branch */
    public TreeNode leftNode;

    /* A reference to the right leaf or branch */
    public TreeNode rightNode;

    /**
     * A default constructor. Sets everything to null.
     */
    public TreeNode() {
        info = null;
        leftNode = null;
        rightNode = null;
    }

    /**
     * Copy constructor
     * @param otherNode other node to copy from.
     */
    public TreeNode(TreeNode otherNode) {
        //Firstly we check if the other node isn't null
        if (otherNode != null) {
            //Setting branches to null
            this.leftNode = null;
            this.rightNode = null;
            //Copying all the information
            this.info = otherNode.info;

            //Copying branches (Kind of recursion is used)
            if (otherNode.leftNode != null) {
                this.leftNode = new TreeNode(otherNode.leftNode);
            }
            if (otherNode.rightNode != null) {
                this.rightNode = new TreeNode(otherNode.rightNode);
            }
        }
    }
}
