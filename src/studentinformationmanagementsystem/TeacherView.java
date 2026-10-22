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
 * Read-only JTable of all teachers plus Add/Edit/Delete buttons.
 * TeacherManager has no searchByX methods, so there's no search button
 * needed here, unlike StudentView.
 *
 * @author Nishal Kinattukarayil Biju
 */
public class TeacherView extends JPanel implements ModelListener {

    private final JTable teacherTable;
    private final DefaultTableModel tableModel;

    public final JButton addButton = new JButton("Add Teacher");
    public final JButton editButton = new JButton("Edit Teacher");
    public final JButton deleteButton = new JButton("Delete Teacher");

    private TeacherManager teacherManager;

    public TeacherView() {
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Department", "Courses Taught"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teacherTable = new JTable(tableModel);

        setLayout(new BorderLayout());
        add(new JScrollPane(teacherTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setTeacherManager(TeacherManager teacherManager) {
        this.teacherManager = teacherManager;
        onDataChanged();
    }

    public String getSelectedTeacherId() {
        int row = teacherTable.getSelectedRow();
        return row < 0 ? null : (String) tableModel.getValueAt(row, 0);
    }

    @Override
    public void onDataChanged() {
        tableModel.setRowCount(0);
        if (teacherManager == null) {
            return;
        }
        List<Teacher> teachers = teacherManager.listAll();
        for (Teacher t : teachers) {
            tableModel.addRow(new Object[]{t.getId(), t.getName(), t.getDepartment(), t.getCoursesTaught().size()});
        }
    }
}
