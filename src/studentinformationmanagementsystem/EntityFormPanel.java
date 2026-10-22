/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import javax.swing.JPanel;

/**
 * Common contract for the three entity input forms (Student/Teacher/
 * Course), so FormFactory can hand back one type regardless of which
 * concrete form it actually built, and so each *Controller class can talk
 * to whichever form it's using the same way.
 * 
 * @author Nishal
 */
public abstract class EntityFormPanel extends JPanel {

    // Pulls the entered values out of this form's fields, in the order
    // the matching Manager's add-method expects them (see each concrete
    // subclass's javadoc for the exact order).
    public abstract Object[] getFieldValues();

    // Fills the form's fields from an existing entity, for the "Edit" flow
    public abstract void populateFields(Object entity);

    public abstract void clearFields();
}
