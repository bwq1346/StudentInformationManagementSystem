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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vikram Bugata
 */
public class StudentDAO implements CrudOperations<Student>
{

    private final Connection connection;

    public StudentDAO()
    {
        this.connection = DBConnectionManager.getInstance().getConnection();
    }

    @Override
    public void add(Student item) throws DuplicateRecordException
    {
        if(existsById(item.getId()))
        {
            throw new DuplicateRecordException("A student with ID " + item.getId() + " already exists.");
        }
        String sql = "INSERT INTO STUDENT (ID, NAME, EMAIL, PHONE, YEAR_LEVEL, MAJOR, GPA, STATUS, ENROLLED_COURSE_CODES) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, item.getId());
            statement.setString(2, item.getName());
            statement.setString(3, item.getEmail());
            statement.setString(4, item.getPhone());
            statement.setInt(5, item.getYearLevel());
            statement.setString(6, item.getMajor());
            statement.setDouble(7, item.getGpa());
            statement.setString(8, item.getStatus().name());
            statement.setString(9, String.join(",", item.getEnrolledCourseCodes()));
            statement.executeUpdate();
        } 
        catch(SQLException e)
        {
            throw new RuntimeException("Could not add student " + item.getId() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Student item) throws RecordNotFoundException
    {
        String sql = "UPDATE STUDENT SET NAME=?, EMAIL=?, PHONE=?, YEAR_LEVEL=?, MAJOR=?, GPA=?, STATUS=?, "
                + "ENROLLED_COURSE_CODES=? WHERE ID=?";
        try(PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, item.getName());
            statement.setString(2, item.getEmail());
            statement.setString(3, item.getPhone());
            statement.setInt(4, item.getYearLevel());
            statement.setString(5, item.getMajor());
            statement.setDouble(6, item.getGpa());
            statement.setString(7, item.getStatus().name());
            statement.setString(8, String.join(",", item.getEnrolledCourseCodes()));
            statement.setString(9, item.getId());
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected == 0)
            {
                throw new RecordNotFoundException("No student found with ID " + item.getId());
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Could not update student " + item.getId() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) throws RecordNotFoundException
    {
        try
        {
            connection.setAutoCommit(false);
            try
            {
                try(PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM ENROLLMENT WHERE STUDENT_ID=?"))
                {
                    statement.setString(1, id);
                    statement.executeUpdate();
                }

                removeFromListColumn("COURSE", "COURSE_CODE", "ENROLLED_STUDENT_IDS", id);
                removeFromListColumn("COURSE", "COURSE_CODE", "WAITLIST", id);

                try(PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM STUDENT WHERE ID=?"))
                {
                    statement.setString(1, id);
                    if(statement.executeUpdate() == 0)
                    {
                        throw new RecordNotFoundException("No student found with ID " + id);
                    }
                }

                connection.commit();
            }
            catch(SQLException | RecordNotFoundException e)
            {
                connection.rollback();
                throw e;
            }
            finally
            {
                connection.setAutoCommit(true);
            }
        }
        catch(RecordNotFoundException e)
        {
            throw e;
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Could not delete student " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Student findById(String id) throws RecordNotFoundException
    {
        String sql = "SELECT * FROM STUDENT WHERE ID=?";
        try(PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, id);
            try(ResultSet rs = statement.executeQuery())
            {
                if(rs.next())
                {
                    return mapRow(rs);
                }
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Could not look up student " + id + ": " + e.getMessage(), e);
        }
        throw new RecordNotFoundException("No student found with ID " + id);
    }

    @Override
    public List<Student> findAll()
    {
        List<Student> result = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT";
        try(PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery())
        {
            while(rs.next())
            {
                result.add(mapRow(rs));
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Could not load students: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean existsById(String id)
    {
        String sql = "SELECT 1 FROM STUDENT WHERE ID=?";
        try(PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, id);
            try(ResultSet rs = statement.executeQuery())
            {
                return rs.next();
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Could not check student " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int count()
    {
        String sql = "SELECT COUNT(*) FROM STUDENT";
        try(PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery())
        {
            return rs.next() ? rs.getInt(1) : 0;
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Could not count students: " + e.getMessage(), e);
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException
    {
        Student student = new Student(
                rs.getString("ID"),
                rs.getString("NAME"),
                rs.getString("EMAIL"),
                rs.getString("PHONE"),
                rs.getInt("YEAR_LEVEL"),
                rs.getString("MAJOR"),
                rs.getDouble("GPA"),
                StudentStatus.valueOf(rs.getString("STATUS")));
        String courseCodes = rs.getString("ENROLLED_COURSE_CODES");
        if(courseCodes != null && !courseCodes.isEmpty())
        {
            for(String code : courseCodes.split(","))
            {
                student.addCourseCode(code);
            }
        }
        return student;
    }

    private void removeFromListColumn(String table, String idColumn, String listColumn, String value)
            throws SQLException
    {
        Map<String, String> changed = new LinkedHashMap<>();
        String select = "SELECT " + idColumn + ", " + listColumn + " FROM " + table
                + " WHERE " + listColumn + " LIKE ?";
        try(PreparedStatement statement = connection.prepareStatement(select))
        {
            statement.setString(1, "%" + value + "%");
            try(ResultSet rs = statement.executeQuery())
            {
                while(rs.next())
                {
                    String joined = rs.getString(2);
                    List<String> values = new ArrayList<>();
                    if(joined != null && !joined.isEmpty())
                    {
                        for(String entry : joined.split(","))
                        {
                            if(!entry.trim().isEmpty())
                            {
                                values.add(entry.trim());
                            }
                        }
                    }
                    if(values.remove(value))
                    {
                        changed.put(rs.getString(1), String.join(",", values));
                    }
                }
            }
        }

        String update = "UPDATE " + table + " SET " + listColumn + "=? WHERE " + idColumn + "=?";
        try(PreparedStatement statement = connection.prepareStatement(update))
        {
            for(Map.Entry<String, String> entry : changed.entrySet())
            {
                statement.setString(1, entry.getValue());
                statement.setString(2, entry.getKey());
                statement.executeUpdate();
            }
        }
    }
}
