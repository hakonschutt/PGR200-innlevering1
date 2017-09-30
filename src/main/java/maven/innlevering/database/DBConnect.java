package maven.innlevering.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by hakonschutt on 27/09/2017.
 */
public class DBConnect {
    private String user;
    private String pass;
    private String host;
    private String dbName;

    public DBConnect(){}

    public DBConnect(String user, String pass, String host, String dbName) {
        this.user = user;
        this.pass = pass;
        this.host = host;
        this.dbName = dbName;
    }

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

    public String getUser() { return user; }

    public String getPass() { return pass; }

    public String getHost() { return host; }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) { this.dbName = dbName; }
}
