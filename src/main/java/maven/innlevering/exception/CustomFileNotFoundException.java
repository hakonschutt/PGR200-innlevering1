package maven.innlevering.exception;

import java.io.FileNotFoundException;

/**
 * Created by hakonschutt on 15/11/2017.
 */
public class CustomFileNotFoundException extends FileNotFoundException {

    public CustomFileNotFoundException(String message) {
        super(message);
    }

    public static String getErrorMessage(String exceptionCode){
        switch(exceptionCode){
            case "NoProperty":
                return "Unable to locate property file. Make sure its not deleted.";
            case "fileNotFound":
                return "Unable locate chosen file.";
            default:
                return "Unknown FileNotFoundException";
        }
    }
}
