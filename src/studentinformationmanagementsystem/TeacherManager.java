/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.List;

/**
 * Business logic for teachers - much simpler than StudentManager since
 * teachers don't need searching or sorting for this project, just the
 * standard CRUD plus id generation.
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
