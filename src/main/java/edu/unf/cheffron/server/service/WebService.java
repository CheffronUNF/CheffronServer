package edu.unf.cheffron.server.service;

import com.sun.net.httpserver.HttpServer;
import edu.unf.cheffron.server.database.MySQLDatabase;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebService {

    private final MySQLDatabase database;

    private HttpServer server;

    public WebService(MySQLDatabase database, int port) throws IOException {
        this.database = database;
        server = HttpServer.create(new InetSocketAddress(port), 0);

        registerContexts();
    }

    public void listen() {
        server.start();
    }

    private void registerContexts() {
        server.createContext("/auth/login", httpExchange -> {
            // TODO: handle login request
        });
    }
}
