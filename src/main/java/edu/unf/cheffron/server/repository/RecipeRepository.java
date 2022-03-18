package edu.unf.cheffron.server.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import edu.unf.cheffron.server.CheffronLogger;
import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.model.Recipe;

public class RecipeRepository implements Repository<String, Recipe>
{
    public static RecipeRepository instance;

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
        Connection = MySQLDatabase.connect();
        
        CreateStatement = Connection.prepareStatement("");
        ReadStatement = Connection.prepareStatement("");
        ReadAllStatement = Connection.prepareStatement("");
        UpdateStatement = Connection.prepareStatement("");
        DeleteStatement = Connection.prepareStatement("");

        throw new UnsupportedOperationException();
    }

    @Override
    public Recipe create(Recipe item) throws SQLException 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Recipe[] read() throws SQLException 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Recipe read(String id) throws SQLException 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Recipe update(String id, Recipe item) throws SQLException 
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
