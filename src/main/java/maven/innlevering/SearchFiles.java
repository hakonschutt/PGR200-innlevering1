package maven.innlevering;

import maven.innlevering.database.DBConnect;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Created by hakonschutt on 26/09/2017.
 *
 * Search files class lets the user search for entries in the database
 * based on table and column
 */
public class SearchFiles {
    private DBConnect db = new DBConnect();
    private OutputHandler oh;
    private Scanner sc = new Scanner(System.in);

    /**
     * Initiate all necessary methods for the searchFiles class
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
            String colSql = oh.prepareTableDataQuery( tableName );

            String[] columns = getAllColumns(colSql, size);
            oh.printTables(columns);

            System.out.println("Which column do you want to search from?");
            int userColumn = oh.userChoice( size );

            String searchString = userSearchString(columns[userColumn - 1]);

            String newSql = prepareQuerySearch( tableName, columns[ userColumn - 1 ], columns );
            printTableContent(newSql, columns, searchString );
        } catch (Exception e){
            throw new Exception("Unable to search for content.");
        }

    }

    /**
     * Returns all columns in the selected table
     * @param sql
     * @param size
     * @return
     * @throws Exception
     */
    public String[] getAllColumns(String sql, int size) throws Exception {
        String[] tables = new String[ size ];

        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            int i = 0;
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No columns where found");
            }
            do {
                tables[i] = res.getString(1);
                i++;
            } while (res.next());
        } catch (SQLException e){
            throw new SQLException("Unable to query for tables.");
        }
        return tables;
    }

    /**
     * Lets the user enter a search String
     * @param columnName
     * @return
     */
    private String userSearchString(String columnName){
        System.out.print("Search in " + columnName + ": ");
        String searchString = sc.nextLine();
        System.out.println();

        return searchString;
    }

    /**
     * Prepares the searh query
     * @param tablename
     * @param column
     * @param columns
     * @return
     */
    private String prepareQuerySearch(String tablename, String column, String[] columns){
        String sql = "SELECT";

        for (int i = 0; i < columns.length; i++){
            sql += " " + columns[i];

            if(i < columns.length - 1){
                sql += ",";
            } else {
                sql += "";
            }
        }

        sql += " FROM " + tablename + " WHERE " + column + " LIKE ";

        return sql;
    }

    /**
     * Prints all content based on the user input
     * @param sql
     * @param columnName
     * @param searchString
     */
    private void printTableContent(String sql, String[] columnName, String searchString) throws Exception {
        for(int i = 0; i < columnName.length; i++){
            System.out.printf("%-20S", columnName[i]);
        }
        System.out.println();
        for(int i = 0; i < columnName.length; i++){
            System.out.printf("%-20S", "--------------------");
        }
        System.out.println();

        searchString = "%" + searchString + "%";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql + "?")) {
            ps.setString(1, searchString);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) {
                System.out.println("! no data was found !");
            }
            do {
                for(int i = 0; i < columnName.length; i++){
                    System.out.printf("%-20S", rs.getObject(columnName[i]));
                }
                System.out.println();
            } while (rs.next());
        } catch (SQLException e) {
            throw new SQLException("Unable to query for table content");
        }
        System.out.println();
    }
}
