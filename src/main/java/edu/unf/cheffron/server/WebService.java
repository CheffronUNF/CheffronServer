package edu.unf.cheffron.server;

import com.sun.net.httpserver.HttpServer;
import edu.unf.cheffron.server.router.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebService 
{
    private HttpServer server;

    public WebService(int port) throws IOException 
    {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        registerContexts();
    }

    public void listen() 
    {
        server.start();
    }

    private void registerContexts() 
    {
        server.createContext("/auth", new AuthRouter());
        server.createContext("/ingredient", new IngredientRouter());
        server.createContext("/recipe", new RecipeRouter());
        server.createContext("/user", new UserRouter());
        server.createContext("/pantry", new PantryRouter());
    }
}
