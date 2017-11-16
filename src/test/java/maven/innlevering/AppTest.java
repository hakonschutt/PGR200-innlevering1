package maven.innlevering;

import maven.App;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * Created by hakonschutt on 16/11/2017.
 */
public class AppTest {
    final String LINE_SHIFT = System.getProperty("line.separator");

    @Test
    public void appPrintTableTest() {
        //ByteArrayInputStream in = new ByteArrayInputStream(("yes" + LINE_SHIFT + "print" + LINE_SHIFT + "3" + LINE_SHIFT + "exit").getBytes());
        new App().start();
        //System.setIn(in);
        //System.setIn(System.in);
    }
}
