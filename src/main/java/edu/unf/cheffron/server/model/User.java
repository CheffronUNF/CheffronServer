package edu.unf.cheffron.server.model;

public class User {

    private final String userId, username, email, name;
    private final int chefHatsReceived;

    public User(String userId, String username, String email, String name, int chefHatsReceived) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.chefHatsReceived = chefHatsReceived;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public int getChefHatsReceived() {
        return chefHatsReceived;
    }
}
