package model;

public class TestCase {
  private int testId;
  private int questionId;
  private String inputData;
  private String expectedOutput;
  private int marks;

  public TestCase(int testId, int questionId, String inputData, String expectedOutput, int marks) {
    this.testId = testId;
    this.questionId = questionId;
    this.inputData = inputData;
    this.expectedOutput = expectedOutput;
    this.marks = marks;
  }

  public int getTestId() {
    return testId;
  }

  public int getQuestionId() {
    return questionId;
  }

  public String getInputData() {
    return inputData;
  }

  public String getExpectedOutput() {
    return expectedOutput;
  }

  public int getMarks() {
    return marks;
  }

  @Override
  public String toString() {
    return "\nTest Case ID: "
        + testId
        + "\nInput: "
        + inputData
        + "\nExpected Output: "
        + expectedOutput
        + "\nMarks: "
        + marks
        + "\n-------------------------";
  }
}
