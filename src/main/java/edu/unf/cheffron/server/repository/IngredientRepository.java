package edu.unf.cheffron.server.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import edu.unf.cheffron.server.CheffronLogger;
import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.Ingredient;

public class IngredientRepository implements Repository<String, Ingredient>
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
    public Ingredient create(Ingredient item) throws SQLException 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Ingredient[] read() throws SQLException 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Ingredient read(String id) throws SQLException 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Ingredient update(String id, Ingredient item) throws SQLException 
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
