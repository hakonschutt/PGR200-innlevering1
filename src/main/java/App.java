import maven.innlevering.*;
import maven.innlevering.database.DBConnect;
import maven.innlevering.database.DBValidation;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * App class is the applications main class. It is where the use is asked to choose from a string of options
 * Ths user can execute semester print, search and table print.
 */
public class App {
    private static DBConnect connect;
    private static boolean quit = false;
    private static boolean hasScanned;
    private static Scanner sc = new Scanner( System.in );

    /**
     * Main app method that calls all methods used to initiate the program
     */
    public static void main( String[] args ) throws Exception {
        hasScanned = useOldConnection();
        if (!hasScanned){
            DBValidation dbVal = new DBValidation();
            hasScanned = dbVal.main();
        }

        connect = new DBConnect();

        try (Connection con = connect.getConnection()){
            System.out.println( "Successful connection!" );
            TimeUnit.SECONDS.sleep(1);
        } catch ( SQLException e ){
            System.out.println("Unsuccessful connection!");
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
        }
    }

    private static boolean useOldConnection(){
        System.out.println("Do you want to continue with the old connection:");
        System.out.print("(1) Yes. (2) No.");
        int ans = sc.nextInt();
        if (ans == 1){
            return true;
        }
        return false;
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
        System.out.println("The following instructions are valid in this program: ");
        System.out.println("(1) Print instructions (This page).");
        System.out.println("(2) Search for info.");
        System.out.println("(3) Print table.");
        System.out.println("(4) Create semester plan.");
        System.out.println("(5) Quit.");

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
        CreatePlan cp = new CreatePlan();
        cp.main();

        return false;
    }

    /**
     * RunApp is the main method in this class. It directs the application to the correct
     * method based on the users input
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
