package edu.unf.cheffron.server.database;

import java.sql.*;

public class MySQLDatabase {

    private static String databaseHost, databaseName, databaseUser, databasePass;
    private static int databasePort;
    private static Connection connection;

    public static void initialize(String databaseHost, String databaseName, String databaseUser, String databasePass, int databasePort) 
    {
        MySQLDatabase.databaseHost = databaseHost;
        MySQLDatabase.databaseName = databaseName;
        MySQLDatabase.databaseUser = databaseUser;
        MySQLDatabase.databasePass = databasePass;
        MySQLDatabase.databasePort = databasePort;
    }

    public static Connection connect() throws SQLException 
    {
        if (!isConnected()) 
        {
            String connectionUrl = String.format("jdbc:mysql://%s:%d/%s", databaseHost, databasePort, databaseName);

            connection = DriverManager.getConnection(connectionUrl, databaseUser, databasePass);
        }

        return connection;
    }

    public static boolean isConnected() throws SQLException 
    {
        return connection != null && !connection.isClosed();
    }
}
