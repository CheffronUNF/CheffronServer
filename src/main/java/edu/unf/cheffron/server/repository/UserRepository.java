package edu.unf.cheffron.server.repository;

import java.sql.*;

import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.User;

public class UserRepository implements Repository<String, User> 
{
    private final Connection Connection;

    private final PreparedStatement CreateStatement;
    private final PreparedStatement ReadStatement;
    private final PreparedStatement ReadAllStatement;
    private final PreparedStatement UpdateStatement;
    private final PreparedStatement DeleteStatement;

    public UserRepository() throws SQLException
    {
        Connection = MySQLDatabase.connect();

        CreateStatement = Connection.prepareStatement("INSERT INTO User (UserId, Username, Email, Name, ChefHatsReceived) VALUES ?, ?, ?, ?, ?");
        ReadStatement = Connection.prepareStatement("SELECT * FROM User WHERE UserId = ?");
        ReadAllStatement = Connection.prepareStatement("SELECT * FROM User");
        UpdateStatement = Connection.prepareStatement("UPDATE User SET Username = ?, Email = ?, Name = ?, ChefHatsReceived = ? WHERE UserId = ?");
        DeleteStatement = Connection.prepareStatement("DELETE FROM User WHERE UserId = ?");
    }

    @Override
    public User Create(User item) throws SQLException 
    {
        CreateStatement.setString(1, item.getUserId());
        CreateStatement.setString(1, item.getUsername());
        CreateStatement.setString(1, item.getEmail());
        CreateStatement.setString(1, item.getName());
        CreateStatement.setInt(1, item.getChefHatsReceived());

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
        UpdateStatement.setInt(4, item.getChefHatsReceived());
        UpdateStatement.setString(5, id);

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
        int chefHatsReceived = rs.getInt("chefHatsReceived");

        return new User(userId, username, email, name, chefHatsReceived);
    }
}