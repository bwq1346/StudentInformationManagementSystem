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
 * Students tab: a table of all students plus the action buttons.
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

    private StudentManager studentManager;

    public StudentView() {
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Year", "Major", "GPA", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
        onDataChanged();
    }

    public JTable getStudentTable() {
        return studentTable;
    }

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
