/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 *
 * @author Vikra
 */

public class EnrollmentRepository extends AbstractFileRepository<Enrollment>
{
    public EnrollmentRepository(String filePath)
    {
        super(filePath);
    }
    
    @Override
    protected String extractId(Enrollment item)
    {
        return item.getEnrollmentId();
    }
    
    @Override
    protected Enrollment fromLine(String line)
    {
        String[] parts = line.split(",", -1);
        Double grade = "NA".equalsIgnoreCase(parts[4].trim()) ? null : Double.parseDouble(parts[4].trim());
        
        return new Enrollment
        (
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                grade,
                EnrollmentStatus.valueOf(parts[5].trim())
        );
    }
    
    @Override
    protected String toLine(Enrollment item)
    {
        String gradeText = item.getGrade() == null ? "NA" : String.valueOf(item.getGrade());
        
        return String.join
        (",",
                item.getEnrollmentId(),
                item.getStudentId(),
                item.getCourseCode(),
                item.getSemester(),
                gradeText,
                item.getStatus().name()
        );
    }
}
