package maven.innlevering.database;

import maven.innlevering.exception.CustomIOException;
import maven.innlevering.exception.CustomSQLException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Class validates the database. It checks if the database exists and asks the user for input if some conditions arnt met
 * The class also initiate the connection information
 * Created by hakonschutt on 29/09/2017.
 */
public class ValidateUserConnection {
    private DBValidationHandler handler = new DBValidationHandler();
    private DBConnection connect;
    private boolean hasScanned = false;
    private Scanner sc = new Scanner( System.in );

    /**
     * Main method calls the writeDbInfo until the connection is successful
     * @return
     */
    public boolean runDbValidation() {
        boolean works = false;
        while ( !works ){
            works = writeDbInfo();
            System.out.println();
        }

        try{
            writeProperties();
        } catch (CustomIOException e){
            System.out.println(e.getMessage());
        }

        return hasScanned;
    }

    /**
     * Method askes for user input for database connection
     * @return
     */
    private boolean writeDbInfo() {
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
     * @throws CustomIOException
     */
    private void writeProperties() throws CustomIOException {
        Properties properties = new Properties();
        try (OutputStream outputStream = new FileOutputStream("data.properties")) {
            properties.setProperty("user", connect.getUser());
            properties.setProperty("pass", connect.getPass());
            properties.setProperty("host", connect.getHost());
            properties.setProperty("db", connect.getDbName());

            properties.store(outputStream, null);
        } catch (IOException e){
            throw new CustomIOException(CustomIOException.getErrorMessage("writeProperties"));
        }
    }

    /**
     * Method tests the connection with the user input
     * @param dbInfo
     * @return
     */
    private boolean connectToDatabase( String[] dbInfo ) {
        connect = new DBConnection(dbInfo[0], dbInfo[1], dbInfo[2], dbInfo[3]);

        try {
            Connection con = connect.verifyConnectionWithUserInput(false );
            boolean dbExists = handler.validateIfDBExists(con, dbInfo[3]);

            if(!dbExists){

                try {
                    handler.createDataBase(con, dbInfo[3]);
                } catch (CustomSQLException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("Creating database: " + dbInfo[3]);

            } else {
                userInputForConnectionTest(con, dbInfo[3]);
            }
            return true;

        } catch (CustomSQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.print("Try again: ");
        return false;
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
    private void userInputForConnectionTest( Connection con, String dbName ) {
        userInputForConnectionInstruction();
        int asw = 0;

        try {
            asw = sc.nextInt();
        } catch (InputMismatchException e){
            System.out.println("The input is not av valid integer.");
        }

        switch(asw){
            case 1:
                System.out.print( "Connection to " + dbName );
                this.hasScanned = true;
                break;
            case 2:
                changeDatabaseName(con);
                break;
            case 3:
                try {
                    handler.overWriteDatabase( con, dbName );
                    System.out.print( "Overwriting database " + dbName );
                } catch (CustomSQLException e){
                    System.out.println(e.getMessage());
                }
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
    private void changeDatabaseName( Connection con ) {
        System.out.print( "Whats the new name:" );
        Scanner sc = new Scanner( System.in );
        String newDbName = sc.nextLine();
        System.out.println();

        boolean dbExists = false;
        try {
            dbExists = handler.validateIfDBExists( con, newDbName );
        } catch (CustomSQLException e){
            System.out.println(e.getMessage());
        }


        if ( !dbExists ) {
            try {
                handler.createDataBase( con, newDbName );
                System.out.print( "Creating database: " + newDbName );
            } catch (CustomSQLException e){
                System.out.println(e.getMessage());
            }
            connect.setDbName( newDbName );
        } else {
            userInputForConnectionTest( con, newDbName );
        }
    }
}
