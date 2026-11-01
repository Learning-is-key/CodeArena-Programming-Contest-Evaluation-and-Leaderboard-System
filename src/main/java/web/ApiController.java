package web;

import java.util.Map;
import model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ContestService;
import service.QuestionService;
import service.UserService;

@RestController
@RequestMapping("/api")
public class ApiController {

  private final UserService userService = new UserService();
  private final ContestService contestService = new ContestService();
  private final QuestionService questionService = new QuestionService();

  // Request formats
  public record RegisterRequest(String username, String email, String password) {}

  public record LoginRequest(String username, String password) {}

  // Register a participant
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

    try {
      boolean registered =
          userService.register(request.username(), request.email(), request.password());

      if (registered) {
        return ResponseEntity.ok(Map.of("message", "Registration successful"));
      }

      return ResponseEntity.badRequest().body(Map.of("error", "Registration failed"));

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Unable to register. Check the supplied details."));
    }
  }

  // Login
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {

    try {
      User user = userService.login(request.username(), request.password());

      if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Invalid username or password"));
      }

      return ResponseEntity.ok(user);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Login could not be completed"));
    }
  }

  // Get all contests
  @GetMapping("/contests")
  public ResponseEntity<?> getContests() {

    try {
      return ResponseEntity.ok(contestService.getAllContests());

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Unable to load contests"));
    }
  }

  // Get questions belonging to a contest
  @GetMapping("/contests/{contestId}/questions")
  public ResponseEntity<?> getQuestions(@PathVariable int contestId) {

    try {
      return ResponseEntity.ok(questionService.getQuestionsByContest(contestId));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Unable to load questions"));
    }
  }
}
