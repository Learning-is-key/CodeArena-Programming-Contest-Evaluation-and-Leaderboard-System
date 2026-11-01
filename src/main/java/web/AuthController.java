package web;

import jakarta.servlet.http.HttpSession;
import model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.UserService;

@Controller
public class AuthController {

  private final UserService userService = new UserService();

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @GetMapping("/register")
  public String registerPage() {
    return "register";
  }

  @PostMapping("/login")
  public String login(
      @RequestParam String username,
      @RequestParam String password,
      HttpSession session,
      RedirectAttributes redirect) {

    try {
      User user = userService.login(username, password);

      if (user == null) {
        redirect.addFlashAttribute("error", "Invalid username or password.");
        return "redirect:/login";
      }

      session.setAttribute("loggedInUser", user);

      if ("ADMIN".equalsIgnoreCase(user.getRole())) {
        return "redirect:/admin";
      }

      return "redirect:/";

    } catch (Exception e) {
      redirect.addFlashAttribute("error", "Login failed. Please try again.");
      return "redirect:/login";
    }
  }

  @PostMapping("/register")
  public String register(
      @RequestParam String username,
      @RequestParam String email,
      @RequestParam String password,
      RedirectAttributes redirect) {

    try {
      userService.register(username, email, password);

      redirect.addFlashAttribute("success", "Registration successful. Please log in.");
      return "redirect:/login";

    } catch (Exception e) {
      redirect.addFlashAttribute(
          "error", e.getMessage() == null ? "Registration failed." : e.getMessage());
      return "redirect:/register";
    }
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/login";
  }
}
