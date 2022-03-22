package edu.unf.cheffron.server.repository;

import edu.unf.cheffron.server.database.MySQLDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Repository<K, V>
{
    abstract V create(V item) throws SQLException;

    abstract String getReadAllStatement();

    public List<V> read() throws SQLException {
        Connection connection = MySQLDatabase.connect();
        var stmt = connection.prepareStatement(getReadAllStatement());

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

    protected int getResultSetSize(ResultSet rs)
    {
        int size = 0;

        try
        {
            int index = rs.getRow();

            rs.last();
            rs.getRow();

            rs.absolute(index);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return size;
    }
}
