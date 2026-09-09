/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.util.List;
import java.util.Scanner;

/**
 * This is the actual text menu that the user sees and types into. It's
 * intentionally "dumb" - it just reads input, hands it off to the right
 * manager, and prints whatever comes back. None of the actual business
 * rules live in here, they're all in the manager classes, which makes this
 * class a lot easier to follow even though it's the biggest file in the
 * project.
 *
 * @author Nishal
 */
public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final StudentManager studentManager;
    private final TeacherManager teacherManager;
    private final CourseManager courseManager;
    private final EnrollmentManager enrollmentManager;
    private final ReportManager reportManager;

    // this constructor is basically all the "wiring" for the app - it
    // builds the repositories first, then the managers that depend on them,
    // then finally ReportManager which needs all three managers
    public ConsoleUI() {
        this.studentRepository = new StudentRepository("data/students.txt");
        this.teacherRepository = new TeacherRepository("data/teachers.txt");
        this.courseRepository = new CourseRepository("data/courses.txt");
        this.enrollmentRepository = new EnrollmentRepository("data/enrollments.txt");

        this.studentManager = new StudentManager(studentRepository);
        this.teacherManager = new TeacherManager(teacherRepository);
        this.courseManager = new CourseManager(courseRepository);
        this.enrollmentManager = new EnrollmentManager(enrollmentRepository, studentManager, courseManager);
        this.reportManager = new ReportManager(studentManager, courseManager, enrollmentManager);
    }

    public void start() {
        loadAllData();
        printWelcomeBanner();

        try {
            boolean running = true;
            while (running) {
                printMainMenu();
                int choice = InputValidator.readMenuChoice(scanner, "Enter your choice: ", 0, 6);
                switch (choice) {
                    case 1 -> studentMenu();
                    case 2 -> teacherMenu();
                    case 3 -> courseMenu();
                    case 4 -> enrollmentMenu();
                    case 5 -> reportsMenu();
                    case 6 -> {
                        saveAllData();
                        System.out.println("All data saved.\n");
                    }
                    case 0 -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            }
        } finally {
            // finally makes sure we still save even if something above
            // throws an exception we didn't expect - don't want to lose
            // a session's worth of changes over one bug
            saveAllData();
            scanner.close();
        }
        System.out.println("\nGoodbye! All data has been saved to the 'data' folder.");
    }

    private void loadAllData() {
        studentRepository.loadFromFile();
        teacherRepository.loadFromFile();
        courseRepository.loadFromFile();
        enrollmentRepository.loadFromFile();
    }

    private void saveAllData() {
        studentRepository.saveToFile();
        teacherRepository.saveToFile();
        courseRepository.saveToFile();
        enrollmentRepository.saveToFile();
    }

    private void printWelcomeBanner() {
        System.out.println("============================================================");
        System.out.println("   COMP603/ENSE600  -  Student Information Management System");
        System.out.println("   (Command-line Interface - Assignment 1)");
        System.out.println("============================================================");
        System.out.println("Loaded " + studentManager.getStudentCount() + " student(s), " +
                teacherManager.getTeacherCount() + " teacher(s), " +
                courseManager.getCourseCount() + " course(s), " +
                enrollmentManager.getEnrollmentCount() + " enrolment(s) from the data folder.\n");
    }

    private void printMainMenu() {
        System.out.println("---------------------- MAIN MENU ----------------------");
        System.out.println("1. Student Management");
        System.out.println("2. Teacher Management");
        System.out.println("3. Course Management");
        System.out.println("4. Enrollment Management");
        System.out.println("5. Reports");
        System.out.println("6. Save All Data Now");
        System.out.println("0. Save & Exit");
        System.out.println("---------------------------------------------------------");
    }

    // ---------------------------------------------------------------
    // Student Management
    // ---------------------------------------------------------------

    private void studentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- STUDENT MANAGEMENT -----");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students (sorted by GPA)");
            System.out.println("3. Find Student by ID");
            System.out.println("4. Search Students by Name");
            System.out.println("5. Search Students by Major");
            System.out.println("6. Update Student");
            System.out.println("7. Delete Student");
            System.out.println("0. Back to Main Menu");
            int choice = InputValidator.readMenuChoice(scanner, "Enter your choice: ", 0, 7);
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewAllStudents();
                case 3 -> findStudentById();
                case 4 -> searchStudentsByName();
                case 5 -> searchStudentsByMajor();
                case 6 -> updateStudent();
                case 7 -> deleteStudent();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addStudent() {
        System.out.println("\n-- Add Student --");
        // every one of these already comes back validated - InputValidator
        // won't let a bad value get past its own loop, so this method
        // doesn't need to check anything itself
        String name = InputValidator.readNonEmptyString(scanner, "Full name: ");
        String email = InputValidator.readEmail(scanner, "Email: ");
        String phone = InputValidator.readPhone(scanner, "Phone: ");
        int yearLevel = InputValidator.readInt(scanner, "Year level (1-8): ", 1, 8);
        String major = InputValidator.readNonEmptyString(scanner, "Major: ");
        double gpa = InputValidator.readDouble(scanner, "GPA (0.0-4.0): ", 0.0, 4.0);
        StudentStatus status = readStudentStatus();

        try {
            Student student = studentManager.addStudent(name, email, phone, yearLevel, major, gpa, status);
            System.out.println("Student added successfully with ID: " + student.getId());
        } catch (InvalidInputException e) {
            System.out.println("Could not add student: " + e.getMessage());
        }
    }

    private void viewAllStudents() {
        System.out.println("\n-- All Students (sorted by GPA, highest first) --");
        List<Student> students = studentManager.listAllSorted();
        if (students.isEmpty()) {
            System.out.println("(no students on record)");
            return;
        }
        for (Student s : students) {
            System.out.println(s.getSummary());
        }
        System.out.printf("Total: %d student(s). Average GPA: %.2f%n", students.size(), studentManager.getAverageGpa());
    }

    private void findStudentById() {
        String id = InputValidator.readNonEmptyString(scanner, "\nEnter Student ID: ");
        try {
            Student student = studentManager.findStudent(id);
            System.out.println(student.getSummary());
            System.out.println("Enrolled courses: " + student.getEnrolledCourseCodes());
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void searchStudentsByName() {
        String keyword = InputValidator.readNonEmptyString(scanner, "\nEnter (part of) name to search: ");
        List<Student> matches = studentManager.searchByName(keyword);
        if (matches.isEmpty()) {
            System.out.println("No students matched '" + keyword + "'.");
        } else {
            matches.forEach(s -> System.out.println(s.getSummary()));
        }
    }

    private void searchStudentsByMajor() {
        String major = InputValidator.readNonEmptyString(scanner, "\nEnter major to search: ");
        List<Student> matches = studentManager.searchByMajor(major);
        if (matches.isEmpty()) {
            System.out.println("No students found in major '" + major + "'.");
        } else {
            matches.forEach(s -> System.out.println(s.getSummary()));
        }
    }

    private void updateStudent() {
        String id = InputValidator.readNonEmptyString(scanner, "\nEnter Student ID to update: ");
        try {
            Student existing = studentManager.findStudent(id);
            System.out.println("Current record: " + existing.getSummary());
            // deliberately asking for every field again rather than letting
            // the user skip fields they don't want to change - simpler to
            // get right, and avoids weird partial-update bugs
            System.out.println("Enter new values (you must re-enter every field):");
            String name = InputValidator.readNonEmptyString(scanner, "Full name: ");
            String email = InputValidator.readEmail(scanner, "Email: ");
            String phone = InputValidator.readPhone(scanner, "Phone: ");
            int yearLevel = InputValidator.readInt(scanner, "Year level (1-8): ", 1, 8);
            String major = InputValidator.readNonEmptyString(scanner, "Major: ");
            double gpa = InputValidator.readDouble(scanner, "GPA (0.0-4.0): ", 0.0, 4.0);
            StudentStatus status = readStudentStatus();

            studentManager.updateStudent(id, name, email, phone, yearLevel, major, gpa, status);
            System.out.println("Student " + id + " updated successfully.");
        } catch (RecordNotFoundException | InvalidInputException e) {
            System.out.println("Could not update student: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        String id = InputValidator.readNonEmptyString(scanner, "\nEnter Student ID to delete: ");
        // always confirm before an actual delete - too easy to fat-finger
        // an ID otherwise
        if (!InputValidator.readYesNo(scanner, "Are you sure you want to delete student " + id + "?")) {
            System.out.println("Delete cancelled.");
            return;
        }
        try {
            studentManager.deleteStudent(id);
            System.out.println("Student " + id + " deleted.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private StudentStatus readStudentStatus() {
        String value = InputValidator.readOneOf(scanner,
                "Status (ACTIVE/SUSPENDED/GRADUATED/WITHDRAWN): ",
                "ACTIVE", "SUSPENDED", "GRADUATED", "WITHDRAWN");
        return StudentStatus.valueOf(value);
    }

    // ---------------------------------------------------------------
    // Teacher Management
    // ---------------------------------------------------------------

    private void teacherMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- TEACHER MANAGEMENT -----");
            System.out.println("1. Add Teacher");
            System.out.println("2. View All Teachers");
            System.out.println("3. Find Teacher by ID");
            System.out.println("4. Update Teacher");
            System.out.println("5. Delete Teacher");
            System.out.println("0. Back to Main Menu");
            int choice = InputValidator.readMenuChoice(scanner, "Enter your choice: ", 0, 5);
            switch (choice) {
                case 1 -> addTeacher();
                case 2 -> viewAllTeachers();
                case 3 -> findTeacherById();
                case 4 -> updateTeacher();
                case 5 -> deleteTeacher();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addTeacher() {
        System.out.println("\n-- Add Teacher --");
        String name = InputValidator.readNonEmptyString(scanner, "Full name: ");
        String email = InputValidator.readEmail(scanner, "Email: ");
        String phone = InputValidator.readPhone(scanner, "Phone: ");
        String department = InputValidator.readNonEmptyString(scanner, "Department: ");
        try {
            Teacher teacher = teacherManager.addTeacher(name, email, phone, department);
            System.out.println("Teacher added successfully with ID: " + teacher.getId());
        } catch (InvalidInputException e) {
            System.out.println("Could not add teacher: " + e.getMessage());
        }
    }

    private void viewAllTeachers() {
        System.out.println("\n-- All Teachers --");
        List<Teacher> teachers = teacherManager.listAll();
        if (teachers.isEmpty()) {
            System.out.println("(no teachers on record)");
            return;
        }
        teachers.forEach(t -> System.out.println(t.getSummary()));
    }

    private void findTeacherById() {
        String id = InputValidator.readNonEmptyString(scanner, "\nEnter Teacher ID: ");
        try {
            Teacher teacher = teacherManager.findTeacher(id);
            System.out.println(teacher.getSummary());
            System.out.println("Courses taught: " + teacher.getCoursesTaught());
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateTeacher() {
        String id = InputValidator.readNonEmptyString(scanner, "\nEnter Teacher ID to update: ");
        try {
            Teacher existing = teacherManager.findTeacher(id);
            System.out.println("Current record: " + existing.getSummary());
            String name = InputValidator.readNonEmptyString(scanner, "Full name: ");
            String email = InputValidator.readEmail(scanner, "Email: ");
            String phone = InputValidator.readPhone(scanner, "Phone: ");
            String department = InputValidator.readNonEmptyString(scanner, "Department: ");
            teacherManager.updateTeacher(id, name, email, phone, department);
            System.out.println("Teacher " + id + " updated successfully.");
        } catch (RecordNotFoundException e) {
            System.out.println("Could not update teacher: " + e.getMessage());
        }
    }

    private void deleteTeacher() {
        String id = InputValidator.readNonEmptyString(scanner, "\nEnter Teacher ID to delete: ");
        if (!InputValidator.readYesNo(scanner, "Are you sure you want to delete teacher " + id + "?")) {
            System.out.println("Delete cancelled.");
            return;
        }
        try {
            teacherManager.deleteTeacher(id);
            System.out.println("Teacher " + id + " deleted.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Course Management
    // ---------------------------------------------------------------

    private void courseMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- COURSE MANAGEMENT -----");
            System.out.println("1. Add Course");
            System.out.println("2. View All Courses");
            System.out.println("3. Find Course by Code");
            System.out.println("4. Update Course");
            System.out.println("5. Delete Course");
            System.out.println("6. Assign Teacher to Course");
            System.out.println("0. Back to Main Menu");
            int choice = InputValidator.readMenuChoice(scanner, "Enter your choice: ", 0, 6);
            switch (choice) {
                case 1 -> addCourse();
                case 2 -> viewAllCourses();
                case 3 -> findCourseByCode();
                case 4 -> updateCourse();
                case 5 -> deleteCourse();
                case 6 -> assignTeacherToCourse();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addCourse() {
        System.out.println("\n-- Add Course --");
        // uppercase the code so "comp603" and "COMP603" end up as the same course
        String code = InputValidator.readNonEmptyString(scanner, "Course code (e.g. COMP603): ").toUpperCase();
        String name = InputValidator.readNonEmptyString(scanner, "Course name: ");
        int credits = InputValidator.readInt(scanner, "Credit points: ", 1, 60);
        int capacity = InputValidator.readInt(scanner, "Capacity: ", 1, 1000);
        String teacherId = null;
        if (InputValidator.readYesNo(scanner, "Assign a teacher now?")) {
            teacherId = InputValidator.readNonEmptyString(scanner, "Teacher ID: ");
            // still let the course get created even if the id doesn't exist -
            // just warn about it, rather than blocking the whole thing
            if (!teacherManager.exists(teacherId)) {
                System.out.println(
                        "Warning: no teacher with that ID exists yet - course saved without a valid teacher link.");
            }
        }
        try {
            Course course = courseManager.addCourse(code, name, credits, capacity, teacherId);
            System.out.println("Course added successfully: " + course.getCourseCode());
        } catch (InvalidInputException e) {
            System.out.println("Could not add course: " + e.getMessage());
        }
    }

    private void viewAllCourses() {
        System.out.println("\n-- All Courses --");
        List<Course> courses = courseManager.listAll();
        if (courses.isEmpty()) {
            System.out.println("(no courses on record)");
            return;
        }
        courses.forEach(System.out::println);
    }

    private void findCourseByCode() {
        String code = InputValidator.readNonEmptyString(scanner, "\nEnter Course Code: ").toUpperCase();
        try {
            System.out.println(courseManager.findCourse(code));
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateCourse() {
        String code = InputValidator.readNonEmptyString(scanner, "\nEnter Course Code to update: ").toUpperCase();
        try {
            Course existing = courseManager.findCourse(code);
            System.out.println("Current record: " + existing);
            String name = InputValidator.readNonEmptyString(scanner, "Course name: ");
            int credits = InputValidator.readInt(scanner, "Credit points: ", 1, 60);
            int capacity = InputValidator.readInt(scanner, "Capacity: ", 1, 1000);
            courseManager.updateCourse(code, name, credits, capacity);
            System.out.println("Course " + code + " updated successfully.");
        } catch (RecordNotFoundException e) {
            System.out.println("Could not update course: " + e.getMessage());
        }
    }

    private void deleteCourse() {
        String code = InputValidator.readNonEmptyString(scanner, "\nEnter Course Code to delete: ").toUpperCase();
        if (!InputValidator.readYesNo(scanner, "Are you sure you want to delete course " + code + "?")) {
            System.out.println("Delete cancelled.");
            return;
        }
        try {
            courseManager.deleteCourse(code);
            System.out.println("Course " + code + " deleted.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void assignTeacherToCourse() {
        String code = InputValidator.readNonEmptyString(scanner, "\nEnter Course Code: ").toUpperCase();
        String teacherId = InputValidator.readNonEmptyString(scanner, "Enter Teacher ID: ");
        try {
            // both sides of the relationship need updating - the course
            // needs to know its teacher, and the teacher needs the course
            // added to their own "courses taught" list
            Teacher teacher = teacherManager.findTeacher(teacherId);
            courseManager.assignTeacher(code, teacherId);
            teacher.assignCourse(code);
            System.out.println("Teacher " + teacherId + " assigned to course " + code + ".");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Enrollment Management
    // ---------------------------------------------------------------

    private void enrollmentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- ENROLLMENT MANAGEMENT -----");
            System.out.println("1. Enroll Student in Course");
            System.out.println("2. Withdraw Student from Course");
            System.out.println("3. Record Grade for an Enrollment");
            System.out.println("4. View Enrollments for a Student");
            System.out.println("5. View Enrollments for a Course");
            System.out.println("6. View All Enrollments");
            System.out.println("0. Back to Main Menu");
            int choice = InputValidator.readMenuChoice(scanner, "Enter your choice: ", 0, 6);
            switch (choice) {
                case 1 -> enrollStudentInCourse();
                case 2 -> withdrawStudentFromCourse();
                case 3 -> recordGrade();
                case 4 -> viewEnrollmentsForStudent();
                case 5 -> viewEnrollmentsForCourse();
                case 6 -> viewAllEnrollments();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void enrollStudentInCourse() {
        String studentId = InputValidator.readNonEmptyString(scanner, "\nStudent ID: ");
        String courseCode = InputValidator.readNonEmptyString(scanner, "Course Code: ").toUpperCase();
        String semester = InputValidator.readNonEmptyString(scanner, "Semester (e.g. 2026S2): ");
        try {
            // the boolean tells us whether they got a seat or landed on
            // the waitlist, so we can show the right message either way
            boolean enrolled = enrollmentManager.enrollStudent(studentId, courseCode, semester);
            System.out.println(enrolled
                    ? "Student enrolled successfully."
                    : "The course is full - student has been placed on the waitlist.");
        } catch (RecordNotFoundException | InvalidInputException e) {
            System.out.println("Could not enrol student: " + e.getMessage());
        }
    }

    private void withdrawStudentFromCourse() {
        String studentId = InputValidator.readNonEmptyString(scanner, "\nStudent ID: ");
        String courseCode = InputValidator.readNonEmptyString(scanner, "Course Code: ").toUpperCase();
        try {
            enrollmentManager.withdrawStudent(studentId, courseCode);
            System.out.println("Student withdrawn from course " + courseCode + ".");
        } catch (RecordNotFoundException e) {
            System.out.println("Could not withdraw student: " + e.getMessage());
        }
    }

    private void recordGrade() {
        String enrollmentId = InputValidator.readNonEmptyString(scanner, "\nEnrollment ID: ");
        double grade = InputValidator.readDouble(scanner, "Grade (0-100): ", 0.0, 100.0);
        try {
            enrollmentManager.recordGrade(enrollmentId, grade);
            System.out.println("Grade recorded.");
        } catch (RecordNotFoundException | InvalidInputException e) {
            System.out.println("Could not record grade: " + e.getMessage());
        }
    }

    private void viewEnrollmentsForStudent() {
        String studentId = InputValidator.readNonEmptyString(scanner, "\nStudent ID: ");
        List<Enrollment> enrollments = enrollmentManager.getEnrollmentsForStudent(studentId);
        if (enrollments.isEmpty()) {
            System.out.println("No enrolments found for student " + studentId + ".");
        } else {
            enrollments.forEach(System.out::println);
        }
    }

    private void viewEnrollmentsForCourse() {
        String courseCode = InputValidator.readNonEmptyString(scanner, "\nCourse Code: ").toUpperCase();
        List<Enrollment> enrollments = enrollmentManager.getEnrollmentsForCourse(courseCode);
        if (enrollments.isEmpty()) {
            System.out.println("No enrolments found for course " + courseCode + ".");
        } else {
            enrollments.forEach(System.out::println);
        }
    }

    private void viewAllEnrollments() {
        List<Enrollment> enrollments = enrollmentManager.listAll();
        if (enrollments.isEmpty()) {
            System.out.println("(no enrolments on record)");
        } else {
            enrollments.forEach(System.out::println);
        }
    }

    // ---------------------------------------------------------------
    // Reports
    // ---------------------------------------------------------------

    private void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- REPORTS -----");
            System.out.println("1. System Summary");
            System.out.println("2. Top N Students by GPA");
            System.out.println("3. Student Transcript");
            System.out.println("4. Course Roster");
            System.out.println("5. Export Full Report to a Text File");
            System.out.println("0. Back to Main Menu");
            int choice = InputValidator.readMenuChoice(scanner, "Enter your choice: ", 0, 5);
            switch (choice) {
                case 1 -> System.out.println("\n" + reportManager.buildSystemSummary());
                case 2 -> {
                    int topN = InputValidator.readInt(scanner, "\nHow many top students? ", 1, 100);
                    reportManager.printReportLines(reportManager.topStudentsByGpa(topN));
                }
                case 3 -> printTranscript();
                case 4 -> printCourseRoster();
                case 5 -> {
                    String path = reportManager.exportFullReport("data/reports");
                    if (path != null) {
                        System.out.println("Report exported to: " + path);
                    }
                }
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printTranscript() {
        String studentId = InputValidator.readNonEmptyString(scanner, "\nStudent ID: ");
        try {
            System.out.println("\n" + reportManager.buildStudentTranscript(studentId));
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printCourseRoster() {
        String courseCode = InputValidator.readNonEmptyString(scanner, "\nCourse Code: ").toUpperCase();
        try {
            System.out.println("\n" + reportManager.buildCourseRoster(courseCode));
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
