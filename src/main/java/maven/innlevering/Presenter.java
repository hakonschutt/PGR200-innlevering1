package maven.innlevering;

import maven.innlevering.database.DBConnect;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The Presenter class is used to present the semester plan
 * Created by hakonschutt on 26/09/2017.
 */
public class Presenter {
    private Connection con;
    private DBConnect db = new DBConnect();

    /**
     * This constructor is called when semester planing is started. It presents the column names
     */
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

    /**
     * This constructor is used when data is set.
     * It presents the semester data for the current day, block and subject.
     * @param week
     * @param day
     * @param room
     * @param block
     * @param subject_id
     * @throws Exception
     */
    public Presenter(int week, int day, String room, int block, String subject_id) throws Exception {
        System.out.printf("%-8S", week);
        System.out.printf("%-8S", getDayName(day));
        System.out.printf("%-20S", getBlockTime(block));
        System.out.printf("%-10S", room);
        System.out.printf("%-15S", subject_id);
        System.out.printf("%-25S", getTeacherName(subject_id));
        System.out.println();
    }

    /**
     * GetTeacherName takes in a subject parameter and returns the teachers name
     * @param subject
     * @return
     * @throws Exception
     */
    private String getTeacherName(String subject) throws Exception {
        String sql= "SELECT t.name " +
                    "FROM teacher as t " +
                    "JOIN teacher_subject as ts " +
                    "ON ts.teacher_id = t.id " +
                    "WHERE ts.subject_id = '" + subject + "'";

        return exectueQuery(sql);
    }

    /**
     * General class for executing queries and return a string
     * @param sql
     * @return
     * @throws Exception
     */
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

    /**
     * Returns the day with the current day ID
     * @param day_id
     * @return
     */
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

    /**
     * Return the block in time format(String)
     * @param block
     * @return
     */
    private String getBlockTime(int block){
        if(block == 1){
            return "9:00 - 13:00";
        }

        return "13:00 - 17:00";
    }
}
