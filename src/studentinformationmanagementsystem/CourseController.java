package studentinformationmanagementsystem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 * Handles button actions on the Courses tab.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class CourseController implements ActionListener {

    private final CourseView courseView;
    private final CourseManager courseManager;
    private final TeacherManager teacherManager;

    public CourseController(CourseView courseView, CourseManager courseManager, TeacherManager teacherManager) {
        this.courseView = courseView;
        this.courseManager = courseManager;
        this.teacherManager = teacherManager;
        courseView.setCourseManager(courseManager);

        courseView.addButton.addActionListener(this);
        courseView.editButton.addActionListener(this);
        courseView.deleteButton.addActionListener(this);
        courseView.assignTeacherButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == courseView.addButton) {
                handleAdd();
            } else if (e.getSource() == courseView.editButton) {
                handleEdit();
            } else if (e.getSource() == courseView.deleteButton) {
                handleDelete();
            } else if (e.getSource() == courseView.assignTeacherButton) {
                handleAssignTeacher();
            }
        } catch (RuntimeException ex) {
            // DAOs wrap SQLExceptions in RuntimeException
            JOptionPane.showMessageDialog(courseView, ex.getMessage(),
                    "Database error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleAdd() {
        CourseFormPanel form = (CourseFormPanel) FormFactory.createForm(FormFactory.EntityType.COURSE);
        int result = JOptionPane.showConfirmDialog(courseView, form, "Add Course",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        Object[] values = form.getFieldValues();
        String courseCode = (String) values[0];
        String courseName = (String) values[1];
        String teacherId = (String) values[4];
        // teacher is optional - treat a blank field as no teacher
        if (teacherId != null && teacherId.trim().isEmpty()) {
            teacherId = null;
        }

        int creditPoints;
        int capacity;
        try {
            creditPoints = Integer.parseInt(((String) values[2]).trim());
            capacity = Integer.parseInt(((String) values[3]).trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(courseView,
                    "Credit points and capacity must be whole numbers.",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            courseManager.addCourse(courseCode, courseName, creditPoints, capacity, teacherId);
            if (teacherId != null) {
                try {
                    teacherManager.assignCourseToTeacher(teacherId, courseCode);
                } catch (RecordNotFoundException ex) {
                    JOptionPane.showMessageDialog(courseView,
                            "Course was added, but teacher " + teacherId
                                    + " could not be found to update their course list.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
            courseView.onDataChanged();
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(courseView, ex.getMessage(),
                    "Could not add course", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleEdit() {
        String code = courseView.getSelectedCourseCode();
        if (code == null) {
            JOptionPane.showMessageDialog(courseView, "Select a course first.");
            return;
        }

        Course existing;
        try {
            existing = courseManager.findCourse(code);
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(courseView, ex.getMessage(),
                    "Could not load course", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CourseFormPanel form = (CourseFormPanel) FormFactory.createForm(FormFactory.EntityType.COURSE);
        form.populateFields(existing);
        int result = JOptionPane.showConfirmDialog(courseView, form, "Edit Course",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        Object[] values = form.getFieldValues();
        String courseName = (String) values[1];
        int creditPoints;
        int capacity;
        try {
            creditPoints = Integer.parseInt(((String) values[2]).trim());
            capacity = Integer.parseInt(((String) values[3]).trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(courseView,
                    "Credit points and capacity must be whole numbers.",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            courseManager.updateCourse(code, courseName, creditPoints, capacity);
            courseView.onDataChanged();
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(courseView, ex.getMessage(),
                    "Could not update course", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleDelete() {
        String code = courseView.getSelectedCourseCode();
        if (code == null) {
            JOptionPane.showMessageDialog(courseView, "Select a course first.");
            return;
        }
        try {
            courseManager.deleteCourse(code);
            courseView.onDataChanged();
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(courseView, ex.getMessage(),
                    "Could not delete course", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleAssignTeacher() {
        String code = courseView.getSelectedCourseCode();
        if (code == null) {
            JOptionPane.showMessageDialog(courseView, "Select a course first.");
            return;
        }
        
        String oldTeacherId;
        try {
            oldTeacherId = courseManager.findCourse(code).getTeacherId();
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(courseView, ex.getMessage(),
                    "Could not load course", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Teacher> teachers = teacherManager.listAll();
        if (teachers.isEmpty()) {
            JOptionPane.showMessageDialog(courseView, "Add a teacher first - there are none to assign yet.");
            return;
        }

        JComboBox<String> teacherIdBox = new JComboBox<>();
        for (Teacher t : teachers) {
            teacherIdBox.addItem(t.getId() + " - " + t.getName());
        }

        int result = JOptionPane.showConfirmDialog(courseView, teacherIdBox,
                "Assign teacher to " + code, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION || teacherIdBox.getSelectedItem() == null) {
            return;
        }

        String selected = (String) teacherIdBox.getSelectedItem();
        String teacherId = selected.split(" - ")[0];

        // keep the course's teacher and each teacher's course list in sync
        try {
            courseManager.assignTeacher(code, teacherId);
            if (oldTeacherId != null && !oldTeacherId.equals(teacherId)) {
                teacherManager.unassignCourseFromTeacher(oldTeacherId, code);
            }
            teacherManager.assignCourseToTeacher(teacherId, code);
            courseView.onDataChanged();
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(courseView, ex.getMessage(),
                    "Could not assign teacher", JOptionPane.WARNING_MESSAGE);
        }
    }
}