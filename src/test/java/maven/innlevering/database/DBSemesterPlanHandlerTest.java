package maven.innlevering.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * Created by hakonschutt on 16/11/2017.
 */
public class DBSemesterPlanHandlerTest {
    private DBSemesterPlanHandler handler = new DBSemesterPlanHandler();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private DBValidationHandler val = new DBValidationHandler();

    @Before
    public void setUp() throws Exception {
        handler.createTableForSemester();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() throws Exception {
        handler.dropSemesterTable();
        System.setOut(null);
    }

    @Test
    public void createAndDeleteSemesterTest() throws Exception {
        handler.dropSemesterTable();
        assertFalse(val.validateIfSemesterPlanExists());
        handler.createTableForSemester();
        assertTrue(val.validateIfSemesterPlanExists());
    }

    @Test
    public void uploadToTableTest() throws Exception {
        handler.uploadToTable(1, 1,"F101", 2, "PGR200");
        handler.presentAllSemesterData();
        assertTrue(outContent.toString().contains("F101"));
        assertTrue(outContent.toString().contains("PGR200"));
    }

    @Test
    public void getTeacherNameFromIDTest() throws Exception {
        String name = handler.getTeachNameFromID(1);
        assertEquals(name, "Per Lauvås");
    }

    @Test
    public void getTeacherIdBySubjectId() throws Exception {
        int id = handler.getTeacherIdBySubjectId("PGR200");
        assertEquals(id, 1);
    }
}
