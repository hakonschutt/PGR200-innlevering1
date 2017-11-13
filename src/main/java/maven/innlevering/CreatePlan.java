package maven.innlevering;

import java.util.Scanner;

/**
 * CreatePlan is used to start the semesterplaning.
 * It askes the user for start and end point of the semester
 * Created by hakonschutt on 01/10/2017.
 */
public class CreatePlan {
    private Scanner sc;
    private RuleController rc = new RuleController();

    /**
     * Calles the necessary methods to evaulate if the user is connected to the correct database
     */
    public boolean main() throws Exception {
        setSemester();
        rc.startSemesterPlan();
        return true;
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