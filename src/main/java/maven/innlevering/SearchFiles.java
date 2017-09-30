package maven.innlevering;

import maven.innlevering.database.DBConnect;

import java.sql.PreparedStatement;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by hakonschutt on 26/09/2017.
 */
public class SearchFiles {
    private DBConnect db = new DBConnect();
    private String dbName;
    private Scanner sc = new Scanner(System.in);

    public void main() throws Exception {
        System.out.println("back to this... searching...");
        setDatabaseName();
        String sql = prepareQuery();

        String[] tables = getAlleTables(sql);
        int userInput = userChoice();
        String tableName = prepareTable(tables, userInput);

        String colSql = prepateTableDataQuery(tableName);
        String[] columns = getAlleColumns(colSql, tableName);

        int userColumn = userColumnChoice(tableName);
        System.out.print("Search in " + columns[userColumn - 1] + ": ");
        sc.nextLine();
        String searchString = sc.nextLine();
        System.out.println();

        String newSql = prepareQuerySearch(tableName, columns[userColumn - 1], columns);
        printTableContent(newSql, columns, searchString);
    }

    public void setDatabaseName(){
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("data.properties")) {
            properties.load(input);

            this.dbName = properties.getProperty("db");
        } catch (Exception e){
            return;
        }
    }

    public String prepareQuery(){
        return "SHOW TABLES FROM " + this.dbName;
    }

    public int getCount(String sql) throws Exception{
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables");
            }
            return res.getInt("total");
        } catch (Exception e ){
            return -1;
        }
    }

    public String getDBCountQuery(){
        String sql = "SELECT COUNT(*) as total FROM information_schema.tables WHERE table_schema = '" + this.dbName + "'";

        return sql;
    }

    private String getTableCountQuery(String tableName){
        String sql = "SELECT COUNT(*) as total " +
                "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'" +
                tableName + "' AND table_schema = '" + this.dbName + "'";

        return sql;
    }

    private String[] getAlleTables(String sql) throws Exception{
        String[] tables = new String[getCount(getDBCountQuery())];

        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            int i = 0;
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables where found");
            }
            System.out.println("Which table do you want to search from?");
            do {
                tables[i] = res.getString(1);
                i++;
                System.out.println("(" + i + ") " + res.getString(1));
            } while (res.next());
        }
        return tables;
    }

    private int userChoice() throws Exception {
        boolean wrongAns = true;
        int size = getCount(getDBCountQuery());

        while(wrongAns){
            int asw = sc.nextInt();

            if (asw < size + 1 && asw > 0){
                return asw;
            } else {
                System.out.println("Not a valid response. Try again");
            }
        }
        return -1;
    }

    private String prepareTable(String[] tables, int userChoice){
        return tables[userChoice - 1];
    }

    private String prepateTableDataQuery(String tableName) throws Exception {
        String sql = "SELECT COLUMN_NAME " +
                "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'" +
                tableName + "' AND table_schema = '" + this.dbName + "'";

        return sql;
    }

    private String[] getAlleColumns(String sql, String tablename) throws Exception{
        String[] tables = new String[getCount(getTableCountQuery(tablename))];

        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            int i = 0;
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No columns where found");
            }
            System.out.println("Which column do you want to search from?");
            do {
                tables[i] = res.getString(1);
                i++;
                System.out.println("(" + i + ") " + res.getString(1));
            } while (res.next());
        }
        return tables;
    }

    private int userColumnChoice(String tablename) throws Exception {
        boolean wrongAns = true;
        int size = getCount(getTableCountQuery(tablename));
        while(wrongAns){
            int asw = sc.nextInt();

            if (asw < size + 1 && asw > 0){
                return asw;
            } else {
                System.out.println("Not a valid response. Try again!");
            }
        }
        return -1;
    }

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

    private void printTableContent(String sql, String[] columnName, String searchString){
        for(int i = 0; i < columnName.length; i++){
            System.out.printf("%-15S", columnName[i]);
        }
        System.out.println();
        for(int i = 0; i < columnName.length; i++){
            System.out.printf("%-15S", "---------------");
        }
        System.out.println();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql + "?")) {
            ps.setString(1, searchString);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) {
                System.out.println("! No data whore found !");
            }
            do {
                for(int i = 0; i < columnName.length; i++){
                    System.out.printf("%-15S", rs.getObject(columnName[i]));
                }
                System.out.println();
            } while (rs.next());
        } catch (SQLException e) {
            System.out.println();
        }
    }
}
