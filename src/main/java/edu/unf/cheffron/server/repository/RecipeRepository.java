package edu.unf.cheffron.server.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.Recipe;
import edu.unf.cheffron.server.model.RecipeIngredient;
import edu.unf.cheffron.server.util.CheffronLogger;

public class RecipeRepository extends Repository<String, Recipe>
{
    public static RecipeRepository instance;

    private final Connection connection;

    private final String createStatement = "INSERT INTO recipe (recipeId, userId, directions, recipeName, servingSize, glutenFree, spicy, isPrivate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String readStatement = "SELECT * FROM recipe WHERE recipeId = ?";
    private final String readAllStatement = "SELECT * FROM recipe";
    private final String updateStatement = "UDATE recipe SET directions = ?, recipeName = ?, servingSize = ?, glutenFree = ?, spicy = ?, isPrivate = ? WHERE recipeId = ?";
    private final String deleteStatement = "DELETE FROM recipe WHERE recipeId = ?";

    static
    {
        try
        {
            instance = new RecipeRepository();
        }
        catch (SQLException ex)
        {
            CheffronLogger.log(Level.SEVERE, "Could not initialize Recipe Repository!", ex);
            System.exit(1);
        }
    }

    private RecipeRepository() throws SQLException
    {
        connection = MySQLDatabase.connect();
    }

    @Override
    public Recipe create(Recipe item) throws SQLException 
    {
        var stmt = connection.prepareStatement(createStatement);

        StringBuilder directions = new StringBuilder();
        for (String direction : item.directions()) 
        {
            directions.append(direction);
        }
        
        stmt.setString(1, item.recipeId());
        stmt.setString(2, item.userId());
        stmt.setString(3, directions.toString());
        stmt.setString(4, item.recipeName());
        stmt.setInt(5, item.servings());
        stmt.setBoolean(6, item.glutenFree());
        stmt.setBoolean(7, item.spicy());
        stmt.setBoolean(8, item.isPrivate());

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
        var stmt = connection.prepareStatement(readStatement);

        stmt.setString(1, id);

        var rs = stmt.executeQuery();
        if (!rs.next())
        {
            return null;
        }
        
        return createFromRow(rs);
    }

    @Override
    public Recipe update(String id, Recipe item) throws SQLException 
    {
        var stmt = connection.prepareStatement(updateStatement);
        StringBuilder directions = new StringBuilder();

        for (String direction : item.directions()) 
        {
            directions.append(direction);
        }
        
        stmt.setString(1, directions.toString());
        stmt.setString(2, item.recipeName());
        stmt.setInt(3, item.servings());
        stmt.setBoolean(4, item.glutenFree());
        stmt.setBoolean(5, item.spicy());
        stmt.setBoolean(6, item.isPrivate());

        stmt.executeUpdate();

        return new Recipe(id, item.userId(), item.recipeName(), item.directions(), item.ingredients(), item.servings(), item.glutenFree(), item.spicy(), item.isPrivate());
    }

    @Override
    public boolean delete(String id) throws SQLException 
    {
        var stmt = connection.prepareStatement(deleteStatement);

        stmt.setString(1, id);

        return stmt.executeUpdate() > 0;
    }

    @Override
    Recipe createFromRow(ResultSet rs) throws SQLException 
    {
        String recipeId = rs.getString("recipeId");
        String userId = rs.getString("userId");
        String recipeName = rs.getString("recipeName");
        int servings = rs.getInt("servingSize");
        Boolean glutenFree = rs.getBoolean("glutenFree");
        Boolean spicy = rs.getBoolean("spicy");
        Boolean isPrivate = rs.getBoolean("isPrivate");
        
        List<String> directions = rs.getString("directions").lines().toList();
        List<RecipeIngredient> ingredients = IngredientRepository.instance.readByRecipeId(recipeId);

        return new Recipe(recipeId, userId, recipeName, directions, ingredients, servings, glutenFree, spicy, isPrivate);
    }
}
