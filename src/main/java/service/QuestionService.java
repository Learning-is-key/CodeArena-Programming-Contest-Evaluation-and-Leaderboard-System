package service;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Question;

public class QuestionService {

  public boolean addQuestion(
      int contestId,
      String title,
      String description,
      String inputFormat,
      String outputFormat,
      int maxMarks)
      throws SQLException {

    String sql =
        "INSERT INTO questions "
            + "(contest_id, title, description, input_format, "
            + "output_format, max_marks) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, contestId);
      ps.setString(2, title);
      ps.setString(3, description);
      ps.setString(4, inputFormat);
      ps.setString(5, outputFormat);
      ps.setInt(6, maxMarks);

      return ps.executeUpdate() == 1;
    }
  }

  public List<Question> getQuestionsByContest(int contestId) throws SQLException {

    List<Question> questions = new ArrayList<>();

    String sql = "SELECT * FROM questions WHERE contest_id = ? " + "ORDER BY question_id";

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, contestId);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          questions.add(
              new Question(
                  rs.getInt("question_id"),
                  rs.getInt("contest_id"),
                  rs.getString("title"),
                  rs.getString("description"),
                  rs.getString("input_format"),
                  rs.getString("output_format"),
                  rs.getInt("max_marks")));
        }
      }
    }

    return questions;
  }

  public boolean updateQuestion(
      int questionId,
      String title,
      String description,
      String inputFormat,
      String outputFormat,
      int maxMarks)
      throws SQLException {

    String sql =
        """
        UPDATE questions
        SET title = ?,
            description = ?,
            input_format = ?,
            output_format = ?,
            max_marks = ?
        WHERE question_id = ?
        """;

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setString(1, title);
      ps.setString(2, description);
      ps.setString(3, inputFormat);
      ps.setString(4, outputFormat);
      ps.setInt(5, maxMarks);
      ps.setInt(6, questionId);

      return ps.executeUpdate() == 1;
    }
  }

  public boolean deleteQuestion(int questionId) throws SQLException {

    String sql = "DELETE FROM questions WHERE question_id = ?";

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, questionId);

      return ps.executeUpdate() == 1;
    }
  }
}
