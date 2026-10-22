package studentinformationmanagementsystem;

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Form for entering or editing student details.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class StudentFormPanel extends EntityFormPanel {

    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField yearLevelField = new JTextField(5);
    private final JTextField majorField = new JTextField(20);
    private final JTextField gpaField = new JTextField(5);
    private final JComboBox<StudentStatus> statusBox = new JComboBox<>(StudentStatus.values());

    public StudentFormPanel() {
        setLayout(new GridLayout(0, 2, 5, 5));
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Phone:"));
        add(phoneField);
        add(new JLabel("Year Level:"));
        add(yearLevelField);
        add(new JLabel("Major:"));
        add(majorField);
        add(new JLabel("GPA:"));
        add(gpaField);
        add(new JLabel("Status:"));
        add(statusBox);
    }

    @Override
    public Object[] getFieldValues() {
        return new Object[]{
            nameField.getText(), emailField.getText(), phoneField.getText(),
            yearLevelField.getText(), majorField.getText(), gpaField.getText(),
            statusBox.getSelectedItem()
        };
    }

    @Override
    public void populateFields(Object entity) {
        Student s = (Student) entity;
        nameField.setText(s.getName());
        emailField.setText(s.getEmail());
        phoneField.setText(s.getPhone());
        yearLevelField.setText(String.valueOf(s.getYearLevel()));
        majorField.setText(s.getMajor());
        gpaField.setText(String.valueOf(s.getGpa()));
        statusBox.setSelectedItem(s.getStatus());
    }

    @Override
    public void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        yearLevelField.setText("");
        majorField.setText("");
        gpaField.setText("");
        statusBox.setSelectedIndex(0);
    }
}