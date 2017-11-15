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
            case "createTableQuery":
                return "Unable to create table for database";
            case "insertDataToTableQuery":
                return "Unable to insert data into table in database";
            case "queryTables":
                return "Unable to query for tables";
            case "executeUpdateFK":
                return "Unable to execute update of foreign keys";
            case "count":
                return "Unable to query for count to construct content";
            case "overwriteDatabase":
                return "Unable to overwrite the current database";
            case "createDatabase":
                return "Unable to create the given database";
            case "noValidation":
                return "Unable to validate if database exists";
            default:
                return "Unknown SQLException";
        }
    }
}
