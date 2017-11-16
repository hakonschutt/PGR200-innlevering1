package maven.innlevering.database;

import maven.innlevering.support.PropertySupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * Created by hakonschutt on 16/11/2017.
 */
public class DBValidationHandlerTest {
    DBValidationHandler val = new DBValidationHandler();
    DBConnection connect;

    @Before
    public void setUp() throws Exception {
        String[] array = PropertySupport.getPropertyEntries();
        connect = new DBConnection(array[0], array[1], array[2], array[3]);


    }

    @After
    public void tearDown() throws Exception {}

    @Test
    public void createDataBaseTest() throws Exception {
        boolean exists = val.validateIfDBExists(connect.verifyConnectionWithUserInput(false), "testForCreate");
        assertFalse(exists);
        val.createDataBase(connect.verifyConnectionWithUserInput(false), "testForCreate");
        exists = val.validateIfDBExists(connect.verifyConnectionWithUserInput(false), "testForCreate");
        assertTrue(exists);
        val.executeUpdate("DROP DATABASE testForCreate");
    }

    @Test
    public void overWriteDatabaseTest() throws Exception {
        val.createDataBase(connect.verifyConnectionWithUserInput(false), "testForOverwrite");
        boolean exists = val.validateIfDBExists(connect.verifyConnectionWithUserInput(false), "testForOverwrite");
        assertTrue(exists);
        val.overWriteDatabase(connect.verifyConnectionWithUserInput(false), "testForOverwrite");
        exists = val.validateIfDBExists(connect.verifyConnectionWithUserInput(false), "testForOverwrite");
        assertTrue(exists);
        val.executeUpdate("DROP DATABASE testForOverwrite");
    }

    @Test
    public void validateIfDBExistsTest() throws Exception {
        boolean exists = val.validateIfDBExists(connect.verifyConnectionWithUserInput(false), "testForExists");
        assertFalse(exists);
        val.createDataBase(connect.verifyConnectionWithUserInput(false), "testForExists");
        exists = val.validateIfDBExists(connect.verifyConnectionWithUserInput(false), "testForExists");
        assertTrue(exists);
        val.executeUpdate("DROP DATABASE testForExists");
        exists = val.validateIfDBExists(connect.verifyConnectionWithUserInput(false), "testForExists");
        assertFalse(exists);
    }
}
