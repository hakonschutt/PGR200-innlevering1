package maven.innlevering.database;

import maven.innlevering.SemesterPresenter;
import maven.innlevering.exception.CustomFileNotFoundException;
import maven.innlevering.exception.CustomIOException;
import maven.innlevering.exception.CustomSQLException;

import java.io.IOException;
import java.sql.*;

/**
 * This DB handler assists the ceatesemester plan with database quieres and uploads.
 *
 * Created by hakonschutt on 12/11/2017.
 */
public class DBSemesterPlanHandler{
    private DBConnection db = new DBConnection();

    /**
     * Creates semester plan table from semester plan query.
     * @throws IOException
     * @throws SQLException
     */
    public void createTableForSemester() throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        dropSemesterTable();
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(createTableSQL());
        } catch (SQLException e){
            throw new CustomSQLException(CustomSQLException.getErrorMessage("createSemester"));
        }
    }

    /**
     * Drops semester plan table if it exists.
     * @throws IOException
     * @throws SQLException
     */
    private void dropSemesterTable() throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        String sql = "DROP TABLE IF EXISTS `semester_plan`";

        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e){
            throw new CustomSQLException(CustomSQLException.getErrorMessage("dropSemester"));
        }
    }

    /**
     * Prints all semester plan data to the console.
     * @throws IOException
     * @throws SQLException
     */
    public void presentAllSemesterData() throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        SemesterPresenter.presentHeader();
        String sql = getDataQuery();

        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()) {
                throw new SQLException("No tables where found");
            }
            do {
                String teacher = getTeachNameFromID(res.getInt("teacher_id"));
                SemesterPresenter.presentData(res.getInt("week"), res.getInt("day"), res.getString("room"), res.getInt("block"), res.getString("subject_id"), teacher);
            } while (res.next());
        } catch (SQLException e){
            throw new CustomSQLException(CustomSQLException.getErrorMessage("querySemester"));
        }

        SemesterPresenter.presentFooter();
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
     * @throws IOException
     * @throws SQLException
     */
    public void uploadToTable(int week, int day, String room_id, int block, String subject_id) throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
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
        } catch (SQLException e){
            throw new CustomSQLException(CustomSQLException.getErrorMessage("uploadSemester"));
        }
    }

    /**
     * Inserts data into semester plan table.
     * @return
     */
    private String insertIntoSemesterPlanerQuery() {
        String sql = "INSERT INTO `semester_plan` (`week`, `day`, `room`, `block`, `subject_id`, `teacher_id`)" +
                    "VALUES (?,?,?,?,?,?)";

        return sql;
    }

    /**
     * Method returns a teacher name from the teacher id
     * @param teacher_id
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public String getTeachNameFromID(int teacher_id) throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
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
            throw new CustomSQLException(CustomSQLException.getErrorMessage("teacher"));
        }
    }

    /**
     * Method returns a teacher id from the subject id input.
     * @param subjectID
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public int getTeacherIdBySubjectId(String subjectID) throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
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
            throw new CustomSQLException(CustomSQLException.getErrorMessage("teacher"));
        }
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
