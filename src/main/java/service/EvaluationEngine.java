package service;

import database.DBConnection;
import model.TestCase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EvaluationEngine {

    private final TestCaseService testCaseService = new TestCaseService();
    private final SubmissionService submissionService = new SubmissionService();

    public void evaluateSubmission(int submissionId) throws Exception {

        String sourceFile;
        int questionId;

        String sql = "SELECT source_file, question_id FROM submissions "
                   + "WHERE submission_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, submissionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Submission not found.");
                }

                sourceFile = rs.getString("source_file");
                questionId = rs.getInt("question_id");
            }
        }

        Path source = Paths.get(sourceFile).toAbsolutePath();

        if (!Files.exists(source)) {
            throw new IOException("Submitted source file not found.");
        }

        List<TestCase> testCases =
                testCaseService.getTestCasesByQuestion(questionId);

        int totalTests = testCases.size();
        int passedTests = 0;
        double score = 0;
        String status = "WRONG_ANSWER";

        if (totalTests == 0) {
            submissionService.updateEvaluation(
                    submissionId, 0, 0, 0, "NO_TEST_CASES");
            System.out.println("No test cases available.");
            return;
        }

        Path buildDirectory = source.getParent().resolve("build");
        Files.createDirectories(buildDirectory);

        Path compileLog = buildDirectory.resolve("compile.log");

        Process compileProcess = new ProcessBuilder(
                "javac",
                "-encoding", "UTF-8",
                "-d", buildDirectory.toString(),
                source.toString()
        )
        .redirectErrorStream(true)
        .redirectOutput(compileLog.toFile())
        .start();

        boolean compiledInTime =
                compileProcess.waitFor(15, TimeUnit.SECONDS);

        if (!compiledInTime) {
            compileProcess.destroyForcibly();

            submissionService.updateEvaluation(
                    submissionId, 0, 0, totalTests,
                    "COMPILE_TIMEOUT");

            System.out.println("Compilation timed out.");
            return;
        }

        if (compileProcess.exitValue() != 0) {
            String error = Files.readString(
                    compileLog, StandardCharsets.UTF_8);

            submissionService.updateEvaluation(
                    submissionId, 0, 0, totalTests,
                    "COMPILE_ERROR");

            System.out.println("Compilation failed:");
            System.out.println(error);
            return;
        }

        System.out.println("\nCompilation successful!");
        System.out.println("\n===== TEST RESULTS =====");

        for (TestCase testCase : testCases) {

            Path inputFile = buildDirectory.resolve(
                    "input_" + testCase.getTestId() + ".txt");

            Path outputFile = buildDirectory.resolve(
                    "output_" + testCase.getTestId() + ".txt");

            Path errorFile = buildDirectory.resolve(
                    "error_" + testCase.getTestId() + ".txt");

            Files.writeString(
                    inputFile,
                    testCase.getInputData(),
                    StandardCharsets.UTF_8
            );

            Process runProcess = new ProcessBuilder(
                    "java",
                    "-cp", buildDirectory.toString(),
                    "Main"
            )
            .redirectInput(inputFile.toFile())
            .redirectOutput(outputFile.toFile())
            .redirectError(errorFile.toFile())
            .start();

            boolean finished =
                    runProcess.waitFor(3, TimeUnit.SECONDS);

            if (!finished) {
                runProcess.destroyForcibly();

                status = "TIME_LIMIT_EXCEEDED";
                System.out.println("Test " + testCase.getTestId()
                        + ": TIME LIMIT EXCEEDED");
                break;
            }

            if (runProcess.exitValue() != 0) {
                status = "RUNTIME_ERROR";

                System.out.println("Test " + testCase.getTestId()
                        + ": RUNTIME ERROR");

                System.out.println(Files.readString(
                        errorFile, StandardCharsets.UTF_8));

                break;
            }

            String actualOutput = Files.readString(
                    outputFile, StandardCharsets.UTF_8);

            String expectedOutput = testCase.getExpectedOutput();

            if (normalize(actualOutput).equals(
                    normalize(expectedOutput))) {

                passedTests++;
                score += testCase.getMarks();

                System.out.println("Test " + testCase.getTestId()
                        + ": PASSED");

            } else {
                System.out.println("Test " + testCase.getTestId()
                        + ": FAILED");
                System.out.println("Expected: " + expectedOutput);
                System.out.println("Actual: " + actualOutput);
            }
        }

        if (status.equals("WRONG_ANSWER")) {
            if (passedTests == totalTests) {
                status = "ACCEPTED";
            } else if (passedTests > 0) {
                status = "PARTIAL";
            }
        }

        submissionService.updateEvaluation(
                submissionId,
                score,
                passedTests,
                totalTests,
                status
        );

        System.out.println("\n===== EVALUATION SUMMARY =====");
        System.out.println("Submission ID: " + submissionId);
        System.out.println("Passed Tests: " + passedTests
                + "/" + totalTests);
        System.out.println("Score: " + score);
        System.out.println("Status: " + status);
    }

    private String normalize(String output) {
        return output.replace("\r\n", "\n")
                     .trim()
                     .replaceAll("\\s+", " ");
    }
}