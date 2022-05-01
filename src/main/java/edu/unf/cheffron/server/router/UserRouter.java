package edu.unf.cheffron.server.router;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.controller.UserController;
import edu.unf.cheffron.server.exception.HttpException;
import edu.unf.cheffron.server.util.CheffronLogger;
import edu.unf.cheffron.server.util.HttpUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

public class UserRouter implements HttpHandler 
{
    private UserController controller = new UserController();

    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        var uri = exchange.getRequestURI().getRawPath();
        var path = uri.split("/");

        try
        {
            routeRequest(exchange, path);
        }
        catch (HttpException e)
        {
            HttpUtil.respondError(exchange, e.statusCode, e.message);
        }
        catch (Exception e)
        {
            CheffronLogger.log(Level.SEVERE, e.getMessage(), e);
            HttpUtil.respondError(exchange, 500, "Internal Server Error.");
        }
    }

    private void routeRequest(HttpExchange exchange, String[] path) throws SQLException
    {

        switch (path.length)
        {
            case 2:
                users(exchange);
                break;
            case 3:
                user(exchange, path[2]);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
                break;
        }
    }

    private void users(HttpExchange exchange) throws SQLException
    {
        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
                break;
            case "POST":
                controller.postUser(exchange);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }

    private void user(HttpExchange exchange, String id) throws SQLException
    {
        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                controller.getUser(exchange, id);
                break;
            case "PATCH":
                controller.patchUser(exchange, id);
                break;
            case "DELETE":
                controller.deleteUser(exchange, id);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }
}

