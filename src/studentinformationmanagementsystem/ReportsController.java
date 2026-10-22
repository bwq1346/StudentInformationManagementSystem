/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 *
 * @author Vikram Bugata
 */

public class ReportsController implements ActionListener
{

    private static final String EXPORT_FOLDER = "reports";

    private final ReportsView reportsView;
    private final ReportManager reportManager;

    public ReportsController(ReportsView reportsView, ReportManager reportManager)
    {
        this.reportsView = reportsView;
        this.reportManager = reportManager;

        reportsView.summaryButton.addActionListener(this);
        reportsView.topStudentsButton.addActionListener(this);
        reportsView.transcriptButton.addActionListener(this);
        reportsView.rosterButton.addActionListener(this);
        reportsView.exportButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            if(e.getSource() == reportsView.summaryButton)
            {
                reportsView.showReport("System Summary", reportManager.buildSystemSummary());
            }
            else if(e.getSource() == reportsView.topStudentsButton)
            {
                handleTopStudents();
            }
            else if(e.getSource() == reportsView.transcriptButton)
            {
                handleTranscript();
            }
            else if(e.getSource() == reportsView.rosterButton)
            {
                handleRoster();
            }
            else if(e.getSource() == reportsView.exportButton)
            {
                handleExport();
            }
        }
        catch(RuntimeException ex)
        {
            JOptionPane.showMessageDialog(reportsView, ex.getMessage(), "Database error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleTopStudents()
    {
        String input = JOptionPane.showInputDialog(reportsView, "How many students to show?", "5");
        if(input == null || input.trim().isEmpty())
        {
            return;
        }
        int topN;
        try
        {
            topN = Integer.parseInt(input.trim());
        }
        catch(NumberFormatException ex)
        {
            showWarning("Please enter a whole number, e.g. 5", "Invalid input");
            return;
        }
        if(topN < 1)
        {
            showWarning("Please enter a number of 1 or more.", "Invalid input");
            return;
        }

        List<Student> top = reportManager.topStudentsByGpa(topN);
        StringBuilder sb = new StringBuilder();
        if(top.isEmpty())
        {
            sb.append("(no students on record)\n");
        }
        else
        {
            sb.append(String.format("%-5s %-9s %-22s %-18s %5s%n", "Rank", "ID", "Name", "Major", "GPA"));
            sb.append("-------------------------------------------------------------\n");
            int rank = 1;
            for (Student s : top)
            {
                sb.append(String.format("%-5d %-9s %-22s %-18s %5.2f%n",
                        rank++, s.getId(), s.getName(), s.getMajor(), s.getGpa()));
            }
        }
        reportsView.showReport("Top " + topN + " Students by GPA", sb.toString());
    }

    private void handleTranscript()
    {
        List<Student> students = reportManager.topStudentsByGpa(Integer.MAX_VALUE);
        if(students.isEmpty())
        {
            JOptionPane.showMessageDialog(reportsView, "There are no students yet.");
            return;
        }
        JComboBox<String> studentBox = new JComboBox<>();
        for(Student s : students)
        {
            studentBox.addItem(s.getId() + " - " + s.getName());
        }
        int result = JOptionPane.showConfirmDialog(reportsView, studentBox, "Choose a student",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(result != JOptionPane.OK_OPTION)
        {
            return;
        }
        String studentId = students.get(studentBox.getSelectedIndex()).getId();
        try
        {
            reportsView.showReport("Transcript - " + studentId, reportManager.buildStudentTranscript(studentId));
        }
        catch(RecordNotFoundException ex)
        {
            showWarning(ex.getMessage(), "Student not found");
        }
    }

    private void handleRoster()
    {
        String code = JOptionPane.showInputDialog(reportsView, "Course code (e.g. COMP603):");
        if(code == null || code.trim().isEmpty())
        {
            return;
        }
        code = code.trim();
        try
        {
            reportsView.showReport("Roster - " + code, reportManager.buildCourseRoster(code));
        }
        catch(RecordNotFoundException ex)
        {
            showWarning("There is no course with code '" + code + "'.", "Course not found");
        }
    }

    private void handleExport()
    {
        String path = reportManager.exportFullReport(EXPORT_FOLDER);
        if(path == null)
        {
            showWarning("The report could not be written to the '" + EXPORT_FOLDER + "' folder.",
                    "Export failed");
            return;
        }
        String fullPath = new File(path).getAbsolutePath();
        reportsView.showReport("Export Full Report", "Full report saved to:\n" + fullPath);
        JOptionPane.showMessageDialog(reportsView, "Report saved to:\n" + fullPath);
    }

    private void showWarning(String message, String title)
    {
        JOptionPane.showMessageDialog(reportsView, message, title, JOptionPane.WARNING_MESSAGE);
    }
}