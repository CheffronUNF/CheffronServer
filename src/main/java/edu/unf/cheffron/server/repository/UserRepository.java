package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.model.User;

import java.sql.*;

public class UserRepository extends Repository<String, User>
{
    public static UserRepository instance;

    private final static String createStatement = "INSERT INTO user (UserId, Username, Email, Name, Password) VALUES (?, ?, ?, ?, ?)";
    private final static String readStatement = "SELECT * FROM user WHERE UserId = ?";
    private final static String readByEmailStatement = "SELECT * FROM user WHERE Email = ?";
    private final static String readByUsernameStatement = "SELECT * FROM user WHERE Username = ?";
    private final static String readAllStatement = "SELECT * FROM user";
    private final static String updateStatement = "UPDATE user SET Username = ?, Email = ?, Name = ?, Password = ? WHERE UserId = ?";
    private final static String deleteStatement = "DELETE FROM user WHERE UserId = ?";
    
    static
    {
        instance = new UserRepository();
    }

    @Override
    public User create(User item) throws SQLException
    {
        var stmt = createStatement(createStatement);

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
        var stmt = createStatement(readStatement);

        stmt.setString(1, id);

        var rs = stmt.executeQuery();
        return rs.next() ? createFromRow(rs) : null;
    }

    public User readByEmail(String email) throws SQLException
    {
        var stmt = createStatement(readByEmailStatement);

        stmt.setString(1, email);

        var rs = stmt.executeQuery();
        return rs.next() ? createFromRow(rs) : null;
    }

    public User readByUsername(String username) throws SQLException
    {
        var stmt = createStatement(readByUsernameStatement);

        stmt.setString(1, username);

        var rs = stmt.executeQuery();
        return rs.next() ? createFromRow(rs) : null;
    }

    @Override
    public User update(String id, User item) throws SQLException
    {
        var stmt = createStatement(updateStatement);

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
        var stmt = createStatement(deleteStatement);

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