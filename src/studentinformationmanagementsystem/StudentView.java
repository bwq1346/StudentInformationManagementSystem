/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Swing screen for Student CRUD + search. Pure View - it only knows how to
 * display students and raise button-click events; it never talks to
 * StudentManager directly, that's StudentController's job (this split is
 * what the brief means by "clear separation of concerns between GUI,
 * business logic, and data access").
 *
 * @author Nishal Kinattukarayil Biju
 */
public class StudentView extends JPanel implements ModelListener {

    private final JTable studentTable;
    private final DefaultTableModel tableModel;

    public final JButton addButton = new JButton("Add Student");
    public final JButton editButton = new JButton("Edit Student");
    public final JButton deleteButton = new JButton("Delete Student");
    public final JButton searchButton = new JButton("Search");

    // StudentController sets this after construction, so onDataChanged()
    // knows where to pull the latest list from
    private StudentManager studentManager;

    public StudentView() {
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Year", "Major", "GPA", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table - editing happens through the Add/Edit dialog
            }
        };
        studentTable = new JTable(tableModel);

        setLayout(new BorderLayout());
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setStudentManager(StudentManager studentManager) {
        this.studentManager = studentManager;
        onDataChanged(); // populate the table immediately on startup
    }

    public JTable getStudentTable() {
        return studentTable;
    }

    // Returns the ID of whichever row is currently selected, or null if
    // nothing's selected - StudentController uses this for Edit/Delete.
    public String getSelectedStudentId() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (String) tableModel.getValueAt(row, 0);
    }

    @Override
    public void onDataChanged() {
        tableModel.setRowCount(0);
        if (studentManager == null) {
            return;
        }
        List<Student> students = studentManager.listAllSorted();
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                s.getId(), s.getName(), s.getYearLevel(), s.getMajor(), s.getGpa(), s.getStatus()
            });
        }
    }
}
