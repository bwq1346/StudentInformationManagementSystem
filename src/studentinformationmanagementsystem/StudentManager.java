package studentinformationmanagementsystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Business logic for students, sitting between the UI and the repository.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class StudentManager {

    private final CrudOperations<Student> studentRepository;

    public StudentManager(CrudOperations<Student> studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(String name, String email, String phone, int yearLevel,
            String major, double gpa, StudentStatus status) throws InvalidInputException {
        try {
            String id = IdGenerator.next("STU", 4, studentRepository.findAll(), Student::getId);
            Student student = new Student(id, name, email, phone, yearLevel, major, gpa, status);
            studentRepository.add(student);
            ActivityLogger.log("Added student " + id + " (" + name + ")");
            return student;
        } catch (DuplicateRecordException e) {
            throw new InvalidInputException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public void updateStudent(String id, String name, String email, String phone, int yearLevel,
            String major, double gpa, StudentStatus status)
            throws RecordNotFoundException, InvalidInputException {
        try {
            Student existing = studentRepository.findById(id);
            existing.setName(name);
            existing.setEmail(email);
            existing.setPhone(phone);
            existing.setYearLevel(yearLevel);
            existing.setMajor(major);
            existing.setGpa(gpa);
            existing.setStatus(status);
            studentRepository.update(existing);
            ActivityLogger.log("Updated student " + id);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public void deleteStudent(String id) throws RecordNotFoundException {
        studentRepository.delete(id);
        ActivityLogger.log("Deleted student " + id);
    }

    public Student findStudent(String id) throws RecordNotFoundException {
        return studentRepository.findById(id);
    }

    // persists a Student that was changed elsewhere (e.g. by EnrollmentManager)
    public void saveStudent(Student student) throws RecordNotFoundException {
        studentRepository.update(student);
    }

    public boolean exists(String id) {
        return studentRepository.existsById(id);
    }

    public List<Student> listAllSorted() {
        List<Student> all = studentRepository.findAll();
        Collections.sort(all);
        return all;
    }

    public List<Student> searchByName(String keyword) {
        List<Student> matches = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Student student : studentRepository.findAll()) {
            if (student.getName().toLowerCase().contains(lowerKeyword)) {
                matches.add(student);
            }
        }
        return matches;
    }

    public List<Student> searchByMajor(String major) {
        List<Student> matches = new ArrayList<>();
        for (Student student : studentRepository.findAll()) {
            if (student.getMajor().equalsIgnoreCase(major)) {
                matches.add(student);
            }
        }
        return matches;
    }

    public int getStudentCount() {
        return studentRepository.count();
    }

    public double getAverageGpa() {
        List<Student> all = studentRepository.findAll();
        if (all.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Student student : all) {
            total += student.getGpa();
        }
        return total / all.size();
    }
}
