/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 * @author Vikra
 */

import java.util.StringTokenizer;


/**
 * Handles turning a Student into one comma-separated line and back again.
 * All the actual file reading/writing/CRUD is done up in
 * AbstractFileRepository - this class only knows about the Student-specific
 * file format.
 *
 * Line format:
 * id,name,email,phone,yearLevel,major,gpa,status,course1;course2;...
 */
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
        // -1 keeps trailing empty fields (e.g. no enrolled courses yet)
        // instead of split() silently dropping them
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

        // enrolled courses are semicolon-separated inside their own comma
        // field, so this only runs if that field actually has something in it
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
        // pack all the enrolled course codes into one semicolon-separated string
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
