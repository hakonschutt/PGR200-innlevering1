package maven.innlevering.support;

import java.io.*;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

/**
 * Created by hakonschutt on 16/11/2017.
 */
public class PropertySupport {

    public static void setUpTempProperty() throws Exception {

        Properties propertiesOut = new Properties();
        InputStream input = new FileInputStream("data.properties");
        propertiesOut.load(input);

        Properties propertiesIn = new Properties();
        OutputStream outputStream = new FileOutputStream("new.properties");
        propertiesIn.setProperty("user", propertiesOut.getProperty("user"));
        propertiesIn.setProperty("pass", propertiesOut.getProperty("pass"));
        propertiesIn.setProperty("host", propertiesOut.getProperty("host"));
        propertiesIn.setProperty("db", "westerdalsTestSchHak");

        propertiesIn.store(outputStream, null);

        new File("data.properties").renameTo(new File("temp.properties"));
        new File("new.properties").renameTo(new File("data.properties"));
    }

    public static void resetProperty() throws Exception {
        File file = new File("data.properties");
        if(file.delete()){
            new File("temp.properties").renameTo(new File("data.properties"));
        }
    }

    public static String[] getPropertyEntries() throws Exception {
        String[] array = new String[4];

        InputStream input = new FileInputStream("data.properties");

        Properties propertiesOut = new Properties();
        propertiesOut.load(input);

        array[0] = propertiesOut.getProperty("user");
        array[1] = propertiesOut.getProperty("pass");
        array[2] = propertiesOut.getProperty("host");
        array[3] = propertiesOut.getProperty("db");

        return array;

    }
}
