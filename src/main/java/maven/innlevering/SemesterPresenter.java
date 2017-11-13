package maven.innlevering;
/**
 * The SemesterPresenter class is used to present the semester plan
 * Created by hakonschutt on 26/09/2017.
 */
public class SemesterPresenter {
    private static final String SEMESTER_STRING_FORMAT = "| %-8S| %-8S| %-20S| %-10S| %-15S| %-25S|";

    /**
     * This constructor is called when semester planing is started. It presents the column names
     */
    public static void presentHeader() {
        String intro = String.format(SEMESTER_STRING_FORMAT, "Week", "Day", "Block", "Room", "Subject", "Teacher");
        System.out.println(generateLine(intro));
        System.out.println(intro);
        System.out.println(generateLine(intro));
    }

    /**
     * This constructor is used when data is set.
     * It presents the semester data for the current day, block and subject.
     * @param week
     * @param day
     * @param room
     * @param block
     * @param subject_id
     */
    public static void presentData(int week, int day, String room, int block, String subject_id, String teacherName) {
        String line = String.format(SEMESTER_STRING_FORMAT, week, getDayName(day), getBlockTime(block), room, subject_id, teacherName);
        System.out.println(line);
    }

    /**
     * Presents a line at the end of the semester plan print
     */
    public static void presentFooter(){
        String intro = String.format(SEMESTER_STRING_FORMAT, "", "", "", "", "", "");
        System.out.println(generateLine(intro));
    }

    /**
     * Generates a line from the intro text
     * @param line
     * @return
     */
    private static String generateLine(String line){
        String finalString = "";

        for(int i = 0; i < line.length(); i++){
            finalString += "-";
        }

        return finalString;
    }

    /**
     * Returns the day with the current day ID
     * @param day_id
     * @return
     */
    private static String getDayName(int day_id){
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
    private static String getBlockTime(int block){
        if(block == 1){
            return "9:00 - 13:00";
        }
        return "13:00 - 17:00";
    }
}
