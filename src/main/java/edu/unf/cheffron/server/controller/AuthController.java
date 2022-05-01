package edu.unf.cheffron.server.controller;

import com.sun.net.httpserver.HttpExchange;

import edu.unf.cheffron.server.model.User;
import edu.unf.cheffron.server.repository.UserRepository;
import edu.unf.cheffron.server.util.AuthUtil;
import edu.unf.cheffron.server.util.HttpUtil;

import java.sql.SQLException;
import java.util.Base64;

import javax.naming.AuthenticationException;

public class AuthController 
{
    private static final String AuthHeader = "Authorization";

    public void login(HttpExchange exchange) throws AuthenticationException, SQLException
    {
        var headers = exchange.getRequestHeaders();

        if (!headers.containsKey(AuthHeader))
        {
            throw new AuthenticationException("No Authorization header.");
        }

        var auth = headers.get(AuthHeader);

        if (auth.isEmpty())
        {
            throw new AuthenticationException("Malformed Authorization header.");
        }

        // remove the "Basic " from the string
        String basic = auth.get(0);
        String credentials = basic.substring(6, basic.length());
        String encoded = new String(Base64.getDecoder().decode(credentials));

        String[] userpass = encoded.split(":");

        if (userpass.length != 2)
        {
            throw new AuthenticationException("Missing username or password.");
        }

        User user = UserRepository.instance.readByUsername(userpass[0]);

        if (!AuthUtil.authenticate(userpass[1].toCharArray(), user.password()))
        {
            throw new AuthenticationException("Incorrect username or password.");
        }
        
        String jwt = AuthUtil.createJWT(user.userId(), userpass[0]);

        exchange.getResponseHeaders().add("jwt", jwt);
        HttpUtil.respond(exchange, 200);
    }

    public void updatePassword(HttpExchange exchange) throws SQLException
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

        User user = UserRepository.instance.read(userId);

        UserRepository.instance.update(userId, new User(user.userId(), user.username(), user.email(), user.name(), pass, user.chefHatsReceived()));

        HttpUtil.respond(exchange, 201);
    }
}
