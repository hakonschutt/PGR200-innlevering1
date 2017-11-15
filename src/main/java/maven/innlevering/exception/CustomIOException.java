package maven.innlevering.exception;

import java.io.IOException;

/**
 * Created by hakonschutt on 15/11/2017.
 */
public class CustomIOException extends IOException {

    public CustomIOException(String message) {
        super(message);
    }

    public static String getErrorMessage(String exceptionCode){
        switch(exceptionCode){
            case "readProperties":
                return "Unable to read from property file.";
            case "readFile":
                return "Unable to read from given the given file";
            default:
                return "Unknown IOException";
        }
    }
}
