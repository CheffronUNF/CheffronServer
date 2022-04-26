package edu.unf.cheffron.server.router;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.unf.cheffron.server.model.Pantry;
import edu.unf.cheffron.server.model.RecipeIngredient;
import edu.unf.cheffron.server.repository.PantryRepository;
import edu.unf.cheffron.server.util.AuthUtil;
import edu.unf.cheffron.server.util.HttpUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PantryRouter implements HttpHandler {

    private PantryRepository repository = PantryRepository.instance;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getPantry(exchange);
                break;
            case "PATCH":
                updatePantry(exchange);
                break;
            default:
                HttpUtil.respondError(exchange, 400, "Invalid request method!");
        }
    }

    private void getPantry(HttpExchange exchange) {
        var userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null) {
            HttpUtil.respond(exchange, 401, "Must be logged in.");
            return;
        }

        try {
            Pantry pantry = repository.read(userId);
            String json = HttpUtil.toJson(pantry);
            HttpUtil.respond(exchange, 200, json);
        } catch (SQLException e) {
            HttpUtil.respondError(exchange, 500, "Could not read pantry!");
        }
    }

    private void updatePantry(HttpExchange exchange) {
        var userId = AuthUtil.authenticateRequest(exchange);

        if (userId == null) {
            HttpUtil.respond(exchange, 401, "Must be logged in.");
            return;
        }

        try {
            JsonObject body = HttpUtil.getJsonBody(exchange);

            JsonArray arr = body.getAsJsonArray("ingredients");

            List<RecipeIngredient> ingredients = new ArrayList<>();
            for (JsonElement element : arr) {
                var obj = element.getAsJsonObject();

                obj.addProperty("recipeId", "");
                obj.addProperty("ingredientId", "");

                ingredients.add(RecipeIngredient.fromJson(obj));
            }
            try {
                repository.update(userId, new Pantry(userId, ingredients));
                HttpUtil.respond(exchange, 200, repository.read(userId));
            } catch (SQLException e) {
                HttpUtil.respondError(exchange, 500, "Could not update pantry!");
            }
        } catch (Exception e) {
            HttpUtil.respondError(exchange, 400, "Invalid JSON body: " + e.getMessage());
        }
    }
}
