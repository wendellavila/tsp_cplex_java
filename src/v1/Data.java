package v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Data {
    
    static int cities;
    static int[][] distances = new int[cities][cities];
    
    public static void readInstance() throws IOException{
        System.out.println("Reading instance...");
        
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Path path = Paths.get("brazil58.txt");
        Scanner scan = new Scanner(path);

        cities = scan.nextInt();
        distances = new int[cities][cities];

        //fill matrix with info from the file
        int temp = 1;
        for(int i = 0; i < cities; i++){
            for(int j = temp; j < cities; j++){
                distances[i][j] = scan.nextInt();
            }
            temp++;
        }

        //fill the rest of the matrix
        for(int i = 0; i < cities; i++){
            for(int j = 0; j < cities; j++){
                if(i == j){
                    distances[i][j] = 0;
                }
                else if (i > j){
                    distances[i][j] = distances[j][i];
                }
            }
        }
        
        scan.close();
        br.close();
        System.out.println("Instance reading finished.");
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
