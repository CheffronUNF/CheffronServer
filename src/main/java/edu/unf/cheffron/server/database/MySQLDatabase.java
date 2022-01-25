package edu.unf.cheffron.server.database;

import edu.unf.cheffron.server.database.model.User;

import java.sql.*;

public class MySQLDatabase {

    private final String databaseHost, databaseName, databaseUser, databasePass;
    private final int databasePort;
    private Connection connection;

    public MySQLDatabase(String databaseHost, String databaseName, String databaseUser, String databasePass,
                         int databasePort) {
        this.databaseHost = databaseHost;
        this.databaseName = databaseName;
        this.databaseUser = databaseUser;
        this.databasePass = databasePass;
        this.databasePort = databasePort;
    }

    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public Connection connect() throws SQLException {
        if (!isConnected()) {
            String connectionUrl = String.format("jdbc:mysql://%s:%d/%s", databaseHost, databasePort, databaseName);
            connection = DriverManager.getConnection(connectionUrl, databaseUser, databasePass);
        }
        return connection;
    }

    private Connection getConnection() throws SQLException {
        return connect();
    }

    private Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }

    // example SQL request
    public User getUserDetailsById(int userId) throws SQLException {
        try (Statement statement = createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM User WHERE userId = " + userId);

            if (!resultSet.next()) {
                // not found
                return null;
            }

            String username = resultSet.getString("username");
            String email = resultSet.getString("email");
            String name = resultSet.getString("name");
            int chefHatsReceived = resultSet.getInt("chefHatsReceived");

            return new User(userId, username, email, name, chefHatsReceived);
        }
    }
}
