package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.model.Ingredient;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IngredientRepository extends Repository<String, Ingredient>
{
    public static IngredientRepository instance;

    private final String createStatement = "INSERT INTO ingredient(ingredientID, ingredientName) VALUES (?, ?)";
    private final String readStatement = "SELECT ingredientID, ingredientName FROM ingredient WHERE ingredientID = ?";
    private final String readByNameStatement = "SELECT ingredientID, ingredientName FROM ingredient WHERE ingredientName = ?";
    private final String readAllStatement = "SELECT ingredientID, ingredientName FROM ingredient";

    static
    {
        instance = new IngredientRepository();
    }

    @Override
    public Ingredient create(Ingredient item) throws SQLException
    {
        var stmt = createStatement(createStatement);

        stmt.setString(1, item.ingredientId());
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
        var stmt = createStatement(readStatement);
        stmt.setString(1, id);

        var rs = stmt.executeQuery();
        return rs.next() ? createFromRow(rs) : null;
    }

    public Ingredient readByName(String name) throws SQLException 
    {
        var stmt = createStatement(readByNameStatement);
        stmt.setString(1, name);

        var rs = stmt.executeQuery();
        return rs.next() ? createFromRow(rs) : null;
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
