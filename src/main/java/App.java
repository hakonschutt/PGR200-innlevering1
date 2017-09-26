import maven.innlevering.ReadFile;
import maven.innlevering.SearchFiles;
import maven.innlevering.database.DBHandler;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 */
public class App {
    private static DBHandler DBconnection;
    private static Connection con;
    private static boolean hasScanned = false;

    public static void main( String[] args ) {
        connectToDB();
        printInstructions();

        boolean quit = false;
        while(!quit){
            System.out.println("What command do you want to execute?");
            quit = runApp();
        }
    }

    public void connectToDB(){
        System.out.println("Readying to connect to database....");
        Scanner sc = new Scanner(System.in);
        System.out.print("DB user: ");
        String dbUser = sc.nextLine();
        System.out.print("DB pass: ");
        String dbPass = sc.nextLine();
        System.out.print("DB host: ");
        String dbHost = sc.nextLine();
        System.out.print("DB name: ");
        String dbName = sc.nextLine();

        DBconnection = new DBHandler(dbUser, dbPass, dbHost, dbName);
        con = DBconnection.getConnection();


        System.out.println("Connection successful...");
        System.out.println("Prossessing to start program.");
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

    private static void testConnection(){
        try {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM city WHERE id = ?");
            ps.setInt(1, 2807);
            try {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println("City name: " + rs.getString(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private static boolean scanInputFiles(){
        if(hasScanned){
            System.out.println("File has already been scanned.");
        } else {
            ReadFile rf = new ReadFile();
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

    private static boolean runApp(){
        Scanner sc = new Scanner(System.in);
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
            case 6:
                testConnection();
            default:
                System.out.println("Not a valid command. Try again!");
                return false;
        }
    }
}
