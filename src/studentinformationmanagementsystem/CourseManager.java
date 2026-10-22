package studentinformationmanagementsystem;

/**
 * Business logic for courses, sitting between the UI and the repository.
 *
 * @author Nishal Kinattukarayil Biju
 */

import java.util.List;

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

    // persists a Course that was changed elsewhere (e.g. by EnrollmentManager)
    public void saveCourse(Course course) throws RecordNotFoundException
    {
        courseRepository.update(course);
    }

    public boolean exists(String courseCode)
    {
        return courseRepository.existsById(courseCode);
    }

    public List<Course> listAll()
    {
        return courseRepository.findAll();
    }

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
