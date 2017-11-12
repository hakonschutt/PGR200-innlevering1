package maven.innlevering.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
            createDataBase( con, dbName );
        } catch ( SQLException e ){
            System.out.println("Unable to drop database : " + dbName);
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
            System.out.println("Unable to create table : " + newDbName);
        }
    }

    /**
     * Validates if the database already exists.
     * @param con
     * @param databaseName
     * @return
     * @throws Exception
     */
    public boolean validateIfDBExists( Connection con, String databaseName ) {
        try (Statement stmt = con.createStatement();
             ResultSet res =
                     stmt.executeQuery(
                             "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"
                                     + databaseName + "'")){

            return res.next();

        } catch ( SQLException e ){
            System.out.println("Unable to validate database : " + databaseName);
            return false;
        }
    }

    /**
     * Validates if the database contain a semester plan table.
     * @return
     */
    public boolean validateIfSemesterPlanExists() {
        OutputHandler oh = new OutputHandler();
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
     * @throws Exception
     */
    public boolean validateTables() {
        OutputHandler oh = new OutputHandler();
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

    public void fixForeignKeysForTable(String fileName) {
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
        } catch (IOException e) {
            System.out.println("Unable to read from file : " + fileName);
        }
    }

    public void executeUpdate(String sql){
        DBConnect db = new DBConnect();
        try (Connection con = db.getConnection();
                Statement stmt = con.createStatement()){
             stmt.executeUpdate(sql);
        } catch ( SQLException e ){
            System.out.println("Unable to add foreign key");
        }
    }
}
