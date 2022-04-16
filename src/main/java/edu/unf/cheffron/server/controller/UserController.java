package edu.unf.cheffron.server.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;

import edu.unf.cheffron.server.model.User;
import edu.unf.cheffron.server.repository.UserRepository;
import edu.unf.cheffron.server.util.AuthUtil;
import edu.unf.cheffron.server.util.CheffronLogger;
import edu.unf.cheffron.server.util.HttpUtil;

import java.sql.SQLException;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;

public class UserController
{
    public void getUser(HttpExchange exchange)
    {
        var json = HttpUtil.getJsonBody(exchange);
        var userId = json.get("userId").getAsString();

        try 
        {
            var user = UserRepository.instance.read(userId);
            var res = HttpUtil.toJson(user);

            json = JsonParser.parseString(res).getAsJsonObject();
            json.remove("name");
            json.remove("email");
            json.remove("password");

            HttpUtil.respond(exchange, 200, json);
        } 
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            HttpUtil.respondError(exchange, 500, "Internal server error");
        }
    }

    public void createUser(HttpExchange exchange) 
    {
        JsonObject json = HttpUtil.getJsonBody(exchange);

        if (json == null)
        {
            HttpUtil.respondError(exchange, 400, "Invalid json data received.");
            return;
        }

        json.addProperty("userId", UUID.randomUUID().toString());
        User user = User.fromJson(json);

        if (user == null)
        {
            HttpUtil.respondError(exchange, 400, "Invalid data received.");
            return;
        }

        try 
        {
            if (UserRepository.instance.readByUsername(user.username()) != null) 
            {
                HttpUtil.respondError(exchange, 400, "Username already exists");
                return;
            } 
            
            if (UserRepository.instance.readByEmail(user.email()) != null) 
            {
                HttpUtil.respondError(exchange, 400, "Email already exists");
                return;
            }

            String password = AuthUtil.hash(user.password().toCharArray());

            UserRepository.instance.create(new User(user.userId(), user.username(), user.email(), user.name(), password, 0));
            String jwt = AuthUtil.createJWT(user.userId(), user.username());

            JsonObject response = new JsonObject();
            response.addProperty("jwt", jwt);

            HttpUtil.respond(exchange, 200, response);
        }
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            HttpUtil.respondError(exchange, 500, "Internal server error");
        } 
    }

    public void updateUser(HttpExchange exchange)
    {
        var userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null)
        {
            HttpUtil.respond(exchange, 401, "Must be logged in.");
            return;
        }

        var json = HttpUtil.getJsonBody(exchange);
        
        try 
        {
            var userOrig = UserRepository.instance.read(userId);
            var jsonOrig = JsonParser.parseString(HttpUtil.toJson(userOrig)).getAsJsonObject();

            var jsonSet = json.entrySet();
            for (Entry<String, JsonElement> entry : jsonSet) 
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

            HttpUtil.respond(exchange, 200, "User updated");
        } 
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            HttpUtil.respondError(exchange, 500, "Internal server error");
        }
    }

    public void deleteUser(HttpExchange exchange)
    {
        var userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null)
        {
            HttpUtil.respond(exchange, 401, "Must be logged in.");
        }

        try 
        {
            UserRepository.instance.delete(userId);
        } 
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            HttpUtil.respondError(exchange, 500, "Internal server error");
        }
    }
}
