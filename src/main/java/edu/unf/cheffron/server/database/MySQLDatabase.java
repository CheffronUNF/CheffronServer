package edu.unf.cheffron.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String connectionUrl = String.format("jdbc:mysql://%s:%d/%s", databaseHost, databasePort, databaseName);
            connection = DriverManager.getConnection(connectionUrl, databaseUser, databasePass);
        }
    }
}
