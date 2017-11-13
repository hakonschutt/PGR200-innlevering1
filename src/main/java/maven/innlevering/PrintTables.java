package maven.innlevering;

import maven.innlevering.database.DBConnect;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Print table class uses OutputHandler to get user information about which table to print.
 * The class is used to printing the content of a table in the database
 * Created by hakonschutt on 01/10/2017.
 */
public class PrintTables {
    private DBConnect db = new DBConnect();
    private OutputHandler oh;
    private Scanner sc = new Scanner(System.in);

    /**
     * Main method initiate neccassary output methods to receive user data
     * Using this data to start print method for this class
     * @throws Exception
     */
    public void main() throws Exception {
        try {
            oh = new OutputHandler();
            String[] tables = oh.getAllTables();
            oh.printTables(tables);

            System.out.println("Which table do you want to search from?");
            int userInput = oh.userChoice(tables.length);

            String tableName = oh.prepareTable( tables, userInput );
            int size = oh.getCount( oh.getTableCountQuery( tableName ) );
            String query = oh.prepareTableDataQuery( tableName );

            prepareTableQuery(tableName, query, size);
        } catch (Exception e){
            throw new Exception("Unable to print table content.");
        }
    }

    /**
     * Prepares the query to receive all table data
     * @param tableName
     * @param sql
     * @param size
     * @throws Exception
     */
    private void prepareTableQuery(String tableName, String sql, int size) throws Exception {
        String[] data = new String[ size ];
        String finalSQL = "SELECT";

        try (Connection con = db.getConnection();
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
        } catch (SQLException e ){
            throw new SQLException("Unable to execute table query");
        }

        finalSQL += " FROM " + tableName;

        printTableContent(finalSQL, data);
    }

    /**
     * Print the content of the table based on sql parsed over, and column names received from outputHandler
     * @param sql
     * @param columnName
     */
    private void printTableContent(String sql, String[] columnName) throws Exception {
        for(int i = 0; i < columnName.length; i++){
            System.out.printf("%-20S", columnName[i]);
        }
        System.out.println();
        for(int i = 0; i < columnName.length; i++){
            System.out.printf("%-20S", "--------------------");
        }
        System.out.println();

        try (Connection con = db.getConnection();
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
            throw new SQLException("Unable to print table content.");
        }
    }
}
