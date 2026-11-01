package web;

import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.ContestService;
import service.QuestionService;
import service.TestCaseService;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

  private final ContestService contestService = new ContestService();
  private final QuestionService questionService = new QuestionService();
  private final TestCaseService testCaseService = new TestCaseService();

  private boolean isAdmin(HttpSession session) {
    User user = (User) session.getAttribute("loggedInUser");
    return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
  }

  @GetMapping
  public String dashboard(HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/";
    }

    try {
      var contests = contestService.getAllContests();
      var questions = new ArrayList<model.Question>();
      Map<Integer, List<model.Question>> questionsByContest = new HashMap<>();
      Map<Integer, List<model.TestCase>> testCasesByQuestion = new HashMap<>();

      for (var contest : contests) {
        var contestQuestions = questionService.getQuestionsByContest(contest.getContestId());

        questions.addAll(contestQuestions);

        questionsByContest.put(contest.getContestId(), contestQuestions);
      }

      for (var question : questions) {
        testCasesByQuestion.put(
            question.getQuestionId(),
            testCaseService.getTestCasesByQuestion(question.getQuestionId()));
      }

      model.addAttribute("contests", contests);
      model.addAttribute("questions", questions);
      model.addAttribute("testCasesByQuestion", testCasesByQuestion);
      model.addAttribute("questionsByContest", questionsByContest);

    } catch (Exception e) {
      model.addAttribute("error", "Unable to load admin data.");
      logger.error("Failed to load admin dashboard data.", e);
    }

    return "admin";
  }

  @PostMapping("/contests")
  public String createContest(
      @RequestParam String name,
      @RequestParam String description,
      @RequestParam String startTime,
      @RequestParam String endTime,
      HttpSession session,
      RedirectAttributes redirect) {

    if (!isAdmin(session)) return "redirect:/";

    try {
      contestService.addContest(
          name,
          description,
          Timestamp.valueOf(LocalDateTime.parse(startTime)),
          Timestamp.valueOf(LocalDateTime.parse(endTime)));
      redirect.addFlashAttribute("success", "Contest created successfully!");
    } catch (Exception e) {
      redirect.addFlashAttribute("error", "Could not create contest: " + e.getMessage());
    }

    return "redirect:/admin";
  }

  @PostMapping("/questions")
  public String createQuestion(
      @RequestParam int contestId,
      @RequestParam String title,
      @RequestParam String description,
      @RequestParam String inputFormat,
      @RequestParam String outputFormat,
      @RequestParam int maxMarks,
      HttpSession session,
      RedirectAttributes redirect) {

    if (!isAdmin(session)) return "redirect:/";

    try {
      questionService.addQuestion(
          contestId, title, description, inputFormat, outputFormat, maxMarks);
      redirect.addFlashAttribute("success", "Question added successfully!");
    } catch (Exception e) {
      redirect.addFlashAttribute("error", "Could not add question: " + e.getMessage());
    }

    return "redirect:/admin";
  }

  @PostMapping("/test-cases")
  public String createTestCase(
      @RequestParam int questionId,
      @RequestParam String input,
      @RequestParam String expectedOutput,
      @RequestParam int marks,
      HttpSession session,
      RedirectAttributes redirect) {

    if (!isAdmin(session)) return "redirect:/";

    try {
      testCaseService.addTestCase(questionId, input, expectedOutput, marks);
      redirect.addFlashAttribute("success", "Test case added successfully!");
    } catch (Exception e) {
      redirect.addFlashAttribute("error", "Could not add test case: " + e.getMessage());
    }

    return "redirect:/admin";
  }

  @PostMapping("/contests/update")
  public String updateContest(
      @RequestParam int contestId,
      @RequestParam String name,
      @RequestParam String description,
      @RequestParam String startTime,
      @RequestParam String endTime,
      HttpSession session,
      RedirectAttributes redirect) {

    if (!isAdmin(session)) return "redirect:/";

    try {
      boolean updated =
          contestService.updateContest(
              contestId,
              name,
              description,
              Timestamp.valueOf(LocalDateTime.parse(startTime)),
              Timestamp.valueOf(LocalDateTime.parse(endTime)));

      redirect.addFlashAttribute(
          updated ? "success" : "error",
          updated ? "Contest updated successfully!" : "Contest not found.");

    } catch (Exception e) {
      redirect.addFlashAttribute("error", "Could not update contest: " + e.getMessage());
    }

    return "redirect:/admin";
  }

  @PostMapping("/contests/delete")
  public String deleteContest(
      @RequestParam int contestId, HttpSession session, RedirectAttributes redirect) {

    if (!isAdmin(session)) return "redirect:/";

    try {
      boolean deleted = contestService.deleteContest(contestId);

      redirect.addFlashAttribute(
          deleted ? "success" : "error",
          deleted ? "Contest deleted successfully!" : "Contest not found.");

    } catch (Exception e) {
      redirect.addFlashAttribute(
          "error", "Could not delete contest. Remove its related records first.");
    }

    return "redirect:/admin";
  }

  @PostMapping("/questions/update")
  public String updateQuestion(
      @RequestParam int questionId,
      @RequestParam String title,
      @RequestParam String description,
      @RequestParam String inputFormat,
      @RequestParam String outputFormat,
      @RequestParam int maxMarks,
      HttpSession session,
      RedirectAttributes redirect) {

    if (!isAdmin(session)) return "redirect:/";

    try {
      boolean updated =
          questionService.updateQuestion(
              questionId, title, description, inputFormat, outputFormat, maxMarks);

      redirect.addFlashAttribute(
          updated ? "success" : "error",
          updated ? "Question updated successfully!" : "Question not found.");

    } catch (Exception e) {
      redirect.addFlashAttribute("error", "Could not update question: " + e.getMessage());
    }

    return "redirect:/admin";
  }

  @PostMapping("/questions/delete")
  public String deleteQuestion(
      @RequestParam int questionId, HttpSession session, RedirectAttributes redirect) {

    if (!isAdmin(session)) return "redirect:/";

    try {
      boolean deleted = questionService.deleteQuestion(questionId);

      redirect.addFlashAttribute(
          deleted ? "success" : "error",
          deleted ? "Question deleted successfully!" : "Question not found.");

    } catch (Exception e) {
      redirect.addFlashAttribute(
          "error", "Could not delete question. It may have existing test cases or submissions.");
    }

    return "redirect:/admin";
  }

  @PostMapping("/test-cases/update")
  public String updateTestCase(
      @RequestParam int testId,
      @RequestParam String input,
      @RequestParam String expectedOutput,
      @RequestParam int marks,
      HttpSession session,
      RedirectAttributes redirectAttributes) {

    if (!isAdmin(session)) return "redirect:/login";

    try {
      testCaseService.updateTestCase(testId, input, expectedOutput, marks);

      redirectAttributes.addFlashAttribute("success", "Test case updated successfully.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute(
          "error", "Could not update test case: " + e.getMessage());
    }

    return "redirect:/admin";
  }

  @PostMapping("/test-cases/delete")
  public String deleteTestCase(
      @RequestParam int testId, HttpSession session, RedirectAttributes redirectAttributes) {

    if (!isAdmin(session)) return "redirect:/login";

    try {
      testCaseService.deleteTestCase(testId);

      redirectAttributes.addFlashAttribute("success", "Test case deleted successfully.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute(
          "error", "Could not delete test case. It may have existing submission results.");
    }

    return "redirect:/admin";
  }
}
