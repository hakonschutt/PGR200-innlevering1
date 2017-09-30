package maven.innlevering;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import maven.innlevering.database.DBConnect;
import maven.innlevering.database.DBHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.*;
import org.junit.Assert.*;

import java.sql.*;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by hakonschutt on 30/09/2017.
 */
public class SearchFilesTest {
    private DBConnect db;
    private Connection con;
    private SearchFiles sf;

    @Before
    public void setUp() throws Exception {
        db = new DBConnect();
        sf = new SearchFiles();
    }

    @After
    public void tearDown() throws Exception {
        con.close();
    }

    @Test
    public void getTablesCount() throws Exception {
        // Arrange
        sf.setDatabaseName();

        // Act
        String sql = sf.getDBCountQuery();
        int count = sf.getCount(sql);

        // Assert
        assertTrue(count == 8);
    }



    /*private void updateOslo(String newName) {
        try (Connection con = getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate("UPDATE city SET name = '" + newName + "' WHERE id = " + ID_OSLO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSensitivity() {
        try (Connection con = getConnection();
             Statement firstStatement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                     ResultSet.CONCUR_UPDATABLE)) {
            DatabaseMetaData meta = con.getMetaData();
            boolean res = meta.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
            assertTrue(res);
            ResultSet result1 = firstStatement.executeQuery("SELECT name FROM city WHERE id = " + ID_OSLO);
            updateOslo("jalla");
            result1.next();
            assertEquals(result1.getString(1), "jalla");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadOnly() {
        try (Connection con = getConnection();
             Statement firstStatement = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                     ResultSet.CONCUR_READ_ONLY)) {
            DatabaseMetaData meta = con.getMetaData();
            boolean res = meta.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            assertTrue(res);
            ResultSet result1 = firstStatement.executeQuery("SELECT name FROM city WHERE id = " + ID_OSLO);
            result1.next();
            result1.updateString(1, "jalla");
            fail("update should not be accepted in read only mode");
        } catch (Exception e) {
        }
    }

    private Connection getConnection() throws SQLException {
        return db.getConnection();
    }*/
}
