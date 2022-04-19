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
        var uri = exchange.getRequestURI().getRawPath();
        var split = uri.split("/");

        switch (split.length)
        {
            case 2:
                recipes(exchange);
                break;
            case 3:
                recipe(exchange, split[2]);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
                break;
        }
    }

    private void recipes(HttpExchange exchange)
    {

        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                controller.getRecipes(exchange);
            case "POST":
                controller.postRecipes(exchange);
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }

    private void recipe(HttpExchange exchange, String id)
    {

        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                controller.getRecipe(exchange, id);
            case "PATCH":
                controller.patchRecipe(exchange, id);
            case "DELETE":
                controller.deleteRecipe(exchange, id);
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }
}
