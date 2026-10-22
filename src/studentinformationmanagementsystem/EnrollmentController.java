package studentinformationmanagementsystem;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Handles button actions on the Enrolments tab.
 *
 * @author Vikram Bugata
 */

public class EnrollmentController implements ActionListener
{

    private static final int MAX_SEMESTER_LENGTH = 20;

    private final EnrollmentView enrollmentView;
    private final EnrollmentManager enrollmentManager;
    private final StudentManager studentManager;
    private final CourseManager courseManager;

    public EnrollmentController(EnrollmentView enrollmentView, EnrollmentManager enrollmentManager,
            StudentManager studentManager, CourseManager courseManager) {
        this.enrollmentView = enrollmentView;
        this.enrollmentManager = enrollmentManager;
        this.studentManager = studentManager;
        this.courseManager = courseManager;
        enrollmentView.setManagers(enrollmentManager, studentManager, courseManager);

        enrollmentView.enrolButton.addActionListener(this);
        enrollmentView.withdrawButton.addActionListener(this);
        enrollmentView.gradeButton.addActionListener(this);
        enrollmentView.refreshButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try {
            if (e.getSource() == enrollmentView.enrolButton) {
                handleEnrol();
            } else if (e.getSource() == enrollmentView.withdrawButton) {
                handleWithdraw();
            } else if (e.getSource() == enrollmentView.gradeButton) {
                handleRecordGrade();
            } else if (e.getSource() == enrollmentView.refreshButton) {
                enrollmentView.onDataChanged();
            }
        } catch (RuntimeException ex) {
            // DAOs wrap SQLExceptions in RuntimeException
            showWarning(ex.getMessage(), "Database error");
            enrollmentView.onDataChanged();
        }
    }

