import java.util.List;
import java.util.Scanner;
import model.TestCase;
import service.TestCaseService;

public class TestCaseTest {

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    TestCaseService service = new TestCaseService();

    try {
      System.out.println("===== TEST CASE MANAGEMENT =====");
      System.out.println("1. Add Test Case");
      System.out.println("2. View Test Cases");
      System.out.print("Choose: ");

      int choice = sc.nextInt();
      sc.nextLine();

      System.out.print("Enter Question ID: ");
      int questionId = sc.nextInt();
      sc.nextLine();

      if (choice == 1) {
        System.out.print("Input data: ");
        String input = sc.nextLine();

        System.out.print("Expected output: ");
        String expected = sc.nextLine();

        System.out.print("Marks: ");
        int marks = sc.nextInt();

        boolean added = service.addTestCase(questionId, input, expected, marks);

        if (added) {
          System.out.println("Test case added successfully!");
        }
      }

      List<TestCase> testCases = service.getTestCasesByQuestion(questionId);

      System.out.println("\n===== TEST CASES =====");

      for (TestCase testCase : testCases) {
        System.out.println(testCase);
      }

    } catch (Exception e) {
      System.out.println("Operation failed: " + e.getMessage());
      e.printStackTrace();
    } finally {
      sc.close();
    }
  }
}
