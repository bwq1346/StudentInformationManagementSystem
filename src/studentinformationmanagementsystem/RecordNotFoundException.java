package studentinformationmanagementsystem;

/**
 * Custom checked exception thrown when a queried ID does not exist in repositories.
 *
 * Attribution:
 * - Assisted by Claude AI; refactored by Nishal[cite: 5].
 *
 * References:
 * - Horstmann, C. S., & Cornell, G. (2012). Core Java: Volume I—Fundamentals (9th ed.). Prentice Hall[cite: 1].
 *
 * @author Nishal Kinattukarayil Biju
 */
public class RecordNotFoundException extends Exception {
    public RecordNotFoundException(String message) {
        super(message);
    }
}