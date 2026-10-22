/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Vikram Bugata
 */

public class EnrollmentManagerTest {

    private StudentManager studentManager;
    private CourseManager courseManager;
    private EnrollmentManager enrollmentManager;

    @Before
    public void setUp() throws Exception {
        studentManager = new StudentManager(new FakeRepository<>(Student::getId));
        courseManager = new CourseManager(new FakeRepository<>(Course::getCourseCode));
        enrollmentManager = new EnrollmentManager(
                new FakeRepository<>(Enrollment::getEnrollmentId), studentManager, courseManager);

        // STU0001, STU0002, STU0003
        studentManager.addStudent("Nishal Biju", "nishal@example.com", "0211234567",
                2, "Computer Science", 3.8, StudentStatus.ACTIVE);
        studentManager.addStudent("Vikram Bugata", "vikram@example.com", "0217654321",
                2, "Computer Science", 3.5, StudentStatus.ACTIVE);
        studentManager.addStudent("Third Student", "third@example.com", "0219999999",
                1, "Software Engineering", 3.0, StudentStatus.ACTIVE);

        // capacity 1 so the second student goes onto the waitlist
        courseManager.addCourse("COMP603", "Software Dev Project 2", 15, 1, null);
        courseManager.addCourse("COMP610", "Database Systems", 15, 30, null);
    }

    @Test
    public void enrollStudent_courseHasSpace_enrolsStudentAndCreatesRecord() throws Exception {
        boolean enrolled = enrollmentManager.enrollStudent("STU0001", "COMP610", "S1-2026");

        assertTrue(enrolled);
        assertTrue(studentManager.findStudent("STU0001").isEnrolledIn("COMP610"));
        assertTrue(courseManager.findCourse("COMP610").getEnrolledStudentIds().contains("STU0001"));

        List<Enrollment> records = enrollmentManager.getEnrollmentsForStudent("STU0001");
        assertEquals(1, records.size());
        Enrollment record = records.get(0);
        assertEquals("ENR00001", record.getEnrollmentId());
        assertEquals("COMP610", record.getCourseCode());
        assertEquals("S1-2026", record.getSemester());
        assertNull(record.getGrade());
        assertEquals(EnrollmentStatus.ENROLLED, record.getStatus());
    }

    @Test
    public void enrollStudent_courseFull_waitlistsStudent() throws Exception {
        enrollmentManager.enrollStudent("STU0001", "COMP603", "S1-2026");

        boolean enrolled = enrollmentManager.enrollStudent("STU0002", "COMP603", "S1-2026");

        assertFalse(enrolled);
        assertFalse(studentManager.findStudent("STU0002").isEnrolledIn("COMP603"));
        Course course = courseManager.findCourse("COMP603");
        assertTrue(course.getWaitlist().contains("STU0002"));
        assertEquals(1, course.getEnrolledStudentIds().size());
    }

    @Test(expected = InvalidInputException.class)
    public void enrollStudent_alreadyEnrolled_throwsInvalidInputException() throws Exception {
        enrollmentManager.enrollStudent("STU0001", "COMP610", "S1-2026");
        enrollmentManager.enrollStudent("STU0001", "COMP610", "S1-2026");
    }

    @Test(expected = InvalidInputException.class)
    public void enrollStudent_alreadyOnWaitlist_throwsInvalidInputException() throws Exception {
        enrollmentManager.enrollStudent("STU0001", "COMP603", "S1-2026");
        enrollmentManager.enrollStudent("STU0002", "COMP603", "S1-2026");
        enrollmentManager.enrollStudent("STU0002", "COMP603", "S1-2026");
    }

    @Test(expected = RecordNotFoundException.class)
    public void enrollStudent_unknownStudent_throwsRecordNotFoundException() throws Exception {
        enrollmentManager.enrollStudent("STU9999", "COMP610", "S1-2026");
    }

    @Test(expected = RecordNotFoundException.class)
    public void enrollStudent_unknownCourse_throwsRecordNotFoundException() throws Exception {
        enrollmentManager.enrollStudent("STU0001", "NOPE999", "S1-2026");
    }

    @Test
    public void withdrawStudent_marksRecordWithdrawnAndRemovesCourse() throws Exception {
        enrollmentManager.enrollStudent("STU0001", "COMP610", "S1-2026");

        enrollmentManager.withdrawStudent("STU0001", "COMP610");

        assertFalse(studentManager.findStudent("STU0001").isEnrolledIn("COMP610"));
        assertFalse(courseManager.findCourse("COMP610").getEnrolledStudentIds().contains("STU0001"));
        Enrollment record = enrollmentManager.getEnrollmentsForStudent("STU0001").get(0);
        assertEquals(EnrollmentStatus.WITHDRAWN, record.getStatus());
    }

