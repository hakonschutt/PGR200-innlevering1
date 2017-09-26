package maven.innlevering.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.*;

/**
 * Created by hakonschutt on 22/09/2017.
 */
public class DBHandler {
    private String user;
    private String pass;
    private String host;
    private String dbName;
    private boolean dbIsHandled = false;

    public DBHandler(String user, String pass, String host, String dbName) {
        this.user = user;
        this.pass = pass;
        this.host = host;
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setDbIsHandled(boolean dbIsHandled) {
        this.dbIsHandled = dbIsHandled;
    }

    public Connection getConnection() throws SQLException {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(this.host);
        ds.setUser(this.user);
        ds.setPassword(this.pass);

        if(!dbIsHandled){
            ds.setDatabaseName(getDbName());
        }

        Connection con = ds.getConnection();

        return con;
    }

    public boolean testConnection(){
        try {
            Connection tempCon = getConnection();
            return validateDB(tempCon);

        } catch (Exception e){
            return false;
        }
    }

    public void overWriteDatabase(){
        System.out.println("Overwriting database");
        try{
            Connection con = getConnection();

            Statement stmt = con.createStatement();
            int res = stmt.executeUpdate("DROP DATABASE " + getDbName() +  "");

            createDataBase();

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void createDataBase(){
        try{
            Connection con = getConnection();

            Statement stmt = con.createStatement();

            System.out.println("Creating database: " + getDbName() + "... ");

            int res = stmt.executeUpdate("CREATE DATABASE " + getDbName() +  "");

            setDbIsHandled(true);

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private boolean validateDB(Connection tempCon){
        try {
            Statement stmt = tempCon.createStatement();
            ResultSet res;
            res = stmt.executeQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + getDbName() + "'");

            if(!res.next()){
                createDataBase();
                return false;
            } else {
                System.out.println("DB exists...");
                return true;
            }

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
