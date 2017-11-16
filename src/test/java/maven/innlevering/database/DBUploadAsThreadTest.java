package maven.innlevering.database;

import maven.innlevering.support.PropertySupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by hakonschutt on 16/11/2017.
 */
public class DBUploadAsThreadTest {
    private DBUploadAsThread thread = new DBUploadAsThread();
    private DBValidationHandler val = new DBValidationHandler();
    private DBTableContentHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new DBTableContentHandler();
        PropertySupport.setUpTempProperty();

        String[] input = PropertySupport.getPropertyEntries();

        DBConnection tempCon = new DBConnection(input[0], input[1], input[2], input[3]);

        val.overWriteDatabase(tempCon.verifyConnectionWithUserInput(false), input[3]);
    }

    @After
    public void tearDown() throws Exception {
        val.executeUpdate("DROP DATABASE westerdalsTestSchHak");
        PropertySupport.resetProperty();
    }

    @Test
    public void createQueryTest() throws Exception {
        int amount = handler.getCount(handler.getDBCountQuery());
        assertEquals(0, amount);

        thread.createQuery("subject.txt");

        amount = handler.getCount(handler.getDBCountQuery());
        assertEquals(1, amount);

        int rows = handler.getCount("SELECT COUNT(*) as total FROM subject");
        assertEquals(0, rows);

        thread.insertQuery("subject.txt");

        rows = handler.getCount("SELECT COUNT(*) as total FROM subject");
        assertEquals(14, rows);
    }
}
