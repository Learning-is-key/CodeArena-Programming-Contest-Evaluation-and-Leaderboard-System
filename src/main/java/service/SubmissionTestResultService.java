package service;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.SubmissionTestResult;

public class SubmissionTestResultService {

  public void clearResults(int submissionId) throws SQLException {
    String sql = "DELETE FROM submission_test_results WHERE submission_id = ?";

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, submissionId);
      ps.executeUpdate();
    }
  }

  public void saveResult(
      int submissionId,
      int testId,
      String inputData,
      String expectedOutput,
      String actualOutput,
      String resultStatus,
      double marksAwarded,
      long executionTimeMs,
      String errorMessage)
      throws SQLException {

    String sql =
        """
        INSERT INTO submission_test_results
        (submission_id, test_id, input_data, expected_output,
         actual_output, result_status, marks_awarded,
         execution_time_ms, error_message)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, submissionId);
      ps.setInt(2, testId);
      ps.setString(3, inputData);
      ps.setString(4, expectedOutput);
      ps.setString(5, actualOutput);
      ps.setString(6, resultStatus);
      ps.setDouble(7, marksAwarded);
      ps.setLong(8, executionTimeMs);
      ps.setString(9, errorMessage);

      ps.executeUpdate();
    }
  }

  public List<SubmissionTestResult> getResults(int submissionId) throws SQLException {

    List<SubmissionTestResult> results = new ArrayList<>();

    String sql =
        """
        SELECT * FROM submission_test_results
        WHERE submission_id = ?
        ORDER BY result_id
        """;

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, submissionId);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          results.add(
              new SubmissionTestResult(
                  rs.getInt("result_id"),
                  rs.getInt("submission_id"),
                  rs.getInt("test_id"),
                  rs.getString("input_data"),
                  rs.getString("expected_output"),
                  rs.getString("actual_output"),
                  rs.getString("result_status"),
                  rs.getDouble("marks_awarded"),
                  rs.getLong("execution_time_ms"),
                  rs.getString("error_message")));
        }
      }
    }

    return results;
  }
}
