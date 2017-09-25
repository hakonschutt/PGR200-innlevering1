package maven.innlevering;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by hakonschutt on 25/09/2017.
 */
public class ReadFile {
    private Scanner sc;

    public ReadFile(){
        instructions();
    }

    private void instructions(){
        System.out.println("Which table should be read?");
        System.out.println("(1) Lectures");
        System.out.println("(2) Subjects");
        System.out.println("(3) Rooms");
        sc = new Scanner(System.in);
        int choice = sc.nextInt();
        checkFile(choice);
    }

    private void checkFile(int choice){
        switch(choice){
            case 1:
                readFile("test.txt");
                break;
            case 2:
                readFile("test.txt");
                break;
            case 3:
                readFile("test.txt");
                break;
            default:
                System.out.println("Not a valid choice.... ");
                System.out.println("Exiting. ");
                break;
        }
    }

    public void readFile(String file){
        try{
            String dir = "input/";
            BufferedReader in = new BufferedReader(new FileReader(dir + file));



            String s;
            while((s = in.readLine()) != null){
                String[] var = s.split("/");

                for(int i = 0; i < var.length; i++){
                    System.out.println(var[i]);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
