package edu.unf.cheffron.server.controller;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import edu.unf.cheffron.server.model.User;
import edu.unf.cheffron.server.repository.UserRepository;
import edu.unf.cheffron.server.util.AuthUtil;
import edu.unf.cheffron.server.util.CheffronLogger;
import edu.unf.cheffron.server.util.HttpUtil;

import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;

public class AuthController 
{
    private static final String AuthHeader = "Authorization";

    public void login(HttpExchange exchange)
    {
        var headers = exchange.getRequestHeaders();

        if (!headers.containsKey(AuthHeader))
        {
            HttpUtil.respondError(exchange, 401, "No Authorization header");
            return;
        }

        var auth = headers.get(AuthHeader);

        if (auth.isEmpty())
        {
            HttpUtil.respondError(exchange, 401, "No username or password");
            return;
        }

        String[] userpass;

        try
        {
            // remove the "Basic " from the string
            String basic = auth.get(0);
            String credentials = basic.substring(6, basic.length());
            String encoded = new String(Base64.getDecoder().decode(credentials));

            userpass = encoded.split(":");
        }
        catch (Exception ex)
        {
            HttpUtil.respondError(exchange, 401, "Malformed Authorization header");
            return;
        }

        if (userpass.length != 2)
        {
            HttpUtil.respondError(exchange, 401, "Missing username or password");
            return;
        }

        try
        {
            User user = UserRepository.instance.readByUsername(userpass[0]);

            if (!AuthUtil.authenticate(userpass[1].toCharArray(), user.password()))
            {
                HttpUtil.respondError(exchange, 401, "Incorrent username or password");
            }
            else
            {
                String jwt = AuthUtil.createJWT(user.userId(), userpass[0]);

                JsonObject response = new JsonObject();
                response.addProperty("jwt", jwt);

                HttpUtil.respond(exchange, 200, response);
            }
        }
        catch (Exception e)
        {
            CheffronLogger.log(Level.WARNING, e.getMessage());
            HttpUtil.respondError(exchange, 500, "Internal server error encountered when validating login");
        }
    }

    public void updatePassword(HttpExchange exchange)
    {
        var userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null)
        {
            HttpUtil.respond(exchange, 401, "Must be logged in.");
            return;
        }

        var json = HttpUtil.getJsonBody(exchange);
        var pass = json.get("password").getAsString();

        pass = AuthUtil.hash(pass.toCharArray());

        User user;
        try 
        {
            user = UserRepository.instance.read(userId);

            user = UserRepository.instance.update(userId, new User(user.userId(), user.username(), user.email(), user.name(), pass, user.chefHatsReceived()));
        } 
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
            HttpUtil.respondError(exchange, 500, "Internal server error");
            return;
        }

        HttpUtil.respond(exchange, 201);
    }
}
