package maven.innlevering;

import maven.innlevering.database.DBConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by hakonschutt on 02/10/2017.
 */
public class OutputHandlerTest {
    private Connection con;
    private TableContentHandler oh;
    private DBConnection connect;
    Connection connection;

    @Before
    public void setUp() throws Exception {
        oh = new TableContentHandler();
        connect = new DBConnection("root", "root", "localhost", "westerdal");
    }

    @After
    public void tearDown() throws Exception {}

    @Test
    public void checkConnection() throws Exception {
        Connection con = connect.verifyConnectionWithUserInput(true);

        assertNotNull(con);
    }

    @Test
    public void checkConnectionWithoutDatabase() throws Exception {
        Connection con = connect.verifyConnectionWithUserInput(false);

        assertNotNull(con);
    }

    @Test
    public void testGetAlleTables() throws Exception {
        String[] tables = oh.getAllTables();

        assertEquals(tables[0], "day_teacher_unavailability");
        assertEquals(tables[3], "room");
    }

    @Test
    public void testGetCount() throws Exception {
        String sql = "SELECT COUNT(*) as total FROM room";

        try {
            assertEquals(oh.getCount(sql), 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPrepareTable() throws Exception {
        String[] ent = {"test", "hello", "world", "!!"};
        int userchoice = 2;

        assertEquals(oh.prepareTable(ent, userchoice), "hello");
    }

    @Test
    public void testSetDB() throws Exception {
        oh.setDbName();

        assertEquals(oh.getDbName(), "westerdals");
    }
}
