import model.LeaderboardEntry;
import service.LeaderboardService;

import java.util.List;
import java.util.Scanner;

public class LeaderboardTest {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        LeaderboardService service = new LeaderboardService();

        try {
            System.out.print("Enter Contest ID: ");
            int contestId = sc.nextInt();

            List<LeaderboardEntry> leaderboard =
                    service.getLeaderboard(contestId);

            System.out.println("\n===== CONTEST LEADERBOARD =====");

            if (leaderboard.isEmpty()) {
                System.out.println("No accepted submissions yet.");
            } else {
                for (LeaderboardEntry entry : leaderboard) {
                    System.out.println(entry);
                }
            }

        } catch (Exception e) {
            System.out.println("Leaderboard error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}