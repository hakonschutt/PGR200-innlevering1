package maven.innlevering;

import maven.innlevering.database.DBConnection;
import maven.innlevering.database.DBTableContentHandler;

import java.io.IOException;
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
public class SearchContent {
    private DBConnection database = new DBConnection();
    private DBTableContentHandler tableHandler;
    private Scanner sc = new Scanner(System.in);

    /**
     * Initiate all necessary methods for the searchFiles class
     * @throws IOException
     * @throws SQLException
     */
    public void main() throws IOException, SQLException {
        tableHandler = new DBTableContentHandler();
        String[] tables = tableHandler.getAllTables();
        tableHandler.printTables(tables);

        System.out.println("Which table do you want to search from?");
        int userInput = tableHandler.userChoice(tables.length);
        String tableName = tableHandler.prepareTable( tables, userInput );
        int size = tableHandler.getCount( tableHandler.getTableCountQuery( tableName ) );
        String colSql = tableHandler.prepareTableDataQuery( tableName );

        String[] columns = getAllColumns(colSql, size);
        tableHandler.printTables(columns);

        System.out.println("Which column do you want to search from?");
        int userColumn = tableHandler.userChoice( size );

        String searchString = userSearchString(columns[userColumn - 1]);

        String newSql = prepareQuerySearch( tableName, columns[ userColumn - 1 ], columns );
        printTableContent(newSql, columns, searchString );
    }

    /**
     * Returns all columns in the selected table
     * @param sql
     * @param size
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public String[] getAllColumns(String sql, int size) throws IOException, SQLException {
        String[] tables = new String[ size ];

        try (Connection con = database.getConnection();
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
     * @throws IOException
     * @throws SQLException
     */
    private void printTableContent(String sql, String[] columnName, String searchString) throws IOException, SQLException {
        for(int i = 0; i < columnName.length; i++){
            System.out.printf("%-20S", columnName[i]);
        }
        System.out.println();
        for(int i = 0; i < columnName.length; i++){
            System.out.printf("%-20S", "--------------------");
        }
        System.out.println();

        searchString = "%" + searchString + "%";

        try (Connection con = database.getConnection();
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
        }
        System.out.println();
    }
}