    @Test
    public void withdrawStudent_fromFullCourse_promotesNextWaitlistedStudent() throws Exception {
        enrollmentManager.enrollStudent("STU0001", "COMP603", "S1-2026");
        enrollmentManager.enrollStudent("STU0002", "COMP603", "S1-2026"); // waitlisted
        enrollmentManager.enrollStudent("STU0003", "COMP603", "S1-2026"); // waitlisted, behind STU0002

        enrollmentManager.withdrawStudent("STU0001", "COMP603");

        Course course = courseManager.findCourse("COMP603");
        assertTrue(course.getEnrolledStudentIds().contains("STU0002"));
        assertFalse(course.getWaitlist().contains("STU0002"));
        assertTrue(studentManager.findStudent("STU0002").isEnrolledIn("COMP603"));
        // FIFO - STU0003 is still waiting
        assertTrue(course.getWaitlist().contains("STU0003"));
        assertFalse(studentManager.findStudent("STU0003").isEnrolledIn("COMP603"));
    }

    @Test(expected = RecordNotFoundException.class)
    public void withdrawStudent_unknownCourse_throwsRecordNotFoundException() throws Exception {
        enrollmentManager.withdrawStudent("STU0001", "NOPE999");
    }

    @Test
    public void recordGrade_validGrade_setsGradeAndCompletesEnrollment() throws Exception {
        enrollmentManager.enrollStudent("STU0001", "COMP610", "S1-2026");
        String enrollmentId = enrollmentManager.getEnrollmentsForStudent("STU0001").get(0).getEnrollmentId();

        enrollmentManager.recordGrade(enrollmentId, 87.5);

        Enrollment record = enrollmentManager.getEnrollmentsForStudent("STU0001").get(0);
        assertEquals(87.5, record.getGrade(), 0.001);
        assertEquals(EnrollmentStatus.COMPLETED, record.getStatus());
        assertEquals("A", record.getLetterGrade());
        assertTrue(record.isPassing());
    }

    @Test(expected = InvalidInputException.class)
    public void recordGrade_gradeAbove100_throwsInvalidInputException() throws Exception {
        enrollmentManager.enrollStudent("STU0001", "COMP610", "S1-2026");
        String enrollmentId = enrollmentManager.getEnrollmentsForStudent("STU0001").get(0).getEnrollmentId();

        enrollmentManager.recordGrade(enrollmentId, 120.0);
    }

    @Test(expected = InvalidInputException.class)
    public void recordGrade_negativeGrade_throwsInvalidInputException() throws Exception {
        enrollmentManager.enrollStudent("STU0001", "COMP610", "S1-2026");
        String enrollmentId = enrollmentManager.getEnrollmentsForStudent("STU0001").get(0).getEnrollmentId();

        enrollmentManager.recordGrade(enrollmentId, -5.0);
    }

    @Test(expected = RecordNotFoundException.class)
    public void recordGrade_unknownEnrollment_throwsRecordNotFoundException() throws Exception {
        enrollmentManager.recordGrade("ENR99999", 75.0);
    }

    @Test
    public void getEnrollmentsForCourse_returnsOnlyThatCoursesRecords() throws Exception {
        enrollmentManager.enrollStudent("STU0001", "COMP610", "S1-2026");
        enrollmentManager.enrollStudent("STU0002", "COMP610", "S1-2026");
        enrollmentManager.enrollStudent("STU0001", "COMP603", "S1-2026");

        List<Enrollment> comp610 = enrollmentManager.getEnrollmentsForCourse("COMP610");

        assertEquals(2, comp610.size());
        for (Enrollment e : comp610) {
            assertEquals("COMP610", e.getCourseCode());
        }
        assertEquals(3, enrollmentManager.getEnrollmentCount());
        assertEquals(3, enrollmentManager.listAll().size());
    }

    @Test
    public void enrollStudent_generatesSequentialIds() throws Exception {
        enrollmentManager.enrollStudent("STU0001", "COMP610", "S1-2026");
        enrollmentManager.enrollStudent("STU0002", "COMP610", "S1-2026");

        List<Enrollment> all = enrollmentManager.listAll();
        assertEquals("ENR00001", all.get(0).getEnrollmentId());
        assertEquals("ENR00002", all.get(1).getEnrollmentId());
    }
}