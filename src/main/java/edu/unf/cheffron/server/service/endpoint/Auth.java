package edu.unf.cheffron.server.service.endpoint;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import edu.unf.cheffron.server.database.MySQLDatabase;

public class Auth extends Endpoint {
    @Override
    public void registerContexts(HttpServer server, MySQLDatabase database) {
        createContext(server, "/auth/create", "POST", httpExchange -> {
            JsonObject json = getJsonBody(httpExchange);
            if (json == null)
                return;
            // TODO : create account
        });
        createContext(server, "/auth/login", "POST", httpExchange -> {
            JsonObject json = getJsonBody(httpExchange);
            if (json == null)
                return;
            // TODO: handle login request
        });

    }
}
