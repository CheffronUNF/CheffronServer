package edu.unf.cheffron.server.service.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.unf.cheffron.server.repository.UserRepository;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;

public class UserHandler extends Endpoint implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                break;
            case "POST":
                createUser(exchange);
                break;
            case "PATCH":
                break;
            case "DELETE":
                break;
            default:
                throw new Error("Unexpected request type");
        }
    }

    private void createUser(HttpExchange exchange) {
        JsonObject body = getJsonBody(exchange);

        if (!body.has("username") || !body.has("name")
                || !body.has("email") || !body.has("password")) {
            respondError(exchange, 400, "Missing required fields");
            return;
        }

        try {
            String username = body.get("username").getAsString();
            String name = body.get("name").getAsString();
            String email = body.get("email").getAsString();
            String password = new String(Base64.getDecoder().decode(body.get("password").getAsString()));

            int userExists = UserRepository.getUserRepository().checkIfUserExists(username, email);

            switch(userExists) {
                case 1:
                    respondError(exchange, 400, "Username already exists");
                    return;
                case 2:
                    respondError(exchange, 400, "Email already exists");
                    return;
            }

            String encryptedPassword = new String(Base64.getEncoder()
                    .encode(rsaEncrypt.doFinal(password.getBytes(StandardCharsets.UTF_8))));

            String userId = UserRepository.getUserRepository().createUser(username, name, email, encryptedPassword);
            String jwt = createJWTToken(userId, username);

            JsonObject response = new JsonObject();
            response.addProperty("jwt", jwt);

            respond(exchange, 200, response);
        } catch (JsonSyntaxException | NullPointerException e) {
            LOG.log(Level.WARNING, "Error while parsing user creation", e);
            respondError(exchange, 500, "Could not parse input data");
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error communicating with database!", e);
            respondError(exchange, 500, "Internal server error when creating account");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            LOG.log(Level.SEVERE, "Error when encrypting password!", e);
            respondError(exchange, 500, "Internal server error when creating account");
        }
    }
}
