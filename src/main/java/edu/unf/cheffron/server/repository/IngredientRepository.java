package edu.unf.cheffron.server.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import edu.unf.cheffron.server.CheffronLogger;
import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.RecipeIngredient;

public class IngredientRepository implements Repository<String, RecipeIngredient>
{
    public static IngredientRepository instance;

    private final Connection Connection;

    private final PreparedStatement CreateStatement;
    private final PreparedStatement ReadStatement;
    private final PreparedStatement ReadAllStatement;
    private final PreparedStatement UpdateStatement;
    private final PreparedStatement DeleteStatement;

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
        
        CreateStatement = Connection.prepareStatement("");
        ReadStatement = Connection.prepareStatement("");
        ReadAllStatement = Connection.prepareStatement("");
        UpdateStatement = Connection.prepareStatement("");
        DeleteStatement = Connection.prepareStatement("");

        throw new UnsupportedOperationException();
    }

    @Override
    public RecipeIngredient create(RecipeIngredient item) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RecipeIngredient[] read() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RecipeIngredient read(String id) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RecipeIngredient update(String id, RecipeIngredient item) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean delete(String id) throws SQLException 
    {
        // TODO Auto-generated method stub
        return false;
    }
    
}
