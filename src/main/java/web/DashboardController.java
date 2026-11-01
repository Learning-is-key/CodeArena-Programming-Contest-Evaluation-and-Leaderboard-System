package web;

import jakarta.servlet.http.HttpSession;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import service.ContestService;

@Controller
public class DashboardController {

  private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

  private final ContestService contestService = new ContestService();

  @GetMapping("/")
  public String dashboard(HttpSession session, Model model) {

    User user = (User) session.getAttribute("loggedInUser");

    // Require login before showing the dashboard
    if (user == null) {
      return "redirect:/login";
    }

    model.addAttribute("username", user.getUsername());
    model.addAttribute("isAdmin", "ADMIN".equalsIgnoreCase(user.getRole()));

    try {
      var contests = contestService.getAllContests();
      model.addAttribute("contests", contests);
      model.addAttribute("contestCount", contests.size());
    } catch (Exception e) {
      model.addAttribute("contests", java.util.Collections.emptyList());
      model.addAttribute("contestCount", 0);
      model.addAttribute("error", "Unable to load contests. Please check the database connection.");
      logger.error("Failed to load dashboard contests.", e);
    }

    return "dashboard";
  }
}
