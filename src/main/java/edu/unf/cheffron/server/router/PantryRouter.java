package edu.unf.cheffron.server.router;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.controller.PantryController;
import edu.unf.cheffron.server.util.CheffronLogger;
import edu.unf.cheffron.server.util.HttpUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.naming.AuthenticationException;

public class PantryRouter implements HttpHandler 
{
    PantryController controller = new PantryController();

    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        try
        {
            routeRequest(exchange);
        }
        catch (AuthenticationException ex)
        {
            HttpUtil.respondError(exchange, 401, ex.getMessage());
        }
        catch (Exception e)
        {
            CheffronLogger.log(Level.SEVERE, e.getMessage(), e);
            HttpUtil.respondError(exchange, 500, "Internal Server Error.");
        }
    }

    private void routeRequest(HttpExchange exchange) throws SQLException, AuthenticationException
    {
        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                controller.getPantry(exchange);
                break;
            case "PATCH":
                controller.updatePantry(exchange);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }
}
