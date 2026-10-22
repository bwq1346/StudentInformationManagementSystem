package studentinformationmanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Derby-backed CrudOperations for enrolments.
 *
 * @author Vikram Bugata
 */

public class EnrollmentDAO implements CrudOperations<Enrollment>
{

    private final Connection connection;

    public EnrollmentDAO()
    {
        this.connection = DBConnectionManager.getInstance().getConnection();
    }

    @Override
    public void add(Enrollment item) throws DuplicateRecordException
    {
        if(existsById(item.getEnrollmentId()))
        {
            throw new DuplicateRecordException("An enrolment with ID " + item.getEnrollmentId() + " already exists.");
        }
        String sql = "INSERT INTO ENROLLMENT (ENROLLMENT_ID, STUDENT_ID, COURSE_CODE, SEMESTER, GRADE, STATUS) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, item.getEnrollmentId());
            statement.setString(2, item.getStudentId());
            statement.setString(3, item.getCourseCode());
            statement.setString(4, item.getSemester());
            setGrade(statement, 5, item.getGrade());
            statement.setString(6, item.getStatus().name());
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not add enrolment " + item.getEnrollmentId() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Enrollment item) throws RecordNotFoundException
    {
        String sql = "UPDATE ENROLLMENT SET GRADE=?, STATUS=? WHERE ENROLLMENT_ID=?";
        try(PreparedStatement statement = connection.prepareStatement(sql))
        {
            setGrade(statement, 1, item.getGrade());
            statement.setString(2, item.getStatus().name());
            statement.setString(3, item.getEnrollmentId());
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected == 0)
            {
                throw new RecordNotFoundException("No enrolment found with ID " + item.getEnrollmentId());
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not update enrolment " + item.getEnrollmentId() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) throws RecordNotFoundException
    {
        String sql = "DELETE FROM ENROLLMENT WHERE ENROLLMENT_ID=?";
        try(PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, id);
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected == 0)
            {
                throw new RecordNotFoundException("No enrolment found with ID " + id);
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Could not delete enrolment " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Enrollment findById(String id) throws RecordNotFoundException
    {
        String sql = "SELECT * FROM ENROLLMENT WHERE ENROLLMENT_ID=?";
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
            throw new RuntimeException("Could not look up enrolment " + id + ": " + e.getMessage(), e);
        }
        throw new RecordNotFoundException("No enrolment found with ID " + id);
    }

    @Override
    public List<Enrollment> findAll()
    {
        List<Enrollment> result = new ArrayList<>();
        String sql = "SELECT * FROM ENROLLMENT ORDER BY ENROLLMENT_ID";
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
            throw new RuntimeException("Could not load enrolments: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean existsById(String id)
    {
        String sql = "SELECT 1 FROM ENROLLMENT WHERE ENROLLMENT_ID=?";
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
            throw new RuntimeException("Could not check enrolment " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int count()
    {
        String sql = "SELECT COUNT(*) FROM ENROLLMENT";
        try(PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery())
        {
            return rs.next() ? rs.getInt(1) : 0;
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Could not count enrolments: " + e.getMessage(), e);
        }
    }

    private Enrollment mapRow(ResultSet rs) throws SQLException
    {
        double gradeValue = rs.getDouble("GRADE");
        // getDouble returns 0 for NULL, so wasNull tells an ungraded row apart from a real 0
        Double grade = rs.wasNull() ? null : gradeValue;
        return new Enrollment(
                rs.getString("ENROLLMENT_ID"),
                rs.getString("STUDENT_ID"),
                rs.getString("COURSE_CODE"),
                rs.getString("SEMESTER"),
                grade,
                EnrollmentStatus.valueOf(rs.getString("STATUS")));
    }

    private void setGrade(PreparedStatement statement, int index, Double grade) throws SQLException
    {
        if(grade == null)
        {
            statement.setNull(index, Types.DOUBLE);
        }
        else
        {
            statement.setDouble(index, grade);
        }
    }
}