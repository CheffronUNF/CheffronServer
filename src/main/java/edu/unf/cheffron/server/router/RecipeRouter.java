package edu.unf.cheffron.server.router;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.controller.RecipeController;
import edu.unf.cheffron.server.util.CheffronLogger;
import edu.unf.cheffron.server.util.HttpUtil;

public class RecipeRouter implements HttpHandler 
{
    private RecipeController controller = new RecipeController();

    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        var uri = exchange.getRequestURI().getRawPath();
        var path = uri.split("/");

        try
        {
            routeRequest(exchange, path);
        }
        catch (SQLException e)
        {
            CheffronLogger.log(Level.SEVERE, "Error communicating with database!", e);
        }
    }

    private void routeRequest(HttpExchange exchange, String[] path) throws SQLException
    {

        switch (path.length)
        {
            case 2:
                recipes(exchange);
                break;
            case 3:
                recipe(exchange, path[2]);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
                break;
        }
    }

    private void recipes(HttpExchange exchange) throws SQLException
    {

        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                controller.getRecipes(exchange);
                break;
            case "POST":
                controller.postRecipes(exchange);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }

    private void recipe(HttpExchange exchange, String id) throws SQLException
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
