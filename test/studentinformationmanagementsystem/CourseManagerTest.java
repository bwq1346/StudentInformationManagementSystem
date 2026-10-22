package studentinformationmanagementsystem;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for CourseManager.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class CourseManagerTest {

    private CourseManager courseManager;

    @Before
    public void setUp() {
        courseManager = new CourseManager(new FakeRepository<>(Course::getCourseCode));
    }

    @Test(expected = InvalidInputException.class)
    public void addCourse_duplicateCourseCode_throwsInvalidInputException() throws Exception {
        // course codes are typed in by the user, so duplicates are possible
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