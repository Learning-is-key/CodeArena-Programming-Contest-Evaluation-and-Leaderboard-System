
import model.*;
import service.*;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static final UserService userService = new UserService();
    private static final ContestService contestService = new ContestService();
    private static final QuestionService questionService = new QuestionService();
    private static final SubmissionService submissionService = new SubmissionService();
    private static final EvaluationEngine evaluationEngine = new EvaluationEngine();
    private static final LeaderboardService leaderboardService = new LeaderboardService();

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println(" PROGRAMMING CONTEST SYSTEM");
        System.out.println("==================================");

        try {
            System.out.print("Username: ");
            String username = sc.nextLine();

            System.out.print("Password: ");
            String password = sc.nextLine();

            User user = userService.login(username, password);

            if (user == null) {
                System.out.println("Invalid login credentials.");
                return;
            }

            System.out.println("\nWelcome, " + user.getUsername() + "!");

            int choice;

            do {
                System.out.println("\n========== MAIN MENU ==========");
                System.out.println("1. View Contests");
                System.out.println("2. View Questions");
                System.out.println("3. Submit Java Code");
                System.out.println("4. View My Submissions");
                System.out.println("5. View Leaderboard");
                System.out.println("6. Exit");
                System.out.print("Enter choice: ");

                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1 -> viewContests();
                    case 2 -> viewQuestions();
                    case 3 -> submitCode(user.getUserId());
                    case 4 -> viewSubmissions(user.getUserId());
                    case 5 -> viewLeaderboard();
                    case 6 -> System.out.println("Thank you for participating!");
                    default -> System.out.println("Invalid choice.");
                }

            } while (choice != 6);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void viewContests() throws Exception {

        List<Contest> contests = contestService.getAllContests();

        System.out.println("\n========== AVAILABLE CONTESTS ==========");

        for (Contest contest : contests) {
            System.out.println(contest);
        }
    }

    private static void viewQuestions() throws Exception {

        System.out.print("Enter Contest ID: ");
        int contestId = Integer.parseInt(sc.nextLine());

        List<Question> questions =
                questionService.getQuestionsByContest(contestId);

        System.out.println("\n========== QUESTIONS ==========");

        for (Question question : questions) {
            System.out.println(question);
        }
    }

    private static void submitCode(int userId) throws Exception {

        System.out.print("Enter Question ID: ");
        int questionId = Integer.parseInt(sc.nextLine());

        System.out.println("Enter Java source code.");
        System.out.println("Type END on a separate line to finish.");

        StringBuilder sourceCode = new StringBuilder();

        while (true) {
            String line = sc.nextLine();

            if (line.equals("END")) {
                break;
            }

            sourceCode.append(line).append("\n");
        }

        int submissionId = submissionService.submitCode(
                userId, questionId, sourceCode.toString()
        );

        System.out.println("Submission ID: " + submissionId);
        System.out.println("\nStarting automatic evaluation...");

        evaluationEngine.evaluateSubmission(submissionId);
    }

    private static void viewSubmissions(int userId) throws Exception {

        List<Submission> submissions =
                submissionService.getSubmissionsByUser(userId);

        System.out.println("\n========== MY SUBMISSIONS ==========");

        for (Submission submission : submissions) {
            System.out.println(submission);
        }
    }

    private static void viewLeaderboard() throws Exception {

        System.out.print("Enter Contest ID: ");
        int contestId = Integer.parseInt(sc.nextLine());

        List<LeaderboardEntry> leaderboard =
                leaderboardService.getLeaderboard(contestId);

        System.out.println("\n========== LEADERBOARD ==========");

        if (leaderboard.isEmpty()) {
            System.out.println("No accepted submissions yet.");
        }

        for (LeaderboardEntry entry : leaderboard) {
            System.out.println(entry);
        }
    }
}