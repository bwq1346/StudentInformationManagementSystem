/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 * Thrown when a value breaks one of our business rules - e.g. a GPA outside
 * 0.0-4.0 - after it's already made it past the console input checks. Having
 * this here too means the rule still gets enforced even if a manager method
 * gets called directly instead of through the menu.
 *
 * @author Nishal
 */
public class InvalidInputException extends Exception {
    private static final long serialVersionUID = 1L;
    public InvalidInputException(String message) {
        super(message);
    }
}
