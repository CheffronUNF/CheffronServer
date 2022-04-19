package edu.unf.cheffron.server.controller;

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
import java.util.logging.Level;

public class UserController
{
    public void getUser(HttpExchange exchange, String id)
    {
        try 
        {
            var user = UserRepository.instance.read(id);

            if (user == null)
            {
                HttpUtil.respondError(exchange, 404, "User not found.");
                return;
            }

            var res = HttpUtil.toJson(user);
            var json = JsonParser.parseString(res).getAsJsonObject();
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

    public void postUser(HttpExchange exchange) 
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
            if (UserRepository.instance.readByUsername(user.username()) != null || UserRepository.instance.readByEmail(user.email()) != null) 
            {
                HttpUtil.respondError(exchange, 406, "Username or Email already used.");
                return;
            } 

            String password = AuthUtil.hash(user.password().toCharArray());

            user = UserRepository.instance.create(new User(user.userId(), user.username(), user.email(), user.name(), password, 0));

            JsonObject response = HttpUtil.toJsonObject(user);
            response.remove("name");
            response.remove("email");
            response.remove("password");

            HttpUtil.respond(exchange, 201, response.toString());
        }
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            HttpUtil.respondError(exchange, 500, "Internal server error");
        } 
    }

    public void patchUser(HttpExchange exchange, String id)
    {
        var userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null || !userId.equals(id))
        {
            HttpUtil.respond(exchange, 401, "Must be logged in.");
            return;
        }

        var json = HttpUtil.getJsonBody(exchange);
        
        try 
        {
            var user = UserRepository.instance.read(userId);

            if (user == null)
            {
                HttpUtil.respondError(exchange, 404, "User not found.");
                return;
            }

            json.remove("userId");
            json.addProperty("userId", user.userId());

            json.remove("password");
            json.addProperty("password", user.password());

            UserRepository.instance.update(userId, User.fromJson(json));
            HttpUtil.respond(exchange, 201);
        } 
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            HttpUtil.respondError(exchange, 500, "Internal server error");
        }
    }

    public void deleteUser(HttpExchange exchange, String id)
    {
        var userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null || !userId.equals(id))
        {
            HttpUtil.respond(exchange, 401, "Must be logged in.");
        }

        try 
        {
            var user = UserRepository.instance.read(id);

            if (user == null)
            {
                HttpUtil.respondError(exchange, 404, "User not found.");
                return;
            }
            
            UserRepository.instance.delete(userId);
            HttpUtil.respond(exchange, 200);
        } 
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            HttpUtil.respondError(exchange, 500, "Internal server error");
        }
    }
}
