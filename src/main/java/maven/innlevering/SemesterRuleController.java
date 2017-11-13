package maven.innlevering;

import maven.innlevering.database.DBConnection;
import maven.innlevering.database.DBSemesterPlanHandler;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * SemesterRuleController is the runDbValidation semesterPrinter class. It keeps track of all
 * the logic for the semester planer
 *
 * Created by hakonschutt on 26/09/2017.
 */
public class SemesterRuleController {
    private int startWeek;
    private int endWeek;
    private int currentWeek;
    private int currentDay;
    private DBConnection database = new DBConnection();
    private DBSemesterPlanHandler semesterPlanHandler = new DBSemesterPlanHandler();


    /**
     * Initiate the semester planing
     * @throws IOException
     * @throws SQLException
     */
    public void startSemesterPlan() throws IOException, SQLException {
        System.out.println();
        semesterPlanHandler.createTableForSemester();

        createTotalColumn();

        int diff = endWeek - startWeek;
        boolean finished = false;

        currentWeek = startWeek;
        while(currentWeek <= endWeek){
            createInWeekColumn();
            int i = 1;
            while(i <= 5){
                currentDay = i;
                boolean tempQuit = checkSingleDay(i);
                if(finished || tempQuit)
                    finished = tempQuit;

                i++;
            }
            deleteInWeekColumn();

            if(!finished){
                currentWeek++;
                printProgressBar(diff, currentWeek - startWeek);
            } else {
                break;
            }
        }
        deleteTotalColumn();
        System.out.println("\n\nFinished creating semesterplan in " + (currentWeek - startWeek) + " weeks!");
    }

    /**
     * Prints progress bar to the console when semester plan is being created.
     * @param total
     * @param currentDiff
     */
    public void printProgressBar(int total, int currentDiff){
        String formatForString = "[%-" + ((total * 3)+1) + "s]";
        String progress = "";
        for(int i = 0; i < currentDiff; i++){
            progress += "===";
        }
        progress += ">";

        String stringToPrint = String.format(formatForString, progress);




        System.out.print("\r" + stringToPrint + " " + currentDiff + "/" + total + " weeks created.");
    }


    /**
     * Creates a total column on subject table
     * @throws IOException
     * @throws SQLException
     */
    private void createTotalColumn() throws IOException, SQLException {
        executeUpdateQuery("ALTER TABLE subject ADD total int(2) DEFAULT 0 NOT NULL");
    }

    /**
     * Deletes total column from subject table
     * @throws IOException
     * @throws SQLException
     */
    private void deleteTotalColumn() throws IOException, SQLException {
        executeUpdateQuery("ALTER TABLE subject DROP COLUMN total");
    }

    /**
     * Creates isInWeek column on subject table.
     * This is used to filter out subjects that has allready
     * occurred this week.
     * @throws IOException
     * @throws SQLException
     */
    private void createInWeekColumn() throws IOException, SQLException {
        executeUpdateQuery("ALTER TABLE subject ADD isInWeek" + currentWeek + " int(1) DEFAULT 0 NOT NULL");
    }

    /**
     * Deletes the isInWeek column when the week is over
     * @throws IOException
     * @throws SQLException
     */
    private void deleteInWeekColumn() throws IOException, SQLException {
        executeUpdateQuery("ALTER TABLE subject DROP COLUMN isInWeek" + currentWeek);
    }

    /**
     * Creates columns in field_of_study and teacher to make sure study and teachers don't have two lecture on the same day
     * @throws IOException
     * @throws SQLException
     */
    private void createFieldsForDay() throws IOException, SQLException {
        executeUpdateQuery("ALTER TABLE field_of_study ADD isOn" + currentDay + " int(1) DEFAULT 0 NOT NULL");
        executeUpdateQuery("ALTER TABLE teacher ADD isOn" + currentDay + " int(1) DEFAULT 0 NOT NULL");
    }

    /**
     * Deletes columns isOn"day" after day is over
     * @throws IOException
     * @throws SQLException
     */
    private void deleteFieldsForDay() throws IOException, SQLException {
        executeUpdateQuery("ALTER TABLE field_of_study DROP COLUMN isOn" + currentDay);
        executeUpdateQuery("ALTER TABLE teacher DROP COLUMN isOn" + currentDay);
    }

