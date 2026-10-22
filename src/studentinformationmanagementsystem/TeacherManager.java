package studentinformationmanagementsystem;
import java.util.List;

/**
 * Business logic for teachers, sitting between the UI and the repository.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class TeacherManager {

    private final CrudOperations<Teacher> teacherRepository;

    public TeacherManager(CrudOperations<Teacher> teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public Teacher addTeacher(String name, String email, String phone, String department) throws InvalidInputException {
        try {
            String id = IdGenerator.next("TCH", 3, teacherRepository.findAll(), Teacher::getId);
            Teacher teacher = new Teacher(id, name, email, phone, department);
            teacherRepository.add(teacher);
            ActivityLogger.log("Added teacher " + id + " (" + name + ")");
            return teacher;
        } catch (DuplicateRecordException e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public void updateTeacher(String id, String name, String email, String phone, String department)
            throws RecordNotFoundException {
        Teacher existing = teacherRepository.findById(id);
        existing.setName(name);
        existing.setEmail(email);
        existing.setPhone(phone);
        existing.setDepartment(department);
        teacherRepository.update(existing);
        ActivityLogger.log("Updated teacher " + id);
    }

    public void deleteTeacher(String id) throws RecordNotFoundException {
        teacherRepository.delete(id);
        ActivityLogger.log("Deleted teacher " + id);
    }

    public Teacher findTeacher(String id) throws RecordNotFoundException {
        return teacherRepository.findById(id);
    }

    // keeps the teacher's course list in step with Course.teacherId
    public void assignCourseToTeacher(String teacherId, String courseCode) throws RecordNotFoundException {
        Teacher teacher = teacherRepository.findById(teacherId);
        teacher.assignCourse(courseCode);
        teacherRepository.update(teacher);
    }

    public void unassignCourseFromTeacher(String teacherId, String courseCode) throws RecordNotFoundException {
        Teacher teacher = teacherRepository.findById(teacherId);
        teacher.unassignCourse(courseCode);
        teacherRepository.update(teacher);
    }

    public boolean exists(String id) {
        return teacherRepository.existsById(id);
    }

    public List<Teacher> listAll() {
        return teacherRepository.findAll();
    }

    public int getTeacherCount() {
        return teacherRepository.count();
    }
}
