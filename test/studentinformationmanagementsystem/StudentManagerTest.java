package studentinformationmanagementsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for StudentManager.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class StudentManagerTest {

    private StudentManager studentManager;

    @Before
    public void setUp() {
        studentManager = new StudentManager(new FakeRepository<>(Student::getId));
    }

    @Test
    public void addStudent_validData_generatesIdAndIsRetrievable() throws Exception {
        Student added = studentManager.addStudent(
                "Nishal Biju", "nishal@example.com", "0211234567",
                2, "Computer Science", 3.8, StudentStatus.ACTIVE);

        assertEquals("STU0001", added.getId());

        Student fetched = studentManager.findStudent("STU0001");
        assertEquals("Nishal Biju", fetched.getName());
        assertEquals("Computer Science", fetched.getMajor());
        assertEquals(StudentStatus.ACTIVE, fetched.getStatus());
        assertTrue(studentManager.exists("STU0001"));
    }

    @Test(expected = InvalidInputException.class)
    public void addStudent_gpaAboveMaximum_throwsInvalidInputException() throws Exception {
        studentManager.addStudent(
                "Bad GPA Student", "bad@example.com", "0211111111",
                1, "Computer Science", 4.5, StudentStatus.ACTIVE);
    }
}
