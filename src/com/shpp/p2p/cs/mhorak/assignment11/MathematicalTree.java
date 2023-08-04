package com.shpp.p2p.cs.mhorak.assignment11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The class represents a binary tree of a mathematical expression
 */
public class MathematicalTree {
    //The root of a tree
    private TreeNode rootNode;

    /**
     * The constructor. Creates a binary tree, basing on an equation.
     * @param equation the expression to make a tree from
     */
    public MathematicalTree(String equation) {
        //Making sure we don't have any spaces or comas in an equation
        equation = equation.replaceAll(" ", "");
        equation = equation.replaceAll(",", ".");

        //And just creating every single node, using recursion
        rootNode = new TreeNode();
        createNode(equation, rootNode);

    }

    /**
     * Copy constructor
     * @param otherTree the tree to copy from
     */
    private MathematicalTree(MathematicalTree otherTree) {
        //Simply copying everything
        this.string = otherTree.string;
        this.rootNode = new TreeNode(otherTree.rootNode);
    }

    /* List of mathematical operations, that can stay before minus,
    so that it means that it is not an operation, but just a negative number */
    private static final ArrayList<Character> OPERATIONS_BEFORE_MINUS =
            new ArrayList<>(Arrays.asList('+', '/', '*', '^', '('));

    /* List of all operations, located in a priority from least to most */
    ArrayList<String> OPERATIONS =
            new ArrayList<>(Arrays.asList("+","-","/","*","^","sin","cos","atan","tan","log10","log2", "sqrt"));

    /**
     * Creates a node basing on a string. Uses recursion
     * @param equation a string. An expression, a part of it, or just a single number,
     *                depending on the situation
     * @param node a node to create and fill
     */
    private void createNode(String equation, TreeNode node) {
        //Here we remove the border brackets if there are any
        equation = MathematicalTree.removeBorderBrackets(equation);

        //An index of found (or not) mathematical operation
        int index;

        //An indicator, that tells if there were any operations found.
        //If they weren't found, then we have to deal with parameter or number.
        boolean areAnyOperations = false;



        for (String operation: OPERATIONS) {

            //An indicator that tells if there were any specific operations found.
            //If they were found, then we have to check if there are any specific operations, that can match
            //and then be processed
            boolean areThereAnyThisOperations;

            //We make a cycle to check all the operators and process them
            String checker = equation;
            do {
                areThereAnyThisOperations = false;

                //We check if the operator is found and if it is not inside brackets
                if ((index = checker.lastIndexOf(operation)) != -1
                        && !MathematicalTree.isInBrackets(index, equation)) {
                    //If it is a normal minus or any other operation
                    if (!operation.equals("-")
                            || (index != 0 && !OPERATIONS_BEFORE_MINUS.contains(equation.charAt(index - 1)))) {
                        //I differently process binary operators and functions (like unary operators)
                        //Here I process binary operators
                        if (operation.length() == 1) {
                            node.info = operation;
                            node.leftNode = new TreeNode();
                            node.rightNode = new TreeNode();
                            createNode(equation.substring(0, index), node.leftNode);
                            createNode(equation.substring(index + 1), node.rightNode);
                        }
                        //If it is a function, then there will be only right node, because it's unary
                        else {
                            node.info = operation;
                            node.leftNode = null;
                            node.rightNode = new TreeNode();
                            createNode(equation.substring(index + operation.length()), node.rightNode);
                        }

                        //If I found and processed an operator, then I exit the cycle
                        areAnyOperations = true;
                        break;
                    }
                }

                //Here I make sure that all the specific operators were processed
                if (index != -1) {
                    checker = checker.substring(0, index);
                    areThereAnyThisOperations = true;
                }
            } while (areThereAnyThisOperations);

            //Making sure we exited this loop also
            if (areAnyOperations) {
                break;
            }
        }

        //If it is a minus like a negative number
        if (!areAnyOperations && (index = equation.lastIndexOf("-")) != -1){
            node.info = "-";
            node.leftNode = new TreeNode();
            node.rightNode = new TreeNode();
            node.leftNode.info = "0";
            node.leftNode.leftNode = null;
            node.leftNode.rightNode = null;
            createNode(equation.substring(index + 1), node.rightNode);

            areAnyOperations = true;
        }

        //If we didn't find any operation, then it must be a number or parameter
        if (!areAnyOperations) {
            node.info = equation;
            node.leftNode = null;
            node.rightNode = null;
        }

    }


    //This string represents how the tree is visible with toString method
    private String string;

    /**
     * Returns a string of a tree
     * @return string of a tree
     */
    @Override
    public String toString() {
        string = "";
        //Going through all the nodes, and placing symbols at the end
        concatenateNode(rootNode);
        return string;
    }

    /**
     * Adds each node as a string to the end of a string
     * @param node node to put into string
     */
    private void concatenateNode(TreeNode node) {
        if (node != null) {
            string = string.concat(node.info);
            //Using recursion we are going to the left leaf
            if (node.leftNode != null) {
                concatenateNode(node.leftNode);
            }
            //And then to the right
            if (node.rightNode != null) {
                concatenateNode(node.rightNode);
            }
        }
    }

