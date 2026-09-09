/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.io.File;

/**
 * Program entry point. Doesn't really contain any "logic" of its own - it
 * just makes sure the data folder and activity log exist, then hands
 * control over to ConsoleUI, which runs until the user chooses to exit.
 *
 * @author Nishal
 */
public class StudentInformationManagementSystem {

    public static void main(String[] args) {
        // make sure the data folder exists before anything tries to read
        // or write into it (first run on a fresh machine won't have it yet)
        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        ActivityLogger.setLogFilePath("data/activity_log.txt");
        ActivityLogger.log("Program started.");

        // wrapping the whole run in a try/catch as a last line of defence -
        // if something genuinely unexpected happens we want a clean message
        // instead of a raw stack trace dumped on the user
        try {
            new ConsoleUI().start();
        } catch (Exception e) {
            System.out.println("\nAn unexpected error occurred: " + e.getMessage());
            System.out.println(
                    "The program will now close. Your most recent save (if any) is safe in the 'data' folder.");
            ActivityLogger.log("FATAL ERROR: " + e);
        }

        ActivityLogger.log("Program exited.");
    }
}
