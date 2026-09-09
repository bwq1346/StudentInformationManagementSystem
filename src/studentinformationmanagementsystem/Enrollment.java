/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.Objects;

/**
 *
 * @author Nishal
 */
public class Enrollment implements Reportable {

    private final String enrollmentId;
    private final String studentId;
    private final String courseCode;
    private final String semester;
    private Double grade;
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

   
