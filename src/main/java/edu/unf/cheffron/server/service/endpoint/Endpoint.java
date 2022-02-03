package edu.unf.cheffron.server.service.endpoint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.database.model.User;
import edu.unf.cheffron.server.service.AuthenticatedHttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;

public abstract class Endpoint {

    protected static final System.Logger LOG = System.getLogger("CheffronWebService");

    abstract void registerContexts(HttpServer server, MySQLDatabase database);

    protected void createContext(HttpServer server, String path, String method, HttpHandler handler) {
        server.createContext(path, exchange -> {
            if (exchange.getRequestMethod().equals(method)) {
                handler.handle(exchange);
            }
        });
    }

    protected void createAuthenticatedContext(HttpServer server, String path, String method,
                                            AuthenticatedHttpHandler handler) {
        createContext(server, path, method, exchange -> {
            edu.unf.cheffron.server.database.model.User user = authenticateUser(exchange);

            if (user == null) {
                respond(exchange, 401, "Could not authenticate!");
            } else {
                handler.handle(exchange, user);
            }
        });
    }

    protected User authenticateUser(HttpExchange exchange) {
        String jwt = exchange.getRequestHeaders().getFirst("Bearer");
        // validate jwt
        // check if issued date is after token expiration date for this user
        // return User object from database if authentication successful
        // return null if null successful
        // TODO : handle database authentication
        return null;
    }

    protected JsonObject getJsonBody(HttpExchange exchange) {
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

    protected String readFirstLine(HttpExchange exchange) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        return input.readLine();
    }

    protected void respond(HttpExchange exchange, int statusCode, String message) {
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
