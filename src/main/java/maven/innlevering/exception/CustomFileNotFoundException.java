package maven.innlevering.exception;

import java.io.FileNotFoundException;

/**
 * Custom file not found exception class. Is used when the program is not able to locate a certain file.
 * Created by hakonschutt on 15/11/2017.
 */
public class CustomFileNotFoundException extends FileNotFoundException {

    /**
     * Constuctor for custom file not found exception class. Accepts an error message to throw.
     * @param message
     */
    public CustomFileNotFoundException(String message) {
        super(message);
    }

    /**
     * Switch method to return a error message based of the exception code.
     * @param exceptionCode
     * @return
     */
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
