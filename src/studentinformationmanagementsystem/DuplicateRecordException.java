package studentinformationmanagementsystem;

/**
 * Custom checked exception thrown when an entity with an existing ID is inserted.
 *
 * Attribution:
 * - Assisted by Claude AI; refactored by Nishal to pass customized error details to superclass[cite: 5].
 *
 * References:
 * - Horstmann, C. S., & Cornell, G. (2012). Core Java: Volume I—Fundamentals (Exception Handling)[cite: 1].
 *
 * @author Nishal Kinattukarayil Biju
 */
public class DuplicateRecordException extends Exception {
    public DuplicateRecordException(String message) {
        super(message);
    }
}