package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.CheffronLogger;
import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.Ingredient;
import edu.unf.cheffron.server.model.RecipeIngredient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class IngredientRepository extends Repository<String, Ingredient>
{
    public static IngredientRepository instance;

    private final Connection connection;

    private final String createStatement = "INSERT INTO ingredient(ingredientID, ingredientName) VALUES (?, ?)";
    private final String readStatement = "SELECT ingredientID, ingredientName FROM ingredient WHERE ingredientID = ?";
    private final String readByNameStatement = "SELECT ingredientID, ingredientName FROM ingredient WHERE ingredientName = ?";
    private final String readByRecipeIdStatement = "SELECT ingredientID, ingredientName, amount, measurementType FROM display_recipeIngredients WHERE recipeID = ?";
    private final String readAllStatement = "SELECT ingredientID, ingredientName FROM ingredient";

    static
    {
        try
        {
            instance = new IngredientRepository();
        }
        catch (SQLException ex)
        {
            CheffronLogger.log(Level.SEVERE, "Could not initialize Ingredient Repository!", ex);
            System.exit(1);
        }
    }

    private IngredientRepository() throws SQLException
    {
        connection = MySQLDatabase.connect();
    }

    @Override
    public Ingredient create(Ingredient item) throws SQLException
    {
        var stmt = connection.prepareStatement(createStatement);

        stmt.setString(1, item.id());
        stmt.setString(2, item.name());

        stmt.executeUpdate();

        return item;
    }

    @Override
    public String getReadAllStatement() 
    {
        return readAllStatement;
    }

    @Override
    public Ingredient read(String id) throws SQLException
    {
        var stmt = connection.prepareStatement(readStatement);
        stmt.setString(1, id);

        var rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        return createFromRow(rs);
    }

    public Ingredient readByName(String name) throws SQLException 
    {
        var stmt = connection.prepareStatement(readByNameStatement);
        stmt.setString(1, name);

        var rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        return createFromRow(rs);
    }

    public List<RecipeIngredient> readByRecipeId(String id) throws SQLException
    {
        var stmt = connection.prepareStatement(readByRecipeIdStatement);

        var rs = stmt.executeQuery();
        var size = getResultSetSize(rs);

        var res = new ArrayList<RecipeIngredient>(size);
        while (rs.next())
        {
            String ingredientId = rs.getString(1);
            String name = rs.getString(2);
            Double quantity = rs.getDouble(3);
            String unit = rs.getString(4);

            res.add(new RecipeIngredient(ingredientId, name, quantity, unit));
        }

        return res;
    }

    @Override
    public Ingredient update(String id, Ingredient item)
    {
        throw new UnsupportedOperationException("Ingredients cannot be modified!");
    }

    @Override
    public boolean delete(String id)
    {
        throw new UnsupportedOperationException("Ingredients cannot be deleted!");
    }

    @Override
    protected Ingredient createFromRow(ResultSet rs) throws SQLException 
    {
        String ingredientId = rs.getString(1);
        String ingredientName = rs.getString(2);

        return new Ingredient(ingredientId, ingredientName);
    }
}
