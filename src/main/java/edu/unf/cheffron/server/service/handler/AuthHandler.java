package edu.unf.cheffron.server.service.handler;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.CheffronLogger;
import edu.unf.cheffron.server.model.User;
import edu.unf.cheffron.server.repository.UserRepository;
import edu.unf.cheffron.server.service.AuthService;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;

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
            default:
                respondError(exchange, 400, "Invalid request method!");
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
            respondError(exchange, 401, "Missing username or password");
            return;
        }

        try
        {
            User user = UserRepository.instance.readByUsername(userpass[0]);

            if (!AuthService.authenticate(userpass[1].toCharArray(), user.password()))
            {
                respondError(exchange, 401, "Incorrent username or password");
            }
            else
            {
                String jwt = AuthService.createJWT(user.userId(), userpass[0]);

                JsonObject response = new JsonObject();
                response.addProperty("jwt", jwt);

                respond(exchange, 200, response);
            }
        }
        catch (Exception e)
        {
            respondError(exchange, 500, "Internal server error encountered when validating login");
            CheffronLogger.log(Level.WARNING, e.getMessage());
        }
    }
}
