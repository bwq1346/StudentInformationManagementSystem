/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentinformationmanagementsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vikram Bugata
 */
public class EnrollmentManager {

    private final CrudOperations<Enrollment> enrollmentRepository;
    private final StudentManager studentManager;
    private final CourseManager courseManager;

    public EnrollmentManager(CrudOperations<Enrollment> enrollmentRepository,
            StudentManager studentManager, CourseManager courseManager) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentManager = studentManager;
        this.courseManager = courseManager;
    }

    public boolean enrollStudent(String studentId, String courseCode, String semester)
            throws RecordNotFoundException, InvalidInputException {
        Student student = studentManager.findStudent(studentId);
        Course course = courseManager.findCourse(courseCode);

        if (student.isEnrolledIn(courseCode)) {
            throw new InvalidInputException("Student " + studentId + " is already enrolled in " + courseCode + ".");
        }

        if (course.getWaitlist().contains(studentId)) {
            throw new InvalidInputException("Student " + studentId
                    + " is already on the waitlist for " + courseCode + ".");
        }

        boolean enrolledImmediately = course.enrolOrWaitlist(studentId);
        if (enrolledImmediately) {
            student.addCourseCode(courseCode);
        }

        courseManager.saveCourse(course);
        studentManager.saveStudent(student);

        try {
            String enrollmentId = IdGenerator.next("ENR", 5, enrollmentRepository.findAll(),
                    Enrollment::getEnrollmentId);
            EnrollmentStatus status = EnrollmentStatus.ENROLLED;
            Enrollment enrollment = new Enrollment(enrollmentId, studentId, courseCode, semester, null, status);
            enrollmentRepository.add(enrollment);
            ActivityLogger.log((enrolledImmediately ? "Enrolled" : "Waitlisted") +
                    " student " + studentId + " in course " + courseCode);
        } catch (DuplicateRecordException e) {
            throw new InvalidInputException(e.getMessage());
        }
        return enrolledImmediately;
    }

    public void withdrawStudent(String studentId, String courseCode) throws RecordNotFoundException {
        Student student = studentManager.findStudent(studentId);
        Course course = courseManager.findCourse(courseCode);

        student.removeCourseCode(courseCode);
        String promotedStudentId = course.withdraw(studentId);

        studentManager.saveStudent(student);
        courseManager.saveCourse(course);

        for (Enrollment enrollment : enrollmentRepository.findAll()) {
            if (enrollment.getStudentId().equals(studentId) && enrollment.getCourseCode().equals(courseCode)
                    && enrollment.getStatus() != EnrollmentStatus.WITHDRAWN) {
                enrollment.setStatus(EnrollmentStatus.WITHDRAWN);
                enrollmentRepository.update(enrollment);
            }
        }

        if (promotedStudentId != null) {
            try {
                Student promotedStudent = studentManager.findStudent(promotedStudentId);
                promotedStudent.addCourseCode(courseCode);
                studentManager.saveStudent(promotedStudent);
                ActivityLogger.log("Promoted waitlisted student " + promotedStudentId + " into course " + courseCode);
            } catch (RecordNotFoundException ignored) {
            }
        }
        ActivityLogger.log("Withdrew student " + studentId + " from course " + courseCode);
    }

    public void recordGrade(String enrollmentId, double grade) throws RecordNotFoundException, InvalidInputException {
        try {
            Enrollment enrollment = enrollmentRepository.findById(enrollmentId);
            enrollment.setGrade(grade);
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollmentRepository.update(enrollment);
            ActivityLogger.log("Recorded grade " + grade + " for enrollment " + enrollmentId);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public List<Enrollment> getEnrollmentsForStudent(String studentId) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : enrollmentRepository.findAll()) {
            if (enrollment.getStudentId().equals(studentId)) {
                result.add(enrollment);
            }
        }
        return result;
    }

    public List<Enrollment> getEnrollmentsForCourse(String courseCode) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : enrollmentRepository.findAll()) {
            if (enrollment.getCourseCode().equals(courseCode)) {
                result.add(enrollment);
            }
        }
        return result;
    }

    public List<Enrollment> listAll() {
        return enrollmentRepository.findAll();
    }

    public int getEnrollmentCount() {
        return enrollmentRepository.count();
    }
}

