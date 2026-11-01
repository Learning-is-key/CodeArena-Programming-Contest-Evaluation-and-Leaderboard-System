import java.util.Scanner;
import model.User;
import service.UserService;

public class UserTest {

  public static void main(String[] args) {

    Scanner sc = new Scanner(System.in);
    UserService service = new UserService();

    try {
      System.out.println("===== USER MANAGEMENT =====");
      System.out.println("1. Register");
      System.out.println("2. Login");
      System.out.print("Choose: ");

      int choice = sc.nextInt();
      sc.nextLine();

      if (choice == 1) {

        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        if (service.register(username, email, password)) {
          System.out.println("Registration successful!");
        }

      } else if (choice == 2) {

        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        User user = service.login(username, password);

        if (user != null) {
          System.out.println("\nLogin successful!");
          System.out.println(user);
        } else {
          System.out.println("Invalid username or password.");
        }

      } else {
        System.out.println("Invalid choice.");
      }

    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }

    sc.close();
  }
}
