/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class CourseManagerTest {

    private CourseManager courseManager;

    @Before
    public void setUp() {
        courseManager = new CourseManager(new FakeRepository<>(Course::getCourseCode));
    }

    @Test(expected = InvalidInputException.class)
    public void addCourse_duplicateCourseCode_throwsInvalidInputException() throws Exception {
        // course codes are typed by the user (not auto-generated like
        // Student/Teacher ids), so a duplicate code is a real scenario a
        // user could trigger from the GUI
        courseManager.addCourse("COMP603", "Software Dev Project 2", 15, 50, null);
        courseManager.addCourse("COMP603", "Different Name, Same Code", 15, 50, null);
    }

    @Test
    public void assignTeacher_updatesCoursesTeacherId() throws Exception {
        courseManager.addCourse("COMP603", "Software Dev Project 2", 15, 50, null);

        courseManager.assignTeacher("COMP603", "TCH001");

        Course course = courseManager.findCourse("COMP603");
        assertEquals("TCH001", course.getTeacherId());
    }
}