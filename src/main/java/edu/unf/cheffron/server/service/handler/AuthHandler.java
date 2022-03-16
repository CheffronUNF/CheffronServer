package edu.unf.cheffron.server.service.handler;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.model.User;
import edu.unf.cheffron.server.repository.UserRepository;
import edu.unf.cheffron.server.service.AuthService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;

public class AuthHandler extends Endpoint implements HttpHandler
{
    private static final String AuthHeader = "Authorization";

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                login(exchange);
                break;
            case "POST":
                break;
            case "PATCH":
                break;
            case "DELETE":
                break;
            default:
                throw new Error("Unexpected request type");
        }
    }

    private void login(HttpExchange exchange)
    {
        var headers = exchange.getRequestHeaders();

        if (!headers.containsKey(AuthHeader))
        {
            respondError(exchange, 401, "No Authorization header");
            return;
        }

        var auth = headers.get(AuthHeader);

        if (auth.isEmpty())
        {
            respondError(exchange, 401, "No username or password");
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
            respondError(exchange, 401, "Malformed Authorization header");
            return;
        }

        if (userpass.length != 2)
        {
            respondError(exchange, 401, "Incorrect username or password");
            return;
        }

        String username = userpass[0];
        String password = userpass[1];

        try 
        {
            User user = UserRepository.instance.readByUsername(username);

            if (!AuthService.authenticate(password.toCharArray(), user.getPassword())) 
            {
                respondError(exchange, 401, "Login failed");
            } 
            else 
            {
                String jwt = AuthService.createJWT(user.getUserId(), username);

                JsonObject response = new JsonObject();
                response.addProperty("jwt", jwt);

                respond(exchange, 200, response);
            }
        }
        catch (SQLException e) 
        {
            respondError(exchange, 500, "Internal server error encountered when validating login");
        }
    }
}
