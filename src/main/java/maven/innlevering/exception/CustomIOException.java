package maven.innlevering.exception;

import java.io.IOException;

/**
 * Custom IO exception class is used to create custom Exception for reading and writing to files. (property)
 * Created by hakonschutt on 15/11/2017.
 */
public class CustomIOException extends IOException {

    /**
     * Constructor for custom IO exception. Accepts a custom error message.
     * @param message
     */
    public CustomIOException(String message) {
        super(message);
    }

    /**
     * Switch case to return the error message based of the exception code.
     * @param exceptionCode
     * @return
     */
    public static String getErrorMessage(String exceptionCode){
        switch(exceptionCode){
            case "readProperties":
                return "Unable to read from property file.";
            case "readFile":
                return "Unable to read from given the given file";
            case "writeProperties":
                return "Unable to write to property file";
            default:
                return "Unknown IOException";
        }
    }
}
