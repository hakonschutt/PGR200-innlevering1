import maven.innlevering.Inputhandler;
import maven.innlevering.SearchFiles;
import maven.innlevering.database.DBConnect;
import maven.innlevering.database.DBHandler;

import java.io.*;
import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * This is the client class
 */
public class App {
    private static DBHandler handler = new DBHandler();
    private static DBConnect connect;
    private static Connection connection;
    private static boolean quit = false;
    private static boolean hasScanned = false;
    private static Scanner sc = new Scanner(System.in);


    /*
     * Main app method that calls all methods used.
     */
    public static void main( String[] args ) throws Exception {
        boolean works = false;
        while (!works){
            works = writeDBinfo();
            System.out.println();
        }

        writeProperties();

        try (Connection connection = connect.getConnection()){
            System.out.println("Successful connection!");
            TimeUnit.SECONDS.sleep(1);
            System.out.println();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        printInstructions();

        while(!quit){
            System.out.println("What command do you want to execute?");
            quit = runApp();
        }
    }

    /*
     * User input for database connection
     */
    private static boolean writeDBinfo() throws Exception {
        String[] dbInfo = new String[4];
        System.out.print("DB user: ");
        dbInfo[0] = sc.nextLine();
        System.out.print("DB pass: ");
        dbInfo[1] = sc.nextLine();
        System.out.print("DB host: ");
        dbInfo[2] = sc.nextLine();
        System.out.print("DB name: ");
        dbInfo[3] = sc.nextLine();

        return connectToDatabase(dbInfo);
    }

    /*
     * Writing user input to properties file AFTER checking if the connection works with database
     */
    private static void writeProperties() throws IOException {
        Properties properties = new Properties();
        OutputStream outputStream = new FileOutputStream("data.properties");;

        properties.setProperty("user", connect.getUser());
        properties.setProperty("pass", connect.getPass());
        properties.setProperty("host", connect.getHost());
        properties.setProperty("db", connect.getDbName());

        properties.store(outputStream, null);
    }

    /*
     * Test connection with the user input
     */
    private static boolean connectToDatabase ( String[] dbInfo ) throws Exception {
        connect = new DBConnect(dbInfo[0], dbInfo[1], dbInfo[2], dbInfo[3]);

        try (Connection con = connect.testConnection(false )){
            boolean dbExists = handler.validateIfDBExists(con, dbInfo[3]);

            if(!dbExists){
                handler.createDataBase(con, dbInfo[3]);
                System.out.print("Creating database: " + dbInfo[3]);
                printLoader();
            } else {
                userInputForConnectionTest(con, dbInfo[3]);
            }

            return true;

        } catch (Exception e){
            System.out.println("Unable to connect with the current information");
            System.out.println("Try again: ");
            System.out.println();

            return false;
        }
    }

    /*
     * User input IF the database already exists
     */
    private static void userInputForConnectionTest(Connection con, String dbName) throws Exception {
        System.out.println("How would you like to continue?");
        System.out.println("(1) Continue with this connection");
        System.out.println("(2) Change to a new database name");
        System.out.println("(3) ! Overwrite the current database !");

        int asw = sc.nextInt();
        switch(asw){
            case 1:
                System.out.print("Connection to " + dbName);
                printLoader();
                hasScanned = true;
                break;
            case 2:
                changeDatabaseName(con);
                break;
            case 3:
                System.out.println("Overwriting database " + dbName);
                printLoader();
                handler.overWriteDatabase(con, dbName);
                break;
            default:
                System.out.println("Not a valid command.");
                System.out.print("Connection to " + dbName);
                printLoader();
                break;
        }
    }

    /*
     * Lets the user change the database name IF the database already exists
     */
    private static void changeDatabaseName(Connection con) throws Exception {
        System.out.println("Whats the new name:");
        Scanner sc = new Scanner(System.in);
        String newDbName = sc.nextLine();

        boolean dbExists = handler.validateIfDBExists(con, newDbName);

        if (!dbExists) {
            handler.createDataBase(con, newDbName);
            System.out.print("Creating database: " + newDbName);
            printLoader();
            connect.setDbName(newDbName);
        } else {
            userInputForConnectionTest(con, newDbName);
        }
    }

    /*
     * Prints the possible instructions for the method runApp()
     */
    private static boolean printInstructions(){
        System.out.println("The following instructions are valid in this program: ");
        System.out.println("(1) Print instructions (This page).");
        System.out.println("(2) Scan input file to database.");
        System.out.println("(3) Search for info.");
        System.out.println("(4) Print semester plan.");
        System.out.println("(5) Quit.");

        return false;
    }

    /*
     * Scans the input files from the input directory
     */
    private static boolean scanInputFiles(){
        // TODO: Implement scanInputFiles() FIRST!

        if(hasScanned){
            System.out.println("File has already been scanned.");
        } else {
            Inputhandler rf = new Inputhandler();
            rf.startInputScan();

            hasScanned = true;
        }
        return false;
    }

    /*
     * Print the optimal time schedule
     */
    private static boolean printPlan(){
        // TODO: Implement printPlan() LAST!

        if(!hasScanned){
            System.out.println("No input has been scanned. Execute scann followed by print...");
            hasScanned = true;
        } else {
            System.out.println("Printing plan..");
        }
        return false;
    }

    /*
     * Search method which calls a new instance of the class Search Files.
     */
    private static boolean searchFiles(){
        // TODO: Implement searchFiles() SECOND LAST!
        SearchFiles search = new SearchFiles();
        search.instructions();

        return false;
    }

    /*
     * RunApp is the main method. It runs until the user wants to quit
     */
    private static boolean runApp() throws Exception {
        int asw = sc.nextInt();
        switch (asw) {
            case 1:
                return printInstructions();
            case 2:
                return scanInputFiles();
            case 3:
                return searchFiles();
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

    /*
     * Print loader creates a processing in various parts of the program
     */
    private static void printLoader() throws InterruptedException {
        System.out.print(".");
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.print(".");
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.print(".");
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.print(".");
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.println();
    }
}
