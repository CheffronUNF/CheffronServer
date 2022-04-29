package edu.unf.cheffron.server.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import edu.unf.cheffron.server.model.Pantry;
import edu.unf.cheffron.server.model.RecipeIngredient;
import edu.unf.cheffron.server.repository.PantryRepository;
import edu.unf.cheffron.server.util.AuthUtil;
import edu.unf.cheffron.server.util.HttpUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.AuthenticationException;

public class PantryController 
{
    private PantryRepository repository = PantryRepository.instance;
    
    public void getPantry(HttpExchange exchange) throws SQLException, AuthenticationException 
    {
        var userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null) 
        {
            throw new AuthenticationException("Must be logged in.");
        }

        Pantry pantry = repository.read(userId);
        String json = HttpUtil.toJson(pantry);
        HttpUtil.respond(exchange, 200, json);
    }

    public void updatePantry(HttpExchange exchange) throws SQLException, AuthenticationException 
    {
        var userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null) 
        {
            throw new AuthenticationException("Must be logged in.");
        }

        JsonObject body = HttpUtil.getJsonBody(exchange);

        JsonArray arr = body.getAsJsonArray("ingredients");
        List<RecipeIngredient> ingredients = new ArrayList<>();

        for (JsonElement element : arr) {
            var obj = element.getAsJsonObject();

            obj.addProperty("recipeId", "");
            obj.addProperty("ingredientId", "");

            ingredients.add(RecipeIngredient.fromJson(obj));
        }
        
        repository.update(userId, new Pantry(userId, ingredients));
        HttpUtil.respond(exchange, 200, repository.read(userId));
    }
}
