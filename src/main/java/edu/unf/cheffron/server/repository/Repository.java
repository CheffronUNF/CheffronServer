package edu.unf.cheffron.server.repository;

import java.sql.SQLException;

public interface Repository<K, V> 
{
    public V create(V item) throws SQLException;

    public V[] read() throws SQLException;
    public V read(K id) throws SQLException;

    public V update(K id,V item) throws SQLException;

    public boolean delete(K id) throws SQLException;
}
