package edu.unf.cheffron.server.model;

import com.google.gson.JsonObject;

public record User(String userId, String username, String email, String name, String password, int chefHatsReceived) 
{
    public User
    {
        
    }

    public static User fromJson(JsonObject json)
    {
        if (!json.has("userId") || !json.has("username") || !json.has("name") || !json.has("email") || !json.has("password")) 
        {
            return null;
        }

        String id = json.get("userId").getAsString();
        String username = json.get("username").getAsString();
        String name = json.get("name").getAsString();
        String email = json.get("email").getAsString();
        String password = json.get("password").getAsString();

        return new User(id, username, email, name, password, 0);
    }
}
