package maven.innlevering.database;

/**
 * Created by hakonschutt on 22/09/2017.
 */
public class DBHandler {
    private String user;
    private String pass;
    private String host;

    public DBHandler(String user, String pass, String host) {
        this.user = user;
        this.pass = pass;
        this.host = host;
        DBConnect();
    }

    private boolean DBConnect(){
        return true;

    }
}
