package edu.unf.cheffron.server.repository;

import java.sql.SQLException;

public interface Repository<K, V> 
{
    public V Create(V item) throws SQLException;

    public V[] Read() throws SQLException;
    public V Read(K id) throws SQLException;

    public V Update(K id,V item) throws SQLException;

    public boolean Delete(K id) throws SQLException;
}
