/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
/**
 *
 * @author Nishal Kinattukarayil Biju
 */
public class CourseFormPanel extends EntityFormPanel {

    private final JTextField courseCodeField = new JTextField(10);
    private final JTextField courseNameField = new JTextField(25);
    private final JTextField creditPointsField = new JTextField(5);
    private final JTextField capacityField = new JTextField(5);
    private final JTextField teacherIdField = new JTextField(10);

    public CourseFormPanel() {
        setLayout(new GridLayout(0, 2, 5, 5));
        add(new JLabel("Course Code:"));
        add(courseCodeField);
        add(new JLabel("Course Name:"));
        add(courseNameField);
        add(new JLabel("Credit Points:"));
        add(creditPointsField);
        add(new JLabel("Capacity:"));
        add(capacityField);
        add(new JLabel("Teacher ID (optional):"));
        add(teacherIdField);
    }

    @Override
    public Object[] getFieldValues() {
        return new Object[]{
            courseCodeField.getText(), courseNameField.getText(),
            creditPointsField.getText(), capacityField.getText(), teacherIdField.getText()
        };
    }

    @Override
    public void populateFields(Object entity) {
        Course c = (Course) entity;
        courseCodeField.setText(c.getCourseCode());
        courseCodeField.setEnabled(false); // course code can't be changed once created
        courseNameField.setText(c.getCourseName());
        creditPointsField.setText(String.valueOf(c.getCreditPoints()));
        capacityField.setText(String.valueOf(c.getCapacity()));
        teacherIdField.setText(c.getTeacherId());
    }

    @Override
    public void clearFields() {
        courseCodeField.setText("");
        courseCodeField.setEnabled(true);
        courseNameField.setText("");
        creditPointsField.setText("");
        capacityField.setText("");
        teacherIdField.setText("");
    }
}
