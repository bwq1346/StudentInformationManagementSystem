/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 *
 * @author Vikra
 */

import java.util.StringTokenizer;


public class StudentRepository extends AbstractFileRepository<Student>
{    
    public StudentRepository(String filePath)
    {
        super(filePath);
    }
    
    @Override
    protected String extractId(Student item)
    {
        return item.getId();
    }
    
    @Override
    protected Student fromLine(String line)
    {
        String[] parts = line.split(",", -1);
        
        Student student = new Student
        (
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                Integer.parseInt(parts[4].trim()),
                parts[5].trim(),
                Double.parseDouble(parts[6].trim()),
                StudentStatus.valueOf(parts[7].trim())
        );

        if(parts.length > 8 && !parts[8].isBlank())
        {
            StringTokenizer tokenizer = new StringTokenizer(parts[8].trim(), ";");
            
            while(tokenizer.hasMoreTokens())
            {
                student.addCourseCode(tokenizer.nextToken().trim());
            }
        }
        return student;
    }
    
    @Override
    protected String toLine(Student item)
    {
        StringBuilder courses = new StringBuilder();
        
        for(String code : item.getEnrolledCourseCodes())
        {
            if (courses.length() > 0)
            {
                courses.append(";");
            }
            courses.append(code);
        }
        
        return String.join
        (",",
                item.getId(),
                item.getName(),
                item.getEmail(),
                item.getPhone(),
                String.valueOf(item.getYearLevel()),
                item.getMajor(),
                String.valueOf(item.getGpa()),
                item.getStatus().name(),
                courses.toString()
        );
    }
}
