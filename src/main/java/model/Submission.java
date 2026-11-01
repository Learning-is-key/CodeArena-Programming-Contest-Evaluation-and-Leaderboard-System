package model;

import java.sql.Timestamp;

public class Submission {
  private int submissionId;
  private int userId;
  private int questionId;
  private String sourceFile;
  private double score;
  private int passedTests;
  private int totalTests;
  private String status;
  private Timestamp submittedAt;

  public Submission(
      int submissionId,
      int userId,
      int questionId,
      String sourceFile,
      double score,
      int passedTests,
      int totalTests,
      String status,
      Timestamp submittedAt) {
    this.submissionId = submissionId;
    this.userId = userId;
    this.questionId = questionId;
    this.sourceFile = sourceFile;
    this.score = score;
    this.passedTests = passedTests;
    this.totalTests = totalTests;
    this.status = status;
    this.submittedAt = submittedAt;
  }

  public int getSubmissionId() {
    return submissionId;
  }

  public int getUserId() {
    return userId;
  }

  public int getQuestionId() {
    return questionId;
  }

  public String getSourceFile() {
    return sourceFile;
  }

  public double getScore() {
    return score;
  }

  public int getPassedTests() {
    return passedTests;
  }

  public int getTotalTests() {
    return totalTests;
  }

  public String getStatus() {
    return status;
  }

  public Timestamp getSubmittedAt() {
    return submittedAt;
  }

  @Override
  public String toString() {
    return "\nSubmission ID: "
        + submissionId
        + "\nUser ID: "
        + userId
        + "\nQuestion ID: "
        + questionId
        + "\nScore: "
        + score
        + "\nStatus: "
        + status
        + "\n-------------------------";
  }
}
