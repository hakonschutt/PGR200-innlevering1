package maven.innlevering;

import maven.innlevering.database.DBConnection;
import maven.innlevering.database.DBTableContentHandler;
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
    private DBTableContentHandler oh;
    private DBConnection connect;

    @Before
    public void setUp() throws Exception {
        oh = new DBTableContentHandler();
        connect = new DBConnection("root", "root", "localhost", "westerdal");
    }

    @After
    public void tearDown() throws Exception {}

    @Test
    public void testGetAlleTables() throws Exception {
        String[] tables = oh.getAllTables();

        assertEquals(tables[0], "day_teacher_unavailability");
        assertEquals(tables[3], "room");
    }

    @Test
    public void testGetCount() throws Exception {
        String sql = "SELECT COUNT(*) as total FROM room";

        assertEquals(oh.getCount(sql), 4);
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
