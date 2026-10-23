package studentinformationmanagementsystem;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton that owns the embedded Derby connection and creates the tables on first run.
 *
 * Singleton structure and CREATE TABLE statements drafted with assistance from
 * Claude AI; modified by Vikram to use a DatabaseMetaData table check, match our
 * column names and share one connection — see AI Interaction Log.
 *
 * @author Vikram Bugata
 */

public final class DBConnectionManager
{

    private static final String DB_URL = "jdbc:derby:data/SIMSDB;create=true";

    private static DBConnectionManager instance;

    private final Connection connection;

    private DBConnectionManager()
    {
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            createTablesIfMissing();
        } 
        catch (SQLException e)
        {
            throw new RuntimeException("Could not start the embedded Derby database: " + e.getMessage(), e);
        }
    }

    public static synchronized DBConnectionManager getInstance()
    {
        if(instance == null)
        {
            instance = new DBConnectionManager();
        }
        return instance;
    }

    public Connection getConnection()
    {
        return connection;
    }

    private void createTablesIfMissing() throws SQLException
    {
        createTableIfMissing("STUDENT",
                "CREATE TABLE STUDENT ("
                + "ID VARCHAR(10) PRIMARY KEY, "
                + "NAME VARCHAR(100) NOT NULL, "
                + "EMAIL VARCHAR(100) NOT NULL, "
                + "PHONE VARCHAR(20) NOT NULL, "
                + "YEAR_LEVEL INT NOT NULL, "
                + "MAJOR VARCHAR(100) NOT NULL, "
                + "GPA DOUBLE NOT NULL, "
                + "STATUS VARCHAR(20) NOT NULL, "
                + "ENROLLED_COURSE_CODES VARCHAR(500))");

        createTableIfMissing("TEACHER",
                "CREATE TABLE TEACHER ("
                + "ID VARCHAR(10) PRIMARY KEY, "
                + "NAME VARCHAR(100) NOT NULL, "
                + "EMAIL VARCHAR(100) NOT NULL, "
                + "PHONE VARCHAR(20) NOT NULL, "
                + "DEPARTMENT VARCHAR(100) NOT NULL, "
                + "COURSES_TAUGHT VARCHAR(500))");

        createTableIfMissing("COURSE",
                "CREATE TABLE COURSE ("
                + "COURSE_CODE VARCHAR(10) PRIMARY KEY, "
                + "COURSE_NAME VARCHAR(150) NOT NULL, "
                + "CREDIT_POINTS INT NOT NULL, "
                + "CAPACITY INT NOT NULL, "
                + "TEACHER_ID VARCHAR(10), "
                + "ENROLLED_STUDENT_IDS VARCHAR(1000), "
                + "WAITLIST VARCHAR(1000))");

        createTableIfMissing("ENROLLMENT",
                "CREATE TABLE ENROLLMENT ("
                + "ENROLLMENT_ID VARCHAR(10) PRIMARY KEY, "
                + "STUDENT_ID VARCHAR(10) NOT NULL, "
                + "COURSE_CODE VARCHAR(10) NOT NULL, "
                + "SEMESTER VARCHAR(20) NOT NULL, "
                + "GRADE DOUBLE, "
                + "STATUS VARCHAR(20) NOT NULL)");
    }

    // DatabaseMetaData table check written by Vikram, replacing the AI's original approach.
    private void createTableIfMissing(String tableName, String createSql) throws SQLException
    {
        DatabaseMetaData meta = connection.getMetaData();
        try(ResultSet rs = meta.getTables(null, null, tableName, null))
        {
            if(rs.next())
            {
                return;
            }
        }
        try(Statement statement = connection.createStatement())
        {
            statement.executeUpdate(createSql);
        }
    }

    public void shutdown()
    {
        try
        {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        }
        catch (SQLException e)
        {
            // Derby always throws on a successful shutdown, so this is expected
        }
    }
}