    /**
     * Calculates a result of an expression
     * @param variables a hashmap of variables and their values
     * @return result of an expression
     */
    public double getResult(HashMap<String,Double> variables) throws Exception{
        //I decided to create another tree, so we don't lose our first one
        //And in the future we can use it if it is needed
        MathematicalTree treeToCalculate = new MathematicalTree(this);

        //Using recursion we calculate each node
        return calculateNode(treeToCalculate.rootNode, variables);
    }

    /**
     * Calculates a node
     * Puts the result of two leafs in the info of a current leaf as a string value
     * @param node a node to calculate result
     * @param variables a hashmap of variables
     * @return the result of each operation. At the end it is the result of whole expression
     * @throws Exception an error in calculation
     */
    private double calculateNode(TreeNode node, HashMap<String, Double> variables) throws Exception {
        if (node.leftNode == null && node.rightNode == null) { //We reached a leaf
            //If we reached a leaf, we check if is a variable and change it to the value
            if (variables.containsKey(node.info)) {
                node.info = Double.toString(variables.get(node.info));
            }
            try {
                Double.parseDouble(node.info);
            } catch (NumberFormatException exception) {
                throw new Exception("Not enough parameters");
            }

            return Double.parseDouble(node.info);
        }

        //Here we have all the mathematical operations, and how to solve them
        switch (node.info) {
            case "-":
                node.info = Double.toString(calculateNode(node.leftNode, variables)
                        - calculateNode(node.rightNode, variables));
                break;
            case "+":
                node.info = Double.toString(calculateNode(node.leftNode, variables)
                        + calculateNode(node.rightNode, variables));
                break;
            case "/":
                //Checking for zero division
                if (calculateNode(node.rightNode, variables) == 0) {
                    throw new Exception("Division by zero");
                }
                node.info = Double.toString(calculateNode(node.leftNode, variables)
                        / calculateNode(node.rightNode, variables));
                break;
            case "*":
                node.info = Double.toString(calculateNode(node.leftNode, variables)
                        * calculateNode(node.rightNode, variables));
                break;
            case "^":
                node.info = Double.toString(Math.pow(calculateNode(node.leftNode, variables),
                        calculateNode(node.rightNode, variables)));
                break;
            case "sin":
                node.info = Double.toString(Math.sin(calculateNode(node.rightNode, variables)));
                break;
            case "cos":
                node.info = Double.toString(Math.cos(calculateNode(node.rightNode, variables)));
                break;
            case "tan":
                node.info = Double.toString(Math.tan(calculateNode(node.rightNode, variables)));
                break;
            case "atan":
                node.info = Double.toString(Math.atan(calculateNode(node.rightNode, variables)));
                break;
            case "log10":
                node.info = Double.toString(Math.log10(calculateNode(node.rightNode, variables)));
                break;
            case "log2":
                node.info = Double.toString(Math.log(calculateNode(node.rightNode, variables) / Math.log(2)));
                break;
            case "sqrt":
                node.info = Double.toString(Math.sqrt(calculateNode(node.rightNode, variables)));
                break;
        }

        //At the end we return the value we calculated
        return Double.parseDouble(node.info);
    }

    /**
     * This function checks if an operator (or actually just a character) is in brackets or not
     * @param index an index in expression on which the operator is
     * @param expression an expression in which do we have to check
     * @return true if an operator is between brackets, false if an operator is 'free'
     */
    private static boolean isInBrackets(int index, String expression) {
        int countOfOpenedBrackets = 0;

        //We are going through all the characters in an expression
        for (int i = 0; i < expression.length(); i++) {

            //We keep tracking on a count of opened brackets at the moment
            if (expression.charAt(i) == '(') {
                countOfOpenedBrackets++;
            }

            if (expression.charAt(i) == ')') {
                countOfOpenedBrackets--;
            }

            //If we reached our character we check count of opened brackets and decide
            if (i == index && countOfOpenedBrackets == 0) {
                return false;
            }
            else if (i == index) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method removes useless brackets that are on the left and right of the expression
     * @param expression an expression to remove brackets from
     * @return a new expression without those brackets
     */
    private static String removeBorderBrackets(String expression) {

        boolean wasTheBracketsBefore;

        //We remove all the unnecessary brackets, so we do it in a loop
        do {
            wasTheBracketsBefore = false;

            //We keep tracking on a count of opened brackets
            int countOfOpenedBrackets = 0;

            //And we check if it is needed to remove border brackets
            boolean areThereBorderBrackets = true;

            //We go through all the characters in an expression
            for (int i = 0; i < expression.length(); i++) {
                //Keep tracking on opened brackets
                if (expression.charAt(i) == '(') {
                    countOfOpenedBrackets++;
                }

                //And here we decide if it is needed to remove those brackets
                /*
                 * (a+b)*c - Note that there is a moment, where no brackets are opened
                 * ((a+b)*c) - While there is always at least one pair of brackets opened.
                 * So that is what we check here
                 */
                if (countOfOpenedBrackets == 0) {
                    areThereBorderBrackets = false;
                    break;
                }

                if (expression.charAt(i) == ')') {
                    countOfOpenedBrackets--;
                }
            }

            //And we simply remove a pair of brackets if it is needed
            if (areThereBorderBrackets) {
                expression = expression.substring(1, expression.length() - 1);
                wasTheBracketsBefore = true;
            }

        } while (wasTheBracketsBefore);

        return expression;
    }

}
