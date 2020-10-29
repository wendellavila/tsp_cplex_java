package v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

public class Data {
    
    static int cities;
    static float xy[][];
    static double[][] distances = new double[cities][cities];
    
    public static void readInstance() throws IOException{
        System.out.println("Reading instance...");
        
        Path path = Paths.get("newInstance.tsp");
        Scanner scan = new Scanner(path);
        
        scan.useLocale(Locale.US);
        
        cities = scan.nextInt();
        xy = new float[cities][2];
        distances = new double[cities][cities];
        
        //read xy positions from file
        for(int i = 0; i < cities; i++){
            xy[i][0] = scan.nextFloat();
            xy[i][1] = scan.nextFloat();
        }
        
        scan.close();
        findDistances();
        
        System.out.println("Instance reading finished.");
    }
    
    public static void findDistances(){
        for(int i = 0; i < cities; i++){
            for(int j = 0; j < cities; j++){
                //euclidean distance
                distances[i][j] = (float) Math.sqrt(
                    (Math.pow((xy[i][0] - xy[j][0]),2) +
                    Math.pow((xy[i][1] - xy[j][1]),2))
                ); 
            }
        }
    }
    
    public static void printInstance(){
        for(int i = 0; i < cities; i++){
            for(int j = 0; j < cities; j++){
                System.out.print(distances[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
