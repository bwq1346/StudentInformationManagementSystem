package studentinformationmanagementsystem;
import javax.swing.JPanel;

/**
 * Base class for the add/edit forms shown in dialogs.
 *
 * @author Nishal Kinattukarayil Biju
 */
public abstract class EntityFormPanel extends JPanel {

    public abstract Object[] getFieldValues();

    public abstract void populateFields(Object entity);

    public abstract void clearFields();
}
