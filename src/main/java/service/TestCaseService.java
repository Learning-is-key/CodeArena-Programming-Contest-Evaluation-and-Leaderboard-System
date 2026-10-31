package service;

import database.DBConnection;
import model.TestCase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestCaseService {

    public boolean addTestCase(int questionId, String input,
                               String expectedOutput, int marks)
            throws SQLException {

        String sql = "INSERT INTO test_cases "
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

    public List<TestCase> getTestCasesByQuestion(int questionId)
            throws SQLException {

        List<TestCase> testCases = new ArrayList<>();

        String sql = "SELECT * FROM test_cases "
                   + "WHERE question_id = ? ORDER BY test_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    testCases.add(new TestCase(
                            rs.getInt("test_id"),
                            rs.getInt("question_id"),
                            rs.getString("input_data"),
                            rs.getString("expected_output"),
                            rs.getInt("marks")
                    ));
                }
            }
        }

        return testCases;
    }
}