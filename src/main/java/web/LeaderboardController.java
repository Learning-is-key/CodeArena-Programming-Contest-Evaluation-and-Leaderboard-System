package web;

import jakarta.servlet.http.HttpSession;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import service.LeaderboardService;

@Controller
public class LeaderboardController {
  private static final Logger logger = LoggerFactory.getLogger(ContestController.class);
  private final LeaderboardService leaderboardService = new LeaderboardService();

  @GetMapping("/contests/{contestId}/leaderboard")
  public String showLeaderboard(@PathVariable int contestId, HttpSession session, Model model) {

    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
      return "redirect:/login";
    }

    try {
      model.addAttribute("leaderboard", leaderboardService.getLeaderboard(contestId));

      model.addAttribute("contestId", contestId);
      model.addAttribute("username", user.getUsername());

    } catch (Exception e) {
      logger.error("Failed to load contest details.", e);
      model.addAttribute("error", "Unable to load leaderboard.");
    }

    return "leaderboard";
  }
}
