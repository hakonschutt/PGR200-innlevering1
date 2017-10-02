package maven.innlevering.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Main Connection class. All classes implements this classes getConnection.
 * Created by hakonschutt on 27/09/2017.
 */
public class DBConnect {
    private String user;
    private String pass;
    private String host;
    private String dbName;

    /**
     * Empty constructor if the user is not testing, and doesnt want to set
     * the table data but use the variables from property file
     */
    public DBConnect(){}

    /**
     * Second constructor that lets the user set custom user, pass, host and dbname.
     * Primarily used for testing.
     * @param user
     * @param pass
     * @param host
     * @param dbName
     */
    public DBConnect(String user, String pass, String host, String dbName) {
        this.user = user;
        this.pass = pass;
        this.host = host;
        this.dbName = dbName;
    }

    /**
     * Method can be called to test connection with or without database information
     * @param withDatabaseConnection
     * @return
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public Connection testConnection (boolean withDatabaseConnection) throws SQLException, FileNotFoundException {
        MysqlDataSource ds = new MysqlDataSource();

        if(withDatabaseConnection)
            ds.setDatabaseName(this.dbName);

        ds.setServerName(this.host);
        ds.setUser(this.user);
        ds.setPassword(this.pass);

        Connection connect = ds.getConnection();

        return connect;
    }

    /**
     * main getConnection class. Used throughout the program to get the database connection
     * @return
     */
    public Connection getConnection(){
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("data.properties")) {
            MysqlDataSource ds = new MysqlDataSource();
            properties.load(input);

            ds.setDatabaseName(properties.getProperty("db"));
            ds.setServerName(properties.getProperty("host"));
            ds.setUser(properties.getProperty("user"));
            ds.setPassword(properties.getProperty("pass"));

            Connection connect = ds.getConnection();

            return connect;

        } catch (Exception e){
            return null;
        }
    }

    /**
     * Returns username for database connection
     * @return
     */
    public String getUser() { return user; }

    /**
     * Returns password for database connection
     * @return
     */
    public String getPass() { return pass; }

    /**
     * Returns host name for database connection
     * @return
     */
    public String getHost() { return host; }

    /**
     * Returns database name within current connection
     * @return
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * Set the current database
     * This is only used in testing.
     * @param dbName
     */
    public void setDbName(String dbName) { this.dbName = dbName; }
}
