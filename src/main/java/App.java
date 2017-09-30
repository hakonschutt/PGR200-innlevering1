import maven.innlevering.Inputhandler;
import maven.innlevering.RuleController;
import maven.innlevering.SearchFiles;
import maven.innlevering.OutputHandler;
import maven.innlevering.database.DBConnect;
import maven.innlevering.database.DBValidation;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * This is the client class
 */
public class App {
    private static DBConnect connect;
    private static boolean quit = false;
    private static boolean hasScanned = false;
    private static Scanner sc = new Scanner( System.in );

    /*
     * Main app method that calls all methods used.
     */
    public static void main( String[] args ) throws Exception {
        printPlan();

        /*DBValidation dbVal = new DBValidation();
        hasScanned = dbVal.main();

        connect = new DBConnect();

        try (Connection con = connect.getConnection()){
            System.out.println( "Successful connection!" );
            TimeUnit.SECONDS.sleep(1);
        } catch ( SQLException e ){
            throw new RuntimeException(e);
        }

        if( !hasScanned ) {
            System.out.println("Starting input scanner");
            TimeUnit.SECONDS.sleep(1);
            scanInputFiles();
            TimeUnit.SECONDS.sleep(2);
        }

        System.out.println();

        printInstructions();
        while( !quit ){
            quit = runApp();
        }*/
    }

    /*
     * Scans the input files from the input directory
     */
    private static void scanInputFiles(){
        Inputhandler rf = new Inputhandler();
        rf.startInputScan();
    }

    /*
     * Prints the possible instructions for the method runApp()
     */
    private static boolean printInstructions(){
        System.out.println("The following instructions are valid in this program: ");
        System.out.println("(1) Print instructions (This page).");
        System.out.println("(2) Search for info.");
        System.out.println("(3) Print table.");
        System.out.println("(4) Create semester plan.");
        System.out.println("(5) Quit.");

        return false;
    }

    /*
     * Search method which calls a new instance of the class Search Files.
     */
    private static boolean searchFiles() throws Exception {
        SearchFiles search = new SearchFiles();
        search.main();

        return false;
    }

    /*
     * Prints all the content in the table
     */
    private static boolean printTable() throws Exception {
        OutputHandler out = new OutputHandler();
        out.main();

        return false;
    }

    /*
     * Print the optimal time schedule
     */
    private static boolean printPlan(){
        RuleController rc = new RuleController();
        rc.main();

        return false;
    }

    /*
     * RunApp is the main method. It runs until the user wants to quit
     */
    private static boolean runApp() throws Exception {
        System.out.println("What command do you want to execute?");
        int asw = sc.nextInt();
        switch (asw) {
            case 1:
                return printInstructions();
            case 2:
                return searchFiles();
            case 3:
                return printTable();
            case 4:
                return printPlan();
            case 5:
                System.out.println("Quiting program...");
                return true;
            default:
                System.out.println("Not a valid command. Try again!");
                return false;
        }
    }
}
