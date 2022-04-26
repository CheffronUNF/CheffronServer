package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.Ingredient;
import edu.unf.cheffron.server.model.Pantry;
import edu.unf.cheffron.server.model.RecipeIngredient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PantryRepository extends Repository<String, Pantry> 
{
    public static PantryRepository instance = new PantryRepository();

    private static final String createStatement = "INSERT INTO link_user_ingredient (userID, ingredientID, Amount, MeasurementType) VALUES (?, ?, ?, ?)";
    private static final String readStatement = "SELECT * FROM link_user_ingredient WHERE userID = ?";
    private static final String deleteStatement = "DELETE FROM link_user_ingredient WHERE userID = ?";

    @Override
    public Pantry create(Pantry item) throws SQLException 
    {
        for (RecipeIngredient recipeIngredient : item.ingredients()) 
        {
            Ingredient ingredient = IngredientRepository.instance.readByName(recipeIngredient.name());

            if (ingredient == null) 
            {
                ingredient = new Ingredient(UUID.randomUUID().toString(), recipeIngredient.name());
                IngredientRepository.instance.create(ingredient);
            }

            var stmt = createStatement(createStatement);

            stmt.setString(1, item.userId());
            stmt.setString(2, ingredient.ingredientId());
            stmt.setDouble(3, recipeIngredient.quantity());
            stmt.setString(4, recipeIngredient.unit());

            stmt.executeUpdate();
        }

        return item;
    }

    @Override
    protected String getReadAllStatement() 
    {
        return null;
    }

    @Override
    public List<Pantry> read() throws SQLException 
    {
        throw new UnsupportedOperationException("Cannot read all pantries!");
    }

    @Override
    public Pantry read(String id) throws SQLException 
    {
        var stmt = createStatement(readStatement);
        stmt.setString(1, id);

        var rs = stmt.executeQuery();
        return createFromRow(rs);
    }

    @Override
    public Pantry update(String id, Pantry item) throws SQLException 
    {
        delete(id);

        var updated = new Pantry(id, item.ingredients());

        return create(updated);
    }

    @Override
    public boolean delete(String id) throws SQLException 
    {
        var stmt = createStatement(deleteStatement);
        stmt.setString(1, id);
        return stmt.executeUpdate() > 0;
    }

    @Override
    public Pantry createFromRow(ResultSet rs) throws SQLException 
    {
        var lines = getResultSetSize(rs);

        List<RecipeIngredient> ingredients = new ArrayList<>(lines);

        String userId = null;

        while (rs.next()) 
        {
            userId = rs.getString("userID");
            String ingredientId = rs.getString("ingredientID");
            String recipeId = "";
            double quantity = rs.getDouble("Amount");
            String unit = rs.getString("MeasurementType");
            String name = IngredientRepository.instance.read(ingredientId).name();

            ingredients.add(new RecipeIngredient(ingredientId, recipeId, name, quantity, unit));
        }

        return new Pantry(userId, ingredients);
    }
}
