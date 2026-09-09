/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 * Thrown whenever code looks up, updates or deletes a record by an id that
 * doesn't actually exist - e.g. searching for a student ID that was never
 * added, or was already deleted.
 *
 * @author Nishal
 */
public class RecordNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    public RecordNotFoundException(String message) {
        super(message);
    }
}
