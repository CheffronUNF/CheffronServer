package edu.unf.cheffron.server.util;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public abstract class HttpUtil
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Object obj)
    {
        return gson.toJson(obj);
    }

    public static JsonObject toJsonObject(Object obj)
    {
        return gson.toJsonTree(obj).getAsJsonObject();
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

    public static void respond(HttpExchange exchange, int statusCode)
    {
        try
        {
            exchange.sendResponseHeaders(statusCode, 0);
            exchange.close();
        }
        catch (IOException ex)
        {
            CheffronLogger.log(Level.WARNING, "Could not respond to client!", ex);
        }
    }

    public static void respond(HttpExchange exchange, int statusCode, Object obj)
    {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        respond(exchange, statusCode, toJson(obj));
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

         exchange.getResponseHeaders().add("Content-Type", "application/json");
         respond(exchange, statusCode, json);
    }

    public static void respondError(HttpExchange exchange, int statusCode, String error)
    {
        JsonObject object = new JsonObject();
        object.addProperty("error", error);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        respond(exchange, statusCode, object);
    }
}
