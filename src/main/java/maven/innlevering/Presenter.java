package maven.innlevering;

import maven.innlevering.database.DBConnect;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by hakonschutt on 26/09/2017.
 */
public class Presenter {
    private Connection con;
    private DBConnect db = new DBConnect();

    public Presenter() {
        System.out.printf("%-8S", "Week");
        System.out.printf("%-8S", "Day");
        System.out.printf("%-20S", "Block");
        System.out.printf("%-10S", "Room");
        System.out.printf("%-15S", "Subject");
        System.out.printf("%-25S", "Teacher");
        System.out.println();
        System.out.printf("%-8S", "--------");
        System.out.printf("%-8S", "--------");
        System.out.printf("%-20S", "--------------------");
        System.out.printf("%-10S", "----------");
        System.out.printf("%-15S", "---------------");
        System.out.printf("%-25S", "-------------------------");
        System.out.println();
    }

    public Presenter(int week, int day, String room, int block, String subject_id) throws Exception {
        System.out.printf("%-8S", week);
        System.out.printf("%-8S", getDayName(day));
        System.out.printf("%-20S", getBlockTime(block));
        System.out.printf("%-10S", room);
        System.out.printf("%-15S", subject_id);
        System.out.printf("%-25S", getTeacherName(subject_id));
        System.out.println();
    }

    private String getTeacherName(String subject) throws Exception {
        String sql= "SELECT t.name " +
                    "FROM teacher as t " +
                    "JOIN teacher_subject as ts " +
                    "ON ts.teacher_id = t.id " +
                    "WHERE ts.subject_id = '" + subject + "'";

        return exectueQuery(sql);
    }

    private String exectueQuery(String sql) throws Exception{
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables where found");
            }
            do {
                return res.getString(1);
            } while (res.next());
        } catch (SQLException e){
            throw new SQLException("Unable to connect with current connection");
        }
    }

    private String getDayName(int day_id){
        switch(day_id){
            case 1:
                return "Mon";
            case 2:
                return "Tue";
            case 3:
                return "Wed";
            case 4:
                return "Thur";
            case 5:
                return "Fri";
            default:
                return "Weekend?";
        }
    }

    private String getBlockTime(int block){
        if(block == 1){
            return "9:00 - 13:00";
        }

        return "13:00 - 17:00";
    }
}
