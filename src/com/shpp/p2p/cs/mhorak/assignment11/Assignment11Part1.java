package com.shpp.p2p.cs.mhorak.assignment11;

import java.util.HashMap;


/**
 * The main class of the program
 * The calculator
 */
public class Assignment11Part1 {
    /**
     * Gets an equation and variables and prints the solution
     * @param args first argument is an equation, which supports variables,
     *             and mathematical operations like -,+,*,/,^
     *             <br>
     *             All the rest arguments are the variables with their definitions
     */
    public static void main(String[] args) {

        //Making sure that args is not empty
        if (args.length == 0) throw new AssertionError();

        HashMap<String,Double> variables = getVariables(args);
        try {
            System.out.println(calculate(args[0], variables));
        } catch (Exception exception) {
            System.out.println("Error: " + exception.getMessage());
        }

    }


    /**
     * Gets a hashmap with variables and their definitions
     * @param args arguments with variables
     * @return hashmap with variables and their values
     */
    private static HashMap<String, Double> getVariables(String[] args) {
        //At first, we check if there are any variables present
        if (args.length < 1) {
            return null;
        }

        //If there are, we get an information about variables
        HashMap<String, Double> variables = new HashMap<>();
        //Simply going through all the data in an array
        for (int i = 1; i < args.length; i++) {
            //Removing spaces and comas
            args[i] = args[i].replaceAll(" ", "");
            args[i] = args[i].replaceAll(",", ".");
            variables.put(args[i].substring(0, args[i].indexOf('=')),
                    Double.parseDouble(args[i].substring(args[i].indexOf('=') + 1)));
        }

        return variables;
    }

    /**
     * Calculates the result of formula, putting values of variables in it
     * @param formula an expression to calculate
     * @param variables the HashMap of variables
     * @return the calculated result
     */
    public static double calculate(String formula, HashMap<String,Double> variables) throws Exception{
        //At first, we create a binary tree of the equation
        MathematicalTree tree = new MathematicalTree(formula);

        //And then we just calculate the result
        return tree.getResult(variables);
    }

}