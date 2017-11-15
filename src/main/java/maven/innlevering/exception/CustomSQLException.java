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
            case "userConnection":
                return "Unable to connect with the input values";
            case "connection":
                return "Unable to setup a connection";
            default:
                return "Unknown SQLException";
        }
    }
}
