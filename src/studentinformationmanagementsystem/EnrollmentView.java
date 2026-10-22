package studentinformationmanagementsystem;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Enrolments tab: a sortable table of all enrolments plus the action buttons.
 *
 * @author Vikram Bugata
 */

public class EnrollmentView extends JPanel implements ModelListener
{

    static final int COL_ID = 0;
    static final int COL_STUDENT_ID = 1;
    static final int COL_COURSE = 3;
    static final int COL_STATUS = 7;

    static final String WAITLISTED = "WAITLISTED";

    private final JTable enrollmentTable;
    private final DefaultTableModel tableModel;

    public final JButton enrolButton = new JButton("Enrol Student");
    public final JButton withdrawButton = new JButton("Withdraw");
    public final JButton gradeButton = new JButton("Record Grade");
    public final JButton refreshButton = new JButton("Refresh");

    private EnrollmentManager enrollmentManager;
    private StudentManager studentManager;
    private CourseManager courseManager;

    public EnrollmentView()
    {
        tableModel = new DefaultTableModel(
                new Object[]{"Enrolment ID", "Student ID", "Student Name", "Course", "Semester",
                    "Grade", "Letter", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrollmentTable = new JTable(tableModel);
        enrollmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enrollmentTable.setAutoCreateRowSorter(true);

        setLayout(new BorderLayout());
        add(new JScrollPane(enrollmentTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(enrolButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(gradeButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setManagers(EnrollmentManager enrollmentManager, StudentManager studentManager,
            CourseManager courseManager) {
        this.enrollmentManager = enrollmentManager;
        this.studentManager = studentManager;
        this.courseManager = courseManager;
        onDataChanged();
    }

    public String getSelectedValue(int column)
    {
        int viewRow = enrollmentTable.getSelectedRow();
        if(viewRow < 0)
        {
            return null;
        }
        int modelRow = enrollmentTable.convertRowIndexToModel(viewRow); // view order differs once sorted
        return String.valueOf(tableModel.getValueAt(modelRow, column));
    }

    @Override
    public void onDataChanged()
    {
        tableModel.setRowCount(0);
        if(enrollmentManager == null)
        {
            return;
        }

        Map<String, String> studentNames = new HashMap<>();
        for(Student s : studentManager.listAllSorted())
        {
            studentNames.put(s.getId(), s.getName());
        }
        Map<String, Course> courses = new HashMap<>();
        for(Course c : courseManager.listAll())
        {
            courses.put(c.getCourseCode(), c);
        }

        List<Enrollment> enrollments = enrollmentManager.listAll();
        for(Enrollment e : enrollments)
        {
            String status = e.getStatus().name();
            Course course = courses.get(e.getCourseCode());
            // waitlisted students are stored as ENROLLED, so show them as WAITLISTED here
            if(e.getStatus() == EnrollmentStatus.ENROLLED && course != null
                    && course.getWaitlist().contains(e.getStudentId()))
            {
                status = WAITLISTED;
            }
            tableModel.addRow(new Object[]{
                e.getEnrollmentId(),
                e.getStudentId(),
                studentNames.getOrDefault(e.getStudentId(), "(unknown)"),
                e.getCourseCode(),
                e.getSemester(),
                e.getGrade() == null ? "" : String.format("%.1f", e.getGrade()),
                e.getLetterGrade(),
                status
            });
        }
    }
}
