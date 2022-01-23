package edu.unf.cheffron.server;

import edu.unf.cheffron.server.database.MySQLDatabase;

import java.sql.SQLException;

public class CheffronServer {

    public static void main(String[] args) {
        // connect to database and instantiate database object
        MySQLDatabase database = connectToDatabase(args);

        if (database == null) {
            System.exit(1);
            return;
        }

        // TODO: start web server and begin listening for requests
    }

    private static MySQLDatabase connectToDatabase(String[] args) {
        System.out.println("Attempting connection to database...");

        if (args.length < 5) {
            System.err.println("Invalid starting parameters. Enter database information as command line arguments");
            System.err.println("Format: [db host] [db name] [db user] [db pass] [db port]");
            return null;
        }

        String host = args[0];
        String name = args[1];
        String user = args[2];
        String pass = args[3];
        String port = args[4];

        if (!port.matches("[0-9]+")) {
            System.err.println("Port must be a valid integer!");
            return null;
        }

        MySQLDatabase database = new MySQLDatabase(host, name, user, pass, Integer.parseInt(port));

        // test connection
        try {
            database.connect();
        } catch (SQLException e) {
            System.err.println("Could not connect database! Check host and credentials");
            e.printStackTrace();
            return null;
        }

        System.out.println("Successfully connected to database.");

        return database;
    }
}
