package edu.unf.cheffron.server.router;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.controller.UserController;
import edu.unf.cheffron.server.util.HttpUtil;

import java.io.IOException;

public class UserRouter implements HttpHandler 
{
    private UserController controller = new UserController();

    @Override
    public void handle(HttpExchange exchange) throws IOException 
    {
        var uri = exchange.getRequestURI().getRawPath();
        var split = uri.split("/");

        switch (split.length)
        {
            case 2:
                users(exchange);
                break;
            case 3:
                user(exchange, split[2]);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
                break;
        }
    }

    private void user(HttpExchange exchange, String id)
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

    private void users(HttpExchange exchange)
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
}

