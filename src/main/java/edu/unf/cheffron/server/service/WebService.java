package edu.unf.cheffron.server.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.database.model.User;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.System.Logger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WebService {

    private static final System.Logger LOG = System.getLogger("CheffronWebService");

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
        createContext(server, "/auth/login", "POST", httpExchange -> {
            // TODO: handle login request
        });
    }

    private void createContext(HttpServer server, String path, String method, HttpHandler handler) {
        server.createContext(path, exchange -> {
            if (exchange.getRequestMethod().equals(method)) {
                handler.handle(exchange);
            }
        });
    }

    private void createAuthenticatedContext(HttpServer server, String path, String method,
                                            AuthenticatedHttpHandler handler) {
        createContext(server, path, method, exchange -> {
            User user = authenticateUser(exchange);

            if (user == null) {
                respond(exchange, 401, "Could not authenticate!");
            } else {
                handler.handle(exchange, user);
            }
        });
    }

    private User authenticateUser(HttpExchange exchange) {
        String jwt = exchange.getRequestHeaders().getFirst("Bearer");
        // validate jwt
        // check if issued date is after token expiration date for this user
        // return User object from database if authentication successful
        // return null if null successful
        // TODO : handle database authentication
        return null;
    }

    private void respond(HttpExchange exchange, int statusCode, String message) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;

        try {
            exchange.sendResponseHeaders(statusCode, length);
            OutputStream body = exchange.getResponseBody();

            body.write(bytes);
            body.flush();
            exchange.close();
        } catch (IOException ex) {
            LOG.log(Logger.Level.WARNING, "Could not respond to client!", ex);
        }
    }
}
