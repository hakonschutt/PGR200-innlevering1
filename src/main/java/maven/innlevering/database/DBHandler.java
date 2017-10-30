package maven.innlevering.database;

import java.util.concurrent.TimeUnit;
import java.sql.*;
import maven.innlevering.OutputHandler;

/**
 * Class is used to controll database overwriting and creating.
 * Created by hakonschutt on 22/09/2017.
 */
public class DBHandler {

    /**
     * Deletes the database if the user wants to overwrite the current database
     * @param con
     * @param dbName
     */
    public void overWriteDatabase( Connection con, String dbName ){
        try ( Statement stmt = con.createStatement() ){
            stmt.executeUpdate("DROP DATABASE " + dbName +  "");
            TimeUnit.SECONDS.sleep(2 );

            createDataBase( con, dbName );
        } catch ( SQLException e ){
            throw new RuntimeException( e );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new database if the user has entered a new database name or want to overwrite the current database
     * @param con
     * @param newDbName
     */
    public void createDataBase( Connection con, String newDbName ){
        try (Statement stmt = con.createStatement()){
            stmt.executeUpdate("CREATE DATABASE " + newDbName +  "");
        } catch ( SQLException e ){
            throw new RuntimeException( e );
        }
    }

    /**
     * Method validates if tables used in semester planing is present in database
     * @return
     * @throws Exception
     */
    public boolean validateTables() throws Exception {
        OutputHandler oh = new OutputHandler();
        String[] tables = oh.getAlleTables();
        int total = checkForTables(tables);

        if(total == 4){
            return true;
        }

        return false;
    }

    /**
     * Method checks for 4 tables that are important to the semester planing.
     * @param tables
     * @return
     */
    private int checkForTables(String[] tables){
        int count = 0;

        for(int i = 0; i < tables.length; i++){
            if(tables[i].equals("field_of_study") || tables[i].equals("room") || tables[i].equals("subject") || tables[i].equals("teacher")){
                count++;
            }
        }

        return count;
    }

    /**
     * Validates if the database already exists.
     * @param con
     * @param databaseName
     * @return
     * @throws Exception
     */
    public boolean validateIfDBExists( Connection con, String databaseName ) throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet res =
                     stmt.executeQuery(
                     "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"
                             + databaseName + "'")){

            return res.next();

        } catch ( SQLException e ){
            throw new RuntimeException( e );
        }
    }
}
