package service;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.TestCase;

public class TestCaseService {

  public boolean addTestCase(int questionId, String input, String expectedOutput, int marks)
      throws SQLException {

    String sql =
        "INSERT INTO test_cases "
            + "(question_id, input_data, expected_output, marks) "
            + "VALUES (?, ?, ?, ?)";

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, questionId);
      ps.setString(2, input);
      ps.setString(3, expectedOutput);
      ps.setInt(4, marks);

      return ps.executeUpdate() == 1;
    }
  }

  public List<TestCase> getTestCasesByQuestion(int questionId) throws SQLException {

    List<TestCase> testCases = new ArrayList<>();

    String sql = "SELECT * FROM test_cases " + "WHERE question_id = ? ORDER BY test_id";

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, questionId);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          testCases.add(
              new TestCase(
                  rs.getInt("test_id"),
                  rs.getInt("question_id"),
                  rs.getString("input_data"),
                  rs.getString("expected_output"),
                  rs.getInt("marks")));
        }
      }
    }

    return testCases;
  }

  public boolean updateTestCase(int testId, String input, String expectedOutput, int marks)
      throws SQLException {

    String sql =
        """
        UPDATE test_cases
        SET input_data = ?, expected_output = ?, marks = ?
        WHERE test_id = ?
        """;

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setString(1, input);
      ps.setString(2, expectedOutput);
      ps.setInt(3, marks);
      ps.setInt(4, testId);

      return ps.executeUpdate() == 1;
    }
  }

  public boolean deleteTestCase(int testId) throws SQLException {

    String sql = "DELETE FROM test_cases WHERE test_id = ?";

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, testId);

      return ps.executeUpdate() == 1;
    }
  }
}
