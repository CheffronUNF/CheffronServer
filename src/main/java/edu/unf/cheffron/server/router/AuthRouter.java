package edu.unf.cheffron.server.router;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.unf.cheffron.server.controller.AuthController;
import edu.unf.cheffron.server.util.CheffronLogger;
import edu.unf.cheffron.server.util.HttpUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.naming.AuthenticationException;

public class AuthRouter implements HttpHandler
{
    private AuthController controller = new AuthController();

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
        catch (Exception ex)
        {
            CheffronLogger.log(Level.SEVERE, ex.getMessage());
            HttpUtil.respondError(exchange, 500, "Internal Server Error.");
        }
    }

    private void routeRequest(HttpExchange exchange) throws AuthenticationException, SQLException
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