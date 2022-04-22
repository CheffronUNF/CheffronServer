package edu.unf.cheffron.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public record Recipe(String recipeId, 
                        String userId,
                        String recipeName, 
                        String directions, 
                        List<RecipeIngredient> ingredients, 
                        int servings, 
                        Boolean glutenFree, 
                        Boolean spicy, 
                        Boolean isPrivate) 
{

    public static Recipe fromJson(JsonObject json)
    {
        if (!json.has("recipeId") || !json.has("userId") || !json.has("recipeName") || !json.has("directions") || !json.has("ingredients") || !json.has("userId") )
        {
            return null;
        }

        String recipeId = json.get("recipeId").getAsString();
        String userId = json.get("userId").getAsString();
        String recipeName = json.get("recipeName").getAsString();
        String directions = json.get("directions").getAsString();
        int servings = json.get("servings").getAsInt();
        Boolean glutenFree = json.has("glutenFree") ? json.get("glutenFree").getAsBoolean() : false;
        Boolean spicy = json.has("spicy") ? json.get("spicy").getAsBoolean() : false;
        Boolean isPrivate = json.has("isPrivate") ? json.get("isPrivate").getAsBoolean() : false;

        JsonArray arr = json.getAsJsonArray("ingredients");

        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (JsonElement element : arr) 
        {
            var obj = element.getAsJsonObject();

            obj.addProperty("recipeId", recipeId);
            obj.addProperty("ingredientId", UUID.randomUUID().toString());

            ingredients.add(RecipeIngredient.fromJson(obj));
        }

        return new Recipe(recipeId, userId, recipeName, directions, ingredients, servings, glutenFree, spicy, isPrivate);
    }
}
