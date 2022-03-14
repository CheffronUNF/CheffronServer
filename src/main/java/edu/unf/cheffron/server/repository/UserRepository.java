package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.User;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Base64;
import java.util.UUID;

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

            CreateStatement = Connection.prepareStatement("INSERT INTO user (UserId, Username, Email, Name) VALUES ?, ?, ?, ?");
            ReadStatement = Connection.prepareStatement("SELECT * FROM user WHERE UserId = ?");
            ReadAllStatement = Connection.prepareStatement("SELECT * FROM user");
            UpdateStatement = Connection.prepareStatement("UPDATE user SET Username = ?, Email = ?, Name = ? WHERE UserId = ?");
            DeleteStatement = Connection.prepareStatement("DELETE FROM user WHERE UserId = ?");
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
     * Creates user from required user fields inputted through account page
     *
     * @return userId of newly created user
     */
    public String createUser(String username, String name, String email, String password) throws SQLException {
        String userId = findNextUserId();

        try (Statement statement = Connection.createStatement()) {
            String sql = String.format("INSERT INTO user (userId, username, name, email, password) " +
                    "VALUES ('%s', '%s', '%s', '%s', '%s')", userId, username, name, email, password);
            statement.execute(sql);

            return userId;
        }
    }

    public String findNextUserId() throws SQLException {
        while (true) {
            UUID uuid = UUID.randomUUID();
            User user = Read(uuid.toString());
            if (user == null)
                return uuid.toString();
        }
    }

    /**
     * Check if user already exists based on username and email supplied from create account
     *
     * @return 0 if user does not exist, 1 if username exists, 2 if email exists
     */
    public int checkIfUserExists(String username, String email) throws SQLException {
        try (Statement statement = Connection.createStatement()) {
            ResultSet rs = statement
                    .executeQuery(String.format("SELECT username FROM user WHERE username = '%s' OR email = '%s'"
                            , username, email));
            if (rs.next()) {
                if (username.equalsIgnoreCase(rs.getString("username"))) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        }
    }

    /**
     * Validates that the password matches for the username
     *
     * @return userId if validation successful, null otherwise
     */
    public String validateUserPassword(String username, String password, Cipher rsaDecrypt) throws SQLException, IllegalBlockSizeException, BadPaddingException {
        try (Statement statement = Connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT userId, password FROM user WHERE username = '" + username + "'");
            if (rs.next()) {
                System.out.println(password);
                String dbPassword = rs.getString("password");
                System.out.println(dbPassword);
                dbPassword = new String(Base64.getDecoder().decode(dbPassword));
                System.out.println(dbPassword);
                dbPassword = new String(rsaDecrypt.doFinal(dbPassword.getBytes(StandardCharsets.UTF_8)));

                System.out.println(dbPassword);

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