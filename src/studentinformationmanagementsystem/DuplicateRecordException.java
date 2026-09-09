/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 * Thrown when something tries to add a record whose id already exists.
 * In practice this shouldn't happen much since ids are auto-generated, but
 * it's there as a safety net for the repository layer.
 *
 * @author Nishal
 */
public class DuplicateRecordException extends Exception {
    private static final long serialVersionUID = 1L;
    public DuplicateRecordException(String message) {
        super(message);
    }
}
