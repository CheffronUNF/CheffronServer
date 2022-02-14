package edu.unf.cheffron.server.service;

import com.sun.net.httpserver.HttpServer;
import edu.unf.cheffron.server.service.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebService {
    private static final System.Logger LOG = System.getLogger("CheffronWebService");

    private HttpServer server;

    public WebService(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        registerContexts();
    }

    public void listen() {
        server.start();
    }

    private void registerContexts() {
        server.createContext("/auth", new AuthHandler());
        server.createContext("/ingredient", new IngredientHandler());
        server.createContext("/recipe", new RecipeHandler());
        server.createContext("/user", new RecipeHandler());
    }
}
