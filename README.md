# CodeArena – Programming Contest Evaluation and Leaderboard System

## Overview

CodeArena is a web-based Programming Contest Evaluation and Leaderboard System developed using Java, Spring Boot, Thymeleaf, JDBC, and MariaDB.

The system allows participants to register, log in, view programming contests, solve coding questions, submit Java programs, and receive evaluation results based on predefined test cases.

It also provides an administrator dashboard for managing contests, questions, and test cases, along with a leaderboard that ranks participants according to their performance.

## Objectives

- Provide a platform for conducting programming contests.
- Evaluate Java submissions against predefined test cases.
- Calculate scores based on test-case results.
- Maintain submission history and individual test-case results.
- Rank participants using their best scores.
- Provide administrative controls for contest management.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Core application logic |
| Spring Boot 3.5.6 | Backend application framework |
| Spring MVC | Request handling and routing |
| Thymeleaf | Dynamic web pages |
| HTML and CSS | User interface |
| JDBC | Database connectivity |
| MariaDB | Relational database |
| Maven | Dependency and build management |
| Java Collections | Managing application data |
| Stream API | Data processing and sorting |
| File Handling | Storing submitted Java source files |
| ProcessBuilder | Compiling and executing submissions |

## System Modules

### 1. User Authentication
- User registration and login.
- Password hashing using PBKDF2.
- Session-based authentication.
- Role-based access for participants and administrators.

### 2. Contest Management
- Display available contests.
- View contest questions and descriptions.
- Access input and output formats for programming problems.

### 3. Submission and Evaluation
- Accept Java source-code submissions.
- Store submitted source files.
- Compile and execute programs using ProcessBuilder.
- Evaluate submissions against predefined test cases.
- Calculate marks for passed test cases.
- Identify compilation errors, incorrect answers, runtime errors, and time-limit violations.

### 4. Submission History
- Display previous submissions.
- Show submission status and scores.
- View individual test-case results.
- Display expected and actual outputs.

### 5. Leaderboard
- Calculate participants' best scores for each question.
- Calculate total scores and percentages.
- Rank participants in descending order of performance.
- Use Java Collections and Stream API for data processing.

### 6. Administrator Dashboard
- Create and manage contests.
- Add, edit, and delete questions.
- Add, edit, and delete test cases.
- Manage contest information through an administrative interface.

## Database Design

The application uses MariaDB with the following tables:

| Table | Purpose |
|---|---|
| users | Stores user accounts and roles |
| contests | Stores programming contest details |
| questions | Stores contest programming questions |
| test_cases | Stores input, expected output, and marks |
| submissions | Stores submission records and evaluation summaries |
| submission_test_results | Stores individual test-case evaluation results |

Foreign-key relationships maintain consistency between contests, questions, test cases, submissions, and users.

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   ├── database/
│   │   │   └── DBConnection.java
│   │   ├── model/
│   │   │   ├── Contest.java
│   │   │   ├── LeaderboardEntry.java
│   │   │   ├── Question.java
│   │   │   ├── Submission.java
│   │   │   ├── SubmissionTestResult.java
│   │   │   ├── TestCase.java
│   │   │   └── User.java
│   │   ├── service/
│   │   │   ├── ContestService.java
│   │   │   ├── EvaluationEngine.java
│   │   │   ├── LeaderboardService.java
│   │   │   ├── QuestionService.java
│   │   │   ├── SubmissionService.java
│   │   │   ├── SubmissionTestResultService.java
│   │   │   ├── TestCaseService.java
│   │   │   └── UserService.java
│   │   └── web/
│   │       ├── AdminController.java
│   │       ├── ApiController.java
│   │       ├── AuthController.java
│   │       ├── BrowserApplication.java
│   │       ├── ContestController.java
│   │       ├── DashboardController.java
│   │       ├── HealthController.java
│   │       ├── LeaderboardController.java
│   │       └── SubmissionController.java
│   └── resources/
│       ├── templates/
│       │   ├── admin.html
│       │   ├── contest-details.html
│       │   ├── dashboard.html
│       │   ├── leaderboard.html
│       │   ├── login.html
│       │   ├── register.html
│       │   ├── submission-details.html
│       │   ├── submission-history.html
│       │   └── submission-result.html
│       └── application.properties
└── test/
```

## Setup and Execution

### Prerequisites

- Java 17 or later
- Maven
- MariaDB
- Git

### 1. Clone the repository

```bash
git clone <repository-url>
cd Programming-Contest-Evaluation-and-Leaderboard-System
```

### 2. Start MariaDB

```bash
sudo service mariadb start
```

### 3. Configure the database

Create the database and configure the required tables and relationships.

The application expects these environment variables:

```bash
export DB_URL="jdbc:mariadb://localhost:3306/programming_contest"
export DB_USER="cbp_user"
export DB_PASSWORD="your_database_password"
```

Replace the password with the password configured for your MariaDB user.

### 4. Run the application

```bash
mvn spring-boot:run
```

### 5. Open the application

Visit:

```text
http://localhost:8080
```

In GitHub Codespaces, open the forwarded port 8080.

## Important Implementation Notes

- Database operations are performed using JDBC.
- Java source files are stored in the application's submissions directory.
- Evaluation uses Java compiler and execution processes through ProcessBuilder.
- The leaderboard uses the best submission score for each question.
- Database relationships help maintain consistency between application records.

**Execution Security:** The current evaluator executes submitted Java programs directly on the host machine. It is intended for controlled demonstrations with trusted submissions, not public deployment with arbitrary untrusted code. Production deployment would require an isolated execution environment and additional security controls.

## Conclusion

CodeArena demonstrates the integration of Java programming, database connectivity, web application development, file handling, collections, and algorithmic evaluation in a programming contest platform. It provides participants with automated submission evaluation and performance rankings while allowing administrators to manage contest activities.