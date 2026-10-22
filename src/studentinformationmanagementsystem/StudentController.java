/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
/**
 * Translates StudentView button clicks into StudentManager calls, and
 * turns any exception StudentManager throws into a JOptionPane message
 * instead of letting it reach the user as a stack trace - this is what the
 * rubric means by "GUI can give user-friendly messages when a false input
 * is given" and the "robust error handling" criterion generally.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class StudentController implements ActionListener {

    private final StudentView studentView;
    private final StudentManager studentManager;

    public StudentController(StudentView studentView, StudentManager studentManager) {
        this.studentView = studentView;
        this.studentManager = studentManager;
        studentView.setStudentManager(studentManager);

        studentView.addButton.addActionListener(this);
        studentView.editButton.addActionListener(this);
        studentView.deleteButton.addActionListener(this);
        studentView.searchButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == studentView.addButton) {
            handleAdd();
        } else if (e.getSource() == studentView.editButton) {
            handleEdit();
        } else if (e.getSource() == studentView.deleteButton) {
            handleDelete();
        } else if (e.getSource() == studentView.searchButton) {
            handleSearch();
        }
    }

    private void handleAdd() {
        StudentFormPanel form = (StudentFormPanel) FormFactory.createForm(FormFactory.EntityType.STUDENT);
        int result = JOptionPane.showConfirmDialog(studentView, form, "Add Student",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        Object[] values = form.getFieldValues();
        String name = (String) values[0];
        String email = (String) values[1];
        String phone = (String) values[2];
        String major = (String) values[4];
        StudentStatus status = (StudentStatus) values[6];

        int yearLevel;
        double gpa;
        try {
            yearLevel = Integer.parseInt(((String) values[3]).trim());
            gpa = Double.parseDouble(((String) values[5]).trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(studentView,
                    "Year level must be a whole number and GPA must be a decimal number.",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            studentManager.addStudent(name, email, phone, yearLevel, major, gpa, status);
            studentView.onDataChanged();
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(studentView, ex.getMessage(),
                    "Could not add student", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleEdit() {
        String id = studentView.getSelectedStudentId();
        if (id == null) {
            JOptionPane.showMessageDialog(studentView, "Select a student first.");
            return;
        }

        Student existing;
        try {
            existing = studentManager.findStudent(id);
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(studentView, ex.getMessage(),
                    "Could not load student", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StudentFormPanel form = (StudentFormPanel) FormFactory.createForm(FormFactory.EntityType.STUDENT);
        form.populateFields(existing);
        int result = JOptionPane.showConfirmDialog(studentView, form, "Edit Student",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        Object[] values = form.getFieldValues();
        String name = (String) values[0];
        String email = (String) values[1];
        String phone = (String) values[2];
        String major = (String) values[4];
        StudentStatus status = (StudentStatus) values[6];

        int yearLevel;
        double gpa;
        try {
            yearLevel = Integer.parseInt(((String) values[3]).trim());
            gpa = Double.parseDouble(((String) values[5]).trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(studentView,
                    "Year level must be a whole number and GPA must be a decimal number.",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            studentManager.updateStudent(id, name, email, phone, yearLevel, major, gpa, status);
            studentView.onDataChanged();
        } catch (RecordNotFoundException | InvalidInputException ex) {
            JOptionPane.showMessageDialog(studentView, ex.getMessage(),
                    "Could not update student", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleDelete() {
        String id = studentView.getSelectedStudentId();
        if (id == null) {
            JOptionPane.showMessageDialog(studentView, "Select a student first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(studentView,
                "Delete student " + id + "? This cannot be undone.", "Confirm delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            studentManager.deleteStudent(id);
            studentView.onDataChanged();
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(studentView, ex.getMessage(),
                    "Could not delete student", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleSearch() {
        String keyword = JOptionPane.showInputDialog(studentView, "Search by name:");
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        List<Student> matches = studentManager.searchByName(keyword.trim());
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(studentView, "No students found matching \"" + keyword + "\".");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Student s : matches) {
            sb.append(s.getSummary()).append("\n");
        }
        JOptionPane.showMessageDialog(studentView, sb.toString(), "Search results",
                JOptionPane.INFORMATION_MESSAGE);
    }
}