package maven.innlevering;

import maven.innlevering.database.DBSemesterPlanHandler;
import maven.innlevering.database.DBValidationHandler;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by hakonschutt on 16/11/2017.
 */
public class SemesterCreatorTest {
    final String LINE_SHIFT = System.getProperty("line.separator");

    @Test
    public void createSemesterPlanTest() throws Exception {
        DBSemesterPlanHandler handler = new DBSemesterPlanHandler();
        handler.dropSemesterTable();

        assertFalse(new DBValidationHandler().validateIfSemesterPlanExists());

        SemesterCreator creator = new SemesterCreator();
        ByteArrayInputStream in = new ByteArrayInputStream(("2" + LINE_SHIFT + "16" + LINE_SHIFT + "no").getBytes());
        System.setIn(in);
        creator.main();
        System.setIn(System.in);

        assertTrue(new DBValidationHandler().validateIfSemesterPlanExists());
    }
}
