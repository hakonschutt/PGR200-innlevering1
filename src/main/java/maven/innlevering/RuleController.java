package maven.innlevering;

import maven.innlevering.database.DBConnect;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * RuleController is the main semesterPrinter class. It keeps track of all
 * the logic for the semester planer
 *
 * Created by hakonschutt on 26/09/2017.
 */
public class RuleController {
    private int startWeek;
    private int endWeek;
    private int currentWeek;
    private int currentDay;
    private DBConnect db = new DBConnect();
    private Connection con;
    private Presenter presenter;

    /**
     * Initiate the semester planing
     * @throws Exception
     */
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

    /**
     * Creates a total column on subject table
     */
    private void createTotalColumn(){
        executeUpdateQuery("ALTER TABLE subject ADD total int(2) DEFAULT 0 NOT NULL");
    }

    /**
     * Deletes total column from subject table
     */
    private void deleteTotalColumn(){
        executeUpdateQuery("ALTER TABLE subject DROP COLUMN total");
    }

    /**
     * Creates isInWeek column on subject table.
     * This is used to filter out subjects that has allready
     * occurred this week.
     */
    private void createInWeekColumn(){
        executeUpdateQuery("ALTER TABLE subject ADD isInWeek" + currentWeek + " int(1) DEFAULT 0 NOT NULL");
    }

    /**
     * Deletes the isInWeek column when the week is over
     */
    private void deleteInWeekColumn(){
        executeUpdateQuery("ALTER TABLE subject DROP COLUMN isInWeek" + currentWeek);
    }

    /**
     * Creates columns in field_of_study and teacher to make sure study and teachers dont have two lecture on the same day
     */
    private void createFieldsForDay(){
        executeUpdateQuery("ALTER TABLE field_of_study ADD isOn" + currentDay + " int(1) DEFAULT 0 NOT NULL");
        executeUpdateQuery("ALTER TABLE teacher ADD isOn" + currentDay + " int(1) DEFAULT 0 NOT NULL");
    }

    /**
     * Deletes columns isOn"day" after day is over
     */
    private void deleteFieldForDay(){
        executeUpdateQuery("ALTER TABLE field_of_study DROP COLUMN isOn" + currentDay);
        executeUpdateQuery("ALTER TABLE teacher DROP COLUMN isOn" + currentDay);
    }

    /**
     * General method to execute alle update queries implementet in this class
     * @param sql
     */
    private void executeUpdateQuery(String sql){
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            int res = stmt.executeUpdate(sql);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks possible subejects and rooms for day.
     * For loop is used to run through the 2 possible blocks
     * 1 = 9 - 13 AND 2 = 13 - 17
     * @param day
     * @throws Exception
     */
    private void checkSingleDay(int day) throws Exception {
        String sql = getPossibleSubjectsQuery(day);
        HashMap<String, Integer> subjects = getItemInHashMap(sql);
        createFieldsForDay();
        checkIfLecturesCanOccure(subjects);
        /*for(int j = 1; j < 3; j++){
            String roomSql = getPossibleRooms();
            HashMap<String, Integer> rooms = getItemInHashMap(roomSql);
            checkIfLecturesCanOccure(rooms, subjects, j);
        }*/

        deleteFieldForDay();
    }

    /**
     * Returns possible rooms to use on the current day and block
     * @return
     */
    private String getPossibleRooms(){
        String sql = "SELECT room_id, maks_kapasitet FROM room ORDER BY maks_kapasitet DESC";
        return sql;
    }

    /**
     * Returns alle subjects where teacher is available on current day and lecture has not occurred more then 12 times.
     * @param day
     * @return
     */
    private String getPossibleSubjectsQuery(int day){
        String sql= "SELECT distinct s.subject_id, s.number_of_attendees " +
                    "FROM day_teacher_unavailability as dtu " +
                    "INNER JOIN teacher as t " +
                        "ON t.id = dtu.teacher_id " +
                    "INNER JOIN teacher_subject as ts " +
                        "ON ts.teacher_id = t.id " +
                    "INNER JOIN subject as s " +
                        "ON s.subject_id = ts.subject_id " +
                    "WHERE s.total < 12 AND s.isInWeek" + currentWeek + " = 0 AND t.id NOT IN ( " +
                        "SELECT teacher_id FROM day_teacher_unavailability " +
                        "WHERE day_id = " + day + " ) ORDER BY s.total * 1 ASC";

        return sql;
    }

    /**
     * General class to use with rooms and subjects that returns name and id in a HashMap
     * @param sql
     * @return
     */
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
        } catch (SQLException e){}

