package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.CheffronLogger;
import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.Ingredient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class IngredientRepository extends Repository<String, Ingredient>
{
    public static IngredientRepository instance;

    private final Connection Connection;

    private final String CreateStatement = "INSERT INTO ingredient(ingredientID, ingredientName) VALUES (?, ?)";
    private final String ReadStatement = "SELECT ingredientID, ingredientName FROM ingredient WHERE ingredientID = ?";
    private final String ReadByNameStatement = "SELECT ingredientID, ingredientName FROM ingredient " +
            "WHERE ingredientName = ?";
    private final String ReadAllStatement = "SELECT ingredientID, ingredientName FROM ingredient";

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
        Connection = MySQLDatabase.connect();
    }

    @Override
    public Ingredient create(Ingredient item) throws SQLException
    {
        var stmt = Connection.prepareStatement(CreateStatement);

        stmt.setString(1, item.id());
        stmt.setString(2, item.name());

        stmt.executeUpdate();

        return item;
    }

    @Override
    public String getReadAllStatement() {
        return ReadAllStatement;
    }

    @Override
    public Ingredient read(String id) throws SQLException
    {
        var stmt = Connection.prepareStatement(ReadStatement);
        stmt.setString(1, id);

        var rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        return createFromRow(rs);
    }

    public Ingredient readByName(String name) throws SQLException {
        var stmt = Connection.prepareStatement(ReadByNameStatement);
        stmt.setString(1, name);

        var rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        return createFromRow(rs);
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
    protected Ingredient createFromRow(ResultSet rs) throws SQLException {
        String ingredientId = rs.getString(1);
        String ingredientName = rs.getString(2);

        return new Ingredient(ingredientId, ingredientName);
    }
}
