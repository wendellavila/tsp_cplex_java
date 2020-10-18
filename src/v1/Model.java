package v1;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model {
    
    public static IloCplex model;
    
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

    public static void main(String[] args) throws IOException {
        try {
            Data.readInstance();
            Data.printInstance();
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            long startTime = System.currentTimeMillis();
            model = new IloCplex();
            
            //time limit = 60 sec
            model.setParam(IloCplex.DoubleParam.TiLim, 60.0);
            
            //decision variables
            IloIntVar[][] s = new IloIntVar[Data.cities][Data.cities];
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
            
            
            
            //if there's a path going from city a to b,
            //there's also a path in the opposite direction, from b to a.
            //traveler can't go back to the city they just left.
            for(int i = 0; i < Data.cities; i++){
                for(int j = 0; j < Data.cities; j++){
                    //model.addEq(s[i][j], s[j][i]);
                    model.add(model.ifThen(model.eq(s[i][j], 1.0), model.eq(s[j][i], 0.0)));
                }
            }
            
            //exactly one edge going in for each city
            for(int i = 0; i < Data.cities; i++){
                IloLinearNumExpr edgeGoingInConstraint = model.linearNumExpr();
                for(int j = 0; j < Data.cities; j++){
                    edgeGoingInConstraint.addTerm(1.0, s[i][j]);
                }
                model.addEq(edgeGoingInConstraint, 1);
            }
            
            //exactly one edge going out for each city
            for(int j = 0; j < Data.cities; j++){
                IloLinearNumExpr edgeGoingOutConstraint = model.linearNumExpr();
                for(int i = 0; i < Data.cities; i++){
                    edgeGoingOutConstraint.addTerm(1.0, s[i][j]);
                }
                model.addEq(edgeGoingOutConstraint, 1);
            }
            
            if(model.solve()){
                System.out.println("=============================================================");
                System.out.println(model.getStatus());
                System.out.println(model.getObjValue());
                System.out.println("=============================================================");
                System.out.println("========================= Solution ==========================");
                for(int i = 0; i < Data.cities; i++){
                    for(int j = 0; j < Data.cities; j++){
                        System.out.print(Math.abs(model.getValue(s[i][j])) + "\t");
                    }
                    System.out.println();
                }
                printToFile(s);
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
