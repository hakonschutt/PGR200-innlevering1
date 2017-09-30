package maven.innlevering;

import maven.innlevering.database.DBConnect;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by hakonschutt on 29/09/2017.
 */
public class OutputHandler {
    private DBConnect db = new DBConnect();
    private String dbName;
    private Scanner sc = new Scanner(System.in);

    public void main() throws Exception {
        setDatabaseName();
        String sql = prepareQuery();
        String[] tables = getAlleTables(sql);
        int userInput = userChoice();
        String tableName = prepareTable(tables, userInput);
        prepateTableDataQuery( tableName );
    }

    private void setDatabaseName(){
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("data.properties")) {
            properties.load(input);

            this.dbName = properties.getProperty("db");
        } catch (Exception e){
            return;
        }
    }

    private String prepareQuery(){
        return "SHOW TABLES FROM " + this.dbName;
    }

    private int getCount(String sql) throws Exception{
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables where found");
            }
            return res.getInt("total");
        } catch (Exception e ){
            return -1;
        }
    }

    private String getDBCountQuery(){
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
            System.out.println("Which table do you want to print?");
            int asw = sc.nextInt();

            if (asw < size + 1 && asw > 0){
                return asw;
            } else {
                System.out.println("Not a valid response");
            }
        }
        return -1;
    }

    private String prepareTable(String[] tables, int userChoice){
        return tables[userChoice - 1];
    }

    private void prepateTableDataQuery(String tableName) throws Exception {
        String sql = "SELECT COLUMN_NAME " +
                "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'" +
                tableName + "' AND table_schema = '" + this.dbName + "'";

        prepareTableQuery(tableName, sql);
    }

    private void prepareTableQuery(String tableName, String sql) throws Exception {
        int size = getCount( getTableCountQuery( tableName ) );

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
