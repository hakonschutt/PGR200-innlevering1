package maven.innlevering.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by hakonschutt on 22/09/2017.
 */
public class DBHandler {
    private String user;
    private String pass;
    private String host;
    private String dbName;

    public DBHandler(String user, String pass, String host, String dbName) {
        this.user = user;
        this.pass = pass;
        this.host = host;
        this.dbName = dbName;
    }

    public Connection getConnection() throws SQLException {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setDatabaseName(this.dbName);
        ds.setServerName(this.host);
        ds.setUser(this.user);
        ds.setPassword(this.pass);
        Connection con = ds.getConnection();

        //Connection con = validDB(con);

        return con;
    }

    /*private Connection validDB(Connection con){



    }*/


    /*public static void main(String[] args) {
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name FROM city WHERE id = ?");
            ps.setInt(1, 2807);
            try {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println("City name: " + rs.getString(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
}
