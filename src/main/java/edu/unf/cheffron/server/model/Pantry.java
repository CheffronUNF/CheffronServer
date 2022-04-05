package edu.unf.cheffron.server.model;

import java.util.List;

public record Pantry(String userId, List<RecipeIngredient> ingredients) 
{
    
}
