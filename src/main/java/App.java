import maven.innlevering.ReadFile;
import maven.innlevering.database.DBHandler;

import java.util.Scanner;

/**
 *
 */
public class App {
    private DBHandler con;
    private static boolean hasScanned = false;

    public static void main( String[] args ) {
        connectToDB();
        printInstructions();
        boolean quit = false;

        while(!quit){
            quit = runApp();
        }
    }

    private static void connectToDB(){
        System.out.println("Readying to connect to database....");
        Scanner sc = new Scanner(System.in);
        System.out.print("DB user: ");
        String dbUser = sc.nextLine();
        System.out.print("DB pass: ");
        String dbPass = sc.nextLine();
        System.out.print("DB host: ");
        String dbHost = sc.nextLine();

        System.out.println("Connection successful...");
        System.out.println("Prossessing to start program.");
    }

    private static void printInstructions(){
        System.out.println("The following instructions are valid in this program: ");
        System.out.println("(1) Print instructions (This page).");
        System.out.println("(2) Scan input file to database.");
        System.out.println("(3) Print semester plan.");
        System.out.println("(4) Quit.");
    }

    private static boolean runApp(){
        System.out.println("What command do you want to execute?");
        Scanner sc = new Scanner(System.in);
        int asw = sc.nextInt();
        switch(asw){
            case 1:
                printInstructions();
                return false;
            case 2:
                if(hasScanned){
                    System.out.println("File has already been scanned.");
                } else {
                    ReadFile rf = new ReadFile();
                    hasScanned = true;
                }
                return false;
            case 3:
                if(!hasScanned){
                    System.out.println("No input has been scanned. Executing scann followed by print...");
                    hasScanned = true;
                } else {
                    System.out.println("Printing plan..");
                }
                return false;
            case 4:
                System.out.println("Quiting program...");
                return true;
            default:
                System.out.println("Not a valid command. Try again!");
                return false;
        }
    }
}
