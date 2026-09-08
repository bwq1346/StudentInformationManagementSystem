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
                getId(), getName(), yearLevel, major)
    }
    
}
