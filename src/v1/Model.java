package v1;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model {
    
    public static IloCplex model;
    public static IloIntVar[][] s;
    public static GUI gui;
    
    public static void printToFile(IloIntVar[][] s) throws IOException, IloException {
        File file = new File("graph.txt");
        FileWriter write = new FileWriter(file);
        PrintWriter print = new PrintWriter(write);

        for(int i = 0; i < Data.cities; i++){
            for(int j = 0; j < Data.cities; j++){
                print.print(((int) model.getValue(s[i][j]) * Data.distances[i][j]) + " ");
            }
            print.println();
        }
        print.close();
        write.close();
    }
    
    public static void createSubtourConstraints() throws IloException {
        
            Scanner in = new Scanner(System.in);
            System.out.println("=============================================================");
            System.out.println("Creating subtour constraints");
            while(true){
                System.out.println("Enter with at least 3 and at most "+ (Data.cities-1) +" comma separated cities (e.g '0,1,2'): ");
                String input = in.nextLine();
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
                    model.addLe(noSubtours, (subtour.length - 1));
                    System.out.println("Constraint added for subset " + input);
                }
                else {
                    System.out.println(input + " is not a valid subset. Leaving...");
                    System.out.println("=============================================================");
                    break;
                }
            }
        
    }

    public static void main(String[] args) throws IOException {
        try {
            Data.readInstance();
            //Data.printInstance();
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            long startTime = System.currentTimeMillis();
            model = new IloCplex();
            
            //time limit = 60 sec
            model.setParam(IloCplex.DoubleParam.TiLim, 60.0);
            
            //decision variable
            s = new IloIntVar[Data.cities][Data.cities];
            for(int i = 0; i < Data.cities; i++){
                for(int j = 0; j < Data.cities; j++){
                    //each edge can be used or not
                    s[i][j] = model.boolVar();
                }
            }
            
            //objective function
            IloLinearNumExpr objFunction = model.linearNumExpr();
            for(int i = 0; i < Data.cities; i++){
                for(int j = 0; j < Data.cities; j++){
                    objFunction.addTerm(s[i][j], Data.distances[i][j]);
                }
            }
            model.addMinimize(objFunction);
            
            //constraints
            //traveling can only happen between different cities.
            for(int i = 0; i < Data.cities; i++){
                model.addEq(s[i][i], 0.0);
            }
            
            //traveler can't go back to the city they just left.
            for(int i = 0; i < Data.cities; i++){
                for(int j = 0; j < Data.cities; j++){
                    model.add(model.ifThen(model.eq(s[i][j], 1.0), model.eq(s[j][i], 0.0)));
                }
            }
            
            //exactly one edge going in for each city
            for(int i = 0; i < Data.cities; i++){
                IloLinearNumExpr edgeGoingInConstraint = model.linearNumExpr();
                for(int j = 0; j < Data.cities; j++){
                    edgeGoingInConstraint.addTerm(1.0, s[i][j]);
                }
                model.addEq(edgeGoingInConstraint, 1.0);
            }
            
            //exactly one edge going out for each city
            for(int j = 0; j < Data.cities; j++){
                IloLinearNumExpr edgeGoingOutConstraint = model.linearNumExpr();
                for(int i = 0; i < Data.cities; i++){
                    edgeGoingOutConstraint.addTerm(1.0, s[i][j]);
                }
                model.addEq(edgeGoingOutConstraint, 1.0);
            }
            createSubtourConstraints();
            if(model.solve()){
                System.out.println("=============================================================");
                System.out.println(model.getStatus());
                System.out.println(model.getObjValue());
                System.out.println("=============================================================");
                
                gui = new GUI(Data.xy);
                gui.repaint();
            }
            else {
               System.out.println(model.getStatus()); 
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Execution time: " + (endTime - startTime) + " milliseconds");
            
        } catch (IloException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
