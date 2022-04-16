package edu.unf.cheffron.server.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public record Recipe(String recipeId, 
                        String userId,
                        String recipeName, 
                        List<String> directions, 
                        List<RecipeIngredient> ingredients, 
                        int servings, 
                        Boolean glutenFree, 
                        Boolean spicy, 
                        Boolean isPrivate) 
{
    public Recipe
    {

    }

    public static Recipe fromJson(JsonObject json)
    {
        if (!json.has("recipeId") || !json.has("userId") || !json.has("recipeName") || !json.has("directions") || !json.has("ingredients") || !json.has("userId") )
        {
            return null;
        }

        String recipeId = json.get("recipeId").getAsString();
        String userId = json.get("userId").getAsString();
        String recipeName = json.get("recipeName").getAsString();
        int servings = json.get("servings").getAsInt();
        Boolean glutenFree = json.has("glutenFree") ? json.get("glutenFree").getAsBoolean() : false;
        Boolean spicy = json.has("spicy") ? json.get("spicy").getAsBoolean() : false;
        Boolean isPrivate = json.has("isPrivate") ? json.get("isPrivate").getAsBoolean() : false;

        JsonArray arr = json.getAsJsonArray("directions");
        
        List<String> directions = new ArrayList<>();
        for (JsonElement jsonElement : arr) 
        {
            directions.add(jsonElement.getAsString());
        }

        arr = json.getAsJsonArray("ingredients");

        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (JsonElement element : arr) 
        {
            ingredients.add(RecipeIngredient.fromJson(element.getAsJsonObject()));
        }

        return new Recipe(recipeId, userId, recipeName, directions, ingredients, servings, glutenFree, spicy, isPrivate);
    }
}
