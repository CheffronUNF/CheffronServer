package edu.unf.cheffron.server.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Recipe 
{
    public final String id, name;
    public final Iterable<String> directions;
    public final Iterable<RecipeIngredient> ingredients;

    public Recipe(String id, String name, Iterable<String> directions, Iterable<RecipeIngredient> ingredients)
    {
        this.id = id;
        this.name = name;
        this.directions = directions;
        this.ingredients = ingredients;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Iterable<String> getDirections()
    {
        return directions;
    }

    public Iterable<RecipeIngredient> getIngredients()
    {
        return ingredients;
    }

    public static Recipe fromJson(JsonObject json)
    {
        if (!json.has("id") || !json.has("name") || !json.has("directions") || !json.has("ingredients"))
        {
            return null;
        }

        String id = json.get("id").getAsString();
        String name = json.get("name").getAsString();

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

        return new Recipe(id, name, directions, ingredients);
    }
}