        return hash;
    }

    /**
     * Method is called every block of every day. It checks if lecture and room can pair. If they can Pair
     * it check if field of study and teacher can attand. If this results to true it calls the
     * Presenter class filling the parameters with the current information passed through.
     * @param subjects
     * @throws Exception
     */
    private void checkIfLecturesCanOccure(HashMap subjects) throws Exception {
        HashMap<String, Integer> rooms;
        for(int j = 1; j < 3; j++){
            String roomSql = getPossibleRooms();
            rooms = getItemInHashMap(roomSql);
            Iterator ro = rooms.entrySet().iterator();
            while (ro.hasNext()) {
                Map.Entry room = (Map.Entry)ro.next();
                int room_kap = (int) room.getValue();
                String room_name = (String) room.getKey();

                Iterator su = subjects.entrySet().iterator();
                while ( su.hasNext() ) {
                    Map.Entry subject = (Map.Entry)su.next();
                    int subject_ant = (int) subject.getValue();
                    String subject_id = (String) subject.getKey();

                    if( room_kap >= subject_ant && subject_ant * 2 >= room_kap ){
                        if(checkIfTeacherHasLecture( subject_id ) && checkIfStudyHasLecture( subject_id )) {
                            updateFields(subject_id);
                            Presenter pre = new Presenter(currentWeek, currentDay, room_name, j, subject_id);
                            su.remove();
                            break;
                        }
                    }
                }
                ro.remove();
            }
        }
    }

    /**
     * After checkIfLEcturesCanOccure loops valuates to true it updates the fields so total is increased and other
     * neccessary calls
     * @param subject
     */
    private void updateFields(String subject){
        executeUpdateQuery("UPDATE subject SET total = total + 1 WHERE subject_id = '" + subject + "'");
        executeUpdateQuery("UPDATE subject SET isInWeek" + currentWeek + " = 1 WHERE subject_id = '" + subject + "'");
        executeUpdateQuery("UPDATE teacher SET isON" + currentDay + " = 1 WHERE id IN (SELECT teacher_id FROM teacher_subject WHERE subject_id = '" + subject + "' )");
        executeUpdateQuery("UPDATE field_of_study SET isON" + currentDay + " = 1 WHERE study_id IN (SELECT study_id FROM study_subject WHERE subject_id = '" + subject + "' )");
    }

    /**
     * Evaluates if teacher can have a lecture on the current day with current subject.
     * @param subject
     * @return
     * @throws SQLException
     */
    private boolean checkIfTeacherHasLecture(String subject) throws SQLException {
        String sql= "SELECT COUNT(*) as total " +
                    "FROM teacher as t " +
                    "JOIN teacher_subject as ts " +
                    "ON ts.teacher_id = t.id " +
                    "WHERE ts.subject_id = '" + subject + "' AND t.isOn" + currentDay + " != 1";

        return (executeCountQuery(sql) == 1);
    }

    /**
     * Evaulates if a field of study can have a lecture on the current day
     * @param subject
     * @return
     * @throws SQLException
     */
    private boolean checkIfStudyHasLecture(String subject) throws SQLException {
        String sql= "SELECT COUNT(*) as total " +
                    "FROM field_of_study " +
                    "WHERE isOn" + currentDay + " = 0 AND study_id IN " +
                    "(SELECT study_id FROM study_subject WHERE subject_id = '" + subject + "')";

        String getAlleSql = "SELECT COUNT(*) as total " +
                            "FROM field_of_study " +
                            "WHERE study_id IN " +
                            "(SELECT study_id FROM study_subject WHERE subject_id = '" + subject + "')";

        return (executeCountQuery(sql) == executeCountQuery(getAlleSql));
    }

    /**
     * General execute Query method to be called on to receive the number of items in query.
     * @param sql
     * @return
     * @throws SQLException
     */
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

    /**
     * Returns start week of semester
     * @return
     */
    public int getStartWeek() {
        return startWeek;
    }

    /**
     * Set the start week of the semester
     * @param startWeek
     */
    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    /**
     * Return the end week of the semester
     * @return
     */
    public int getEndWeek() {
        return endWeek;
    }

    /**
     * Set the end week of the semester
     * @param endWeek
     */
    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }
}
