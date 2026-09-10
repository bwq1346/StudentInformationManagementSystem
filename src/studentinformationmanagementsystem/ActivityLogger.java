/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Writes a timestamped line to a log file every time something notable
 * happens (a record added, updated, deleted, or an error). It's separate
 * from the console output on purpose - this way there's a record of what
 * happened even after the console window is closed.
 * 
 * * Append-only timestamped logging approach drafted with assistance from
 * Claude AI; implemented using PrintWriter/FileOutputStream — see AI
 * Interaction Log.
 *
 * @author Vikra
 */
public final class ActivityLogger {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // default path, but Main overrides this once the data folder is set up
    private static String logFilePath = "data/activity_log.txt";

    private ActivityLogger() {
    }

    public static void setLogFilePath(String path) {
        logFilePath = path;
    }

    public static void log(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String line = "[" + timestamp + "] " + message;

        // true = append mode, so every call adds a new line instead of
        // wiping out everything logged before it
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFilePath, true))) {
            writer.println(line);
        } catch (IOException e) {
            // if we can't even write the log, don't crash the program over it -
            // just say so on the console and carry on
            System.out.println("(Could not write to activity log: " + e.getMessage() + ")");
        }
    }
}
