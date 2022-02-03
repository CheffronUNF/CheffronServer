package edu.unf.cheffron.server.database;

import edu.unf.cheffron.server.database.model.User;

import java.sql.*;

public class MySQLDatabase {

    private static String databaseHost, databaseName, databaseUser, databasePass;
    private static int databasePort;
    private static Connection connection;

    public static void initialize(String databaseHost, String databaseName, String databaseUser, String databasePass,
                                  int databasePort) {
        MySQLDatabase.databaseHost = databaseHost;
        MySQLDatabase.databaseName = databaseName;
        MySQLDatabase.databaseUser = databaseUser;
        MySQLDatabase.databasePass = databasePass;
        MySQLDatabase.databasePort = databasePort;
    }

    public static boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public static Connection connect() throws SQLException {
        if (!isConnected()) {
            String connectionUrl = String.format("jdbc:mysql://%s:%d/%s", databaseHost, databasePort, databaseName);
            connection = DriverManager.getConnection(connectionUrl, databaseUser, databasePass);
        }
        return connection;
    }

    private static Connection getConnection() throws SQLException {
        return connect();
    }

    private static Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }

    // example SQL request
    public static User getUserDetailsById(int userId) throws SQLException {
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
