package edu.unf.cheffron.server.controller;

import java.sql.SQLException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import edu.unf.cheffron.server.controller.IngredientController;
import edu.unf.cheffron.server.model.Ingredient;
import edu.unf.cheffron.server.repository.IngredientRepository;
import edu.unf.cheffron.server.util.HttpUtil;

public class IngredientController 
{
    public void getAllIngredients(HttpExchange exchange) 
    {
        try 
        {
            List<Ingredient> ingredients = IngredientRepository.instance.read();
            HttpUtil.respond(exchange, 200, HttpUtil.toJson(ingredients));
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            HttpUtil.respondError(exchange, 500, "Error when getting ingredients");
        }
    }
}
