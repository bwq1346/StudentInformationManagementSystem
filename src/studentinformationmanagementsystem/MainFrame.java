package studentinformationmanagementsystem;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * Main window and application entry point. Wires up the managers, views and controllers.
 *
 * @author Nishal Kinattukarayil Biju & Vikram Bugata
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("Student Information Management System");

        StudentManager studentManager = new StudentManager(new StudentDAO());
        TeacherManager teacherManager = new TeacherManager(new TeacherDAO());
        CourseManager courseManager = new CourseManager(new CourseDAO());
        EnrollmentManager enrollmentManager = new EnrollmentManager(new EnrollmentDAO(), studentManager, courseManager);
        ReportManager reportManager = new ReportManager(studentManager, courseManager, enrollmentManager);

        JTabbedPane tabs = new JTabbedPane();

        StudentView studentView = new StudentView();
        new StudentController(studentView, studentManager);
        tabs.addTab("Students", studentView);

        TeacherView teacherView = new TeacherView();
        new TeacherController(teacherView, teacherManager);
        tabs.addTab("Teachers", teacherView);

        CourseView courseView = new CourseView();
        new CourseController(courseView, courseManager, teacherManager);
        tabs.addTab("Courses", courseView);

        EnrollmentView enrollmentView = new EnrollmentView();
        new EnrollmentController(enrollmentView, enrollmentManager, studentManager, courseManager);
        tabs.addTab("Enrollments", enrollmentView);

        ReportsView reportsView = new ReportsView();
        new ReportsController(reportsView, reportManager);
        tabs.addTab("Reports", reportsView);

        // refresh a tab when it's opened so it shows changes made on other tabs
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() instanceof ModelListener) {
                try {
                    ((ModelListener) tabs.getSelectedComponent()).onDataChanged();
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Database error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        add(tabs);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // shut Derby down cleanly before exiting
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DBConnectionManager.getInstance().shutdown();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame().setVisible(true);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(null,
                        "Could not start the application:\n" + ex.getMessage()
                        + "\n\nIf the app is already open, close it and try again.",
                        "Database error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}