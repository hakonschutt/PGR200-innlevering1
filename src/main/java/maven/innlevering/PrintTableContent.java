package maven.innlevering;

import maven.innlevering.database.DBConnection;
import maven.innlevering.database.DBTableContentHandler;
import maven.innlevering.exception.CustomFileNotFoundException;
import maven.innlevering.exception.CustomIOException;
import maven.innlevering.exception.CustomSQLException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Print table class uses DBTableContentHandler to get user information about which table to print.
 * The class is used to printing the content of a table in the database
 * Created by hakonschutt on 01/10/2017.
 */
public class PrintTableContent {
    private DBConnection database = new DBConnection();
    private DBTableContentHandler tableHandler;

    /**
     * Main method initiate neccassary output methods to receive user data
     * Using this data to start print method for this class
     * @throws CustomFileNotFoundException
     * @throws CustomIOException
     * @throws CustomSQLException
     */
    public void main() throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        tableHandler = new DBTableContentHandler();
        String[] tables = tableHandler.getAllTables();
        tableHandler.printTables(tables);

        System.out.println("Which table do you want to search from?");
        int userInput = tableHandler.userChoice(tables.length);

        String tableName = tableHandler.prepareTable( tables, userInput );
        int size = tableHandler.getCount( tableHandler.getTableCountQuery( tableName ) );
        String query = tableHandler.prepareTableDataQuery( tableName );

        prepareTableQuery(tableName, query, size);
    }

    /**
     * Prepares the query to receive all table data
     * @param tableName
     * @param sql
     * @param size
     * @throws CustomFileNotFoundException
     * @throws CustomIOException
     * @throws CustomSQLException
     */
    private void prepareTableQuery(String tableName, String sql, int size) throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        String[] data = new String[ size ];
        String finalSQL = "SELECT";

        try (Connection con = database.getConnection();
             Statement stmt = con.createStatement()) {

            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No columns whore found");
            }

            int i = 0;

            do {
                data[i] = res.getString(1);
                finalSQL += " " + data[i];
                if(i < size - 1){
                    finalSQL += ",";
                }

                i++;
            } while (res.next());
        } catch (SQLException e){
            throw new CustomSQLException(CustomSQLException.getErrorMessage("prepareQuery"));
        }

        finalSQL += " FROM " + tableName;

        printTableContent(finalSQL, data);
    }

    /**
     * Print the content of the table based on sql parsed over, and column names received from outputHandler
     * @param sql
     * @param columnName
     * @throws CustomFileNotFoundException
     * @throws CustomIOException
     * @throws CustomSQLException
     */
    private void printTableContent(String sql, String[] columnName) throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        for(int i = 0; i < columnName.length; i++){
            System.out.printf("%-20S", columnName[i]);
        }
        System.out.println();
        for(int i = 0; i < columnName.length; i++){
            System.out.printf("%-20S", "--------------------");
        }
        System.out.println();

        try (Connection con = database.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables where found");
            }
            do {
                for(int i = 0; i < columnName.length; i++){
                    System.out.printf("%-20S", res.getObject(columnName[i]));
                }
                System.out.println();
            } while (res.next());
        } catch (SQLException e) {
            throw new CustomSQLException(CustomSQLException.getErrorMessage("printTable"));
        }
    }
}
