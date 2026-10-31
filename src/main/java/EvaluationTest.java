import service.EvaluationEngine;
import java.util.Scanner;

public class EvaluationTest {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        EvaluationEngine engine = new EvaluationEngine();

        try {
            System.out.println("===== AUTOMATIC CODE EVALUATION =====");

            System.out.print("Enter Submission ID: ");
            int submissionId = sc.nextInt();

            engine.evaluateSubmission(submissionId);

        } catch (Exception e) {
            System.out.println("Evaluation failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}