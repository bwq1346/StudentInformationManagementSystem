/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 * @author Vikra
 */

import java.util.List;

/**
 * Business logic for courses - creation, updates, deletion, and assigning a
 * teacher. Unlike students/teachers, a course's id (its course code) is
 * typed in by the user rather than auto-generated, since course codes like
 * COMP603 are meant to be human-readable.
 */
public class CourseManager
{
    private final CrudOperations<Course> courseRepository;

    public CourseManager(CrudOperations<Course> courseRepository)
    {
        this.courseRepository = courseRepository;
    }

    public Course addCourse(String courseCode, String courseName, int creditPoints, int capacity, String teacherId) throws InvalidInputException
    {
        try
        {
            Course course = new Course(courseCode, courseName, creditPoints, capacity, teacherId);
            courseRepository.add(course);
            ActivityLogger.log("Added course " + courseCode + " (" + courseName + ")");
            return course;
        }
        catch (DuplicateRecordException e)
        {
            // most likely cause: the course code was already used
            throw new InvalidInputException(e.getMessage());
        }
    }

    public void updateCourse(String courseCode, String courseName, int creditPoints, int capacity) throws RecordNotFoundException
    {
        Course existing = courseRepository.findById(courseCode);
        existing.setCourseName(courseName);
        existing.setCreditPoints(creditPoints);
        existing.setCapacity(capacity);
        courseRepository.update(existing);
        ActivityLogger.log("Updated course " + courseCode);
    }

    public void deleteCourse(String courseCode) throws RecordNotFoundException
    {
        courseRepository.delete(courseCode);
        ActivityLogger.log("Deleted course " + courseCode);
    }

    public Course findCourse(String courseCode) throws RecordNotFoundException
    {
        return courseRepository.findById(courseCode);
    }

    public boolean exists(String courseCode)
    {
        return courseRepository.existsById(courseCode);
    }

    public List<Course> listAll()
    {
        return courseRepository.findAll();
    }

    // updates the course's side of the link only - ConsoleUI is the one
    // that also tells the Teacher object about it, since this class only
    // knows about courses, not teachers
    public void assignTeacher(String courseCode, String teacherId) throws RecordNotFoundException
    {
        Course course = courseRepository.findById(courseCode);
        course.setTeacherId(teacherId);
        courseRepository.update(course);
        ActivityLogger.log("Assigned teacher " + teacherId + " to course " + courseCode);
    }

    public int getCourseCount()
    {
        return courseRepository.count();
    }
}
