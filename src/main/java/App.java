import maven.innlevering.Inputhandler;
import maven.innlevering.SearchFiles;
import maven.innlevering.database.DBConnect;
import maven.innlevering.database.DBHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 */
public class App {
    private static DBHandler handler = new DBHandler();
    private static DBConnect connect;
    private static Connection connection;
    private static Properties properties = new Properties();
    private static OutputStream outputStream;
    private static boolean quit = false;
    private static boolean hasScanned = false;
    private static Scanner sc = new Scanner(System.in);

    public static void main( String[] args ) throws Exception {
        outputStream = new FileOutputStream("data.properties");

        writeDBinfo();
        System.out.println();
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

    private static void writeDBinfo() throws Exception {
        String[] dbInfo = new String[4];
        System.out.print("DB user: ");
        dbInfo[0] = sc.nextLine();
        System.out.print("DB pass: ");
        dbInfo[1] = sc.nextLine();
        System.out.print("DB host: ");
        dbInfo[2] = sc.nextLine();
        System.out.print("DB name: ");
        dbInfo[3] = sc.nextLine();

        connectToDatabase(dbInfo);
    }

    private static void writeProperties() throws IOException {
        properties.setProperty("user", connect.getUser());
        properties.setProperty("pass", connect.getPass());
        properties.setProperty("host", connect.getHost());
        properties.setProperty("db", connect.getDbName());

        properties.store(outputStream, null);
    }

    private static void connectToDatabase ( String[] dbInfo ) throws Exception {
        connect = new DBConnect(dbInfo[0], dbInfo[1], dbInfo[2], dbInfo[3]);
        Connection con = connect.testConnection();

        boolean dbExists = handler.validateIfDBExists(con, dbInfo[3]);

        if(!dbExists){
            handler.createDataBase(con, dbInfo[3]);
            System.out.print("Creating database: " + dbInfo[3]);
            printLoader();
        } else {
            userInputForConnectionTest(con, dbInfo[3]);
        }
    }

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

    private static boolean printInstructions(){
        System.out.println("The following instructions are valid in this program: ");
        System.out.println("(1) Print instructions (This page).");
        System.out.println("(2) Scan input file to database.");
        System.out.println("(3) Search for info.");
        System.out.println("(4) Print semester plan.");
        System.out.println("(5) Quit.");

        return false;
    }

    private static boolean scanInputFiles(){
        if(hasScanned){
            System.out.println("File has already been scanned.");
        } else {
            Inputhandler rf = new Inputhandler();
            rf.instructions();
            hasScanned = true;
        }
        return false;
    }

    private static boolean printPlan(){
        if(!hasScanned){
            System.out.println("No input has been scanned. Execute scann followed by print...");
            hasScanned = true;
        } else {
            System.out.println("Printing plan..");
        }
        return false;
    }

    private static boolean searchFiles(){
        SearchFiles search = new SearchFiles();
        search.instructions();
        return false;
    }

    private static boolean runApp() throws Exception {
        int asw = sc.nextInt();
        switch(asw){
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
