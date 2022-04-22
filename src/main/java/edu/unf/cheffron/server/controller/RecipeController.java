package edu.unf.cheffron.server.controller;

import java.sql.SQLException;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import edu.unf.cheffron.server.model.Ingredient;
import edu.unf.cheffron.server.model.Recipe;
import edu.unf.cheffron.server.model.RecipeIngredient;
import edu.unf.cheffron.server.repository.IngredientRepository;
import edu.unf.cheffron.server.repository.RecipeIngredientRepository;
import edu.unf.cheffron.server.repository.RecipeRepository;
import edu.unf.cheffron.server.util.AuthUtil;
import edu.unf.cheffron.server.util.HttpUtil;

public class RecipeController 
{
    public void getRecipes(HttpExchange exchange) throws SQLException
    {
        var recipes = RecipeRepository.instance.read();

        for (Recipe recipe : recipes)
        {
            var ingredients = RecipeIngredientRepository.instance.readByRecipeId(recipe.recipeId());

            recipe = new Recipe(recipe.recipeId(), recipe.userId(), recipe.recipeName(), recipe.directions(), ingredients, recipe.servings(), recipe.glutenFree(), recipe.spicy(), recipe.isPrivate());
        }

        HttpUtil.respond(exchange, 200, recipes);
    }

    public void postRecipes(HttpExchange exchange) throws SQLException
    {
        String userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null)
        {
            HttpUtil.respond(exchange, 401, "Must be logged in.");
            return;
        }

        JsonObject json = HttpUtil.getJsonBody(exchange);
        json.addProperty("recipeId", UUID.randomUUID().toString());
        json.addProperty("userId", userId);

        Recipe recipe = Recipe.fromJson(json);

        if (recipe == null)
        {
            HttpUtil.respondError(exchange, 406, "Invalid data.");
            return;
        }

        RecipeRepository.instance.create(recipe);

        for (RecipeIngredient recipeIngredient : recipe.ingredients())
        {
            Ingredient ingredient = IngredientRepository.instance.readByName(recipeIngredient.name());

            if (ingredient == null)
            {
                ingredient = Ingredient.fromRecipeIngredient(recipeIngredient);
                IngredientRepository.instance.create(ingredient);
            }
            else
            {
                recipeIngredient = new RecipeIngredient(ingredient.ingredientId(), recipeIngredient.recipeId(), ingredient.name(), recipeIngredient.quantity(), recipeIngredient.unit());
            }

            RecipeIngredientRepository.instance.create(recipeIngredient);
        }
        
        HttpUtil.respond(exchange, 201);
    }

    public void getRecipe(HttpExchange exchange, String id) throws SQLException
    {
        var recipe = RecipeRepository.instance.read(id);

        if (recipe == null)
        {
            HttpUtil.respondError(exchange, 404, "Recipe Not Found.");
            return;
        }

        var ingredients = RecipeIngredientRepository.instance.readByRecipeId(recipe.recipeId());
        recipe = new Recipe(recipe.recipeId(), recipe.userId(), recipe.recipeName(), recipe.directions(), ingredients, recipe.servings(), recipe.glutenFree(), recipe.spicy(), recipe.isPrivate());

        HttpUtil.respond(exchange, 200, recipe);
    }

    public void patchRecipe(HttpExchange exchange, String id) throws SQLException
    {
        String userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null)
        {
            HttpUtil.respond(exchange, 401, "Must be logged in.");
            return;
        }

        var recipe = RecipeRepository.instance.read(id);

        if (recipe == null)
        {
            HttpUtil.respondError(exchange, 404, "Recipe Not Found.");
            return;
        }

        if (recipe.userId() != userId)
        {
            HttpUtil.respond(exchange, 401, "Can only update owned recipes.");
            return;
        }

        RecipeRepository.instance.update(id, recipe);
        HttpUtil.respond(exchange, 200, recipe);
    }

    public void deleteRecipe(HttpExchange exchange, String id) throws SQLException
    {
        String userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null)
        {
            HttpUtil.respond(exchange, 401, "Must be logged in.");
            return;
        }

        var recipe = RecipeRepository.instance.read(id);

        if (recipe == null)
        {
            HttpUtil.respondError(exchange, 404, "Recipe Not Found.");
            return;
        }

        if (recipe.userId() != userId)
        {
            HttpUtil.respond(exchange, 401, "Can only delete owned recipes.");
            return;
        }

        RecipeRepository.instance.delete(id);
        HttpUtil.respond(exchange, 200, recipe);
    }
}
