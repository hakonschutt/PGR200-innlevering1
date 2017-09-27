package maven.innlevering.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.util.concurrent.TimeUnit;

import java.sql.*;

/**
 * Created by hakonschutt on 22/09/2017.
 */
public class DBHandler {

    public void overWriteDatabase(Connection con, String dbName){
        try (Statement stmt = con.createStatement()){
            stmt.executeUpdate("DROP DATABASE " + dbName +  "");
            TimeUnit.SECONDS.sleep(5);

            createDataBase(con, dbName);
        } catch (SQLException e){
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createDataBase(Connection con, String newDbName){
        try (Statement stmt = con.createStatement()){
            stmt.executeUpdate("CREATE DATABASE " + newDbName +  "");
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean validateIfDBExists(Connection con, String databaseName) throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet res =
                     stmt.executeQuery(
                     "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"
                             + databaseName + "'")){

            return res.next();

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
