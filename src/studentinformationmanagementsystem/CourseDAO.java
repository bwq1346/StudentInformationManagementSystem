/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Vikram Bugata
 */

public class CourseDAO implements CrudOperations<Course>
{

    private final Connection connection;

    public CourseDAO()
    {
        this.connection = DBConnectionManager.getInstance().getConnection();
    }

    @Override
    public void add(Course item) throws DuplicateRecordException
    {
        if (existsById(item.getCourseCode()))
        {
            throw new DuplicateRecordException("A course with code " + item.getCourseCode() + " already exists.");
        }
        String sql = "INSERT INTO COURSE (COURSE_CODE, COURSE_NAME, CREDIT_POINTS, CAPACITY, TEACHER_ID, "
                + "ENROLLED_STUDENT_IDS, WAITLIST) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, item.getCourseCode());
            statement.setString(2, item.getCourseName());
            statement.setInt(3, item.getCreditPoints());
            statement.setInt(4, item.getCapacity());
            setTeacherId(statement, 5, item.getTeacherId());
            statement.setString(6, String.join(",", item.getEnrolledStudentIds()));
            statement.setString(7, String.join(",", item.getWaitlist()));
            statement.executeUpdate();
        } 
        catch (SQLException e)
        {
            throw new RuntimeException("Could not add course " + item.getCourseCode() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Course item) throws RecordNotFoundException {
        String sql = "UPDATE COURSE SET COURSE_NAME=?, CREDIT_POINTS=?, CAPACITY=?, TEACHER_ID=?, "
                + "ENROLLED_STUDENT_IDS=?, WAITLIST=? WHERE COURSE_CODE=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.getCourseName());
            statement.setInt(2, item.getCreditPoints());
            statement.setInt(3, item.getCapacity());
            setTeacherId(statement, 4, item.getTeacherId());
            statement.setString(5, String.join(",", item.getEnrolledStudentIds()));
            statement.setString(6, String.join(",", item.getWaitlist()));
            statement.setString(7, item.getCourseCode());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RecordNotFoundException("No course found with code " + item.getCourseCode());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update course " + item.getCourseCode() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String courseCode) throws RecordNotFoundException {
        try {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM ENROLLMENT WHERE COURSE_CODE=?")) {
                    statement.setString(1, courseCode);
                    statement.executeUpdate();
                }
                removeFromListColumn("STUDENT", "ID", "ENROLLED_COURSE_CODES", courseCode);
                removeFromListColumn("TEACHER", "ID", "COURSES_TAUGHT", courseCode);
                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM COURSE WHERE COURSE_CODE=?")) {
                    statement.setString(1, courseCode);
                    if (statement.executeUpdate() == 0) {
                        connection.rollback();
                        throw new RecordNotFoundException("No course found with code " + courseCode);
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true); // back to DBConnectionManager's write-through default
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete course " + courseCode + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Course findById(String courseCode) throws RecordNotFoundException {
        String sql = "SELECT * FROM COURSE WHERE COURSE_CODE=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseCode);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not look up course " + courseCode + ": " + e.getMessage(), e);
        }
        throw new RecordNotFoundException("No course found with code " + courseCode);
    }

    @Override
    public List<Course> findAll() {
        List<Course> result = new ArrayList<>();
        String sql = "SELECT * FROM COURSE ORDER BY COURSE_CODE";
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load courses: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean existsById(String courseCode) {
        String sql = "SELECT 1 FROM COURSE WHERE COURSE_CODE=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseCode);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check course " + courseCode + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM COURSE";
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not count courses: " + e.getMessage(), e);
        }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        Course course = new Course(
                rs.getString("COURSE_CODE"),
                rs.getString("COURSE_NAME"),
                rs.getInt("CREDIT_POINTS"),
                rs.getInt("CAPACITY"),
                rs.getString("TEACHER_ID")); // null when no teacher is assigned
        for (String id : splitList(rs.getString("ENROLLED_STUDENT_IDS"))) {
            course.getEnrolledStudentIds().add(id);
        }
        for (String id : splitList(rs.getString("WAITLIST"))) {
            course.getWaitlist().add(id);
        }
        return course;
    }

    private List<String> splitList(String joined) {
        List<String> values = new ArrayList<>();
        if (joined != null && !joined.isEmpty()) {
            for (String value : joined.split(",")) {
                if (!value.trim().isEmpty()) {
                    values.add(value.trim());
                }
            }
        }
        return values;
    }

    private void setTeacherId(PreparedStatement statement, int index, String teacherId) throws SQLException {
        if (teacherId == null || teacherId.trim().isEmpty()) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, teacherId.trim());
        }
    }

    private void removeFromListColumn(String table, String idColumn, String listColumn, String value)
            throws SQLException {
        Map<String, String> changed = new LinkedHashMap<>();
        String select = "SELECT " + idColumn + ", " + listColumn + " FROM " + table
                + " WHERE " + listColumn + " LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setString(1, "%" + value + "%");
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    List<String> values = splitList(rs.getString(2));
                    if (values.remove(value)) {
                        changed.put(rs.getString(1), String.join(",", values));
                    }
                }
            }
        }
        String update = "UPDATE " + table + " SET " + listColumn + "=? WHERE " + idColumn + "=?";
        try (PreparedStatement statement = connection.prepareStatement(update)) {
            for (Map.Entry<String, String> entry : changed.entrySet()) {
                statement.setString(1, entry.getValue());
                statement.setString(2, entry.getKey());
                statement.executeUpdate();
            }
        }
    }
}