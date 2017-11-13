package maven.innlevering.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.Scanner;

/**
 * Class validates the database. It checks if the database exists and asks the user for input if some conditions arnt met
 * The class also initiate the connection information
 * Created by hakonschutt on 29/09/2017.
 */
public class DBConnectValidation {
    private DBHandler handler = new DBHandler();
    private DBConnect connect;
    private boolean hasScanned = false;
    private Scanner sc = new Scanner( System.in );

    /**
     * Main method calls the writeDBinfo until the connection is successful
     * @return
     * @throws Exception
     */
    public boolean main() throws IOException {
        boolean works = false;
        while ( !works ){
            works = writeDBinfo();
            System.out.println();
        }

        writeProperties();
        return hasScanned;
    }

    /**
     * Method askes for user input for database connection
     */
    private boolean writeDBinfo() {
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

    /**
     * Writing user input to property file AFTER checking if the connection works with database
     */
    private void writeProperties() throws IOException {
        Properties properties = new Properties();
        try (OutputStream outputStream = new FileOutputStream("data.properties")) {
            properties.setProperty("user", connect.getUser());
            properties.setProperty("pass", connect.getPass());
            properties.setProperty("host", connect.getHost());
            properties.setProperty("db", connect.getDbName());

            properties.store(outputStream, null);
        } catch (IOException e){
            throw new IOException("Unable to write to property file.");
        }
    }

    /**
     * Method tests the connection with the user input
     */
    private boolean connectToDatabase( String[] dbInfo ) {
        connect = new DBConnect(dbInfo[0], dbInfo[1], dbInfo[2], dbInfo[3]);

        try (Connection con = connect.testConnection(false )){
            boolean dbExists = handler.validateIfDBExists(con, dbInfo[3]);

            if(!dbExists){
                handler.createDataBase(con, dbInfo[3]);
                System.out.print("Creating database: " + dbInfo[3]);
            } else {
                userInputForConnectionTest(con, dbInfo[3]);
            }

            return true;

        } catch (Exception e){
            System.out.println("Unable to connect with the current information");
            System.out.print("Try again: ");

            return false;
        }
    }

    /**
     * Instruction to print if database exists
     */
    private void userInputForConnectionInstruction(){
        System.out.println("The database already exists!");
        System.out.println("How would you like to continue?");
        System.out.println("(1) Continue with this connection");
        System.out.println("(2) Change to a new database name");
        System.out.println("(3) !Overwrite the current database!");
    }

    /**
     * User input IF the database already exists
     */
    private void userInputForConnectionTest( Connection con, String dbName ) throws Exception {
        userInputForConnectionInstruction();
        int asw = sc.nextInt();
        switch(asw){
            case 1:
                System.out.print( "Connection to " + dbName );
                this.hasScanned = true;
                break;
            case 2:
                changeDatabaseName(con);
                break;
            case 3:
                System.out.print( "Overwriting database " + dbName );
                handler.overWriteDatabase( con, dbName );
                break;
            default:
                System.out.println( "Not a valid command." );
                System.out.print( "Connection to " + dbName );
                this.hasScanned = true;
                break;
        }
    }

    /**
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
            connect.setDbName( newDbName );
        } else {
            userInputForConnectionTest( con, newDbName );
        }
    }
}
