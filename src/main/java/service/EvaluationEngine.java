package service;

import database.DBConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import model.TestCase;

public class EvaluationEngine {

  private final SubmissionService submissionService = new SubmissionService();

  private final TestCaseService testCaseService = new TestCaseService();

  private final SubmissionTestResultService resultService = new SubmissionTestResultService();

  public void evaluateSubmission(int submissionId) throws Exception {

    int questionId;
    String sourceFile;

    try (Connection con = DBConnection.getConnection();
        PreparedStatement ps =
            con.prepareStatement(
                "SELECT question_id, source_file FROM submissions WHERE submission_id = ?")) {

      ps.setInt(1, submissionId);

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          throw new SQLException("Submission not found.");
        }

        questionId = rs.getInt("question_id");
        sourceFile = rs.getString("source_file");
      }
    }

    List<TestCase> testCases = testCaseService.getTestCasesByQuestion(questionId);

    resultService.clearResults(submissionId);

    if (testCases.isEmpty()) {
      submissionService.updateEvaluation(submissionId, 0, 0, 0, "NO_TEST_CASES");
      return;
    }

    Path sourcePath = Path.of(sourceFile);
    Path workingDirectory = sourcePath.getParent();

    Path compileLog = workingDirectory.resolve("compile-output.txt");

    Process compileProcess =
        new ProcessBuilder("javac", sourcePath.getFileName().toString())
            .directory(workingDirectory.toFile())
            .redirectErrorStream(true)
            .redirectOutput(compileLog.toFile())
            .start();

    boolean compiled = compileProcess.waitFor(15, TimeUnit.SECONDS);

    if (!compiled) {
      compileProcess.destroyForcibly();

      saveAllTestResults(submissionId, testCases, "COMPILE_TIMEOUT", "Compilation timed out.");

      submissionService.updateEvaluation(submissionId, 0, 0, testCases.size(), "COMPILE_ERROR");
      return;
    }

    String compileOutput = Files.readString(compileLog);

    if (compileProcess.exitValue() != 0) {
      saveAllTestResults(submissionId, testCases, "COMPILE_ERROR", compileOutput);

      submissionService.updateEvaluation(submissionId, 0, 0, testCases.size(), "COMPILE_ERROR");
      return;
    }

    int passedTests = 0;
    double totalScore = 0;
    boolean hasTimeout = false;
    boolean hasRuntimeError = false;

    for (TestCase testCase : testCases) {

      Path inputFile = workingDirectory.resolve("input.txt");
      Files.writeString(inputFile, testCase.getInputData());

      long startTime = System.nanoTime();

      Path outputFile = workingDirectory.resolve("output-" + testCase.getTestId() + ".txt");

      Process runProcess =
          new ProcessBuilder("sh", "-c", "ulimit -f 2048; exec java Main")
              .directory(workingDirectory.toFile())
              .redirectErrorStream(true)
              .redirectInput(inputFile.toFile())
              .redirectOutput(outputFile.toFile())
              .start();

      boolean completed = runProcess.waitFor(3, TimeUnit.SECONDS);
      long executionTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

      String actualOutput = "";
      String errorMessage = "";
      String resultStatus;
      double marksAwarded = 0;

      if (!completed) {
        runProcess.destroyForcibly();
        hasTimeout = true;
        resultStatus = "TIME_LIMIT_EXCEEDED";
        errorMessage = "Execution exceeded 3 seconds.";

      } else {
        String output = Files.readString(outputFile);

        if (runProcess.exitValue() != 0) {
          hasRuntimeError = true;
          resultStatus = "RUNTIME_ERROR";
          errorMessage = output;

        } else {
          actualOutput = output.trim();

          if (normalize(actualOutput).equals(normalize(testCase.getExpectedOutput()))) {

            resultStatus = "PASSED";
            passedTests++;
            marksAwarded = testCase.getMarks();

          } else {
            resultStatus = "FAILED";
          }
        }
      }

      totalScore += marksAwarded;

      resultService.saveResult(
          submissionId,
          testCase.getTestId(),
          testCase.getInputData(),
          testCase.getExpectedOutput(),
          actualOutput,
          resultStatus,
          marksAwarded,
          executionTime,
          errorMessage);
    }

    String status;

    if (passedTests == testCases.size()) {
      status = "ACCEPTED";
    } else if (passedTests > 0) {
      status = "PARTIAL";
    } else if (hasTimeout) {
      status = "TIME_LIMIT_EXCEEDED";
    } else if (hasRuntimeError) {
      status = "RUNTIME_ERROR";
    } else {
      status = "WRONG_ANSWER";
    }

    submissionService.updateEvaluation(
        submissionId, totalScore, passedTests, testCases.size(), status);
  }

  private void saveAllTestResults(
      int submissionId, List<TestCase> testCases, String status, String error) throws SQLException {

    for (TestCase testCase : testCases) {
      resultService.saveResult(
          submissionId,
          testCase.getTestId(),
          testCase.getInputData(),
          testCase.getExpectedOutput(),
          "",
          status,
          0,
          0,
          error);
    }
  }

  private String normalize(String output) {
    return output.trim().replaceAll("\\s+", " ");
  }
}
