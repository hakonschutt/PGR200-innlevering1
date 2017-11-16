import maven.innlevering.*;
import maven.innlevering.database.DBValidationHandler;
import maven.innlevering.database.ValidateUserConnection;
import maven.innlevering.database.DBConnection;
import maven.innlevering.database.DBSemesterPlanHandler;
import maven.innlevering.exception.CustomFileNotFoundException;
import maven.innlevering.exception.CustomIOException;
import maven.innlevering.exception.CustomSQLException;
import maven.innlevering.exception.ExceptionHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * App class is the applications runDbValidation class. It is where the use is asked to choose from a string of options
 * Ths user can execute semester print, search and table print.
 */
public class App {
    private DBConnection connect = new DBConnection();
    private Scanner sc = new Scanner( System.in );
    private boolean canCreateSemesterPlan = false;
    private boolean hasCreatedSemesterPlan = false;

    /**
     * Initial app method that initiates the entire program.
     */
    private void start() {
        boolean filesHaveBeenScanned = false;
        try {
            filesHaveBeenScanned = checkForPropertyFile() && useOldConnection();
        } catch ( CustomFileNotFoundException | CustomIOException | CustomSQLException e ){
            ExceptionHandler.sqlException("outdatedConnection");
        }

        boolean quit = false;

        if (!filesHaveBeenScanned) filesHaveBeenScanned = new ValidateUserConnection().runDbValidation();

        try {
            Connection con = connect.getConnection();
            if(con == null) return;
            System.out.println( "Successful connection!" );

        } catch ( CustomFileNotFoundException | CustomIOException | CustomSQLException e ){
            System.out.println(e.getMessage());
            return;
        }

        if( !filesHaveBeenScanned ) uploadTextFilesToDatabase();

        try {
            DBValidationHandler handler = new DBValidationHandler();
            hasCreatedSemesterPlan = handler.validateIfSemesterPlanExists();
            canCreateSemesterPlan = handler.validateTables();
        } catch (CustomFileNotFoundException | CustomIOException | CustomSQLException e ){
            System.out.println(e.getMessage());
        }

        printInstructions();

        while( !quit ){
            quit = runApp();
        }
    }

    /**
     * Checks if property file exists in the root directory.
     * @return
     */
    private boolean checkForPropertyFile(){
        return new File("data.properties").exists();
    }

    /**
     * Checks if the program can run using the connection in the existing property file.
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private boolean useOldConnection() throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        try (Connection con = connect.getConnection()){
            if(con == null) return false;

            System.out.print("Do you want to continue with the old connection(yes/no): ");
            while(true){
                String ans = sc.nextLine().trim();
                if (ans.equals("yes") || ans.equals("no")){
                    return ans.equals("yes");
                }
                System.out.print("Not a valid answer. Try again(yes/no): ");
            }
        } catch (SQLException e){
            throw new CustomSQLException(CustomSQLException.getErrorMessage("connection"));
        }
    }

    /**
     * Scans the input files from the input directory
     */
    private void uploadTextFilesToDatabase() {
        try {
            FileUploadHandler rf = new FileUploadHandler();
            rf.startInputScan();
        } catch (CustomFileNotFoundException | CustomIOException | CustomSQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints the possible instructions for the method runApp()
     * @return
     */
    private boolean printInstructions() {
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println(String.format("%-10s %-25s", "Command", "Instruction"));
        System.out.println("-------------------------------------------");
        System.out.println(String.format("%-10s %-25s", "intro", "Print instructions (This page)"));
        System.out.println(String.format("%-10s %-25s", "search", "Search for info"));
        System.out.println(String.format("%-10s %-25s", "print", "Print table"));
        if(canCreateSemesterPlan)
            System.out.println(String.format("%-10s %-25s", "create", "Create semester plan"));
        if(hasCreatedSemesterPlan)
            System.out.println(String.format("%-10s %-25s", "semester", "Print current semester plan"));
        System.out.println(String.format("%-10s %-25s", "exit", "Quit program"));
        System.out.println("-------------------------------------------");

        return false;
    }

    /**
     * searhFiles lets the user search for entries in the database
     * @return
     */
    private boolean searchForContent() {
        try {
            new SearchContent().main();
        } catch (CustomFileNotFoundException | CustomIOException | CustomSQLException e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    /**
     * printTable method is used to prompt the user with table options and print from the selected table
     * @return
     */
    private boolean printTable() {
        try {
            new PrintTableContent().main();
        } catch (CustomFileNotFoundException | CustomIOException | CustomSQLException e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    /**
     * printSemesterPlan method is used to print the semester plan. It initiates the createSemesterPlan class
     * @return
     */
    private boolean createSemesterPlan() {
        try {
            new SemesterCreator().main();
            hasCreatedSemesterPlan = true;
        } catch (CustomFileNotFoundException | CustomIOException | CustomSQLException e){
            System.out.println(e.getMessage());
        }

        System.out.print("Do you want to print the plan? (yes/no) ");
        String ans = sc.nextLine().toLowerCase().replace(" ", "");

        return ans.equals("yes") && printSemesterPlan();
    }

    /**
     * Prints the semester plan currently on the database.
     * @return
     */
    private boolean printSemesterPlan() {
        try {
            new DBSemesterPlanHandler().presentAllSemesterData();
        } catch (CustomFileNotFoundException | CustomIOException | CustomSQLException e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    /**
     * RunApp is the runDbValidation method in this class. It directs the application to the correct
     * method based on the users input
     * @return
     */
    private boolean runApp() {
        System.out.print("What command do you want to execute: ");
        String asw = sc.nextLine().trim();
        System.out.println();
        switch (asw) {
            case "intro":
                return printInstructions();
            case "search":
                return searchForContent();
            case "print":
                return printTable();
            case "create":
                if(canCreateSemesterPlan) return createSemesterPlan();
                else break;
            case "semester":
                if(hasCreatedSemesterPlan) return printSemesterPlan();
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
    public static void main( String[] args ) {
        new App().start();
        System.out.println("Quiting program...");
    }
}
