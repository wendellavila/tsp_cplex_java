package v1;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*============================================
Traveling salesman problem solved with CPLEX
==============================================
Author: Wendell Joao Castro de Avila
RA: 2017.1.08.013
Date: 31.10.2020
==============================================*/

public class LazyConstraintSubtours extends IloCplex.LazyConstraintCallback {
    
    IloIntVar[][] s;
    IloCplex model;
    GUI gui;
    
    LazyConstraintSubtours(IloIntVar[][] s, IloCplex model, GUI gui){
        this.s = s;
        this.model = model;
        this.gui = gui;
    }
    
    public double[][] getCurrentSolution() throws IloException {
        double[][] sTemp = new double[Data.cities][Data.cities];
        for(int i = 0; i < Data.cities; i++){
            for(int j = 0; j < Data.cities; j++){
                sTemp[i][j] = this.getValue(s[i][j]);
            }
        }
        return sTemp;
    }

    @Override
    protected void main() {
        try {
            // Get the current solution
            // update visualization
            gui.setCurrentSolution(getCurrentSolution());
            gui.repaint();

            System.out.println("=============================================================");
            System.out.println("Enter with at least 3 comma separated cities between 0-57 (e.g '0,1,2'): ");
            Scanner scan = new Scanner(System.in);
            while(true){
                String input = scan.nextLine();
                String [] stringArray = input.split(",");
                boolean invalid = false;

                //string to int array
                int[] subtour = new int[stringArray.length];
                try {
                    for(int i = 0; i < stringArray.length; i++){
                        subtour[i] = Integer.parseInt(stringArray[i]);
                        //set will be discarded if invalid values are found
                        if(subtour[i] < 0 || subtour[i] > Data.cities-1){
                            invalid = true;
                        }
                    }
                } catch (NumberFormatException ex){
                    //function will exit if anything other than comma separated integers is passed
                    invalid = true;
                }

                //at least 3 cities and at most n-1 in the subset
                if(stringArray.length >= 3 && stringArray.length < Data.cities && invalid == false){
                    //subtour constraint
                    IloLinearNumExpr noSubtours = model.linearNumExpr();
                    for(int i = 0; i < subtour.length; i++){
                        for(int j = 0; j < subtour.length; j++){
                            noSubtours.addTerm(1.0, s[subtour[i]][subtour[j]]);
                        }
                    }
                    add(model.le(noSubtours, (subtour.length - 1)));
                    System.out.println("Constraint will be added for subset " + input);
                }
                else {
                    System.out.println("All constraints defined for this iteration. Leaving...");
                    System.out.println("=============================================================");
                    break;
                }
            }
        } catch (IloException ex) {
            Logger.getLogger(LazyConstraintSubtours.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
