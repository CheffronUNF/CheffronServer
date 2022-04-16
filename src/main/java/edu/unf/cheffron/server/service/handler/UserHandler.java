package edu.unf.cheffron.server.service.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.CheffronLogger;
import edu.unf.cheffron.server.model.User;
import edu.unf.cheffron.server.repository.UserRepository;
import edu.unf.cheffron.server.service.AuthService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;

public class UserHandler extends RequestHandler implements HttpHandler 
{
    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                getUser(exchange);
                break;
            case "POST":
                createUser(exchange);
                break;
            case "PATCH":
                updateUser(exchange);
                break;
            case "DELETE":
                deleteUser(exchange);
                break;
            default:
                respondError(exchange, 400, "Invalid request method!");
        }
    }

    private void getUser(HttpExchange exchange)
    {
        var json = getJsonBody(exchange);
        var userId = json.get("userId").getAsString();

        try 
        {
            var user = UserRepository.instance.read(userId);
            var res = gson.toJson(user);

            json = JsonParser.parseString(res).getAsJsonObject();
            json.remove("name");
            json.remove("email");
            json.remove("password");

            respond(exchange, 200, json);
        } 
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            respondError(exchange, 500, "Internal server error");
        }
    }

    private void createUser(HttpExchange exchange) 
    {
        JsonObject json = getJsonBody(exchange);

        if (json == null)
        {
            respondError(exchange, 400, "Invalid json data received.");
            return;
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
            if (UserRepository.instance.readByUsername(user.username()) != null) 
            {
                respondError(exchange, 400, "Username already exists");
                return;
            } 
            
            if (UserRepository.instance.readByEmail(user.email()) != null) 
            {
                respondError(exchange, 400, "Email already exists");
                return;
            }

            String password = AuthService.hash(user.password().toCharArray());

            UserRepository.instance.create(new User(user.userId(), user.username(), user.email(), user.name(), password, 0));
            String jwt = AuthService.createJWT(user.userId(), user.username());

            JsonObject response = new JsonObject();
            response.addProperty("jwt", jwt);

            respond(exchange, 200, response);
        }
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            respondError(exchange, 500, "Internal server error");
        } 
    }

    private void updateUser(HttpExchange exchange)
    {
        var userId = AuthService.authenticateRequest(exchange);

        if (userId == null)
        {
            respond(exchange, 401, "Must be logged in.");
            return;
        }

        var json = getJsonBody(exchange);
        
        try 
        {
            var userOrig = UserRepository.instance.read(userId);
            var jsonOrig = JsonParser.parseString(gson.toJson(userOrig)).getAsJsonObject();

            var jsonSet = json.entrySet();
            for (Entry<String,JsonElement> entry : jsonSet) 
            {
                if (entry.getKey().equals("userId") || entry.getKey().equals("password"))
                {
                    continue;
                }

                jsonOrig.remove(entry.getKey());
                jsonOrig.add(entry.getKey(), entry.getValue());
            }

            var user = User.fromJson(jsonOrig);
            UserRepository.instance.update(userId, user);

            respond(exchange, 200, "User updated");
        } 
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            respondError(exchange, 500, "Internal server error");
        }
    }

    private void deleteUser(HttpExchange exchange)
    {
        var userId = AuthService.authenticateRequest(exchange);

        if (userId == null)
        {
            respond(exchange, 401, "Must be logged in.");
        }

        try 
        {
            UserRepository.instance.delete(userId);
        } 
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            respondError(exchange, 500, "Internal server error");
        }
    }
}
