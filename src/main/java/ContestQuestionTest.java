import service.ContestService;
import service.QuestionService;
import model.Contest;
import model.Question;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class ContestQuestionTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ContestService contestService = new ContestService();
        QuestionService questionService = new QuestionService();

        try {
            System.out.println("===== CONTEST MANAGEMENT =====");
            System.out.println("1. Add Contest");
            System.out.println("2. View Contests");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                System.out.print("Contest name: ");
                String name = sc.nextLine();

                System.out.print("Description: ");
                String description = sc.nextLine();

                LocalDateTime start = LocalDateTime.now();
                LocalDateTime end = start.plusHours(2);

                boolean added = contestService.addContest(
                        name,
                        description,
                        Timestamp.valueOf(start),
                        Timestamp.valueOf(end)
                );

                if (added) {
                    System.out.println("Contest added successfully!");
                }
            }

            List<Contest> contests = contestService.getAllContests();

            System.out.println("\n===== AVAILABLE CONTESTS =====");
            for (Contest contest : contests) {
                System.out.println(contest);
            }

            if (contests.isEmpty()) {
                System.out.println("No contests available.");
                return;
            }

            System.out.print("Enter Contest ID to manage questions: ");
            int contestId = sc.nextInt();
            sc.nextLine();

            System.out.println("\n1. Add Question");
            System.out.println("2. View Questions");
            System.out.print("Choose: ");

            int questionChoice = sc.nextInt();
            sc.nextLine();

            if (questionChoice == 1) {
                System.out.print("Question title: ");
                String title = sc.nextLine();

                System.out.print("Description: ");
                String desc = sc.nextLine();

                System.out.print("Input format: ");
                String input = sc.nextLine();

                System.out.print("Output format: ");
                String output = sc.nextLine();

                System.out.print("Maximum marks: ");
                int marks = sc.nextInt();

                boolean added = questionService.addQuestion(
                        contestId, title, desc, input, output, marks
                );

                if (added) {
                    System.out.println("Question added successfully!");
                }
            }

            List<Question> questions =
                    questionService.getQuestionsByContest(contestId);

            System.out.println("\n===== CONTEST QUESTIONS =====");
            for (Question question : questions) {
                System.out.println(question);
            }

        } catch (Exception e) {
            System.out.println("Operation failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}