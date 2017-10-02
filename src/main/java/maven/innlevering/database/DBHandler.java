package maven.innlevering.database;

import java.util.concurrent.TimeUnit;
import java.sql.*;

/**
 * Class is used to controll database overwriting and creating.
 * Created by hakonschutt on 22/09/2017.
 */
public class DBHandler {

    /**
     * Deletes the database if the user wants to overwrite the current database
     * @param con
     * @param dbName
     */
    public void overWriteDatabase( Connection con, String dbName ){
        try ( Statement stmt = con.createStatement() ){
            stmt.executeUpdate("DROP DATABASE " + dbName +  "");
            TimeUnit.SECONDS.sleep(2 );

            createDataBase( con, dbName );
        } catch ( SQLException e ){
            throw new RuntimeException( e );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new database if the user has entered a new database name or want to overwrite the current database
     * @param con
     * @param newDbName
     */
    public void createDataBase( Connection con, String newDbName ){
        try (Statement stmt = con.createStatement()){
            stmt.executeUpdate("CREATE DATABASE " + newDbName +  "");
        } catch ( SQLException e ){
            throw new RuntimeException( e );
        }
    }

    /**
     * Validates if the database already exists.
     * @param con
     * @param databaseName
     * @return
     * @throws Exception
     */
    public boolean validateIfDBExists( Connection con, String databaseName ) throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet res =
                     stmt.executeQuery(
                     "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"
                             + databaseName + "'")){

            return res.next();

        } catch ( SQLException e ){
            throw new RuntimeException( e );
        }
    }
}
