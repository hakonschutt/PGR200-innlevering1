package maven.innlevering;

import maven.innlevering.database.DBConnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Created by hakonschutt on 26/09/2017.
 */
public class RuleController {
    private int startWeek;
    private int endWeek;
    private Scanner sc;
    private DBConnect db = new DBConnect();

    public void main() throws Exception {
        boolean isCorrectDB = validateTables();

        if(isCorrectDB){
            setSemester();
        } else {
            System.out.println("You are not using the correct database.");
            System.out.println("You can still search and print with this connection.");
            System.out.println("Connect to a database with the necessary tables if you want to create a semester plan!");

        }

    }

    private boolean validateTables() throws Exception {
        OutputHandler oh = new OutputHandler();
        String[] tables = oh.getAlleTables();
        int total = checkForTables(tables);

        if(total == 4){
            return true;
        }

        return false;
    }

    private int checkForTables(String[] tables){
        int count = 0;

        for(int i = 0; i < tables.length; i++){
            if(tables[i].equals("field_of_study") || tables[i].equals("room") || tables[i].equals("subject") || tables[i].equals("teacher")){
                count++;
            }
        }

        return count;
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
