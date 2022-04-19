package edu.unf.cheffron.server.router;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.controller.IngredientController;
import edu.unf.cheffron.server.util.HttpUtil;

public class IngredientRouter implements HttpHandler 
{
    private IngredientController controller = new IngredientController();

    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                controller.getAllIngredients(exchange);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }
}
