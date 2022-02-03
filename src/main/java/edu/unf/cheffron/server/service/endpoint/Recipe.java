package edu.unf.cheffron.server.service.endpoint;

import com.sun.net.httpserver.HttpServer;
import edu.unf.cheffron.server.database.MySQLDatabase;

public class Recipe extends Endpoint {
    @Override
    public void registerContexts(HttpServer server, MySQLDatabase database) {

    }
}
