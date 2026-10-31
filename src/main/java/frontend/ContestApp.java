
package frontend;

import model.*;
import service.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ContestApp {

    private static final UserService userService = new UserService();
    private static final ContestService contestService = new ContestService();
    private static final QuestionService questionService = new QuestionService();
    private static final SubmissionService submissionService = new SubmissionService();
    private static final EvaluationEngine evaluationEngine = new EvaluationEngine();
    private static final LeaderboardService leaderboardService = new LeaderboardService();

    private static User loggedInUser;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ContestApp::showLogin);
    }

    private static void showLogin() {

        JFrame frame = new JFrame("Programming Contest System - Login");
        frame.setSize(420, 280);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JLabel heading = new JLabel("PROGRAMMING CONTEST SYSTEM");
        heading.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        panel.add(heading);
        panel.add(createFieldPanel("Username:", usernameField));
        panel.add(createFieldPanel("Password:", passwordField));
        panel.add(loginButton);

        loginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());

                loggedInUser = userService.login(username, password);

                if (loggedInUser != null) {
                    frame.dispose();
                    showDashboard();
                } else {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Invalid username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

            } catch (Exception ex) {
                showError(frame, ex);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private static JPanel createFieldPanel(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private static void showDashboard() {

        JFrame frame = new JFrame("Programming Contest System");
        frame.setSize(650, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel welcome = new JLabel(
                "Welcome, " + loggedInUser.getUsername()
        );
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcome.setHorizontalAlignment(SwingConstants.CENTER);

        JButton contestsButton = new JButton("View Contests");
        JButton questionsButton = new JButton("View Questions");
        JButton submitButton = new JButton("Submit Java Code");
        JButton historyButton = new JButton("My Submissions");
        JButton leaderboardButton = new JButton("Leaderboard");
        JButton logoutButton = new JButton("Logout");

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(35, 60, 35, 60)
        );

        buttonPanel.add(contestsButton);
        buttonPanel.add(questionsButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(leaderboardButton);
        buttonPanel.add(logoutButton);

        contestsButton.addActionListener(e -> viewContests(frame));
        questionsButton.addActionListener(e -> viewQuestions(frame));
        submitButton.addActionListener(e -> submitCode(frame));
        historyButton.addActionListener(e -> viewSubmissions(frame));
        leaderboardButton.addActionListener(e -> viewLeaderboard(frame));

        logoutButton.addActionListener(e -> {
            loggedInUser = null;
            frame.dispose();
            showLogin();
        });

        frame.setLayout(new BorderLayout());
        frame.add(welcome, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static void viewContests(JFrame parent) {
        try {
            List<Contest> contests = contestService.getAllContests();

            StringBuilder result = new StringBuilder();

            for (Contest contest : contests) {
                result.append(contest).append("\n\n");
            }

            showText(parent, "Available Contests", result.toString());

        } catch (Exception ex) {
            showError(parent, ex);
        }
    }

    private static void viewQuestions(JFrame parent) {
        try {
            String value = JOptionPane.showInputDialog(
                    parent, "Enter Contest ID:"
            );

            if (value == null) return;

            int contestId = Integer.parseInt(value);

            List<Question> questions =
                    questionService.getQuestionsByContest(contestId);

            StringBuilder result = new StringBuilder();

            for (Question question : questions) {
                result.append(question).append("\n\n");
            }

            showText(parent, "Contest Questions", result.toString());

        } catch (Exception ex) {
            showError(parent, ex);
        }
    }

    private static void submitCode(JFrame parent) {
        try {
            String value = JOptionPane.showInputDialog(
                    parent, "Enter Question ID:"
            );

            if (value == null) return;

            int questionId = Integer.parseInt(value);

            JTextArea codeArea = new JTextArea(18, 55);
            codeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            codeArea.setText(
                    "public class Main {\n" +
                    "    public static void main(String[] args) {\n" +
                    "        // Write your code here\n" +
                    "    }\n" +
                    "}"
            );

            JScrollPane scrollPane = new JScrollPane(codeArea);

            int option = JOptionPane.showConfirmDialog(
                    parent,
                    scrollPane,
                    "Enter Java Source Code",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option != JOptionPane.OK_OPTION) return;

            String sourceCode = codeArea.getText();

            int submissionId = submissionService.submitCode(
                    loggedInUser.getUserId(),
                    questionId,
                    sourceCode
            );

            JOptionPane.showMessageDialog(
                    parent,
                    "Submission successful!\nSubmission ID: " + submissionId
            );

            evaluationEngine.evaluateSubmission(submissionId);

            JOptionPane.showMessageDialog(
                    parent,
                    "Evaluation completed.\nCheck My Submissions for the result."
            );

        } catch (Exception ex) {
            showError(parent, ex);
        }
    }

    private static void viewSubmissions(JFrame parent) {
        try {
            List<Submission> submissions =
                    submissionService.getSubmissionsByUser(
                            loggedInUser.getUserId()
                    );

            StringBuilder result = new StringBuilder();

            for (Submission submission : submissions) {
                result.append(submission).append("\n\n");
            }

            showText(parent, "My Submissions", result.toString());

        } catch (Exception ex) {
            showError(parent, ex);
        }
    }

    private static void viewLeaderboard(JFrame parent) {
        try {
            String value = JOptionPane.showInputDialog(
                    parent, "Enter Contest ID:"
            );

            if (value == null) return;

            int contestId = Integer.parseInt(value);

            List<LeaderboardEntry> leaderboard =
                    leaderboardService.getLeaderboard(contestId);

            StringBuilder result = new StringBuilder();

            for (LeaderboardEntry entry : leaderboard) {
                result.append(entry).append("\n\n");
            }

            if (leaderboard.isEmpty()) {
                result.append("No accepted submissions yet.");
            }

            showText(parent, "Contest Leaderboard", result.toString());

        } catch (Exception ex) {
            showError(parent, ex);
        }
    }

    private static void showText(
            JFrame parent, String title, String content) {

        JTextArea textArea = new JTextArea(content, 18, 45);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JOptionPane.showMessageDialog(
                parent,
                new JScrollPane(textArea),
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static void showError(JFrame parent, Exception ex) {
        JOptionPane.showMessageDialog(
                parent,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
        ex.printStackTrace();
    }
}