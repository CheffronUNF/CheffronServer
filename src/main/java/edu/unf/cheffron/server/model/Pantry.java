package edu.unf.cheffron.server.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public record Pantry(String userId, List<RecipeIngredient> ingredients) {
    public static Pantry fromJson(JsonObject json) {
        String userId = json.get("userId").getAsString();

        JsonArray arr = json.getAsJsonArray("ingredients");

        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (JsonElement element : arr) {
            var obj = element.getAsJsonObject();

            obj.addProperty("recipeId", "");
            obj.addProperty("ingredientId", "");

            ingredients.add(RecipeIngredient.fromJson(obj));
        }

        return new Pantry(userId, ingredients);
    }
}
