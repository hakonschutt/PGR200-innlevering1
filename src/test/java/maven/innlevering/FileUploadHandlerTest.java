package maven.innlevering;

import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.assertTrue;

/**
 * Created by hakonschutt on 16/11/2017.
 */
public class FileUploadHandlerTest {
    FileUploadHandler handler = new FileUploadHandler();

    @Test
    public void getAllFilesTest() throws Exception {
        String[] files = handler.getAllFiles();
        assertTrue(Arrays.asList(files).contains("field-of-study.txt"));
        assertTrue(Arrays.asList(files).contains("room.txt"));
        assertTrue(Arrays.asList(files).contains("subject.txt"));
        assertTrue(Arrays.asList(files).contains("teacher.txt"));
    }
}
