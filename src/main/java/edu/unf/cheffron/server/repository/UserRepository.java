package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.User;

import java.sql.*;

public class UserRepository implements Repository<String, User>
{
    private static UserRepository INSTANCE = new UserRepository();

    private Connection Connection;

    private PreparedStatement CreateStatement;
    private PreparedStatement ReadStatement;
    private PreparedStatement ReadAllStatement;
    private PreparedStatement UpdateStatement;
    private PreparedStatement DeleteStatement;

    public UserRepository()
    {
        try {
            Connection = MySQLDatabase.connect();

            CreateStatement = Connection.prepareStatement("INSERT INTO User (UserId, Username, Email, Name) VALUES ?, ?, ?, ?");
            ReadStatement = Connection.prepareStatement("SELECT * FROM User WHERE UserId = ?");
            ReadAllStatement = Connection.prepareStatement("SELECT * FROM User");
            UpdateStatement = Connection.prepareStatement("UPDATE User SET Username = ?, Email = ?, Name = ? WHERE UserId = ?");
            DeleteStatement = Connection.prepareStatement("DELETE FROM User WHERE UserId = ?");
        } catch (SQLException ex) {
            System.err.println("FATAL! Could not initialize User Repository!");
            ex.printStackTrace();
        }
    }

    public static UserRepository getUserRepository() {
        return INSTANCE;
    }

    @Override
    public User Create(User item) throws SQLException
    {
        CreateStatement.setString(1, item.getUserId());
        CreateStatement.setString(2, item.getUsername());
        CreateStatement.setString(3, item.getEmail());
        CreateStatement.setString(4, item.getName());
//        CreateStatement.setInt(1, item.getChefHatsReceived());

        CreateStatement.executeUpdate();

        return item;
    }

    @Override
    public User[] Read() throws SQLException
    {
        var rs = ReadAllStatement.executeQuery();
        var size = GetResultSetSize(rs);

        var res = new User[size];

        while (rs.next())
        {
            res[rs.getRow() - 1] = CreateUserFromRow(rs);
        }

        return res;
    }

    @Override
    public User Read(String id) throws SQLException
    {
        ReadStatement.setString(1, id);

        var rs = ReadStatement.executeQuery();

        if (!rs.next())
        {
            return null;
        }

        return CreateUserFromRow(rs);
    }

    @Override
    public User Update(String id, User item) throws SQLException
    {
        UpdateStatement.setString(1, item.getUsername());
        UpdateStatement.setString(2, item.getEmail());
        UpdateStatement.setString(3, item.getName());
//        UpdateStatement.setInt(4, item.getChefHatsReceived());
        UpdateStatement.setString(4, id);

        UpdateStatement.executeUpdate();

        return new User(id, item.getUsername(), item.getEmail(), item.getName(), item.getChefHatsReceived());
    }

    @Override
    public boolean Delete(String id) throws SQLException
    {
        DeleteStatement.setString(1, id);

        return DeleteStatement.executeUpdate() > 0;
    }

    private int GetResultSetSize(ResultSet rs)
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

    private User CreateUserFromRow(ResultSet rs) throws SQLException
    {
        String userId = rs.getString("userId");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String name = rs.getString("name");
//        int chefHatsReceived = rs.getInt("chefHatsReceived");

        return new User(userId, username, email, name, 0);
    }

    /**
     * Validates that the password matches for the username
     *
     * @return userId if validation successful, null otherwise
     */
    public String validateUserPassword(String username, String password) throws SQLException {
        try (Statement statement = Connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM user WHERE username = '" + username + "'");
            if (rs.next()) {
                String dbPassword = rs.getString("password");

                if (dbPassword.equals(password)) {
                    return rs.getString("userId");
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}