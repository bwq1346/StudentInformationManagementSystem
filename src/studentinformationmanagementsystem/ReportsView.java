/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Vikram Bugata
 */

public class ReportsView extends JPanel
{

    public final JButton summaryButton = new JButton("System Summary");
    public final JButton topStudentsButton = new JButton("Top Students by GPA");
    public final JButton transcriptButton = new JButton("Student Transcript");
    public final JButton rosterButton = new JButton("Course Roster");
    public final JButton exportButton = new JButton("Export Full Report");

    private final JLabel titleLabel = new JLabel(" ");
    private final JTextArea outputArea = new JTextArea();

    public ReportsView()
    {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        buttonPanel.add(summaryButton);
        buttonPanel.add(topStudentsButton);
        buttonPanel.add(transcriptButton);
        buttonPanel.add(rosterButton);
        buttonPanel.add(exportButton);

        JPanel buttonHolder = new JPanel(new BorderLayout());
        buttonHolder.add(buttonPanel, BorderLayout.NORTH);
        add(buttonHolder, BorderLayout.WEST);

        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));

        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.add(titleLabel, BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(outputPanel, BorderLayout.CENTER);

        showReport("Reports", "Choose a report on the left.");
    }

    public void showReport(String title, String text) {
        titleLabel.setText(title);
        outputArea.setText(text);
        outputArea.setCaretPosition(0); // scroll back to the top
    }
}
