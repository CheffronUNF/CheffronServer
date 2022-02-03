package edu.unf.cheffron.server.service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.service.endpoint.Auth;
import edu.unf.cheffron.server.service.endpoint.Ingredient;
import edu.unf.cheffron.server.service.endpoint.Recipe;

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
        new Auth().registerContexts(server, database);
        new Ingredient().registerContexts(server, database);
        new Recipe().registerContexts(server, database);
        new edu.unf.cheffron.server.service.endpoint.User().registerContexts(server, database);
    }
}
