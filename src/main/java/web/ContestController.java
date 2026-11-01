package web;

import jakarta.servlet.http.HttpSession;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import service.ContestService;
import service.QuestionService;

@Controller
public class ContestController {
  private static final Logger logger = LoggerFactory.getLogger(ContestController.class);
  private final ContestService contestService = new ContestService();
  private final QuestionService questionService = new QuestionService();

  @GetMapping("/contests/{contestId}")
  public String viewContest(@PathVariable int contestId, HttpSession session, Model model) {

    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
      return "redirect:/login";
    }

    try {
      var contest = contestService.getContestById(contestId);

      if (contest == null) {
        return "redirect:/";
      }

      var questions = questionService.getQuestionsByContest(contestId);

      model.addAttribute("contest", contest);
      model.addAttribute("questions", questions);
      model.addAttribute("username", user.getUsername());

      return "contest-details";

    } catch (Exception e) {
      logger.error("Failed to load contest details.", e);
      model.addAttribute("error", "Unable to load contest details.");
      return "contest-details";
    }
  }
}
