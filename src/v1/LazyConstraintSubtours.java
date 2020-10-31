package v1;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import java.util.Scanner;

public class LazyConstraintSubtours extends IloCplex.LazyConstraintCallback {
    
    IloIntVar[][] s;
    IloCplex model;
    GUI gui;
    
    LazyConstraintSubtours(IloIntVar[][] s, IloCplex model){
        this.s = s;
        this.model = model;
        //this.gui = gui;
    }

    @Override
    protected void main() throws IloException {
        
        // Get the current solution
        double[][] sTemp = new double[Data.cities][Data.cities];
        for(int i = 0; i < Data.cities; i++){
            for(int j = 0; j < Data.cities; j++){
                sTemp[i][j] = this.getValue(s[i][j]);
            }
        }
        //gui.setSTemp(sTemp);
        //gui.repaint();
        
        System.out.println("=============================================================");
        System.out.println("Creating subtour constraints from input");
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
                IloRange rng = model.range(0.0, noSubtours, (double) subtour.length);
                model.addLazyConstraint(rng);
                System.out.println("Constraint added for subset " + input);
            }
            else {
                System.out.println(input + " is not a valid subtour. Leaving...");
                System.out.println("=============================================================");
                break;
            }
        }
    }
    
}
