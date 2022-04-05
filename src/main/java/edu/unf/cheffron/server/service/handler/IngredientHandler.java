package edu.unf.cheffron.server.service.handler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.unf.cheffron.server.model.Ingredient;
import edu.unf.cheffron.server.repository.IngredientRepository;

public class IngredientHandler extends Endpoint implements HttpHandler 
{
    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                getAllIngredients(exchange);
                break;
            default:
                respondError(exchange, 400, "Invalid request method!");
        }
    }

    private void getAllIngredients(HttpExchange exchange) 
    {
        try 
        {
            List<Ingredient> ingredients = IngredientRepository.instance.read();
            respond(exchange, 200, gson.toJson(ingredients));
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            respondError(exchange, 500, "Error when getting ingredients");
        }
    }
}
