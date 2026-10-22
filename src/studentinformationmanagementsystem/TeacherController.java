package studentinformationmanagementsystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 * Handles button actions on the Teachers tab.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class TeacherController implements ActionListener {

    private final TeacherView teacherView;
    private final TeacherManager teacherManager;

    public TeacherController(TeacherView teacherView, TeacherManager teacherManager) {
        this.teacherView = teacherView;
        this.teacherManager = teacherManager;
        teacherView.setTeacherManager(teacherManager);

        teacherView.addButton.addActionListener(this);
        teacherView.editButton.addActionListener(this);
        teacherView.deleteButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == teacherView.addButton) {
            handleAdd();
        } else if (e.getSource() == teacherView.editButton) {
            handleEdit();
        } else if (e.getSource() == teacherView.deleteButton) {
            handleDelete();
        }
    }

    private void handleAdd() {
        TeacherFormPanel form = (TeacherFormPanel) FormFactory.createForm(FormFactory.EntityType.TEACHER);
        int result = JOptionPane.showConfirmDialog(teacherView, form, "Add Teacher",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        Object[] values = form.getFieldValues();
        String name = (String) values[0];
        String email = (String) values[1];
        String phone = (String) values[2];
        String department = (String) values[3];
        try {
            teacherManager.addTeacher(name, email, phone, department);
            teacherView.onDataChanged();
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(teacherView, ex.getMessage(),
                    "Could not add teacher", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleEdit() {
        String id = teacherView.getSelectedTeacherId();
        if (id == null) {
            JOptionPane.showMessageDialog(teacherView, "Select a teacher first.");
            return;
        }

        Teacher existing;
        try {
            existing = teacherManager.findTeacher(id);
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(teacherView, ex.getMessage(),
                    "Could not load teacher", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TeacherFormPanel form = (TeacherFormPanel) FormFactory.createForm(FormFactory.EntityType.TEACHER);
        form.populateFields(existing);
        int result = JOptionPane.showConfirmDialog(teacherView, form, "Edit Teacher",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        Object[] values = form.getFieldValues();
        String name = (String) values[0];
        String email = (String) values[1];
        String phone = (String) values[2];
        String department = (String) values[3];
        try {
            teacherManager.updateTeacher(id, name, email, phone, department);
            teacherView.onDataChanged();
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(teacherView, ex.getMessage(),
                    "Could not update teacher", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleDelete() {
        String id = teacherView.getSelectedTeacherId();
        if (id == null) {
            JOptionPane.showMessageDialog(teacherView, "Select a teacher first.");
            return;
        }
        try {
            teacherManager.deleteTeacher(id);
            teacherView.onDataChanged();
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(teacherView, ex.getMessage(),
                    "Could not delete teacher", JOptionPane.WARNING_MESSAGE);
        }
    }
}
