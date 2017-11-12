import maven.innlevering.*;
import maven.innlevering.database.DBHandler;
import maven.innlevering.database.DBConnectValidation;
import maven.innlevering.database.DBConnect;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * App class is the applications main class. It is where the use is asked to choose from a string of options
 * Ths user can execute semester print, search and table print.
 */
public class App {
    private DBConnect connect = new DBConnect();
    private Scanner sc = new Scanner( System.in );
    private boolean canCreateSemesterPlan;

    public void start() throws Exception {
        boolean filesHaveBeenScanned = checkForPropertyFile() && useOldConnection();
        boolean quit = false;

        if (!filesHaveBeenScanned) filesHaveBeenScanned = new DBConnectValidation().main();

        try (Connection con = connect.getConnection()){
            if(con == null) throw new SQLException();

            System.out.println( "Successful connection!" );

            if( !filesHaveBeenScanned ) scanInputFiles();

            canCreateSemesterPlan = new DBHandler().validateTables();
        } catch ( SQLException e ){
            System.out.println("Unknown connection issue.");
            quit = true;
        }

        if(!quit) printInstructions();

        while( !quit ){
            try (Connection con = connect.getConnection()){
                quit = con == null || runApp();
            } catch (SQLException e){
                System.out.println("Lost connection to database.");
                break;
            }
        }

        System.out.println("Quiting program...");
    }

    private boolean checkForPropertyFile(){
        return new File("data.properties").exists();
    }

    private boolean useOldConnection(){
        try (Connection con = connect.getConnection()){
            if(con == null) throw new SQLException();

            System.out.print("Do you want to continue with the old connection(yes/no): ");
            while(true){
                String ans = sc.nextLine().trim();
                if (ans.equals("yes") || ans.equals("no")){
                    return ans.equals("yes");
                }
                System.out.print("Not a valid answer. Try again(yes/no): ");
            }
        } catch (SQLException e){
            System.out.println("Unable to use old connection. Setup a new.");
            return false;
        }
    }

    /**
     * Scans the input files from the input directory
     */
    private void scanInputFiles(){
        Inputhandler rf = new Inputhandler();
        rf.startInputScan();
    }

    /**
     * Prints the possible instructions for the method runApp()
     */
    private boolean printInstructions(){
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println(String.format("%-10s %-25s", "Command", "Instruction"));
        System.out.println("-------------------------------------------");
        System.out.println(String.format("%-10s %-25s", "intro", "Print instructions (This page)"));
        System.out.println(String.format("%-10s %-25s", "search", "Search for info"));
        System.out.println(String.format("%-10s %-25s", "print", "Print table"));
        if(canCreateSemesterPlan)
            System.out.println(String.format("%-10s %-25s", "create", "Create semester plan"));

        System.out.println(String.format("%-10s %-25s", "exit", "Quit program"));
        System.out.println("-------------------------------------------");

        return false;
    }

    /**
     * searhFiles lets the user search for entries in the database
     */
    private boolean searchFiles() throws Exception {
        SearchFiles search = new SearchFiles();
        search.main();

        return false;
    }

    /**
     * printTable method is used to prompt the user with table options and print from the selected table
     */
    private boolean printTable() throws Exception {
        PrintTables pt = new PrintTables();
        pt.main();

        return false;
    }

    /**
     * printPlan method is used to print the semester plan. It initiates the createPlan class
     */
    private boolean printPlan() throws Exception {
        CreatePlan createPlan = new CreatePlan();
        createPlan.main();
        return false;
    }

    /**
     * RunApp is the main method in this class. It directs the application to the correct
     * method based on the users input
     */
    private boolean runApp() throws Exception {
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
                if(canCreateSemesterPlan) return printPlan();
                else break;
            case "exit":
                return true;
            default:
                break;
        }

        System.out.println("Not a valid command. Try again!");
        return false;
    }

    /**
     * Main app method that calls all methods used to initiate the program
     */
    public static void main( String[] args ) throws Exception {
        new App().start();
    }
}
