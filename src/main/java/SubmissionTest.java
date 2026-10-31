import service.SubmissionService;
import model.Submission;

import java.util.List;
import java.util.Scanner;

public class SubmissionTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SubmissionService service = new SubmissionService();

        try {
            System.out.println("===== CODE SUBMISSION =====");

            System.out.print("Enter User ID: ");
            int userId = sc.nextInt();

            System.out.print("Enter Question ID: ");
            int questionId = sc.nextInt();
            sc.nextLine();

            System.out.println("Enter Java source code.");
            System.out.println("Type END on a separate line to finish:");

            StringBuilder sourceCode = new StringBuilder();

            while (true) {
                String line = sc.nextLine();

                if (line.equals("END")) {
                    break;
                }

                sourceCode.append(line).append("\n");
            }

            int submissionId = service.submitCode(
                    userId, questionId, sourceCode.toString());

            System.out.println("\nSubmission successful!");
            System.out.println("Submission ID: " + submissionId);

            List<Submission> submissions =
                    service.getSubmissionsByUser(userId);

            System.out.println("\n===== YOUR SUBMISSIONS =====");

            for (Submission submission : submissions) {
                System.out.println(submission);
            }

        } catch (Exception e) {
            System.out.println("Submission failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}