package edu.unf.cheffron.server.router;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.unf.cheffron.server.model.Ingredient;
import edu.unf.cheffron.server.repository.IngredientRepository;
import edu.unf.cheffron.server.util.HttpUtil;

public class IngredientRouter implements HttpHandler 
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
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }

    private void getAllIngredients(HttpExchange exchange) 
    {
        try 
        {
            List<Ingredient> ingredients = IngredientRepository.instance.read();
            HttpUtil.respond(exchange, 200, HttpUtil.toJson(ingredients));
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            HttpUtil.respondError(exchange, 500, "Error when getting ingredients");
        }
    }
}
