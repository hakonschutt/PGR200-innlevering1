package maven.innlevering;

import java.util.Scanner;

/**
 * Created by hakonschutt on 26/09/2017.
 */
public class SearchFiles {
    Scanner sc;

    public void instructions(){
        System.out.println("Which table should be read?");
        System.out.println("(1) Lectures");
        System.out.println("(2) Subjects");
        System.out.println("(3) Rooms");
        sc = new Scanner(System.in);
        int choice = sc.nextInt();

        switch(choice){
            case 1:
                System.out.println("Searching lectures....");
                break;
            case 2:
                System.out.println("Searching subjects...");
                break;
            case 3:
                System.out.println("Searching Rooms...");
                break;
            default:
                System.out.println("Not a valid command");
                break;
        }
    }
}
