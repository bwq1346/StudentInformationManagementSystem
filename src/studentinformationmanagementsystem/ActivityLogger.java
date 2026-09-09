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
 *
 * @author Vikra
 */
public final class ActivityLogger {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static String logFilePath = "data/activity_log.txt";

    private ActivityLogger() {
    }

    public static void setLogFilePath(String path) {
        logFilePath = path;
    }

    public static void log(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String line = "[" + timestamp + "] " + message;

        try (PrintWriter writer = new PrintWriter(new FileWriter(logFilePath, true))) {
            writer.println(line);
        } catch (IOException e) {
            System.out.println("(Could not write to activity log: " + e.getMessage() + ")");
        }
    }
}
