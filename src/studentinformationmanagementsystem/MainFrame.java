/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package studentinformationmanagementsystem;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * @author Nishal & Vikram
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

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() instanceof ModelListener) {
                ((ModelListener) tabs.getSelectedComponent()).onDataChanged();
            }
        });

        add(tabs);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DBConnectionManager.getInstance().shutdown();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
