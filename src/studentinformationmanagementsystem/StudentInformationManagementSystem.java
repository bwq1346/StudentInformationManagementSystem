/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.io.File;

/**
 *
 * @author Nishal
 */
public class StudentInformationManagementSystem {

    public static void main(String[] args) {
        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        ActivityLogger.setLogFilePath("data/activity_log.txt");
        ActivityLogger.log("Program started.");

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