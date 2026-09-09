/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Nishal
 */
public class Student extends Person implements Comparable<Student> {
    
    private int yearLevel;
    private String major;
    private double gpa;
    private StudentStatus status;
    
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
    
    @Override
    public String getSummary() {
        return String.format("[STUDENT] ID: %-8s Name: %-20s Year: %d Major: %-15s GPA: %.2f Status: %s Courses: %d", 
                getId(), getName(), yearLevel, major, gpa, status, enrolledCourseCodes.size());
    }
    
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
    
    @Override
    public int compareTo(Student other) {
        int gpaCompare = Double.compare(other.gpa, this.gpa);
        if(gpaCompare != 0) {
            return gpaCompare;
        }
        return this.getName().compareToIgnoreCase(other.getName());
    } 
}
