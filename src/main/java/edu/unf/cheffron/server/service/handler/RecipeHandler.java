package edu.unf.cheffron.server.service.handler;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RecipeHandler extends RequestHandler implements HttpHandler 
{
    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                break;
            case "POST":
                break;
            case "PATCH":
                break;
            case "DELETE":
                break;
            default:
                respondError(exchange, 400, "Invalid request method!");
        }
    }
}
