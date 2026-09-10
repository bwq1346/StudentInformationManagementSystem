/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 * Anything that can be turned into a single line for a report or an export
 * file implements this. Student, Teacher, Course and Enrollment all do it
 * differently, which is the whole point - ReportManager can just call
 * toReportLine() without caring what kind of object it's actually holding.
 *
 * @author Vikram Bugata
 */
public interface Reportable {
    String toReportLine();
}
