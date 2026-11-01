package web;

import jakarta.servlet.http.HttpSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import model.Question;
import model.Submission;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.ContestService;
import service.EvaluationEngine;
import service.QuestionService;
import service.SubmissionService;
import service.SubmissionTestResultService;

@Controller
public class SubmissionController {

  private static final Logger logger = LoggerFactory.getLogger(SubmissionController.class);

  private final SubmissionService submissionService = new SubmissionService();

  private final EvaluationEngine evaluationEngine = new EvaluationEngine();

  private final ContestService contestService = new ContestService();

  private final QuestionService questionService = new QuestionService();

  private final SubmissionTestResultService resultService = new SubmissionTestResultService();

  @PostMapping("/questions/{questionId}/submit")
  public String submitCode(
      @PathVariable int questionId,
      @RequestParam String sourceCode,
      HttpSession session,
      Model model) {

    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
      return "redirect:/login";
    }

    try {
      int submissionId = submissionService.submitCode(user.getUserId(), questionId, sourceCode);

      evaluationEngine.evaluateSubmission(submissionId);

      Submission result =
          submissionService.getSubmissionsByUser(user.getUserId()).stream()
              .filter(s -> s.getSubmissionId() == submissionId)
              .findFirst()
              .orElse(null);

      model.addAttribute("submission", result);
      model.addAttribute("username", user.getUsername());

    } catch (Exception e) {
      logger.error("An error occurred while processing the submission request.", e);
      model.addAttribute("error", "Submission failed. Please try again.");
    }

    return "submission-result";
  }

  @GetMapping("/submissions")
  public String submissionHistory(HttpSession session, Model model) {

    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
      return "redirect:/login";
    }

    try {
      var submissions = submissionService.getSubmissionsByUser(user.getUserId());

      Map<Integer, String> questionTitles = new HashMap<>();

      var contests = contestService.getAllContests();

      for (var contest : contests) {
        for (Question question : questionService.getQuestionsByContest(contest.getContestId())) {

          questionTitles.put(question.getQuestionId(), question.getTitle());
        }
      }

      model.addAttribute("submissions", submissions);
      model.addAttribute("questionTitles", questionTitles);
      model.addAttribute("username", user.getUsername());

    } catch (Exception e) {
      logger.error("An error occurred while processing the submission request.", e);
      model.addAttribute("error", "Unable to load submission history.");
    }

    return "submission-history";
  }

  @GetMapping("/submissions/{submissionId}")
  public String submissionDetails(
      @PathVariable int submissionId, HttpSession session, Model model) {

    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
      return "redirect:/login";
    }

    try {
      Submission submission =
          submissionService.getSubmissionsByUser(user.getUserId()).stream()
              .filter(s -> s.getSubmissionId() == submissionId)
              .findFirst()
              .orElse(null);

      if (submission == null) {
        return "redirect:/submissions";
      }

      String sourceCode = Files.readString(Path.of(submission.getSourceFile()));

      model.addAttribute("submission", submission);
      model.addAttribute("sourceCode", sourceCode);
      model.addAttribute("testResults", resultService.getResults(submissionId));
      model.addAttribute("username", user.getUsername());

    } catch (Exception e) {
      logger.error("An error occurred while processing the submission request.", e);
      model.addAttribute("error", "Unable to load submission details.");
    }

    return "submission-details";
  }
}
