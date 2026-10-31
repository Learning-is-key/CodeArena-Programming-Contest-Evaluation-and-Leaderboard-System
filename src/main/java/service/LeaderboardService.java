package service;

import database.DBConnection;
import model.LeaderboardEntry;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardService {

    public List<LeaderboardEntry> getLeaderboard(int contestId)
            throws SQLException {

        Map<Integer, LeaderboardEntry> participantScores = new HashMap<>();

        String sql = """
            SELECT u.user_id, u.username,
                   q.question_id, q.max_marks,
                   MAX(s.score) AS best_score
            FROM users u
            JOIN submissions s ON u.user_id = s.user_id
            JOIN questions q ON s.question_id = q.question_id
            WHERE q.contest_id = ?
              AND s.status = 'ACCEPTED'
            GROUP BY u.user_id, u.username,
                     q.question_id, q.max_marks
            """;

        Map<Integer, Double> scores = new HashMap<>();
        Map<Integer, String> usernames = new HashMap<>();

        double totalPossibleMarks = 0;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, contestId);

            try (ResultSet rs = ps.executeQuery()) {

                Set<Integer> questionIds = new HashSet<>();

                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    int questionId = rs.getInt("question_id");

                    double bestScore = rs.getDouble("best_score");

                    scores.merge(userId, bestScore, Double::sum);
                    usernames.put(userId, username);
                    questionIds.add(questionId);
                }
            }
        }

        String marksSql =
                "SELECT SUM(max_marks) FROM questions WHERE contest_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(marksSql)) {

            ps.setInt(1, contestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalPossibleMarks = rs.getDouble(1);
                }
            }
        }

        for (Map.Entry<Integer, Double> entry : scores.entrySet()) {

            double percentage = totalPossibleMarks > 0
                    ? (entry.getValue() / totalPossibleMarks) * 100
                    : 0;

            participantScores.put(
                    entry.getKey(),
                    new LeaderboardEntry(
                            entry.getKey(),
                            usernames.get(entry.getKey()),
                            entry.getValue(),
                            percentage
                    )
            );
        }

        List<LeaderboardEntry> leaderboard =
                participantScores.values()
                        .stream()
                        .sorted(Comparator.comparingDouble(
                                LeaderboardEntry::getTotalScore
                        ).reversed())
                        .collect(Collectors.toList());

        int rank = 1;

        for (LeaderboardEntry participant : leaderboard) {
            participant.setRank(rank++);
        }

        return leaderboard;
    }
}