package model;

public class SubmissionTestResult {

  private int resultId;
  private int submissionId;
  private int testId;
  private String inputData;
  private String expectedOutput;
  private String actualOutput;
  private String resultStatus;
  private double marksAwarded;
  private long executionTimeMs;
  private String errorMessage;

  public SubmissionTestResult(
      int resultId,
      int submissionId,
      int testId,
      String inputData,
      String expectedOutput,
      String actualOutput,
      String resultStatus,
      double marksAwarded,
      long executionTimeMs,
      String errorMessage) {

    this.resultId = resultId;
    this.submissionId = submissionId;
    this.testId = testId;
    this.inputData = inputData;
    this.expectedOutput = expectedOutput;
    this.actualOutput = actualOutput;
    this.resultStatus = resultStatus;
    this.marksAwarded = marksAwarded;
    this.executionTimeMs = executionTimeMs;
    this.errorMessage = errorMessage;
  }

  public int getResultId() {
    return resultId;
  }

  public int getSubmissionId() {
    return submissionId;
  }

  public int getTestId() {
    return testId;
  }

  public String getInputData() {
    return inputData;
  }

  public String getExpectedOutput() {
    return expectedOutput;
  }

  public String getActualOutput() {
    return actualOutput;
  }

  public String getResultStatus() {
    return resultStatus;
  }

  public double getMarksAwarded() {
    return marksAwarded;
  }

  public long getExecutionTimeMs() {
    return executionTimeMs;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