    private void handleEnrol() {
        List<Student> students = studentManager.listAllSorted();
        List<Course> courses = courseManager.listAll();
        if (students.isEmpty() || courses.isEmpty()) {
            JOptionPane.showMessageDialog(enrollmentView,
                    "You need at least one student and one course before you can enrol anyone.");
            return;
        }

        JComboBox<String> studentBox = new JComboBox<>();
        for (Student s : students) {
            studentBox.addItem(s.getId() + " - " + s.getName());
        }
        JComboBox<String> courseBox = new JComboBox<>();
        for (Course c : courses) {
            String seats = c.isFull() ? "FULL, waitlist " + c.getWaitlist().size()
                    : c.getAvailableSeats() + " seat(s) left";
            courseBox.addItem(c.getCourseCode() + " - " + c.getCourseName() + " (" + seats + ")");
        }
        JTextField semesterField = new JTextField(10);

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Student:"));
        form.add(studentBox);
        form.add(new JLabel("Course:"));
        form.add(courseBox);
        form.add(new JLabel("Semester (e.g. 2026-S2):"));
        form.add(semesterField);

        int result = JOptionPane.showConfirmDialog(enrollmentView, form, "Enrol Student",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        Student student = students.get(studentBox.getSelectedIndex());
        Course course = courses.get(courseBox.getSelectedIndex());
        String semester = semesterField.getText().trim();

        if (semester.isEmpty()) {
            showWarning("Please enter a semester.", "Invalid input");
            return;
        }
        if (semester.length() > MAX_SEMESTER_LENGTH) {
            showWarning("Semester must be at most " + MAX_SEMESTER_LENGTH + " characters.", "Invalid input");
            return;
        }
        if (student.getStatus() != StudentStatus.ACTIVE) {
            showWarning(student.getName() + " is " + student.getStatus()
                    + " - only ACTIVE students can be enrolled.", "Cannot enrol");
            return;
        }

        if (hasActiveEnrollment(student.getId(), course.getCourseCode())) {
            showWarning(student.getName() + " is already enrolled or waitlisted in "
                    + course.getCourseCode() + ".", "Cannot enrol");
            return;
        }

        try {
            boolean gotSeat = enrollmentManager.enrollStudent(student.getId(), course.getCourseCode(), semester);
            enrollmentView.onDataChanged();
            if (gotSeat) {
                JOptionPane.showMessageDialog(enrollmentView,
                        student.getName() + " is now enrolled in " + course.getCourseCode() + ".");
            } else {
                int position = courseManager.findCourse(course.getCourseCode()).getWaitlist().size();
                JOptionPane.showMessageDialog(enrollmentView,
                        course.getCourseCode() + " is full, so " + student.getName()
                        + " has been added to the waitlist (position " + position + ").");
            }
        } catch (RecordNotFoundException | InvalidInputException ex) {
            showWarning(ex.getMessage(), "Could not enrol student");
        }
    }

    private void handleWithdraw() {
        String enrollmentId = enrollmentView.getSelectedValue(EnrollmentView.COL_ID);
        if (enrollmentId == null) {
            JOptionPane.showMessageDialog(enrollmentView, "Select an enrolment first.");
            return;
        }
        String studentId = enrollmentView.getSelectedValue(EnrollmentView.COL_STUDENT_ID);
        String courseCode = enrollmentView.getSelectedValue(EnrollmentView.COL_COURSE);
        String status = enrollmentView.getSelectedValue(EnrollmentView.COL_STATUS);

        if (EnrollmentStatus.WITHDRAWN.name().equals(status)) {
            JOptionPane.showMessageDialog(enrollmentView, "That enrolment has already been withdrawn.");
            return;
        }
        if (EnrollmentStatus.COMPLETED.name().equals(status)) {
            JOptionPane.showMessageDialog(enrollmentView,
                    "That course has already been completed with a grade, so it can't be withdrawn from.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(enrollmentView,
                "Withdraw " + studentId + " from " + courseCode + "?", "Confirm withdrawal",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // check the waitlist before withdrawing so we can report who got promoted
            Course before = courseManager.findCourse(courseCode);
            boolean wasSeated = before.getEnrolledStudentIds().contains(studentId);
            String nextInLine = before.getWaitlist().peek();

            enrollmentManager.withdrawStudent(studentId, courseCode);
            enrollmentView.onDataChanged();

            String message = studentId + " has been withdrawn from " + courseCode + ".";
            if (wasSeated && nextInLine != null) {
                message += "\n" + nextInLine + " has been moved off the waitlist into the free seat.";
            }
            JOptionPane.showMessageDialog(enrollmentView, message);
        } catch (RecordNotFoundException ex) {
            showWarning(ex.getMessage(), "Could not withdraw student");
        }
    }

    private void handleRecordGrade() {
        String enrollmentId = enrollmentView.getSelectedValue(EnrollmentView.COL_ID);
        if (enrollmentId == null) {
            JOptionPane.showMessageDialog(enrollmentView, "Select an enrolment first.");
            return;
        }
        String status = enrollmentView.getSelectedValue(EnrollmentView.COL_STATUS);
        if (EnrollmentStatus.WITHDRAWN.name().equals(status)) {
            showWarning("You can't record a grade for a withdrawn enrolment.", "Cannot record grade");
            return;
        }
        if (EnrollmentStatus.WAITLISTED.name().equals(status)) {
            showWarning("This student is still on the waitlist, so there's no grade to record yet.",
                    "Cannot record grade");
            return;
        }

        String input = JOptionPane.showInputDialog(enrollmentView,
                "Grade for " + enrollmentId + " (0 - 100):");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        double grade;
        try {
            grade = Double.parseDouble(input.trim());
        } catch (NumberFormatException ex) {
            showWarning("Grade must be a number, e.g. 72.5", "Invalid input");
            return;
        }

        try {
            enrollmentManager.recordGrade(enrollmentId, grade);
            enrollmentView.onDataChanged();
        } catch (RecordNotFoundException | InvalidInputException ex) {
            showWarning(ex.getMessage(), "Could not record grade");
        }
    }

    private boolean hasActiveEnrollment(String studentId, String courseCode) {
        for (Enrollment e : enrollmentManager.getEnrollmentsForStudent(studentId)) {
            if (e.getCourseCode().equals(courseCode) && e.getStatus() != EnrollmentStatus.WITHDRAWN) {
                return true;
            }
        }
        return false;
    }

    private void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(enrollmentView, message, title, JOptionPane.WARNING_MESSAGE);
    }
}