/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Vikra
 */
public final class IdGenerator {

    private IdGenerator() {
    }

    public static <T> String next(String prefix, int digits, List<T> existing, Function<T, String> idExtractor) {
        int maxNumber = 0;
        for (T item : existing) {
            String id = idExtractor.apply(item);
            if (id != null && id.startsWith(prefix)) {
                try {
                    int number = Integer.parseInt(id.substring(prefix.length()));
                    maxNumber = Math.max(maxNumber, number);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        int next = maxNumber + 1;
        return prefix + String.format("%0" + digits + "d", next);
    }
}
