# Programming Contest Evaluation and Leaderboard System

## 1. Project Overview

The **Programming Contest Evaluation and Leaderboard System** is a Java-based application designed to conduct programming contests, accept Java code submissions, evaluate them against predefined test cases, and rank participants based on their scores.

The system automates the evaluation process and provides participants with their submission results and leaderboard rankings.

## 2. Objectives

- Manage users and programming contests.
- Store programming questions and test cases.
- Accept Java source-code submissions.
- Automatically compile and execute submitted programs.
- Evaluate outputs against expected results.
- Calculate scores and percentages.
- Generate a ranked leaderboard.

## 3. Technologies Used

| Technology | Purpose |
|---|---|
| Java | Application development |
| Java Swing | Graphical user interface |
| JDBC | Database connectivity |
| MariaDB | Relational database |
| Maven | Dependency and build management |
| Java Collections | Managing application data |
| Java Stream API | Sorting and processing leaderboard data |
| File Handling | Saving submitted source code |
| ProcessBuilder | Compiling and executing Java submissions |

## 4. System Architecture

The application follows a layered architecture.

1. **Frontend Layer:** Provides the user interface through Java Swing and a command-line interface.
2. **Service Layer:** Handles user management, contests, questions, submissions, evaluation and leaderboard operations.
3. **Model Layer:** Represents entities such as User, Contest, Question, TestCase and Submission.
4. **Database Layer:** Connects to MariaDB through JDBC and stores application data.

## 5. Main Modules

### User Management
- User registration and login.
- Password hashing and verification.

### Contest Management
- Create and retrieve programming contests.
- Store contest details and status.

### Question Management
- Add programming questions.
- Store descriptions, input formats, output formats and maximum marks.

### Test Case Management
- Store input and expected output for each question.
- Assign marks to individual test cases.

### Submission Management
- Accept Java source code.
- Save submissions as Java files.
- Maintain submission history and status.

### Evaluation Engine
- Compile submitted Java programs.
- Execute programs against predefined test cases.
- Compare actual and expected outputs.
- Calculate marks based on passed test cases.

### Leaderboard
- Calculate participants' total scores.
- Calculate percentages.
- Sort participants using Java Streams.
- Display rankings.

## 6. Database Design

The system uses MariaDB with the following main tables:

- `users` – Stores participant and account information.
- `contests` – Stores contest details.
- `questions` – Stores programming questions.
- `test_cases` – Stores test inputs, expected outputs and marks.
- `submissions` – Stores submission details, scores and evaluation status.

The tables are connected through primary keys and foreign keys.

## 7. Evaluation and Score Calculation

Each submitted program is compiled and executed against the test cases associated with its question.

The score is calculated by adding the marks of successfully passed test cases.

**Percentage formula:**

Percentage = (Total Score / Maximum Possible Score) × 100

The leaderboard sorts participants based on their total scores.

## 8. Project Structure

```text
ProgrammingContestSystem/
├── pom.xml
├── README.md
├── submissions/
├── testcases/
└── src/main/java/
    ├── Main.java
    ├── database/
    ├── model/
    ├── service/
    └── frontend/
        └── ContestApp.java
```

## 9. How to Run

### Prerequisites
- Java JDK 17 or later
- Maven
- MariaDB

### Step 1: Start MariaDB

```bash
sudo service mariadb start
```

### Step 2: Configure the Database

Create the `programming_contest` database and configure the database connection details in:

```text
src/main/java/database/DBConnection.java
```

Ensure that the required tables and relationships are created before running the application.

### Step 3: Compile the Project

```bash
mvn clean compile
```

### Step 4: Run the Command-Line Application

```bash
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=Main
```

### Step 5: Run the Swing Application

On a machine with a graphical desktop:

```bash
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=frontend.ContestApp
```

## 10. Important Note

The current evaluation engine uses Java ProcessBuilder to compile and execute submitted programs. It is intended for a controlled academic demonstration using trusted submissions. It does not provide a secure sandbox for arbitrary untrusted code.

## 11. Conclusion

The project demonstrates how Java programming, database connectivity, file handling, collections, streams and process execution can be integrated to build an automated programming contest evaluation system.

It reduces manual evaluation effort and provides an organized way to manage programming contests, submissions, scores and participant rankings.