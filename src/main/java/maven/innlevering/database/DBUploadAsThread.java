package maven.innlevering.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * Created by hakonschutt on 29/09/2017.
 */
public class DBUploadAsThread implements Runnable {
    private String file;
    private DBConnect db = new DBConnect();

    public DBUploadAsThread(String file) {
        this.file = file;
    }

    @Override
    public void run() {
        try {
            createQuery(file);
            insertQuery(file);
            System.out.println("Finished importing " + file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createQuery(String fileName) throws IOException {
        try{
            String file = "input/" + fileName;
            BufferedReader in = new BufferedReader(new FileReader(file));

            String table = in.readLine();
            String[] col = in.readLine().split("/");
            String[] dataType = in.readLine().split("/");
            String[] dataSize = in.readLine().split("/");
            String PK = in.readLine();

            String sql;

            sql = "CREATE TABLE `" + table + "` ( ";
            for(int i = 0; i < col.length; i++){
                sql += "`" + col[i] + "` " + dataType[i] + "(" + dataSize[i] + ") NOT NULL,";
            }
            sql += " PRIMARY KEY (`" + PK + "`))";

            executeCreate(sql);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void executeCreate(String sql){
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertQuery(String filename) throws IOException {
        String file = "input/" + filename;
        BufferedReader in = new BufferedReader(new FileReader(file));

        String db = in.readLine();
        String[] col = in.readLine().split("/");
        String[] dataType = in.readLine().split("/");
        String[] dataSize = in.readLine().split("/");
        String PK = in.readLine();

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
    }

    private void executeInsert(String sql, String[] var){
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < var.length; i++){
                ps.setObject(i + 1, var[i]);
            }
            int rs = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println();
        }
    }
}
