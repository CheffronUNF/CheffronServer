package edu.unf.cheffron.server.model;

import com.google.gson.JsonObject;

public class RecipeIngredient
{
    private final String id, name, quantity, unit;

    public RecipeIngredient(String id, String name, String quantity, String unit)
    {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getQuantity()
    {
        return quantity;
    }

    public String getUnit()
    {
        return unit;
    }

    public static RecipeIngredient fromJson(JsonObject json)
    {
        if (!json.has("id") || !json.has("name") || !json.has("quantity") || !json.has("unit"))
        {
            return null;
        }

        String id = json.get("id").getAsString();
        String name = json.get("name").getAsString();
        String quantity = json.get("quantity").getAsString();
        String unit = json.get("unit").getAsString();

        return new RecipeIngredient(id, name, quantity, unit);
    } 
}
