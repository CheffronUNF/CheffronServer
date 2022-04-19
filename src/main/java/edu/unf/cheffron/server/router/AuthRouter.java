package edu.unf.cheffron.server.router;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.controller.AuthController;
import edu.unf.cheffron.server.util.HttpUtil;

import java.io.IOException;

public class AuthRouter implements HttpHandler
{
    private AuthController controller = new AuthController();
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        switch (exchange.getRequestMethod())
        {
            case "GET":
                controller.login(exchange);
                break;
            case "PATCH":
                controller.updatePassword(exchange);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }
}