package v1;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model {
    
    public static IloCplex model;

    public static void main(String[] args) {
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
            for(int i = 0; i < Data.cities; i++){
                for(int j = 0; j < Data.cities; j++){
                    model.addEq(s[i][j], s[j][i]);
                }
            }
            
            //at least two edges connecting each vertex
            for(int i = 0; i < Data.cities; i++){
                IloLinearNumExpr twoEdgesConstraint = model.linearNumExpr();
                for(int j = 0; j < Data.cities; j++){
                    twoEdgesConstraint.addTerm(1.0, s[i][j]);
                }
                model.addGe(twoEdgesConstraint, 2);
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
