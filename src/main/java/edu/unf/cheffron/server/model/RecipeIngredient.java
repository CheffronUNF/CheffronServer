package edu.unf.cheffron.server.model;

import com.google.gson.JsonObject;

public record RecipeIngredient(String id, String name, double quantity, String unit) 
{
    public static RecipeIngredient fromJson(JsonObject json) 
    {
        if (!json.has("id") || !json.has("name") || !json.has("quantity") || !json.has("unit")) 
        {
            return null;
        }

        String id = json.get("id").getAsString();
        String name = json.get("name").getAsString();
        double quantity = json.get("quantity").getAsDouble();
        String unit = json.get("unit").getAsString();

        return new RecipeIngredient(id, name, quantity, unit);
    }
}
