package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.CheffronLogger;
import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.User;

import java.sql.*;
import java.util.logging.Level;

public class UserRepository extends Repository<String, User>
{
    public static UserRepository instance;

    private final Connection Connection;

    private final static String CreateStatement = "INSERT INTO user (UserId, Username, Email, Name, Password) VALUES (?, ?, ?, ?, ?)";
    private final static String ReadStatement = "SELECT * FROM user WHERE UserId = ?";
    private final static String ReadByEmailStatement = "SELECT * FROM user WHERE Email = ?";
    private final static String ReadByUsernameStatement = "SELECT * FROM user WHERE Username = ?";
    private final static String ReadAllStatement = "SELECT * FROM user";
    private final static String UpdateStatement = "UPDATE user SET Username = ?, Email = ?, Name = ?, Password = ? WHERE UserId = ?";
    private final static String DeleteStatement = "DELETE FROM user WHERE UserId = ?";

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
        Connection = MySQLDatabase.connect();
    }

    @Override
    public User create(User item) throws SQLException
    {
        var stmt = Connection.prepareStatement(CreateStatement);

        stmt.setString(1, item.getUserId());
        stmt.setString(2, item.getUsername());
        stmt.setString(3, item.getEmail());
        stmt.setString(4, item.getName());
        stmt.setString(5, item.getPassword());

        stmt.executeUpdate();

        return item;
    }

    @Override
    public String getReadAllStatement() {
        return ReadAllStatement;
    }

    @Override
    public User read(String id) throws SQLException
    {
        var stmt = Connection.prepareStatement(ReadStatement);

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
        var stmt = Connection.prepareStatement(ReadByEmailStatement);

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
        var stmt = Connection.prepareStatement(ReadByUsernameStatement);

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
        var stmt = Connection.prepareStatement(UpdateStatement);

        stmt.setString(1, item.getUsername());
        stmt.setString(2, item.getEmail());
        stmt.setString(3, item.getName());
        stmt.setString(4, item.getPassword());
        stmt.setString(5, id);

        stmt.executeUpdate();

        return new User(id, item.getUsername(), item.getEmail(), item.getName(), item.getPassword(), item.getChefHatsReceived());
    }

    @Override
    public boolean delete(String id) throws SQLException
    {
        var stmt = Connection.prepareStatement(DeleteStatement);

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