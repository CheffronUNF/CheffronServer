package edu.unf.cheffron.server.service.handler;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.CheffronLogger;
import edu.unf.cheffron.server.model.User;
import edu.unf.cheffron.server.repository.UserRepository;
import edu.unf.cheffron.server.service.AuthService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class UserHandler extends Endpoint implements HttpHandler 
{
    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        switch (exchange.getRequestMethod()) 
        {
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

    private void createUser(HttpExchange exchange) 
    {
        JsonObject json = getJsonBody(exchange);

        if (json == null)
        {
            respondError(exchange, 400, "Invalid json data received.");
        }

        json.addProperty("userId", UUID.randomUUID().toString());
        User user = User.fromJson(json);

        if (user == null)
        {
            respondError(exchange, 400, "Invalid data received.");
            return;
        }

        try 
        {
            if (UserRepository.instance.readByUsername(user.getUsername()) != null) {
                respondError(exchange, 400, "Username already exists");
                return;
            } else if (UserRepository.instance.readByEmail(user.getEmail()) != null) {
                respondError(exchange, 400, "Email already exists");
                return;
            }

            String password = AuthService.hash(user.getPassword().toCharArray());

            UserRepository.instance.create(new User(user.getUserId(), user.getUsername(), user.getEmail(), user.getName(), password, 0));
            String jwt = AuthService.createJWT(user.getUserId(), user.getUsername());

            JsonObject response = new JsonObject();
            response.addProperty("jwt", jwt);

            respond(exchange, 200, response);
        }
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            respondError(exchange, 500, "Internal server error when creating account");
        } 
    }
}
