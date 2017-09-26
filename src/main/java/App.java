import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import maven.innlevering.Inputhandler;
import maven.innlevering.SearchFiles;
import maven.innlevering.database.DBHandler;

import java.sql.Connection;

import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 */
public class App {
    private DBHandler DBconnection;
    private Connection con;
    private boolean hasScanned = false;
    private Scanner sc = new Scanner(System.in);

    public static void main( String[] args ) throws SQLException {
        App app = new App();

        app.writeDBinfo();

        try {
            app.DBconnection.getConnection();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        app.printInstructions();

        boolean quit = false;
        while(!quit){
            System.out.println("What command do you want to execute?");
            quit = app.runApp();
        }
    }

    private void writeDBinfo() throws SQLException {
        System.out.println("Readying to connect to database....");
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

    private void connectToDatabase(String[] dbInfo) throws SQLException {
        DBconnection = new DBHandler(dbInfo[0], dbInfo[1], dbInfo[2], dbInfo[3]);
        boolean dbExisist = DBconnection.testConnection();

        if(dbExisist){
            userInputForConnectionTest();
        }
    }

    private void userInputForConnectionTest(){
        System.out.println("How would you like to continue?");
        System.out.println("(1) Continue with this connection");
        System.out.println("(2) Change database name");
        System.out.println("(3) !! Overwrite the current database !!");

        int asw = sc.nextInt();
        switch(asw){
            case 1:
                System.out.println("Connection to " + DBconnection.getDbName() + "....");
                DBconnection.setDbIsHandled(true);
                hasScanned = true;
                break;
            case 2:
                changeDBName();
                break;
            case 3:
                DBconnection.overWriteDatabase();
                break;
            default:
                System.out.println("Not a valid command. Continuing with current connection...");
                break;
        }
    }

    private void changeDBName(){
        System.out.println("Whats the new name:");
        Scanner sc = new Scanner(System.in);
        String newDbName = sc.nextLine();

        DBconnection.setDbName(newDbName);

        boolean dbExisist = DBconnection.testConnection();

        if(dbExisist){
            userInputForConnectionTest();
        }

        DBconnection.createDataBase();
    }

    // APP methods //

    private boolean printInstructions(){
        System.out.println("The following instructions are valid in this program: ");
        System.out.println("(1) Print instructions (This page).");
        System.out.println("(2) Scan input file to database.");
        System.out.println("(3) Search for info.");
        System.out.println("(4) Print semester plan.");
        System.out.println("(5) Quit.");

        return false;
    }

    private boolean scanInputFiles(){
        if(hasScanned){
            System.out.println("File has already been scanned.");
        } else {
            Inputhandler rf = new Inputhandler();
            rf.instructions();
            hasScanned = true;
        }
        return false;
    }

    private boolean printPlan(){
        if(!hasScanned){
            System.out.println("No input has been scanned. Execute scann followed by print...");
            hasScanned = true;
        } else {
            System.out.println("Printing plan..");
        }

        return false;
    }

    private boolean searchFiles(){
        SearchFiles search = new SearchFiles();
        search.instructions();
        return false;
    }

    private boolean runApp(){
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
}
