package maven.innlevering.exception;

/**
 * Created by hakonschutt on 13/11/2017.
 */
public class ExceptionHandler {
    public static void sqlException(String exceptionCode){
        switch(exceptionCode){
            case "createDatabase":
                System.out.println("Unable to create database.");
                break;
            case "overwriteDatabase":
                System.out.println("Unable to overwrite database.");
                break;
            case "wrongDBInformation":
                System.out.println("Unable to connect with the current information");
                break;
            case "noValidation":
                System.out.println("Unable to validate if database exists");
                break;











            default:
                System.out.println("Unknown sqlException exception");
                break;
        }
    }

    public static void inputException(String exceptionCode){
        switch(exceptionCode){
            case "intMismatch":
                System.out.println("The input is not av valid integer.");
                break;


            default:
                System.out.println("Unknown sqlException exception");
                break;
        }
    }

    public static void interruptException(String exceptionCode){
        switch(exceptionCode){
            case "threadJoin":
                System.out.println("Unable to join threads from upload.");
                break;


            default:
                System.out.println("Unknown interrupted exception");
                break;
        }
    }

    public static void ioException(String exceptionCode){
        switch(exceptionCode){
            case "writeProperties":
                System.out.println("Unable to write to property file.");
                break;


            default:
                System.out.println("Unknown IO exception");
                break;
        }
    }

    public static void fileException(String exceptionCode){
        switch(exceptionCode){
            case "fileNotFound":
                System.out.println("Unable find chosen file.");
                break;
            case "notAbleToRead":
                System.out.println("Unable to read from given file.");
                break;
            default:
                System.out.println("Unknown File exception");
                break;
        }
    }
}
