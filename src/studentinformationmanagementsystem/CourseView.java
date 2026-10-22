package studentinformationmanagementsystem;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 * Courses tab: a table of all courses plus the action buttons.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class CourseView extends JPanel implements ModelListener {

    private final JTable courseTable;
    private final DefaultTableModel tableModel;

    public final JButton addButton = new JButton("Add Course");
    public final JButton editButton = new JButton("Edit Course");
    public final JButton deleteButton = new JButton("Delete Course");
    public final JButton assignTeacherButton = new JButton("Assign Teacher");

    private CourseManager courseManager;

    public CourseView() {
        tableModel = new DefaultTableModel(
                new Object[]{"Code", "Name", "Credits", "Enrolled/Capacity", "Waitlisted", "Teacher"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel);

        setLayout(new BorderLayout());
        add(new JScrollPane(courseTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(assignTeacherButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setCourseManager(CourseManager courseManager) {
        this.courseManager = courseManager;
        onDataChanged();
    }

    public String getSelectedCourseCode() {
        int row = courseTable.getSelectedRow();
        return row < 0 ? null : (String) tableModel.getValueAt(row, 0);
    }

    @Override
    public void onDataChanged() {
        tableModel.setRowCount(0);
        if (courseManager == null) {
            return;
        }
        List<Course> courses = courseManager.listAll();
        for (Course c : courses) {
            tableModel.addRow(new Object[]{
                c.getCourseCode(), c.getCourseName(), c.getCreditPoints(),
                c.getEnrolledStudentIds().size() + "/" + c.getCapacity(),
                c.getWaitlist().size(),
                c.getTeacherId() == null ? "Unassigned" : c.getTeacherId()
            });
        }
    }
}
