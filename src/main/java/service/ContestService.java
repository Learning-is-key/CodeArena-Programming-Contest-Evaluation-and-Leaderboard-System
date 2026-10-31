package service;

import database.DBConnection;
import model.Contest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContestService {

    public boolean addContest(String name, String description,
                              Timestamp startTime, Timestamp endTime)
            throws SQLException {

        String sql = "INSERT INTO contests "
                + "(contest_name, description, start_time, end_time, status) "
                + "VALUES (?, ?, ?, ?, 'UPCOMING')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setTimestamp(3, startTime);
            ps.setTimestamp(4, endTime);

            return ps.executeUpdate() == 1;
        }
    }

    public List<Contest> getAllContests() throws SQLException {
        List<Contest> contests = new ArrayList<>();

        String sql = "SELECT * FROM contests ORDER BY contest_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timestamp start = rs.getTimestamp("start_time");
                Timestamp end = rs.getTimestamp("end_time");

                contests.add(new Contest(
                        rs.getInt("contest_id"),
                        rs.getString("contest_name"),
                        rs.getString("description"),
                        start == null ? null : start.toLocalDateTime(),
                        end == null ? null : end.toLocalDateTime(),
                        rs.getString("status")
                ));
            }
        }

        return contests;
    }
}