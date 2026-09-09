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
 * A course record. Handles its own enrolment logic - the enrolled students
 * live in a Set (so nobody can be double-enrolled) and once the course is
 * full, new students go into a Queue-based waitlist instead of being
 * rejected outright.
 *
 * @author Nishal
 */
public class Course implements Reportable {
    private String courseCode;
    private String courseName;
    private int creditPoints;
    private int capacity;
    private String teacherId;

    // LinkedHashSet so we still get "no duplicates" but keep the order
    // students actually enrolled in, which makes the roster easier to read
    private final Set<String> enrolledStudentIds;
    // Queue backed by a LinkedList - first person to join the waitlist is
    // the first person to get pulled off it (FIFO)
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

    // returns true if the student got an actual seat, false if they were
    // sent to the waitlist instead - EnrollmentManager uses that return
    // value to decide what message to show
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

    // if the student who left was actually enrolled (not just on the
    // waitlist) and someone's waiting, pull the next person in line into
    // the seat that just opened up. Returns their id so the caller can
    // update that student's own record too, or null if nobody got promoted.
    public String withdraw(String studentId) {
        boolean wasEnrolled = enrolledStudentIds.remove(studentId);
        waitlist.remove(studentId);

        if(wasEnrolled && !waitlist.isEmpty()) {
            String promoted = waitlist.poll();
            enrolledStudentIds.add(promoted);
            return promoted;
        }
        return null;
    }

    public String getCourseCode() {
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

    public void setCreditPoints(int creditPoints) {
        this.creditPoints = creditPoints;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getTeacherId() {
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

    // two courses are the same course if they share a course code - the
    // name/credits/capacity could theoretically differ but the code is
    // what actually identifies it
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