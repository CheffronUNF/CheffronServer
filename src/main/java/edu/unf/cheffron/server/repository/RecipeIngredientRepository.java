package edu.unf.cheffron.server.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.unf.cheffron.server.model.RecipeIngredient;

public class RecipeIngredientRepository extends Repository<String, RecipeIngredient>
{
    public static RecipeIngredientRepository instance;

    private final String createStatement = "INSERT INTO link_recipe_ingredient (ingredientID, recipeID, amount, measurementType) VALUES (?, ?, ?, ?)";
    private final String readStatement = "SELECT * FROM link_recipe_ingredient WHERE ingredientID = ?";
    private final String readAllStatement = "SELECT * FROM link_recipe_ingredient";
    private final String readByRecipeIdStatement = "SELECT ingredientID, recipeID, Amount, MeasurementType FROM display_recipeIngredients WHERE recipeID = ?";
    private final String updateStatement = "UPDATE link_recipe_ingredient SET ingredientID = ?, recipeID = ?, amount = ?, measurementType = ? WHERE ingredientID = ?";
    private final String deleteStatement = "DELETE FROM link_recipe_ingredient WHERE ingredientID = ?";
    
    static
    {
        instance = new RecipeIngredientRepository();
    }

    @Override
    public RecipeIngredient create(RecipeIngredient item) throws SQLException 
    {
        var stmt = createStatement(createStatement);

        stmt.setString(1, item.ingredientId());
        stmt.setString(2, item.recipeId());
        stmt.setDouble(3, item.quantity());
        stmt.setString(4, item.unit());

        stmt.executeUpdate();

        return item;
    }

    @Override
    String getReadAllStatement() 
    {
        return readAllStatement;
    }

    @Override
    public RecipeIngredient read(String id) throws SQLException 
    {
        var stmt = createStatement(readStatement);
        stmt.setString(1, id);

        var rs = stmt.executeQuery();
        return rs.next() ? createFromRow(rs) : null;
    }

    public List<RecipeIngredient> readByRecipeId(String id) throws SQLException
    {
        var stmt = createStatement(readByRecipeIdStatement);

        stmt.setString(1, id);

        var rs = stmt.executeQuery();
        var size = getResultSetSize(rs);

        var res = new ArrayList<RecipeIngredient>(size);
        while (rs.next())
        {
            res.add(createFromRow(rs));
        }

        return res;
    }

    @Override
    public RecipeIngredient update(String id, RecipeIngredient item) throws SQLException 
    {
        var stmt = createStatement(updateStatement);

        stmt.setString(1, item.ingredientId());
        stmt.setString(2, item.recipeId());
        stmt.setDouble(3, item.quantity());
        stmt.setString(4, item.unit());

        return new RecipeIngredient(id, item.recipeId(), item.name(), item.quantity(), item.unit());
    }

    @Override
    public boolean delete(String id) throws SQLException 
    {
        var stmt = createStatement(deleteStatement);

        stmt.setString(1, id);

        return stmt.executeUpdate() > 0;
    }

    @Override
    RecipeIngredient createFromRow(ResultSet rs) throws SQLException 
    {
        String ingredientId = rs.getString(1);
        String recipeId = rs.getString(2);
        Double quantity = rs.getDouble(3);
        String unit = rs.getString(4);
        String name = IngredientRepository.instance.read(ingredientId).name();

        return new RecipeIngredient(ingredientId, recipeId, name, quantity, unit);
    }
    
}
