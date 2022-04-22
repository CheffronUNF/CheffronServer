package edu.unf.cheffron.server.model;

public record Ingredient(String ingredientId, String name) 
{
    public static Ingredient fromRecipeIngredient(RecipeIngredient ingredient)
    {
        return new Ingredient(ingredient.ingredientId(), ingredient.name());
    }
}
