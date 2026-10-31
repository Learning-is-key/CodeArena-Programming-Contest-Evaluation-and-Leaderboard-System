package model;

public class LeaderboardEntry {

    private int userId;
    private String username;
    private double totalScore;
    private double percentage;
    private int rank;

    public LeaderboardEntry(int userId, String username,
                             double totalScore, double percentage) {
        this.userId = userId;
        this.username = username;
        this.totalScore = totalScore;
        this.percentage = percentage;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public double getPercentage() {
        return percentage;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return rank + ". " + username
                + " | Score: " + totalScore
                + " | Percentage: " + String.format("%.2f", percentage) + "%";
    }
}