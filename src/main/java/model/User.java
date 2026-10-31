
package model;

public class User {

    private int userId;
    private String username;
    private String email;
    private String role;

    public User(int userId, String username, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User ID: " + userId
                + "\nUsername: " + username
                + "\nEmail: " + email
                + "\nRole: " + role;
    }
}