    /**
     * General method to execute alle update queries implemented in this class
     * @param sql
     * @throws IOException
     * @throws SQLException
     */
    private void executeUpdateQuery(String sql) throws IOException, SQLException {
        try (Connection con = database.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    /**
     * Checks possible subejects and rooms for day.
     * For loop is used to run through the 2 possible blocks
     * 1 = 9 - 13 AND 2 = 13 - 17
     * @param day
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private boolean checkSingleDay(int day) throws IOException, SQLException {
        String sql = getPossibleSubjectsQuery(day);
        HashMap<String, Integer> subjects = getItemInHashMap(sql);

        if(subjects != null){
            createFieldsForDay();
            checkIfLecturesCanOccur(subjects);
            deleteFieldsForDay();
            return false;
        }
        return true;
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
     * @throws IOException
     * @throws SQLException
     */
    private HashMap getItemInHashMap(String sql) throws IOException, SQLException {
        HashMap<String, Integer> hash = new HashMap<>();
        try (Connection con = database.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);

            if(!res.next()) return null;

            do {
                hash.put(res.getString(1), res.getInt(2));
            } while (res.next());
        }

        return hash;
    }

    /**
     * Method is called every block of every day. It checks if lecture and room can pair. If they can Pair
     * it check if field of study and teacher can attand. If this results to true it calls the
     * SemesterPresenter class filling the parameters with the current information passed through.
     * @param subjects
     * @throws IOException
     * @throws SQLException
     */
    private void checkIfLecturesCanOccur(HashMap subjects) throws IOException, SQLException {
        HashMap<String, Integer> rooms;
        for(int j = 1; j <= 2; j++){
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
                            semesterPlanHandler.uploadToTable(currentWeek, currentDay, room_name, j, subject_id);
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
     * @throws IOException
     * @throws SQLException
     */
    private void updateFields(String subject) throws IOException, SQLException {
        executeUpdateQuery("UPDATE subject SET total = total + 1 WHERE subject_id = '" + subject + "'");
        executeUpdateQuery("UPDATE subject SET isInWeek" + currentWeek + " = 1 WHERE subject_id = '" + subject + "'");
        executeUpdateQuery("UPDATE teacher SET isON" + currentDay + " = 1 WHERE id IN (SELECT teacher_id FROM teacher_subject WHERE subject_id = '" + subject + "' )");
        executeUpdateQuery("UPDATE field_of_study SET isON" + currentDay + " = 1 WHERE study_id IN (SELECT study_id FROM study_subject WHERE subject_id = '" + subject + "' )");
    }

    /**
     * Evaluates if teacher can have a lecture on the current day with current subject.
     * @param subject
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private boolean checkIfTeacherHasLecture(String subject) throws IOException, SQLException {
        String sql= "SELECT COUNT(*) as total " +
                    "FROM teacher as t " +
                    "JOIN teacher_subject as ts " +
                    "ON ts.teacher_id = t.id " +
                    "WHERE ts.subject_id = '" + subject + "' AND t.isOn" + currentDay + " != 1";

        return (executeCountQuery(sql) == 1);
    }

    /**
     * Evaulates if a field of study can have a lecture on the current day.
     * @param subject
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private boolean checkIfStudyHasLecture(String subject) throws IOException, SQLException {
        String sql= "SELECT COUNT(*) as total " +
                    "FROM field_of_study " +
                    "WHERE isOn" + currentDay + " = 0 AND study_id IN " +
                    "(SELECT study_id FROM study_subject WHERE subject_id = '" + subject + "')";

        String getAllSql = "SELECT COUNT(*) as total " +
                            "FROM field_of_study " +
                            "WHERE study_id IN " +
                            "(SELECT study_id FROM study_subject WHERE subject_id = '" + subject + "')";

        return (executeCountQuery(sql) == executeCountQuery(getAllSql));
    }

    /**
     * General execute Query method to be called on to receive the number of items in query.
     * @param sql
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private int executeCountQuery(String sql) throws IOException, SQLException {
        try (Connection con = database.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) throw new SQLException();
            return res.getInt("total");
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
