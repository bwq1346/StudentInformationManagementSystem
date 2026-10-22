/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vikram Bugata
 */

public class TeacherDAO implements CrudOperations<Teacher>
{

    private final Connection connection;

    public TeacherDAO() {
        this.connection = DBConnectionManager.getInstance().getConnection();
    }

    @Override
    public void add(Teacher item) throws DuplicateRecordException {
        if (existsById(item.getId())) {
            throw new DuplicateRecordException("A teacher with ID " + item.getId() + " already exists.");
        }
        String sql = "INSERT INTO TEACHER (ID, NAME, EMAIL, PHONE, DEPARTMENT, COURSES_TAUGHT) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.getId());
            statement.setString(2, item.getName());
            statement.setString(3, item.getEmail());
            statement.setString(4, item.getPhone());
            statement.setString(5, item.getDepartment());
            statement.setString(6, String.join(",", item.getCoursesTaught()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not add teacher " + item.getId() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Teacher item) throws RecordNotFoundException {
        String sql = "UPDATE TEACHER SET NAME=?, EMAIL=?, PHONE=?, DEPARTMENT=?, COURSES_TAUGHT=? WHERE ID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.getName());
            statement.setString(2, item.getEmail());
            statement.setString(3, item.getPhone());
            statement.setString(4, item.getDepartment());
            statement.setString(5, String.join(",", item.getCoursesTaught()));
            statement.setString(6, item.getId());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RecordNotFoundException("No teacher found with ID " + item.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update teacher " + item.getId() + ": " + e.getMessage(), e);
        }
    }

    // Deleting a teacher also clears COURSE.TEACHER_ID on any course they
    // taught, so no course is left pointing at a teacher who doesn't exist.
    // Both statements run as one transaction: either both happen or neither.
    @Override
    public void delete(String id) throws RecordNotFoundException {
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement unassign = connection.prepareStatement(
                    "UPDATE COURSE SET TEACHER_ID=NULL WHERE TEACHER_ID=?");
                    PreparedStatement statement = connection.prepareStatement("DELETE FROM TEACHER WHERE ID=?")) {
                unassign.setString(1, id);
                unassign.executeUpdate();
                statement.setString(1, id);
                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    throw new RecordNotFoundException("No teacher found with ID " + id);
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true); // back to DBConnectionManager's write-through default
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete teacher " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Teacher findById(String id) throws RecordNotFoundException {
        String sql = "SELECT * FROM TEACHER WHERE ID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not look up teacher " + id + ": " + e.getMessage(), e);
        }
        throw new RecordNotFoundException("No teacher found with ID " + id);
    }

    @Override
    public List<Teacher> findAll() {
        List<Teacher> result = new ArrayList<>();
        String sql = "SELECT * FROM TEACHER ORDER BY ID";
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load teachers: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean existsById(String id) {
        String sql = "SELECT 1 FROM TEACHER WHERE ID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check teacher " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM TEACHER";
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not count teachers: " + e.getMessage(), e);
        }
    }

    private Teacher mapRow(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher(
                rs.getString("ID"),
                rs.getString("NAME"),
                rs.getString("EMAIL"),
                rs.getString("PHONE"),
                rs.getString("DEPARTMENT"));
        String courses = rs.getString("COURSES_TAUGHT");
        if (courses != null && !courses.isEmpty()) {
            for (String code : courses.split(",")) {
                if (!code.trim().isEmpty()) {
                    teacher.assignCourse(code.trim());
                }
            }
        }
        return teacher;
    }
}
