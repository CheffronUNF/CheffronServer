package edu.unf.cheffron.server.service.handler;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.unf.cheffron.server.database.MySQLDatabase;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class AuthHandler extends Endpoint implements HttpHandler 
{
    private static final String AuthHeader = "Authorization";

    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        switch (exchange.getRequestMethod()) {
            case "GET":
                Login(exchange);
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

    private void Login(HttpExchange exchange)
    {
        var headers = exchange.getRequestHeaders();

        if (!headers.containsKey(AuthHeader))
        {
            respond(exchange, 401, "No Authorization header");
            return;
        }

        var auth = headers.get(AuthHeader);
        
        if (auth.isEmpty())
        {
            respond(exchange, 401, "No username or password");
            return;
        }

        String[] userpass;

        try
        {
            var encoded = DecodeBase64(auth.get(0));

            userpass = encoded.split(":");
        }
        catch (Exception ex)
        {
            respond(exchange, 401, "Malformed Authorization header");
            return;
        }

        if (userpass.length != 2)
        {
            respond(exchange, 401, "Incorrect username or password");
            return;
        }

        
    }
    
    private String DecodeBase64(String str)
    {
        return java.util.Base64.getEncoder().encodeToString(str.getBytes());
    }
}
