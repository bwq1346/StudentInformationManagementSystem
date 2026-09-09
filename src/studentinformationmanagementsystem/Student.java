/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.HashSet;
import java.util.Set;

/**
 * A student record. Extends Person and adds the student-specific fields
 * (year level, major, GPA, status) plus the set of course codes the
 * student is currently enrolled in.
 *
 * @author Nishal
 */
public class Student extends Person implements Comparable<Student> {

    private int yearLevel;
    private String major;
    private double gpa;
    private StudentStatus status;

    // Set instead of List - a student can't be enrolled in the same course
    // twice, and we don't care what order the codes came in.
    private final Set<String> enrolledCourseCodes;

    public Student(String studentId, String name, String email, String phone,
            int yearLevel, String major, double gpa, StudentStatus status) {
        super(studentId, name, email, phone);
        this.yearLevel = yearLevel;
        this.major = major;
        this.gpa = gpa;
        this.status = status;
        this.enrolledCourseCodes = new HashSet<>();
    }

    @Override
    public String getRole() {
        return "Student";
    }

    // one-line view used pretty much everywhere in the console output
    @Override
    public String getSummary() {
        return String.format("[STUDENT] ID: %-8s Name: %-20s Year: %d Major: %-15s GPA: %.2f Status: %s Courses: %d",
                getId(), getName(), yearLevel, major, gpa, status, enrolledCourseCodes.size());
    }

    // shorter, comma-separated version - this is what actually gets written
    // to the report export file
    @Override
    public String toReportLine() {
        return getId() + "," + getName() + "," + major + "," + yearLevel + "," + String.format("%.2f", gpa) + "," + status;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        if (yearLevel < 1 || yearLevel > 8) {
            throw new IllegalArgumentException("Year Level must be between 1 and 8.");
        }
        this.yearLevel = yearLevel;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        if (gpa < 0.0 || gpa > 4.0) {
            throw new IllegalArgumentException("GPA must be between 0.0 and 4.0");
        }
        this.gpa = gpa;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    public Set<String> getEnrolledCourseCodes() {
        return enrolledCourseCodes;
    }

    public boolean isEnrolledIn(String courseCode) {
        return enrolledCourseCodes.contains(courseCode);
    }

    public void addCourseCode(String courseCode) {
        enrolledCourseCodes.add(courseCode);
    }

    public void removeCourseCode(String courseCode) {
        enrolledCourseCodes.remove(courseCode);
    }

    // Comparable so we can just call Collections.sort() on a list of students.
    // Highest GPA comes first (that's why it's other.gpa vs this.gpa and not
    // the other way around), and if two students land on the same GPA we
    // fall back to alphabetical name so the ordering isn't random.
    @Override
    public int compareTo(Student other) {
        int gpaCompare = Double.compare(other.gpa, this.gpa);
        if(gpaCompare != 0) {
            return gpaCompare;
        }
        return this.getName().compareToIgnoreCase(other.getName());
    }
}
