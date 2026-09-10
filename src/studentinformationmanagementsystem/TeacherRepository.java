/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 * @author Vikram Bugata
 */

import java.util.StringTokenizer;

/**
 * Same idea as StudentRepository, just for Teacher records.
 *
 * Line format:
 * id,name,email,phone,department,course1;course2;...
 */
public class TeacherRepository extends AbstractFileRepository<Teacher>
{
    public TeacherRepository(String filePath)
    {
        super(filePath);
    }

    @Override
    protected String extractId(Teacher item)
    {
        return item.getId();
    }

    @Override
    protected Teacher fromLine(String line)
    {
        String[] parts = line.split(",", -1);

        Teacher teacher = new Teacher
        (
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim()
        );

        // courses taught, packed the same way student course codes are
        if(parts.length > 5 && !parts[5].isBlank())
        {
            StringTokenizer tokenizer = new StringTokenizer(parts[5].trim(), ";");

            while(tokenizer.hasMoreTokens())
            {
                teacher.assignCourse(tokenizer.nextToken().trim());
            }
        }

        return teacher;
    }

    @Override
    protected String toLine(Teacher item)
    {
        StringBuilder courses = new StringBuilder();

        for(String code : item.getCoursesTaught())
        {
            if(courses.length() > 0)
            {
                courses.append(";");
            }

            courses.append(code);
        }

        return String.join
        (       ",",
                item.getId(),
                item.getName(),
                item.getEmail(),
                item.getPhone(),
                item.getDepartment(),
                courses.toString()
        );
    }
}
