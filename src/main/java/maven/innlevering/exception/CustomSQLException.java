package maven.innlevering.exception;

import java.sql.SQLException;

/**
 * Created by hakonschutt on 15/11/2017.
 */
public class CustomSQLException extends SQLException {

    public CustomSQLException(String message) {
        super(message);
    }

    public static String getErrorMessage(String exceptionCode){
        switch(exceptionCode){
            case "createDatabase":
                return "Unable to create database.";
            case "overwriteDatabase":
                return "Unable to overwrite database.";
            case "wrongDBInformation":
                return "Unable to connect with the current information";
            case "noValidation":
                return "Unable to validate if database exists";
            case "outdatedConnection":
                return "Unable to use old connection!";
            case "upload":
                return "Unabel to upload to database.";
            case "search":
                return "Unable to search for content.";
            case "print":
                return "Unable to print content.";
            case "create":
                return "Unable to create semester plan.";
            case "semesterPrint":
                return "Unable to print semester plan.";
            default:
                return "Unknown sqlException exception";
        }
    }
}
