/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author Nishal
 */
public class Course implements Reportable {
    private String courseCode;
    private String courseName;
    private int creditPoints;
    private int capacity;
    private String teacherId;
    
    private final Set<String> enrolledStudentIds;
    private final Queue<String> waitlist;
    
    public Course(String courseCode, String courseName, int creditPoints, int capacity, String teacherId) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditPoints = creditPoints;
        this.capacity = capacity;
        this.teacherId = teacherId;
        this.enrolledStudentIds = new LinkedHashSet<>();
        this.waitlist = new LinkedList<>();
    }
    
    public boolean isFull() {
        return enrolledStudentIds.size() >= capacity;
    }
    
    public int getAvailableSeats() {
        return Math.max(0, capacity - enrolledStudentIds.size());
    }
    
    public boolean enrolOrWaitlist(String studentId) {
        if(enrolledStudentIds.contains(studentId) || waitlist.contains(studentId)) {
            return enrolledStudentIds.contains(studentId);
        }
        if (isFull()) {
            waitlist.offer(studentId);
            return false;
        }
        enrolledStudentIds.add(studentId);
        return true;
    }
    
    public String withdraen(String studentId) {
        boolean wasEnrolled = enrolledStudentIds.remove(studentId);
        waitlist.remove(studentId);
        
        if(wasEnrolled && !waitlist.isEmpty()) {
            String promoted = waitlist.poll();
            enrolledStudentIds.add(promoted);
            return promoted;
        }
        return null;
    }
    
    public String getrCourseCode() {
        return courseCode;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public int getCreditPoints() {
        return creditPoints;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public String getTeaccherId() {
        return teacherId;
    }
    
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
    
    public Set<String> getEnrolledStudentIds() {
        return enrolledStudentIds;
    }
    
    public Queue<String> getWaitlist() {
        return waitlist;
    }
    
    @Override
    public String toReportLine() {
        return String.format(
        "[COURSE] Code: %-8s Name: %-25s Credits: %d  Enrolled: %d/%d  Waitlisted: %d  Teacher: %s",
        courseCode, courseName, creditPoints, enrolledStudentIds.size(), capacity,
        waitlist.size(), teacherId == null ? "Unassigned" : teacherId);
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Course)) {
            return false;
        }
        return Objects.equals(courseCode, ((Course) o).courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode);
    }
}


