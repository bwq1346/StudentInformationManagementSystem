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

    public void printReportLines(List<? extends Reportable> items) {
        for (Reportable item : items) {
            System.out.println("  " + item.toReportLine());
        }
    }

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
