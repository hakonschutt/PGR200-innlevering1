package maven.innlevering.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by hakonschutt on 16/11/2017.
 */
public class DBTableContentHandlerTest {
    private DBTableContentHandler handler;
    private String currentDB;

    @Before
    public void setUp() throws Exception {
        Properties properties = new Properties();
        InputStream input = new FileInputStream("data.properties");
        properties.load(input);
        this.currentDB = properties.getProperty("db");
        handler = new DBTableContentHandler();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void setDbNameTest() throws Exception {
        // setDbName is initialized in the constructor.
        assertEquals(handler.getDbName(), currentDB);
    }

    @Test
    public void getAllTablesTest() throws Exception {
        String[] tables = handler.getAllTables();

        assertTrue(Arrays.asList(tables).contains("room"));
        assertTrue(Arrays.asList(tables).contains("field_of_study"));
        assertTrue(Arrays.asList(tables).contains("teacher"));
        assertTrue(Arrays.asList(tables).contains("subject"));
    }

    @Test
    public void getCountTest_columnSubject() throws Exception {
        int amount = handler.getCount(handler.getTableCountQuery("subject"));

        assertEquals(3, amount);
    }

    @Test
    public void getCountTest_DBtables() throws Exception {
        int amount = handler.getCount(handler.getDBCountQuery());
        assertEquals(amount, 8);
    }

    @Test
    public void prepareTableTest() {
        String[] array = new String[]{"hello", "test", "tables", "world"};

        assertEquals("test", handler.prepareTable(array, 2));
    }
}
