/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.List;
import java.util.function.Function;

/**
 * Small helper for generating the next id in a sequence, e.g. STU0001,
 * STU0002, and so on. It's generic and takes a Function so it can pull the
 * id back out of any type of record (Student, Teacher, Enrollment...)
 * without needing a separate version of this class for each one.
 *
 * @author Vikram Bugata
 */
public final class IdGenerator {

    // never instantiated - this class is just a holder for the static method below
    private IdGenerator() {
    }

    public static <T> String next(String prefix, int digits, List<T> existing, Function<T, String> idExtractor) {
        int maxNumber = 0;
        // go through every existing record, pull out its id, and if it
        // matches our prefix keep track of the highest number we've seen
        for (T item : existing) {
            String id = idExtractor.apply(item);
            if (id != null && id.startsWith(prefix)) {
                try {
                    int number = Integer.parseInt(id.substring(prefix.length()));
                    maxNumber = Math.max(maxNumber, number);
                } catch (NumberFormatException ignored) {
                    // id didn't end in a number for some reason - just skip it
                }
            }
        }
        // next available number, padded with leading zeros to the requested width
        int next = maxNumber + 1;
        return prefix + String.format("%0" + digits + "d", next);
    }
}
