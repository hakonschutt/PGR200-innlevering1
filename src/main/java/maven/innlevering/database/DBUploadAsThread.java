package maven.innlevering.database;

import maven.innlevering.exception.ExceptionHandler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * Class is ment to run as thread and therefor implements runnable
 * It uploads the text files to the database
 * Created by hakonschutt on 29/09/2017.
 */
public class DBUploadAsThread implements Runnable {
    private String file;
    private DBConnection db = new DBConnection();

    public DBUploadAsThread(String file) {
        this.file = file;
    }

    /**
     * Thread run job method. Calls all methods used by thread.
     */
    @Override
    public void run() {
        try {
            createQuery(file);
            insertQuery(file);
            System.out.println("Finished importing " + file);
        } catch (IOException | SQLException e){
            System.out.println("Unable to finish thread job for " + file);
        }
    }

    /**
     * Creates query to create table from the input file
     * @param fileName
     * @throws IOException
     * @throws SQLException
     */
    private void createQuery(String fileName) throws IOException, SQLException {
        String file = "input/" + fileName;
        try (BufferedReader in = new BufferedReader(new FileReader(file))){
            String table = in.readLine();
            String[] col = in.readLine().split("/");
            String[] dataType = in.readLine().split("/");
            String[] dataSize = in.readLine().split("/");
            String PK = in.readLine();
            in.readLine();

            String sql;

            sql = "CREATE TABLE `" + table + "` ( ";
            for(int i = 0; i < col.length; i++){
                sql += "`" + col[i] + "` " + dataType[i] + "(" + dataSize[i] + ") NOT NULL,";
            }
            sql += " PRIMARY KEY (`" + PK + "`))";

            executeCreate(sql);

        } catch (FileNotFoundException e){
            ExceptionHandler.fileException("fileNotFound");
        }
    }

    /**
     * General execute create query that is used to create tables
     * @param sql
     * @throws IOException
     * @throws SQLException
     */
    private void executeCreate(String sql) throws IOException, SQLException{
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    /**
     * Creates insert Query from the current file
     * @param filename
     * @throws IOException
     * @throws SQLException
     */
    private void insertQuery(String filename) throws IOException, SQLException {
        String file = "input/" + filename;
        try (BufferedReader in = new BufferedReader(new FileReader(file))){
            String db = in.readLine();
            String[] col = in.readLine().split("/");
            for(int i = 0; i < 4; i++)
                in.readLine();

            String sql;

            String s;
            while((s = in.readLine()) != null){
                sql = "INSERT INTO `" + db + "` ( ";
                for(int i = 0; i < col.length; i++){
                    if(i == col.length -1){
                        sql += "`" + col[i] + "`";
                    } else {
                        sql += "`" + col[i] + "`, ";
                    }
                }
                sql += ") VALUES ( ";

                String[] var = s.split("/");

                for(int i = 0; i < var.length; i++){
                    if(i == col.length -1){
                        sql += "? ";
                    } else {
                        sql += "?, ";
                    }
                }
                sql += ")";

                executeInsert(sql, var);
            }
        } catch (FileNotFoundException e) {
            ExceptionHandler.fileException("fileNotFound");
        }
    }

    /**
     * Executes insert query for table information.
     * @param sql
     * @param var
     * @throws IOException
     * @throws SQLException
     */
    private void executeInsert(String sql, String[] var) throws IOException, SQLException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < var.length; i++){
                ps.setObject(i + 1, var[i]);
            }
            ps.executeUpdate();
        }
    }
}
