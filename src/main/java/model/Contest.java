package model;

import java.time.LocalDateTime;

public class Contest {
  private int contestId;
  private String contestName;
  private String description;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String status;

  public Contest(
      int contestId,
      String contestName,
      String description,
      LocalDateTime startTime,
      LocalDateTime endTime,
      String status) {
    this.contestId = contestId;
    this.contestName = contestName;
    this.description = description;
    this.startTime = startTime;
    this.endTime = endTime;
    this.status = status;
  }

  public int getContestId() {
    return contestId;
  }

  public String getContestName() {
    return contestName;
  }

  public String getDescription() {
    return description;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "\nContest ID: "
        + contestId
        + "\nName: "
        + contestName
        + "\nDescription: "
        + description
        + "\nStatus: "
        + status
        + "\n-------------------------";
  }
}
