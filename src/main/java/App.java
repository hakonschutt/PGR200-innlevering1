import maven.innlevering.*;
import maven.innlevering.database.DBValidation;
import maven.innlevering.database.DBConnect;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * App class is the applications main class. It is where the use is asked to choose from a string of options
 * Ths user can execute semester print, search and table print.
 */
public class App {
    private static DBConnect connect = new DBConnect();
    private static boolean quit = false;
    private static Scanner sc = new Scanner( System.in );
    private static boolean canCreateSemesterPlan;
    private static CreatePlan createPlan = new CreatePlan();

    /**
     * Main app method that calls all methods used to initiate the program
     */
    public static void main( String[] args ) throws Exception {

        boolean hasScanned = useOldConnection();

        if (!hasScanned){
            System.out.println("Set up a new connection!");
            DBValidation dbVal = new DBValidation();
            hasScanned = dbVal.main();
        }

        testConnection : try (Connection con = connect.getConnection()){
            if(con == null){
                System.out.println("Unsuccessful connection!");
                quit = true;
                break testConnection;
            }

            System.out.println( "Successful connection!" );
            if( !hasScanned ) {
                System.out.println("Starting input scanner");
                scanInputFiles();
            }
        } catch ( SQLException e ){
            System.out.println("Unsuccessful connection!");
            quit = true;
        }

        canCreateSemesterPlan = createPlan.validateTables();

        if(!quit) printInstructions();

        while( !quit ){
            quit = runApp();
        }

        System.out.println("Quiting program...");
    }

    private static boolean useOldConnection(){
        try (Connection con = connect.getConnection()){
            if(con == null){
                System.out.println("Unable to use old connection. Setup a new.");
                return false;
            }

            System.out.print("Do you want to continue with the old connection(yes/no): ");
            while(true){
                String ans = sc.nextLine().trim();
                if (ans.equals("yes") || ans.equals("no")){
                    return ans.equals("yes");
                } else {
                    System.out.print("Not a valid answer. Try again: ");
                }
            }
        } catch (SQLException e){
            System.out.println("Unable to use old connection. Setup a new.");
            return false;
        }
    }

    /**
     * Scans the input files from the input directory
     */
    private static void scanInputFiles(){
        Inputhandler rf = new Inputhandler();
        rf.startInputScan();
    }

    /**
     * Prints the possible instructions for the method runApp()
     */
    private static boolean printInstructions(){
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println(String.format("%-10s %-25s", "Command", "Instruction"));
        System.out.println("-------------------------------------------");
        System.out.println(String.format("%-10s %-25s", "intro", "Print instructions (This page)"));
        System.out.println(String.format("%-10s %-25s", "search", "Search for info"));
        System.out.println(String.format("%-10s %-25s", "print", "Print table"));

        if(canCreateSemesterPlan) System.out.println(String.format("%-10s %-25s", "create", "Create semester plan"));

        System.out.println(String.format("%-10s %-25s", "exit", "Quit program"));
        System.out.println("-------------------------------------------");

        return false;
    }

    /**
     * searhFiles lets the user search for entries in the database
     */
    private static boolean searchFiles() throws Exception {
        SearchFiles search = new SearchFiles();
        search.main();

        return false;
    }

    /**
     * printTable method is used to prompt the user with table options and print from the selected table
     */
    private static boolean printTable() throws Exception {
        PrintTables pt = new PrintTables();
        pt.main();

        return false;
    }

    /**
     * printPlan method is used to print the semester plan. It initiates the createPlan class
     */
    private static boolean printPlan() throws Exception {
        createPlan.main();

        return false;
    }

    /**
     * RunApp is the main method in this class. It directs the application to the correct
     * method based on the users input
     */
    private static boolean runApp() throws Exception {
        System.out.print("What command do you want to execute: ");
        String asw = sc.nextLine().trim();
        System.out.println();
        switch (asw) {
            case "intro":
                return printInstructions();
            case "search":
                return searchFiles();
            case "print":
                return printTable();
            case "create":
                if(canCreateSemesterPlan){
                    return printPlan();
                } else {
                    System.out.println("Not a valid command. Try again!");
                    return false;
                }
            case "exit":
                return true;
            default:
                System.out.println("Not a valid command. Try again!");
                return false;
        }
    }
}
