/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Pulls together data from all three managers to build the different
 * report views (system summary, top students, transcripts, rosters) and to
 * export everything to a text file. This class doesn't store anything
 * itself - it just reads from the other managers and formats the output.
 *
 * Analytics logic (average grades, pass rates, top students) drafted with
 * assistance from Claude AI using Java 8 streams; rewritten with for-each
 * loops, ArrayList and HashMap instead — see AI Interaction Log.
 * 
 * @author Vikra
 */
public class ReportManager {

    private final StudentManager studentManager;
    private final CourseManager courseManager;
    private final EnrollmentManager enrollmentManager;

    public ReportManager(StudentManager studentManager, CourseManager courseManager,
            EnrollmentManager enrollmentManager) {
        this.studentManager = studentManager;
        this.courseManager = courseManager;
        this.enrollmentManager = enrollmentManager;
    }

    // takes anything Reportable (Student, Course, etc.) and prints its
    // report line - this is the polymorphism paying off, since it doesn't
    // need to know or care what type is actually in the list
    public void printReportLines(List<? extends Reportable> items) {
        for (Reportable item : items) {
            System.out.println("  " + item.toReportLine());
        }
    }

    // students are already sorted by GPA (see StudentManager.listAllSorted),
    // so "top N" is really just "take the first N"
    public List<Student> topStudentsByGpa(int topN) {
        List<Student> sorted = studentManager.listAllSorted();
        return sorted.subList(0, Math.min(topN, sorted.size()));
    }

    public String buildStudentTranscript(String studentId) throws RecordNotFoundException {
        Student student = studentManager.findStudent(studentId);
        StringBuilder sb = new StringBuilder();
        sb.append("Transcript for ").append(student.getName()).append(" (").append(studentId).append(")\n");
        sb.append(student.getSummary()).append("\n");
        sb.append("----------------------------------------------------------\n");
        List<Enrollment> enrollments = enrollmentManager.getEnrollmentsForStudent(studentId);
        if (enrollments.isEmpty()) {
            sb.append("  (no enrolments on record)\n");
        }
        for (Enrollment e : enrollments) {
            sb.append("  ").append(e).append("\n");
        }
        return sb.toString();
    }

    public String buildCourseRoster(String courseCode) throws RecordNotFoundException {
        Course course = courseManager.findCourse(courseCode);
        StringBuilder sb = new StringBuilder();
        sb.append("Roster for ").append(course.getCourseName()).append(" (").append(courseCode).append(")\n");
        sb.append(course).append("\n");
        sb.append("----------------------------------------------------------\n");
        // enrolled students and waitlisted students are shown separately,
        // straight from the course's own Set and Queue
        for (String studentId : course.getEnrolledStudentIds()) {
            try {
                sb.append("  ENROLLED: ").append(studentManager.findStudent(studentId).getSummary()).append("\n");
            } catch (RecordNotFoundException e) {
                sb.append("  ENROLLED: (unknown student ").append(studentId).append(")\n");
            }
        }
        for (String studentId : course.getWaitlist()) {
            sb.append("  WAITLISTED: ").append(studentId).append("\n");
        }
        return sb.toString();
    }

    public String buildSystemSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== System Summary ===\n");
        sb.append("Total students   : ").append(studentManager.getStudentCount()).append("\n");
        sb.append("Total courses    : ").append(courseManager.getCourseCount()).append("\n");
        sb.append("Total enrolments : ").append(enrollmentManager.getEnrollmentCount()).append("\n");
        sb.append(String.format("Average GPA      : %.2f%n", studentManager.getAverageGpa()));
        return sb.toString();
    }

    // writes a full snapshot of the system to its own timestamped file,
    // so you can keep several reports around without overwriting each other
    public String exportFullReport(String outputFolder) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String path = outputFolder + "/report_" + timestamp + ".txt";

        java.io.File folder = new java.io.File(outputFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            writer.println(buildSystemSummary());
            writer.println("=== Students ===");
            for (Student s : studentManager.listAllSorted()) {
                writer.println(s.toReportLine());
            }
            writer.println();
            writer.println("=== Courses ===");
            for (Course c : courseManager.listAll()) {
                writer.println(c.toReportLine());
            }
            writer.println();
            writer.println("=== Enrolments ===");
            for (Enrollment e : enrollmentManager.listAll()) {
                writer.println(e.toReportLine());
            }
            ActivityLogger.log("Exported full report to " + path);
            return path;
        } catch (IOException e) {
            System.out.println("Warning: could not write report file (" + e.getMessage() + ").");
            return null;
        }
    }
}
