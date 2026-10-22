/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Nishal
 */
public class TeacherManagerTest {

    private TeacherManager teacherManager;

    @Before
    public void setUp() {
        teacherManager = new TeacherManager(new FakeRepository<>(Teacher::getId));
    }

    @Test
    public void addTeacher_thenDeleteTeacher_removesFromRepository() throws Exception {
        Teacher added = teacherManager.addTeacher(
                "Dr. Vikram Bugata", "vikram@example.com", "0219876543", "Computer Science");

        assertEquals("TCH001", added.getId());
        assertEquals(1, teacherManager.getTeacherCount());

        teacherManager.deleteTeacher("TCH001");

        assertFalse(teacherManager.exists("TCH001"));
        assertEquals(0, teacherManager.getTeacherCount());
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteTeacher_nonExistentId_throwsRecordNotFoundException() throws Exception {
        teacherManager.deleteTeacher("TCH999");
    }
}
