package edu.unf.cheffron.server.service;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.database.model.User;

import java.io.*;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WebService {

    private static final System.Logger LOG = System.getLogger("CheffronWebService");
    private static final Gson GSON = new Gson();

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
        createContext(server, "/auth/create", "POST", httpExchange -> {
            JsonObject json = getJsonBody(httpExchange);
            if (json == null)
                return;
            // TODO : create account
        });
        createContext(server, "/auth/login", "POST", httpExchange -> {
            JsonObject json = getJsonBody(httpExchange);
            if (json == null)
                return;
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

    private JsonObject getJsonBody(HttpExchange exchange) {
        try {
            String line = readFirstLine(exchange);
            JsonElement element = JsonParser.parseString(line);
            if (element == null || element.isJsonNull() || !element.isJsonObject()) {
                respond(exchange, 400, "Invalid json received.");
            } else {
                // return appropriate json object
                return element.getAsJsonObject();
            }
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Could not read line from request body!", ex);
            respond(exchange, 500, "Could not read request");
        } catch (JsonSyntaxException ex) {
            LOG.log(Level.WARNING, "Received invalid JSON from client!", ex);
            respond(exchange, 400, "Invalid json received: " + ex.getMessage());
        }

        // errored out
        return null;
    }

    private String readFirstLine(HttpExchange exchange) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        return input.readLine();
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
            LOG.log(Level.WARNING, "Could not respond to client!", ex);
        }
    }
}
