package service;

import database.DBConnection;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Submission;

public class SubmissionService {

  public int submitCode(int userId, int questionId, String sourceCode)
      throws SQLException, IOException {

    if (sourceCode == null || sourceCode.trim().isEmpty()) {
      throw new IllegalArgumentException("Source code cannot be empty.");
    }

    String sql =
        "INSERT INTO submissions "
            + "(user_id, question_id, score, passed_tests, "
            + "total_tests, status) VALUES (?, ?, 0, 0, 0, 'PENDING')";

    try (Connection con = DBConnection.getConnection()) {
      con.setAutoCommit(false);

      try {
        int submissionId;

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

          ps.setInt(1, userId);
          ps.setInt(2, questionId);
          ps.executeUpdate();

          try (ResultSet rs = ps.getGeneratedKeys()) {
            if (!rs.next()) {
              throw new SQLException("Could not create submission ID.");
            }
            submissionId = rs.getInt(1);
          }
        }

        Path directory = Paths.get("submissions", String.valueOf(submissionId));

        Files.createDirectories(directory);

        Path sourcePath = directory.resolve("Main.java");
        Files.writeString(sourcePath, sourceCode, StandardCharsets.UTF_8);

        String updateSql = "UPDATE submissions " + "SET source_file = ? WHERE submission_id = ?";

        try (PreparedStatement ps = con.prepareStatement(updateSql)) {
          ps.setString(1, sourcePath.toString());
          ps.setInt(2, submissionId);
          ps.executeUpdate();
        }

        con.commit();
        return submissionId;

      } catch (SQLException | IOException | RuntimeException e) {
        con.rollback();
        throw e;
      }
    }
  }

  public List<Submission> getSubmissionsByUser(int userId) throws SQLException {

    List<Submission> submissions = new ArrayList<>();

    String sql = "SELECT * FROM submissions " + "WHERE user_id = ? ORDER BY submitted_at DESC";

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, userId);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          submissions.add(
              new Submission(
                  rs.getInt("submission_id"),
                  rs.getInt("user_id"),
                  rs.getInt("question_id"),
                  rs.getString("source_file"),
                  rs.getDouble("score"),
                  rs.getInt("passed_tests"),
                  rs.getInt("total_tests"),
                  rs.getString("status"),
                  rs.getTimestamp("submitted_at")));
        }
      }
    }

    return submissions;
  }

  public void updateEvaluation(
      int submissionId, double score, int passedTests, int totalTests, String status)
      throws SQLException {

    String sql =
        "UPDATE submissions SET score = ?, passed_tests = ?, "
            + "total_tests = ?, status = ? WHERE submission_id = ?";

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setDouble(1, score);
      ps.setInt(2, passedTests);
      ps.setInt(3, totalTests);
      ps.setString(4, status);
      ps.setInt(5, submissionId);

      ps.executeUpdate();
    }
  }
}
