package edu.unf.cheffron.server.model;

import java.util.UUID;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.JsonObject;

public class User 
{
    private final String userId, username, email, name, password;
    private final int chefHatsReceived;

    public User(String userId, String username, String email, String name, String password, int chefHatsReceived) 
    {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.password = password;
        this.chefHatsReceived = chefHatsReceived;
    }

    public String getUserId() 
    {
        return userId;
    }

    public String getUsername() 
    {
        return username;
    }

    public String getEmail() 
    {
        return email;
    }

    public String getName() 
    {
        return name;
    }

    public String getPassword()
    {
        return password;
    }

    public int getChefHatsReceived() 
    {
        return chefHatsReceived;
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
