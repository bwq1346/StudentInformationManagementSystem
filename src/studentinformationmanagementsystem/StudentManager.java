/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Sits between ConsoleUI and StudentRepository. This is where the actual
 * "business logic" for students lives - generating ids, applying updates,
 * searching, sorting, and working out stats - so ConsoleUI doesn't have to
 * know anything about how students are stored.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class StudentManager {

    // depends on the CrudOperations interface, not a concrete repository -
    // so this class doesn't actually care that it's a StudentRepository
    // underneath (Dependency Inversion)
    private final CrudOperations<Student> studentRepository;

    public StudentManager(CrudOperations<Student> studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(String name, String email, String phone, int yearLevel,
            String major, double gpa, StudentStatus status) throws InvalidInputException {
        try {
            // id is generated here, not typed by the user - keeps ids
            // consistent and avoids clashes
            String id = IdGenerator.next("STU", 4, studentRepository.findAll(), Student::getId);
            Student student = new Student(id, name, email, phone, yearLevel, major, gpa, status);
            studentRepository.add(student);
            ActivityLogger.log("Added student " + id + " (" + name + ")");
            return student;
        } catch (DuplicateRecordException e) {
            // wrap the lower-level exceptions into one type the UI layer
            // knows how to handle
            throw new InvalidInputException(e.getMessage());
        } catch (IllegalArgumentException e) {
            // this is what catches a bad GPA/year level from the setters
            throw new InvalidInputException(e.getMessage());
        }
    }

    // full replace, not a partial update - caller has to supply every field again
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

    public boolean exists(String id) {
        return studentRepository.existsById(id);
    }

    // Student implements Comparable, so Collections.sort just uses whatever
    // compareTo() defines (GPA descending)
    public List<Student> listAllSorted() {
        List<Student> all = studentRepository.findAll();
        Collections.sort(all);
        return all;
    }

    // simple "contains" search, case-insensitive, on partial name
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

    // exact match on major, but still case-insensitive so "computer science"
    // and "Computer Science" both work
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
            return 0.0; // avoid dividing by zero when there are no students yet
        }
        double total = 0.0;
        for (Student student : all) {
            total += student.getGpa();
        }
        return total / all.size();
    }
}
