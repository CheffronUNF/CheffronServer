package edu.unf.cheffron.server.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

public abstract class HttpUtil 
{
    private static final Gson gson = new Gson();

    public static String toJson(Object obj)
    {
        return gson.toJson(obj);
    }

    public static JsonObject getJsonBody(HttpExchange exchange) 
    {
        try 
        {
            String line = new String(exchange.getRequestBody().readAllBytes());
            JsonElement element = JsonParser.parseString(line);

            if (element == null || element.isJsonNull() || !element.isJsonObject()) 
            {
                return null;
            } 
            else 
            {
                return element.getAsJsonObject(); // return appropriate json object
            }

        } 
        catch (IOException ex) 
        {
            CheffronLogger.log(Level.WARNING, "Could not read line from request body!", ex);
        } 
        catch (JsonSyntaxException ex) 
        {
            CheffronLogger.log(Level.WARNING, "Received invalid JSON from client!", ex);
        }

        return null; // errored out
    }
    
    public static void respond(HttpExchange exchange, int statusCode, String message) 
    {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

        try 
        {
            exchange.sendResponseHeaders(statusCode, bytes.length);
            OutputStream body = exchange.getResponseBody();

            body.write(bytes);
            body.flush();
            exchange.close();
        } 
        catch (IOException ex) 
        {
            CheffronLogger.log(Level.WARNING, "Could not respond to client!", ex);
        }
    }

    public static void respond(HttpExchange exchange, int statusCode, JsonObject body)
     {
        String json = gson.toJson(body);

        respond(exchange, statusCode, json);
    }

    public static void respondError(HttpExchange exchange, int statusCode, String error) 
    {
        JsonObject object = new JsonObject();
        object.addProperty("error", error);

        respond(exchange, statusCode, object);
    }
}
