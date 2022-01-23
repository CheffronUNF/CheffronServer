package edu.unf.cheffron.server.database.model;

public class User {

    private final int userId;
    private final String username, email, name;
    private final int chefHatsReceived;

    public User(int userId, String username, String email, String name, int chefHatsReceived) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.chefHatsReceived = chefHatsReceived;
    }

    public int getUserId() {
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
