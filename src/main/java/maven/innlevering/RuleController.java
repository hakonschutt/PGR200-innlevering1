package maven.innlevering;

import maven.innlevering.database.DBConnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//presenter = new Presenter(1, 2, "F101", 1, "PGR200");

/**
 * Created by hakonschutt on 26/09/2017.
 */
public class RuleController {
    private int startWeek = 1;
    private int endWeek = 1;
    private int currentWeek;
    private int currentDay;
    private DBConnect db = new DBConnect();
    private Connection con;
    private Presenter presenter;

    public void startSemesterPlan() throws Exception {
        System.out.println();
        presenter = new Presenter();

        createTotalColumn();
        currentWeek = startWeek;
        while(currentWeek <= endWeek){
            createInWeekColumn();
            int i = 1;
            while(i <= 5){
                currentDay = i;
                checkSingleDay(i);
                i++;
            }
            deleteInWeekColumn();
            currentWeek++;
        }

        deleteTotalColumn();
    }

    private void createTotalColumn(){
        executeUpdateQuery("ALTER TABLE subject ADD total int(2) DEFAULT 0 NOT NULL");
    }

    private void deleteTotalColumn(){
        executeUpdateQuery("ALTER TABLE subject DROP COLUMN total");
    }

    private void createInWeekColumn(){
        executeUpdateQuery("ALTER TABLE subject ADD isInWeek" + currentWeek + " int(1) DEFAULT 0 NOT NULL");
    }

    private void deleteInWeekColumn(){
        executeUpdateQuery("ALTER TABLE subject DROP COLUMN isInWeek" + currentWeek);
    }

    private void createFieldsForDay(){
        executeUpdateQuery("ALTER TABLE field_of_study ADD isOn" + currentDay + " int(1) DEFAULT 0 NOT NULL");
        executeUpdateQuery("ALTER TABLE teacher ADD isOn" + currentDay + " int(1) DEFAULT 0 NOT NULL");
    }

    private void deleteFieldForDay(){
        executeUpdateQuery("ALTER TABLE field_of_study DROP COLUMN isOn" + currentDay);
        executeUpdateQuery("ALTER TABLE teacher DROP COLUMN isOn" + currentDay);
    }

    private void executeUpdateQuery(String sql){
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            int res = stmt.executeUpdate(sql);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private void checkSingleDay(int day) throws Exception {
        String sql = getPossibleSubjectsQuery(day);
        HashMap<String, Integer> subjects = getItemInHashMap(sql);
        createFieldsForDay();

        // Every day is set at 2 blocks, morning or evening. J represents blocks
        for(int j = 0; j < 2; j++){
            String roomSql = getPossibleRooms();
            HashMap<String, Integer> rooms = getItemInHashMap(roomSql);

            checkIfLecturesCanOccure(rooms, subjects, j);
        }

        deleteFieldForDay();
    }

    private String getPossibleRooms(){
        String sql = "SELECT room_id, maks_kapasitet FROM room ORDER BY maks_kapasitet * 1 ASC";

        return sql;
    }

    private String getPossibleSubjectsQuery(int day){
        String sql= "SELECT distinct s.subject_id, s.number_of_attendees " +
                    "FROM day_teacher_unavailability as dtu " +
                    "INNER JOIN teacher as t " +
                        "ON t.id = dtu.teacher_id " +
                    "INNER JOIN teacher_subject as ts " +
                        "ON ts.teacher_id = t.id " +
                    "INNER JOIN subject as s " +
                        "ON s.subject_id = ts.subject_id " +
                    "WHERE s.total < 12 AND isInWeek" + currentWeek + " = 0 AND t.id NOT IN ( " +
                        "SELECT teacher_id FROM day_teacher_unavailability " +
                        "WHERE day_id = " + day + " )";

        return sql;
    }

    private HashMap getItemInHashMap(String sql){
        HashMap<String, Integer> hash = new HashMap<>();
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables where found");
            }
            do {
                hash.put(res.getString(1), res.getInt(2));
            } while (res.next());
        } catch (SQLException e){
            System.out.println("Not able to connect!");
        }

        return hash;
    }

    private void checkIfLecturesCanOccure(HashMap rooms, HashMap subjects, int currentblock) throws Exception {
        Iterator ro = rooms.entrySet().iterator();
        while (ro.hasNext()) {
            Map.Entry room = (Map.Entry)ro.next();
            int room_kap = (int) room.getValue();
            String room_name = (String) room.getKey();

            Iterator su = subjects.entrySet().iterator();
            while (su.hasNext()) {
                Map.Entry subject = (Map.Entry)su.next();
                int subject_ant = (int) subject.getValue();
                String subject_id = (String) subject.getKey();

                if(room_kap > subject_ant){
                    if(checkIfTeacherHasLecture(subject_id) && checkIfStudyHasLecture(subject_id)) {
                        updateFields();
                        Presenter pre = new Presenter(currentWeek, currentDay, room_name, currentblock, subject_id);
                    }
                }
            }
            ro.remove();
        }
    }

    private void updateFields(String subject){
        executeUpdateQuery("UPDATE subject SET total = total + 1 WHERE subject_id = '" + subject + "'");
        executeUpdateQuery("UPDATE teacher SET isON" + currentDay + " = 1 WHERE subject_id = '" + subject + "'");
        executeUpdateQuery();

    }

    private boolean checkIfTeacherHasLecture(String subject) throws SQLException {
        String sql= "SELECT COUNT(*) as total " +
                    "FROM teacher as t " +
                    "JOIN teacher_subject as ts " +
                    "ON ts.teacher_id = t.id " +
                    "WHERE ts.subject_id = '" + subject + "' AND t.isOn" + currentDay + " != 1";

        return (executeCountQuery(sql) > 0);
    }

    private boolean checkIfStudyHasLecture(String subject) throws SQLException {
        String sql= "SELECT COUNT(*) as total " +
                    "FROM field_of_study as fos " +
                    "JOIN study_subject as ss " +
                    "ON ss.study_id = fos.study_id " +
                    "WHERE ss.subject_id = '" + subject + "' AND fos.isOn" + currentDay + " != 1";

        return (executeCountQuery(sql) > 0);
    }

    private int executeCountQuery(String sql) throws SQLException {
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables where found");
            }
            return res.getInt("total");
        } catch (SQLException e ){
            throw new SQLException("Unable to connect with current connection");
        }
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }
}
