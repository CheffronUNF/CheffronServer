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
        switch (exchange.getRequestMethod()) 
        {
            case "GET":
                controller.getUser(exchange);
                break;
            case "POST":
                controller.createUser(exchange);
                break;
            case "PATCH":
                controller.updateUser(exchange);
                break;
            case "DELETE":
                controller.deleteUser(exchange);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }
}

