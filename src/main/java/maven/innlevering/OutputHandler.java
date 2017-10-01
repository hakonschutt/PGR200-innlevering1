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

    public OutputHandler() {
        setDbName();
    }

    public String getDbName() { return dbName; }

    private void setDbName() {
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

    public String[] getAlleTables() throws Exception{
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

    public int getCount(String sql) throws Exception{
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

    public String getDBCountQuery(){
        String sql = "SELECT COUNT(*) as total FROM information_schema.tables WHERE table_schema = '" + this.dbName + "'";

        return sql;
    }

    public String getTableCountQuery(String tableName){
        String sql = "SELECT COUNT(*) as total " +
                "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'" +
                tableName + "' AND table_schema = '" + this.dbName + "'";

        return sql;
    }

    public void printTables(String[] tables){
        for (int i = 0; i < tables.length; i++){
            System.out.println("(" + (i + 1) + ") " + tables[i]);
        }
    }

    public int userChoice(int size) throws Exception {
        boolean wrongAns = true;

        while(wrongAns){
            int asw = sc.nextInt();

            if (asw < size + 1 && asw > 0){
                return asw;
            } else {
                System.out.println("Not a valid response");
            }
        }
        return -1;
    }

    public String prepareTable(String[] tables, int userChoice){
        return tables[userChoice - 1];
    }

    public String prepareTableDataQuery( String tableName ) throws Exception {
        String sql = "SELECT COLUMN_NAME " +
                "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'" +
                tableName + "' AND table_schema = '" + getDbName() + "'";

        return sql;
    }
}
