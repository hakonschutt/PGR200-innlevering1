package maven.innlevering.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by hakonschutt on 29/09/2017.
 */
public class DBValidation {
    private DBHandler handler = new DBHandler();
    private DBConnect connect;
    private Connection connection;
    private boolean hasScanned = false;
    private Scanner sc = new Scanner( System.in );

    public boolean main() throws Exception {
        boolean works = false;
        while ( !works ){
            works = writeDBinfo();
            System.out.println();
        }

        writeProperties();
        return hasScanned;
    }

    /*
     * User input for database connection
     */
    private boolean writeDBinfo() throws Exception {
        String[] dbInfo = new String[4];
        System.out.print( "DB user: " );
        dbInfo[0] = sc.nextLine();
        System.out.print( "DB pass: ") ;
        dbInfo[1] = sc.nextLine();
        System.out.print( "DB host: " );
        dbInfo[2] = sc.nextLine();
        System.out.print( "DB name: " );
        dbInfo[3] = sc.nextLine();

        return connectToDatabase( dbInfo );
    }

    /*
     * Writing user input to properties file AFTER checking if the connection works with database
     */
    private void writeProperties() throws IOException {
        Properties properties = new Properties();
        OutputStream outputStream = new FileOutputStream("data.properties" );;

        properties.setProperty("user", connect.getUser());
        properties.setProperty("pass", connect.getPass());
        properties.setProperty("host", connect.getHost());
        properties.setProperty("db", connect.getDbName());

        properties.store(outputStream, null);
    }

    /*
     * Test connection with the user input
     */
    private boolean connectToDatabase ( String[] dbInfo ) throws Exception {
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
     * instruction to print if database exists
     */
    private void userInputForConnectionInstruction(){
        System.out.println("How would you like to continue?");
        System.out.println("(1) Continue with this connection");
        System.out.println("(2) Change to a new database name");
        System.out.println("(3) ! Overwrite the current database !");
    }

    /*
     * User input IF the database already exists
     */
    private void userInputForConnectionTest( Connection con, String dbName ) throws Exception {
        userInputForConnectionInstruction();
        int asw = sc.nextInt();
        switch(asw){
            case 1:
                System.out.print( "Connection to " + dbName );
                printLoader();
                this.hasScanned = true;
                break;
            case 2:
                changeDatabaseName(con);
                break;
            case 3:
                System.out.println( "Overwriting database " + dbName );
                printLoader();
                handler.overWriteDatabase( con, dbName );
                break;
            default:
                System.out.println( "Not a valid command." );
                System.out.print( "Connection to " + dbName );
                printLoader();
                this.hasScanned = true;
                break;
        }
    }

    /*
     * Lets the user change the database name IF the database already exists
     */
    private void changeDatabaseName( Connection con ) throws Exception {
        System.out.print( "Whats the new name:" );
        Scanner sc = new Scanner( System.in );
        String newDbName = sc.nextLine();
        System.out.println();

        boolean dbExists = handler.validateIfDBExists( con, newDbName );

        if ( !dbExists ) {
            handler.createDataBase( con, newDbName );
            System.out.print( "Creating database: " + newDbName );
            printLoader();
            connect.setDbName( newDbName );
        } else {
            userInputForConnectionTest( con, newDbName );
        }
    }

    /*
     * Print loader creates a processing in various parts of the program
     */
    private void printLoader() throws InterruptedException {
        System.out.print( "." );
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.print( "." );
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.print( "." );
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.print( "." );
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.println();
    }
}
