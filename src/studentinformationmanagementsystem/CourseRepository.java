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
 * Same pattern again, this time for Course records. This one has two
 * semicolon-separated lists to deal with instead of one - the enrolled
 * students and the waitlist - since a course needs to remember both across
 * a restart.
 *
 * Line format:
 * code,name,creditPoints,capacity,teacherId,student1;student2;...,waitStudent1;waitStudent2;...
 */
public class CourseRepository extends AbstractFileRepository<Course>
{
    public CourseRepository(String filePath)
    {
        super(filePath);
    }

    @Override
    protected String extractId(Course item)
    {
        return item.getCourseCode();
    }

    @Override
    protected Course fromLine(String line)
    {
        String[] parts = line.split(",", -1);
        // teacher might not be assigned yet, in which case this field is blank
        String teacherId = parts[4].trim().isEmpty() ? null : parts[4].trim();
        Course course = new Course
        (
                parts[0].trim(),
                parts[1].trim(),
                Integer.parseInt(parts[2].trim()),
                Integer.parseInt(parts[3].trim()),
                teacherId
        );

        // rebuild the enrolled-students set from the semicolon list
        if(parts.length > 5 && !parts[5].isBlank())
        {
            StringTokenizer enrolledTokens = new StringTokenizer(parts[5].trim(), ";");
            while(enrolledTokens.hasMoreTokens())
            {
                course.getEnrolledStudentIds().add(enrolledTokens.nextToken().trim());
            }
        }

        // and the waitlist queue from its own semicolon list - order matters
        // here since it's a queue, and StringTokenizer hands tokens back in
        // the order they appear, so the waiting order is preserved
        if(parts.length > 6 && !parts[6].isBlank())
        {
            StringTokenizer waitTokens = new StringTokenizer(parts[6].trim(), ";");
            while (waitTokens.hasMoreTokens())
            {
                course.getWaitlist().add(waitTokens.nextToken().trim());
            }
        }
        return course;
    }

    @Override
    protected String toLine(Course item)
    {
        return String.join(",",
                item.getCourseCode(),
                item.getCourseName(),
                String.valueOf(item.getCreditPoints()),
                String.valueOf(item.getCapacity()),
                item.getTeacherId() == null ? "" : item.getTeacherId(),
                String.join(";", item.getEnrolledStudentIds()),
                String.join(";", item.getWaitlist()));
    }
}
