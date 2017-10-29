package maven.innlevering;

import maven.innlevering.database.DBConnect;
import java.util.Scanner;

/**
 * CreatePlan is used to start the semesterplaning.
 * It askes the user for start and end point of the semester
 * Created by hakonschutt on 01/10/2017.
 */
public class CreatePlan {
    private Scanner sc;
    private DBConnect db = new DBConnect();
    private RuleController rc = new RuleController();

    /**
     * Calles the necessary methods to evaulate if the user is connected to the correct database
     * @throws Exception
     */
    public void main() throws Exception {
        boolean isCorrectDB = validateTables();

        if(isCorrectDB){
            setSemester();
            rc.startSemesterPlan();
        } else {
            System.out.println("You are not using the correct database.");
            System.out.println("You can still search and print with this connection.");
            System.out.println("Connect to a database with the necessary tables if you want to create a semester plan!");
        }


    }

    /**
     * Method validates if tables used in semester planing is present in database
     * @return
     * @throws Exception
     */
    public boolean validateTables() throws Exception {
        OutputHandler oh = new OutputHandler();
        String[] tables = oh.getAlleTables();
        int total = checkForTables(tables);

        if(total == 4){
            return true;
        }

        return false;
    }

    /**
     * Method checks for 4 tables that are important to the semester planing.
     * @param tables
     * @return
     */
    private int checkForTables(String[] tables){
        int count = 0;

        for(int i = 0; i < tables.length; i++){
            if(tables[i].equals("field_of_study") || tables[i].equals("room") || tables[i].equals("subject") || tables[i].equals("teacher")){
                count++;
            }
        }

        return count;
    }

    /**
     * Method lets the user set a semester start and end
     */
    private void setSemester(){
        sc = new Scanner(System.in);
        boolean correct = false;

        while(!correct){
            System.out.print("When does the semester start? ");
            int tempStart = sc.nextInt();
            System.out.print("When does the semester end? ");
            int tempEnd = sc.nextInt();

            if(tempStart > 0 && tempEnd < 52){
                if(tempEnd - tempStart > 12){
                    rc.setStartWeek(tempStart);
                    rc.setEndWeek(tempEnd);
                    System.out.println("Creating a semester plan from week " + rc.getStartWeek() + " to week " + rc.getEndWeek() + "...");
                    correct = true;
                } else {
                    System.out.println("The semester can not be shorter then 12weeks.");
                    System.out.println("Set a different end and start point.");
                    System.out.println();
                }
            } else {
                System.out.println("Not valid inputs! Try again.");
                System.out.println();
            }
        }
    }
}