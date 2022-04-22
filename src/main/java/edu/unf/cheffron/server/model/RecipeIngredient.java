package edu.unf.cheffron.server.model;

import com.google.gson.JsonObject;

public record RecipeIngredient(String ingredientId, String recipeId, String name, double quantity, String unit) 
{
    public static RecipeIngredient fromJson(JsonObject json) 
    {
        if (!json.has("ingredientId") || !json.has("recipeId") || !json.has("name") || !json.has("quantity") || !json.has("unit")) 
        {
            return null;
        }

        String ingredientId = json.get("ingredientId").getAsString();
        String recipeId = json.get("recipeId").getAsString();
        String name = json.get("name").getAsString();
        double quantity = json.get("quantity").getAsDouble();
        String unit = json.get("unit").getAsString();

        return new RecipeIngredient(ingredientId, recipeId, name, quantity, unit);
    }
}
