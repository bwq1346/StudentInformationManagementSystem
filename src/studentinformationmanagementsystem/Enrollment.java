/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.Objects;

/**
 * Links one student to one course for one semester. This is basically the
 * "join table" of the system - it's what makes it possible for a student to
 * be in more than one course, and a course to have more than one student,
 * each with their own grade and status.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class Enrollment implements Reportable {

    private final String enrollmentId;
    private final String studentId;
    private final String courseCode;
    private final String semester;
    private Double grade; // Double, not double, so it can be null until a grade's actually recorded
    private EnrollmentStatus status;

    public Enrollment(String enrollmentId, String studentId, String courseCode,
                       String semester, Double grade, EnrollmentStatus status) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.semester = semester;
        this.grade = grade;
        this.status = status;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getSemester() {
        return semester;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        if (grade < 0.0 || grade > 100.0) {
            throw new IllegalArgumentException("Grade must be between 0 and 100.");
        }
        this.grade = grade;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public boolean isPassing() {
        return grade != null && grade >= 50.0;
    }

    // just a simple grade -> letter grade lookup, highest first
    public String getLetterGrade() {
        if (grade == null) {
            return "N/A";
        }
        if (grade >= 90) return "A+";
        if (grade >= 85) return "A";
        if (grade >= 80) return "A-";
        if (grade >= 75) return "B+";
        if (grade >= 70) return "B";
        if (grade >= 65) return "B-";
        if (grade >= 60) return "C+";
        if (grade >= 55) return "C";
        if (grade >= 50) return "C-";
        return "D";
    }

    @Override
    public String toReportLine() {
        return enrollmentId + "," + studentId + "," + courseCode + "," + semester + "," +
                (grade == null ? "N/A" : String.format("%.1f", grade)) + "," + getLetterGrade() + "," + status;
    }

    @Override
    public String toString() {
        return String.format("[ENROLLMENT] %s | Student: %-8s Course: %-8s Semester: %-8s Grade: %-6s (%s)  Status: %s",
                enrollmentId, studentId, courseCode, semester,
                grade == null ? "N/A" : String.format("%.1f", grade), getLetterGrade(), status);
    }

    // an enrolment is identified by its own id, not by the student/course
    // combo - that way the same student can enrol in the same course again
    // in a later semester and it's a different record
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Enrollment)) {
            return false;
        }
        return Objects.equals(enrollmentId, ((Enrollment) o).enrollmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentId);
    }
}