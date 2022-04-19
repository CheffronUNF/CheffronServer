package edu.unf.cheffron.server.router;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.controller.RecipeController;
import edu.unf.cheffron.server.util.HttpUtil;

public class RecipeRouter implements HttpHandler 
{
    private RecipeController controller = new RecipeController();

    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        switch (exchange.getRequestMethod()) 
        {
            case "GET":
            case "POST":
            case "PATCH":
            case "DELETE":
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }
}
