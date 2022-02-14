package edu.unf.cheffron.server.service.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import edu.unf.cheffron.server.model.User;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;

public abstract class Endpoint {

    protected static final System.Logger LOG = System.getLogger("CheffronWebService");

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
            String line = new String(exchange.getRequestBody().readAllBytes());
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

    protected void respond(HttpExchange exchange, int statusCode, String message) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

        try {
            exchange.sendResponseHeaders(statusCode, bytes.length);
            OutputStream body = exchange.getResponseBody();

            body.write(bytes);
            body.flush();
            exchange.close();
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Could not respond to client!", ex);
        }
    }
}
