package maven.innlevering.database;

import maven.innlevering.exception.CustomFileNotFoundException;
import maven.innlevering.exception.CustomIOException;
import maven.innlevering.exception.CustomSQLException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * Class is used to controll database overwriting and creating.
 * Created by hakonschutt on 22/09/2017.
 */
public class DBValidationHandler {

    /**
     * Deletes the database if the user wants to overwrite the current database
     * @param con
     * @param dbName
     * @throws CustomSQLException
     */
    public void overWriteDatabase( Connection con, String dbName ) throws CustomSQLException {
        try ( Statement stmt = con.createStatement() ){
            stmt.executeUpdate("DROP DATABASE " + dbName +  "");
            createDataBase( con, dbName );
        } catch (SQLException e){
            throw new CustomSQLException(CustomSQLException.getErrorMessage("overwriteDatabase"));
        }
    }

    /**
     * Creates a new database if the user has entered a new database name or want to overwrite the current database
     * @param con
     * @param newDbName
     * @throws CustomSQLException
     */
    public void createDataBase( Connection con, String newDbName ) throws CustomSQLException {
        try (Statement stmt = con.createStatement()){
            stmt.executeUpdate("CREATE DATABASE " + newDbName +  "");
        } catch (SQLException e){
            throw new CustomSQLException(CustomSQLException.getErrorMessage("createDatabase"));
        }
    }

    /**
     * Validates if the database already exists.
     * @param con
     * @param databaseName
     * @return
     * @throws CustomSQLException
     */
    public boolean validateIfDBExists( Connection con, String databaseName ) throws CustomSQLException {
        try (Statement stmt = con.createStatement();
             ResultSet res =
                     stmt.executeQuery(
                             "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"
                                     + databaseName + "'")){

            return res.next();
        } catch (SQLException e){
            throw new CustomSQLException(CustomSQLException.getErrorMessage("noValidation"));
        }
    }

    /**
     * Validates if the database contain a semester plan table.
     * @return
     * @throws CustomFileNotFoundException
     * @throws CustomIOException
     * @throws CustomSQLException
     */
    public boolean validateIfSemesterPlanExists() throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        DBTableContentHandler oh = new DBTableContentHandler();
        String[] tables = oh.getAllTables();

        for(int i = 0; i < tables.length; i++){
            if(tables[i].equals("semester_plan")){
                return true;
            }
        }

        return false;
    }

    /**
     * Method validates if tables used in semester planing is present in database
     * @return
     * @throws CustomFileNotFoundException
     * @throws CustomIOException
     * @throws CustomSQLException
     */
    public boolean validateTables() throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        DBTableContentHandler oh = new DBTableContentHandler();
        String[] tables = oh.getAllTables();
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
     * Fixes foreign keys on table so everything works after thread has run the upload.
     * @param fileName
     * @throws CustomFileNotFoundException
     * @throws CustomIOException
     * @throws CustomSQLException
     */
    public void fixForeignKeysForTable(String fileName) throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        String file = "input/" + fileName;
        
        try (BufferedReader in = new BufferedReader(new FileReader(file))){
            String table = in.readLine();

            for (int i = 0; i < 4; i++) in.readLine();

            String[] FK = in.readLine().split("/");

            if(!FK[0].equals("nofk")){
                String sql;

                sql = "ALTER TABLE `" + table + "` ";
                for(int i = 0; i < FK.length; i+=3){
                    sql += "ADD FOREIGN KEY (" + FK[i] + ") REFERENCES " + FK[i + 1] + "(" + FK[i + 2] + ")";
                    if(i == FK.length - 3){
                        sql += "";
                    } else {
                        sql += ", ";
                    }
                }

                executeUpdate(sql);
            }
        } catch (FileNotFoundException e){
            throw new CustomFileNotFoundException(CustomFileNotFoundException.getErrorMessage("fileNotFound"));
        } catch (IOException e){
            throw new CustomIOException(CustomIOException.getErrorMessage("readFile"));
        }
    }

    /**
     * Executes the alter that fixes the foreign key issue.
     * @param sql
     * @throws CustomFileNotFoundException
     * @throws CustomIOException
     * @throws CustomSQLException
     */
    public void executeUpdate(String sql) throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        DBConnection db = new DBConnection();
        try (Connection con = db.getConnection();
                Statement stmt = con.createStatement()){
             stmt.executeUpdate(sql);
        } catch (SQLException e){
            throw new CustomSQLException(CustomSQLException.getErrorMessage("executeUpdateFK"));
        }
    }
}
