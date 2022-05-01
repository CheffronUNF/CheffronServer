package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.util.CheffronLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class Repository<K, V>
{
    private static Connection connection;

    static
    {
        try 
        {
            connection = MySQLDatabase.connect();
        } 
        catch (SQLException e) 
        {
            CheffronLogger.log(Level.SEVERE, "Could not connect to database!", e);
            System.exit(1);
        }
    }

    abstract V create(V item) throws SQLException;

    abstract String getReadAllStatement();

    public List<V> read() throws SQLException 
    {
        var stmt = createStatement(getReadAllStatement());

        var rs = stmt.executeQuery();
        var size = getResultSetSize(rs);

        var res = new ArrayList<V>(size);
        while (rs.next())
        {
            res.add(createFromRow(rs));
        }

        return res;
    }

    abstract V read(K id) throws SQLException;

    abstract V update(K id,V item) throws SQLException;

    abstract boolean delete(K id) throws SQLException;

    abstract V createFromRow(ResultSet rs) throws SQLException;

    protected static PreparedStatement createStatement(String statement)
    {
        try
        {
            connection = MySQLDatabase.connect();

            return connection.prepareStatement(statement, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        }
        catch (Exception e)
        {
            CheffronLogger.log(Level.SEVERE, "Server connection lost!", e);
            return null;
        }
    }

    protected static int getResultSetSize(ResultSet rs)
    {
        int size = 0;

        try
        {
            int index = rs.getRow();

            rs.last();
            size = rs.getRow();

            rs.absolute(index);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return size;
    }
}
