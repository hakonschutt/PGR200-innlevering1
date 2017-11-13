package maven.innlevering.database;

import maven.innlevering.exception.ExceptionHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

/**
 * DBTableContentHandler is used to generate table data.
 * Is is used for ruleController, SearchContent and PrintTable.
 * Created by hakonschutt on 29/09/2017.
 */
public class DBTableContentHandler {
    private DBConnection db = new DBConnection();
    private String dbName;
    private Scanner sc = new Scanner(System.in);


    /**
     * Basic constructor that sets the database name.
     * @throws IOException
     */
    public DBTableContentHandler() throws IOException {
        setDbName();
    }

    /**
     * Returns the database name
     * @return
     */
    public String getDbName() { return dbName; }

    /**
     * Sets the database name based on property file
     */
    public void setDbName() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("data.properties")) {
            properties.load(input);

            this.dbName = properties.getProperty("db");
        }
    }

    /**
     * Prepares query to show tables from the current database
     * @return
     */
    private String prepareQuery(){
        return "SHOW TABLES FROM " + this.dbName;
    }

    /**
     * Method is used to return all tables in a String array format
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public String[] getAllTables() throws IOException, SQLException {
        String sql = prepareQuery();
        String[] tables = new String[getCount(getDBCountQuery())];

        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            int i = 0;
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables where found");
            }
            do {
                tables[i] = res.getString(1);
                i++;
            } while (res.next());
        }
        return tables;
    }

    /**
     * Method is used to get count of a query. It is used to set array sizes throughout the program
     * @param sql
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public int getCount(String sql) throws IOException, SQLException {
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables where found");
            }
            return res.getInt("total");
        }
    }

    /**
     * Returns a database count query that counts number of tables in the current database
     * @return
     */
    public String getDBCountQuery(){
        String sql = "SELECT COUNT(*) as total FROM information_schema.tables WHERE table_schema = '" + this.dbName + "'";

        return sql;
    }

    /**
     * Returns a table count query that counts number of columns in the current table
     * @param tableName
     * @return
     */
    public String getTableCountQuery(String tableName){
        String sql = "SELECT COUNT(*) as total " +
                "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'" +
                tableName + "' AND table_schema = '" + this.dbName + "'";

        return sql;
    }

    /**
     * Prints table so the user can choice which table to execute a command on
     * @param tables
     */
    public void printTables(String[] tables){
        for (int i = 0; i < tables.length; i++){
            System.out.println("(" + (i + 1) + ") " + tables[i]);
        }
    }

    /**
     * Validates if the users choice is within the scope of the size. If it evaluates to true it returns the users choice
     * @param size
     * @return
     */
    public int userChoice(int size) {
        boolean wrongAns = true;

        while(wrongAns){
            int asw = 0;

            try {
                asw = sc.nextInt();
            } catch (InputMismatchException e){
                ExceptionHandler.inputException("intMismatch");
                continue;
            }

            if (asw < size + 1 && asw > 0){
                return asw;
            } else {
                System.out.println("Not a valid response");
            }
        }
        return -1;
    }

    /**
     * Corrects the user difference. Array starts at 0, which is not optimal for the user.
     * The method takes the tables array and user choice, and returns the table the user choose
     * @param tables
     * @param userChoice
     * @return
     */
    public String prepareTable(String[] tables, int userChoice){
        return tables[userChoice - 1];
    }

    /**
     * method returns a query that can be used to get all columns in the current table
     * @param tableName
     * @return
     */
    public String prepareTableDataQuery( String tableName ) {
        String sql = "SELECT COLUMN_NAME " +
                "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'" +
                tableName + "' AND table_schema = '" + getDbName() + "'";

        return sql;
    }
}
