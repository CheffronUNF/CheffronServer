package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.CheffronLogger;
import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.User;

import java.sql.*;
import java.util.logging.Level;

public class UserRepository implements Repository<String, User>
{
    public static UserRepository instance;

    private final Connection Connection;

    private final PreparedStatement CreateStatement;
    private final PreparedStatement ReadStatement;
    private final PreparedStatement ReadByEmailStatement;
    private final PreparedStatement ReadByUsernameStatement;
    private final PreparedStatement ReadAllStatement;
    private final PreparedStatement UpdateStatement;
    private final PreparedStatement DeleteStatement;

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

        CreateStatement = Connection.prepareStatement("INSERT INTO user (UserId, Username, Email, Name, Password) VALUES ?, ?, ?, ?, ?");
        ReadStatement = Connection.prepareStatement("SELECT * FROM user WHERE UserId = ?");
        ReadByEmailStatement = Connection.prepareStatement("SELECT * FROM user WHERE Email = ?");
        ReadByUsernameStatement = Connection.prepareStatement("SELECT * FROM user WHERE Username = ?");
        ReadAllStatement = Connection.prepareStatement("SELECT * FROM user");
        UpdateStatement = Connection.prepareStatement("UPDATE user SET Username = ?, Email = ?, Name = ?, Password = ? WHERE UserId = ?");
        DeleteStatement = Connection.prepareStatement("DELETE FROM user WHERE UserId = ?");
    }

    @Override
    public User create(User item) throws SQLException
    {
        CreateStatement.setString(1, item.getUserId());
        CreateStatement.setString(2, item.getUsername());
        CreateStatement.setString(3, item.getEmail());
        CreateStatement.setString(4, item.getName());
        CreateStatement.setString(5, item.getPassword());

        CreateStatement.executeUpdate();

        return item;
    }

    @Override
    public User[] read() throws SQLException
    {
        var rs = ReadAllStatement.executeQuery();
        var size = getResultSetSize(rs);

        var res = new User[size];

        while (rs.next())
        {
            res[rs.getRow() - 1] = createUserFromRow(rs);
        }

        return res;
    }

    @Override
    public User read(String id) throws SQLException
    {
        ReadStatement.setString(1, id);

        var rs = ReadStatement.executeQuery();

        if (!rs.next())
        {
            return null;
        }

        return createUserFromRow(rs);
    }

    public User readByEmail(String email) throws SQLException
    {
        ReadByEmailStatement.setString(1, email);

        var rs = ReadByEmailStatement.executeQuery();

        if (!rs.next())
        {
            return null;
        }

        return createUserFromRow(rs);
    }

    public User readByUsername(String username) throws SQLException
    {
        ReadByUsernameStatement.setString(1, username);

        var rs = ReadByUsernameStatement.executeQuery();

        if (!rs.next())
        {
            return null;
        }

        return createUserFromRow(rs);
    }

    @Override
    public User update(String id, User item) throws SQLException
    {
        UpdateStatement.setString(1, item.getUsername());
        UpdateStatement.setString(2, item.getEmail());
        UpdateStatement.setString(3, item.getName());
        UpdateStatement.setString(4, item.getPassword());
        UpdateStatement.setString(5, id);

        UpdateStatement.executeUpdate();

        return new User(id, item.getUsername(), item.getEmail(), item.getName(), item.getPassword(), item.getChefHatsReceived());
    }

    @Override
    public boolean delete(String id) throws SQLException
    {
        DeleteStatement.setString(1, id);

        return DeleteStatement.executeUpdate() > 0;
    }

    private int getResultSetSize(ResultSet rs)
    {
        int size = 0;

        try
        {
            int index = rs.getRow();

            rs.last();
            rs.getRow();

            rs.absolute(index);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return size;
    }

    private User createUserFromRow(ResultSet rs) throws SQLException
    {
        String userId = rs.getString("userId");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String name = rs.getString("name");
        String password = rs.getString("password");

        return new User(userId, username, email, name, password, 0);
    }
}