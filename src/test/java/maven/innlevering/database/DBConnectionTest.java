package maven.innlevering.database;

import maven.innlevering.exception.CustomFileNotFoundException;
import maven.innlevering.exception.CustomSQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Make sure the property file is set before testing.gi
 * Created by hakonschutt on 16/11/2017.
 */
public class DBConnectionTest {
    private Connection con;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {}

    @Test (expected = CustomSQLException.class)
    public void verifyConnectionWithUserInputTest_ThrowSQLException() throws Exception {
        DBConnection connect = new DBConnection("hello", "hello", "notlocalhost", "westerdals");
        Connection con = connect.verifyConnectionWithUserInput(true);
    }

    @Test
    public void verifyConnectionWithUserInputTest() throws Exception {
        Properties properties = new Properties();
        InputStream input = new FileInputStream("data.properties");
        properties.load(input);

        String user = properties.getProperty("user");
        String pass = properties.getProperty("pass");
        String host = properties.getProperty("host");
        String db = properties.getProperty("db");

        assertNotNull(user);
        assertNotNull(pass);
        assertNotNull(host);
        assertNotNull(db);

        DBConnection connect = new DBConnection(user, pass, host, db);
        Connection con = connect.verifyConnectionWithUserInput(true);

        assertNotNull(con);
    }

    @Test
    public void getConnectionTest() throws Exception {
        DBConnection connect = new DBConnection();
        Connection con = connect.getConnection();

        assertNotNull(con);
    }

    @Test
    public void getConnectionTest_withoutProperties() throws Exception {
        assertTrue(new File("data.properties").renameTo(new File("temp.properties")));

        try {
            DBConnection connection = new DBConnection();
            Connection con = connection.getConnection();

        } catch (CustomFileNotFoundException e){
            assertEquals(e.getMessage(), "Unable to locate property file. Make sure its not deleted.");

            assertTrue(new File("temp.properties").renameTo(new File("data.properties")));
        }
    }
}
