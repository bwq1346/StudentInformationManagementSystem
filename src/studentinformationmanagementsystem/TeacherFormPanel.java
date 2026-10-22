package studentinformationmanagementsystem;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Form for entering or editing teacher details.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class TeacherFormPanel extends EntityFormPanel {

    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField departmentField = new JTextField(20);

    public TeacherFormPanel() {
        setLayout(new GridLayout(0, 2, 5, 5));
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Phone:"));
        add(phoneField);
        add(new JLabel("Department:"));
        add(departmentField);
    }

    @Override
    public Object[] getFieldValues() {
        return new Object[]{nameField.getText(), emailField.getText(), phoneField.getText(), departmentField.getText()};
    }

    @Override
    public void populateFields(Object entity) {
        Teacher t = (Teacher) entity;
        nameField.setText(t.getName());
        emailField.setText(t.getEmail());
        phoneField.setText(t.getPhone());
        departmentField.setText(t.getDepartment());
    }

    @Override
    public void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        departmentField.setText("");
    }
}
