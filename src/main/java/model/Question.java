package model;

public class Question {
    private int questionId;
    private int contestId;
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private int maxMarks;

    public Question(int questionId, int contestId, String title,
                    String description, String inputFormat,
                    String outputFormat, int maxMarks) {
        this.questionId = questionId;
        this.contestId = contestId;
        this.title = title;
        this.description = description;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.maxMarks = maxMarks;
    }

    public int getQuestionId() { return questionId; }
    public int getContestId() { return contestId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getInputFormat() { return inputFormat; }
    public String getOutputFormat() { return outputFormat; }
    public int getMaxMarks() { return maxMarks; }

    @Override
    public String toString() {
        return "\nQuestion ID: " + questionId
                + "\nTitle: " + title
                + "\nDescription: " + description
                + "\nMaximum Marks: " + maxMarks
                + "\n-------------------------";
    }
}