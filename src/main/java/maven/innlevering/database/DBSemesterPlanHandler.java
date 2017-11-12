package maven.innlevering.database;

import maven.innlevering.Presenter;

import java.sql.*;

/**
 * This DB handler assists the ceatesemester plan with database quieres and uploads.
 *
 * Created by hakonschutt on 12/11/2017.
 */
public class DBSemesterPlanHandler{
    private DBConnect db = new DBConnect();

    /**
     * Creates semester plan table from semester plan query.
     */
    public void createTableForSemester(){
        dropSemesterTable();
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(createTableSQL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Drops semester plan table if it exists.
     */
    private void dropSemesterTable() {
        String sql = "DROP TABLE IF EXISTS `semester_plan`";

        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints all semester plan data to the console.
     */
    public void presentAllSemesterData(){
        Presenter.presentHeader();
        String sql = getDataQuery();

        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables where found");
            }
            do {
                String teacher = getTeachNameFromID(res.getInt("teacher_id"));
                Presenter.presentData(res.getInt("week"), res.getInt("day"), res.getString("room"), res.getInt("block"), res.getString("subject_id"), teacher);
            } while (res.next());
        } catch (Exception e){
            System.out.println("Unable to get semester plan");
        }

        Presenter.presentFooter();
        System.out.println();
    }

    /**
     * Returns a get data query for printing out database content from semester plan.
     * @return
     */
    public String getDataQuery(){
        return "SELECT week, day, room, block, subject_id, teacher_id FROM semester_plan";
    }

    /**
     * Returns a create semester plan table query.
     * @return
     */
    public String createTableSQL(){
        String sql = "CREATE TABLE `semester_plan` (" +
                    "`week` int(2) unsigned NOT NULL," +
                    "`day` int(1) NOT NULL," +
                    "`room` varchar(10) NOT NULL," +
                    "`block` int(1) NOT NULL," +
                    "`subject_id` varchar(10) NOT NULL," +
                    "`teacher_id` int(11) NOT NULL," +
                    "PRIMARY KEY (`week`, `day`, `room`, `block`)," +
                    "FOREIGN KEY (`subject_id`) REFERENCES `subject` (`subject_id`)," +
                    "FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`))";

        return sql;
    }

    /**
     * Uploads information to the semester table.
     * @param week
     * @param day
     * @param room_id
     * @param block
     * @param subject_id
     */
    public void uploadToTable(int week, int day, String room_id, int block, String subject_id){
        String sql = insertIntoSemesterPlanerQuery();
        int teacher_id = getTeacherIdBySubjectId(subject_id);

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, week);
            ps.setInt(2, day);
            ps.setString(3, room_id);
            ps.setInt(4, block);
            ps.setString(5, subject_id);
            ps.setInt(6, teacher_id);

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Unable to upload information to semester table.");
        }
    }

    private String insertIntoSemesterPlanerQuery() {
        String sql = "INSERT INTO `semester_plan` (`week`, `day`, `room`, `block`, `subject_id`, `teacher_id`)" +
                    "VALUES (?,?,?,?,?,?)";

        return sql;
    }

    /**
     * Executes a query for teachers name based of Subject ID
     * @param subjectID
     * @return
     */
    public String getTeachBySubjectID(String subjectID) {
        String sql = getTeacherNameQuery(subjectID);
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
            System.out.println("Unable to query for teachers name from subject id: " + subjectID);
        }


        return null;
    }

    /**
     * Method returns a teacher name from the teacher id
     * @param teacher_id
     * @return
     */
    public String getTeachNameFromID(int teacher_id){
        String sql = getTeacherNameFromIdQuery(teacher_id);
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
            System.out.println("Unable to query for teacher name from teacher id: " + teacher_id);
        }

        return null;
    }

    /**
     * Method returns a teacher id from the subject id input.
     * @param subjectID
     * @return
     */
    public int getTeacherIdBySubjectId(String subjectID) {
        String sql = getTeacherIdQuery(subjectID);
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables where found");
            }
            do {
                return res.getInt(1);
            } while (res.next());
        } catch (SQLException e){
            System.out.println("Unable to query for teachers id from subject id: " + subjectID);
        }

        return -1;
    }

    /**
     * GetTeacherName takes in a subject parameter and returns the teachers name
     * @param subject
     * @return
     */
    private String getTeacherNameQuery(String subject) {
        String sql= "SELECT t.name " +
                "FROM teacher as t " +
                "JOIN teacher_subject as ts " +
                "ON ts.teacher_id = t.id " +
                "WHERE ts.subject_id = '" + subject + "'";

        return sql;
    }

    /**
     * Returns teach id from subject id query
     * @param subject
     * @return
     */
    private String getTeacherIdQuery(String subject) {
        String sql= "SELECT t.id " +
                "FROM teacher as t " +
                "JOIN teacher_subject as ts " +
                "ON ts.teacher_id = t.id " +
                "WHERE ts.subject_id = '" + subject + "'";

        return sql;
    }

    /**
     * Method returns teach name from teacher id query
     * @param teacher_id
     * @return
     */
    private String getTeacherNameFromIdQuery(int teacher_id){
        return  "SELECT name FROM teacher WHERE id = " + teacher_id;
    }
}
