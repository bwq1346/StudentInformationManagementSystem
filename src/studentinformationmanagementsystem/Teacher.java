/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nishal
 */
public class Teacher extends Person {
    private String department;
    private final List<String> coursesTaught;
    
    public Teacher(String staffId, String name, String email, String phone, String department) {
        super(staffId, name, email, phone);
        this.department = department;
        this.coursesTaught = new ArrayList<>();
    }
    
    @Override
    public String getRole() {
        return "Teacher";
    }
    
    @Override
    public String getSummary() {
        return String.format(
        "[TEACHER] ID: %-8s Name: %-20s Department: %-15s Courses taught: %d", getId(), getName(), department, coursesTaught.size());
    }
    
    @Override
    public String toReportLine() {
        return getId() + "," + getName() + "," + department + "," + coursesTaught.size();
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public List<String> getCoursesTaught() {
        return coursesTaught;
    }
    
    public void assignCourse(String courseCode) {
        if(!coursesTaught.contains(courseCode)) {
            coursesTaught.add(courseCode);
        }
    }
    
    public void unassignCourse(String courseCode) {
        coursesTaught.remove(courseCode);
    }
    
}
