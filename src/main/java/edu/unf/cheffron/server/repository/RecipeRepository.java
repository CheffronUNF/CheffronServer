package edu.unf.cheffron.server.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import edu.unf.cheffron.server.model.Recipe;
import edu.unf.cheffron.server.model.RecipeIngredient;

public class RecipeRepository extends Repository<String, Recipe>
{
    public static RecipeRepository instance;

    private final String createStatement = "INSERT INTO recipe (recipeId, userId, directions, recipeName, servingSize, time, glutenFree, spicy, isPrivate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String readStatement = "SELECT * FROM recipe WHERE recipeId = ?";
    private final String readAllStatement = "SELECT * FROM recipe";
    private final String updateStatement = "UPDATE recipe SET directions = ?, recipeName = ?, servingSize = ?, time = ?, glutenFree = ?, spicy = ?, isPrivate = ? WHERE recipeId = ?";
    private final String deleteStatement = "DELETE FROM recipe WHERE recipeId = ?";
    
    static
    {
        instance = new RecipeRepository();
    }

    @Override
    public Recipe create(Recipe item) throws SQLException 
    {
        var stmt = createStatement(createStatement);
        
        stmt.setString(1, item.recipeId());
        stmt.setString(2, item.userId());
        stmt.setString(3, item.directions());
        stmt.setString(4, item.recipeName());
        stmt.setInt(5, item.servings());
        stmt.setString(6, item.time());
        stmt.setBoolean(7, item.glutenFree());
        stmt.setBoolean(8, item.spicy());
        stmt.setBoolean(9, item.isPrivate());

        stmt.executeUpdate();

        return item;
    }

    @Override
    public String getReadAllStatement() 
    {
        return readAllStatement;
    }

    @Override
    public Recipe read(String id) throws SQLException 
    {
        var stmt = createStatement(readStatement);

        stmt.setString(1, id);

        var rs = stmt.executeQuery();
        return rs.next() ? createFromRow(rs) : null;
    }

    @Override
    public Recipe update(String id, Recipe item) throws SQLException 
    {
        var stmt = createStatement(updateStatement);
        
        stmt.setString(1, item.directions());
        stmt.setString(2, item.recipeName());
        stmt.setInt(3, item.servings());
        stmt.setString(4, item.time());
        stmt.setBoolean(5, item.glutenFree());
        stmt.setBoolean(6, item.spicy());
        stmt.setBoolean(7, item.isPrivate());

        stmt.executeUpdate();

        return new Recipe(id, item.userId(), item.recipeName(), item.directions(), item.ingredients(), item.servings(), item.time(), item.glutenFree(), item.spicy(), item.isPrivate());
    }

    @Override
    public boolean delete(String id) throws SQLException 
    {
        var stmt = createStatement(deleteStatement);

        stmt.setString(1, id);

        return stmt.executeUpdate() > 0;
    }

    @Override
    Recipe createFromRow(ResultSet rs) throws SQLException 
    {
        String recipeId = rs.getString("recipeId");
        String userId = rs.getString("userId");
        String recipeName = rs.getString("recipeName");
        String directions = rs.getString("directions");
        int servings = rs.getInt("servingSize");
        String time = rs.getString("time");
        Boolean glutenFree = rs.getBoolean("glutenFree");
        Boolean spicy = rs.getBoolean("spicy");
        Boolean isPrivate = rs.getBoolean("isPrivate");
        
        List<RecipeIngredient> ingredients = RecipeIngredientRepository.instance.readByRecipeId(recipeId);

        return new Recipe(recipeId, userId, recipeName, directions, ingredients, servings, time, glutenFree, spicy, isPrivate);
    }
}
