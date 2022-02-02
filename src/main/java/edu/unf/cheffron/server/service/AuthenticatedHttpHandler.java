package edu.unf.cheffron.server.service;

import com.sun.net.httpserver.HttpExchange;
import edu.unf.cheffron.server.database.model.User;

public interface AuthenticatedHttpHandler {

    void handle(HttpExchange exchange, User user);
}
