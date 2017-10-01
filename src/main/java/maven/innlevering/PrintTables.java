package maven.innlevering;

import maven.innlevering.database.DBConnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Created by hakonschutt on 01/10/2017.
 */
public class PrintTables {
    private DBConnect db = new DBConnect();
    private OutputHandler oh;
    private Scanner sc = new Scanner(System.in);

    public void main() throws Exception {
        oh = new OutputHandler();
        String[] tables = oh.getAlleTables();
        oh.printTables(tables);

        System.out.println("Which table do you want to search from?");
        int userInput = oh.userChoice(tables.length);

        String tableName = oh.prepareTable( tables, userInput );
        int size = oh.getCount( oh.getTableCountQuery( tableName ) );
        String query = oh.prepareTableDataQuery( tableName );

        prepareTableQuery(tableName, query, size);
    }

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
        }

        finalSQL += " FROM " + tableName;

        printTableContent(finalSQL, data);
    }

    private void printTableContent(String sql, String[] columnName){
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
            e.printStackTrace();
        }
    }
}
