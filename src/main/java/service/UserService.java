package service;

import database.DBConnection;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import model.User;

public class UserService {

  private static final int ITERATIONS = 120000;
  private static final int KEY_LENGTH = 256;

  // Register a new participant
  public boolean register(String username, String email, String password) throws Exception {

    if (username == null
        || username.trim().isEmpty()
        || email == null
        || email.trim().isEmpty()
        || password == null
        || password.length() < 8) {
      throw new IllegalArgumentException(
          "Enter a username, email, and password of at least 8 characters.");
    }

    String hashedPassword = hashPassword(password);

    String sql =
        "INSERT INTO users (username, email, password, role) " + "VALUES (?, ?, ?, 'PARTICIPANT')";

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setString(1, username.trim());
      ps.setString(2, email.trim());
      ps.setString(3, hashedPassword);

      return ps.executeUpdate() == 1;
    }
  }

  // Verify login credentials
  public User login(String username, String password) throws Exception {

    String sql =
        "SELECT user_id, username, email, password, role " + "FROM users WHERE username = ?";

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setString(1, username.trim());

      try (ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
          String storedPassword = rs.getString("password");

          if (verifyPassword(password, storedPassword)) {
            return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("role"));
          }
        }
      }
    }

    return null;
  }

  // Generate a salted PBKDF2 password hash
  private String hashPassword(String password) throws Exception {

    byte[] salt = new byte[16];
    new SecureRandom().nextBytes(salt);

    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

    byte[] hash = factory.generateSecret(spec).getEncoded();

    return "pbkdf2$"
        + ITERATIONS
        + "$"
        + Base64.getEncoder().encodeToString(salt)
        + "$"
        + Base64.getEncoder().encodeToString(hash);
  }

  // Compare the entered password with the stored hash
  private boolean verifyPassword(String password, String stored) throws Exception {

    String[] parts = stored.split("\\$");

    if (parts.length != 4 || !parts[0].equals("pbkdf2")) {
      return false;
    }

    int iterations = Integer.parseInt(parts[1]);
    byte[] salt = Base64.getDecoder().decode(parts[2]);
    byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);

    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

    byte[] actualHash = factory.generateSecret(spec).getEncoded();

    return MessageDigest.isEqual(expectedHash, actualHash);
  }
}
