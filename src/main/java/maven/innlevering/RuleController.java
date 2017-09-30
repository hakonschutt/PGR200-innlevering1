package maven.innlevering;

import java.util.Scanner;

/**
 * Created by hakonschutt on 26/09/2017.
 */
public class RuleController {
    private int startWeek;
    private int endWeek;
    private Scanner sc;

    public void main(){
        boolean isCorrectDB = validateTables();
        if(isCorrectDB){
            setSemester();
        } else {
            System.out.println("You are not using the correct database");
            System.out.println("Connect to a database with the necessary tables");
        }

    }

    private boolean validateTables(){
        return true;
    }

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
                    setStartWeek(tempStart);
                    setEndWeek(tempEnd);
                    System.out.println("Creating a semester plan from week" + startWeek + " to week " + endWeek + "...");
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
