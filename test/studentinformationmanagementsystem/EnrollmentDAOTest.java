package studentinformationmanagementsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.sql.Statement;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests EnrollmentDAO against a temporary Derby database, so the real
 * data/SIMSDB database is never touched.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class EnrollmentDAOTest {

    private static File tempDir;
    private EnrollmentDAO dao;

    @BeforeClass
    public static void createTempDatabase() throws Exception {
        // Derby resolves the relative "data/SIMSDB" path against derby.system.home
        tempDir = Files.createTempDirectory("sims-test").toFile();
        new File(tempDir, "data").mkdirs();
        System.setProperty("derby.system.home", tempDir.getAbsolutePath());
    }

    @AfterClass
    public static void deleteTempDatabase() {
        DBConnectionManager.getInstance().shutdown();
        deleteRecursively(tempDir);
    }

    @Before
    public void setUp() throws Exception {
        try (Statement statement = DBConnectionManager.getInstance().getConnection().createStatement()) {
            statement.executeUpdate("DELETE FROM ENROLLMENT");
        }
        dao = new EnrollmentDAO();
    }

    @Test
    public void add_thenFindById_returnsSameValues() throws Exception {
        dao.add(new Enrollment("ENR00001", "STU0001", "COMP603", "2026-S2", null, EnrollmentStatus.ENROLLED));

        Enrollment found = dao.findById("ENR00001");

        assertEquals("STU0001", found.getStudentId());
        assertEquals("COMP603", found.getCourseCode());
        assertEquals("2026-S2", found.getSemester());
        assertNull(found.getGrade());
        assertEquals(EnrollmentStatus.ENROLLED, found.getStatus());
        assertTrue(dao.existsById("ENR00001"));
        assertEquals(1, dao.count());
    }

    @Test
    public void update_savesGradeAndStatus() throws Exception {
        Enrollment enrollment = new Enrollment("ENR00001", "STU0001", "COMP603", "2026-S2",
                null, EnrollmentStatus.ENROLLED);
        dao.add(enrollment);

        enrollment.setGrade(72.5);
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        dao.update(enrollment);

        Enrollment found = dao.findById("ENR00001");
        assertEquals(72.5, found.getGrade(), 0.001);
        assertEquals(EnrollmentStatus.COMPLETED, found.getStatus());
    }

    @Test
    public void waitlistedStatus_isStoredAndLoaded() throws Exception {
        dao.add(new Enrollment("ENR00001", "STU0002", "COMP603", "2026-S2", null, EnrollmentStatus.WAITLISTED));

        assertEquals(EnrollmentStatus.WAITLISTED, dao.findById("ENR00001").getStatus());
    }

    @Test
    public void delete_removesRecord() throws Exception {
        dao.add(new Enrollment("ENR00001", "STU0001", "COMP603", "2026-S2", null, EnrollmentStatus.ENROLLED));

        dao.delete("ENR00001");

        assertFalse(dao.existsById("ENR00001"));
        assertEquals(0, dao.count());
    }

    @Test(expected = DuplicateRecordException.class)
    public void add_duplicateId_throwsDuplicateRecordException() throws Exception {
        dao.add(new Enrollment("ENR00001", "STU0001", "COMP603", "2026-S2", null, EnrollmentStatus.ENROLLED));
        dao.add(new Enrollment("ENR00001", "STU0002", "COMP610", "2026-S2", null, EnrollmentStatus.ENROLLED));
    }

    @Test(expected = RecordNotFoundException.class)
    public void findById_unknownId_throwsRecordNotFoundException() throws Exception {
        dao.findById("ENR99999");
    }

    private static void deleteRecursively(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }
}