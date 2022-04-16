package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.User;
import edu.unf.cheffron.server.util.CheffronLogger;

import java.sql.*;
import java.util.logging.Level;

public class UserRepository extends Repository<String, User>
{
    public static UserRepository instance;

    private final Connection connection;

    private final static String createStatement = "INSERT INTO user (UserId, Username, Email, Name, Password) VALUES (?, ?, ?, ?, ?)";
    private final static String readStatement = "SELECT * FROM user WHERE UserId = ?";
    private final static String readByEmailStatement = "SELECT * FROM user WHERE Email = ?";
    private final static String readByUsernameStatement = "SELECT * FROM user WHERE Username = ?";
    private final static String readAllStatement = "SELECT * FROM user";
    private final static String updateStatement = "UPDATE user SET Username = ?, Email = ?, Name = ?, Password = ? WHERE UserId = ?";
    private final static String deleteStatement = "DELETE FROM user WHERE UserId = ?";

    static
    {
        try
        {
            instance = new UserRepository();
        }
        catch (SQLException ex)
        {
            CheffronLogger.log(Level.SEVERE, "Could not initialize User Repository!", ex);
            System.exit(1);
        }
    }

    private UserRepository() throws SQLException
    {
        connection = MySQLDatabase.connect();
    }

    @Override
    public User create(User item) throws SQLException
    {
        var stmt = connection.prepareStatement(createStatement);

        stmt.setString(1, item.userId());
        stmt.setString(2, item.username());
        stmt.setString(3, item.email());
        stmt.setString(4, item.name());
        stmt.setString(5, item.password());

        stmt.executeUpdate();

        return item;
    }

    @Override
    public String getReadAllStatement() 
    {
        return readAllStatement;
    }

    @Override
    public User read(String id) throws SQLException
    {
        var stmt = connection.prepareStatement(readStatement);

        stmt.setString(1, id);

        var rs = stmt.executeQuery();
        if (!rs.next())
        {
            return null;
        }
        
        return createFromRow(rs);
    }

    public User readByEmail(String email) throws SQLException
    {
        var stmt = connection.prepareStatement(readByEmailStatement);

        stmt.setString(1, email);

        var rs = stmt.executeQuery();
        if (!rs.next())
        {
            return null;
        }

        return createFromRow(rs);
    }

    public User readByUsername(String username) throws SQLException
    {
        var stmt = connection.prepareStatement(readByUsernameStatement);

        stmt.setString(1, username);

        var rs = stmt.executeQuery();
        if (!rs.next())
        {
            return null;
        }

        return createFromRow(rs);
    }

    @Override
    public User update(String id, User item) throws SQLException
    {
        var stmt = connection.prepareStatement(updateStatement);

        stmt.setString(1, item.username());
        stmt.setString(2, item.email());
        stmt.setString(3, item.name());
        stmt.setString(4, item.password());
        stmt.setString(5, id);

        stmt.executeUpdate();

        return new User(id, item.username(), item.email(), item.name(), item.password(), item.chefHatsReceived());
    }

    @Override
    public boolean delete(String id) throws SQLException
    {
        var stmt = connection.prepareStatement(deleteStatement);

        stmt.setString(1, id);

        return stmt.executeUpdate() > 0;
    }

    protected User createFromRow(ResultSet rs) throws SQLException
    {
        String userId = rs.getString("userId");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String name = rs.getString("name");
        String password = rs.getString("password");

        return new User(userId, username, email, name, password, 0);
    }
}