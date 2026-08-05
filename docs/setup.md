# Setup Guide - University Campus Service Hub

This guide walks you through setting up the project on your own PC for the first time.

---

## What you need before you start

You need two things installed on your PC:

| Tool | What it is | Why you need it |
| --- | --- | --- |
| Java JDK (version 17 or newer) | The compiler and runtime for Java | To compile and run the Java code |
| Maven (version 3.6 or newer) | A build tool for Java projects | To compile, test and package the project with one command |

You can check if a tool is already installed by opening a terminal (Command Prompt or PowerShell) and typing its version command:

```
java -version
mvn -version
```

If you get a message like `'java' is not recognized...`, that tool is not installed (or not on your PATH) - install it using the steps below.

---

## Install the tools

### 1. Install Java (JDK 17 or newer)

1. Go to <https://adoptium.net> and download the **Temurin** JDK for your operating system (choose the latest LTS version, e.g. 17 or 21).
2. Run the installer and click through with the default settings.
3. After installing, open a **new** terminal and check:
   ```
   java -version
   ```
   You should see something like `openjdk version "17..."`.

If the command is not found, add the `bin` folder of your JDK to the system `PATH` (see Troubleshooting).

### 2. Install Maven

1. Go to <https://maven.apache.org/download.cgi> and download the **Binary zip archive** (e.g. `apache-maven-3.9.9-bin.zip`).
2. Extract the zip to a folder you can remember, for example:
   - Windows: `C:\apache-maven`
   - macOS/Linux: `~/apache-maven`
3. Add Maven's `bin` folder to your system `PATH` (see Troubleshooting).
4. Open a **new** terminal and check:
   ```
   mvn -version
   ```
   You should see output starting with `Apache Maven 3.9.x`.



The Java (Maven) project lives in the `University-Campus-Service-Hub` folder. Every Maven command below is run from **inside** that folder:

```
cd University-Campus-Service-Hub
```

---

## Build, test and run

All commands in this section are run from the `University-Campus-Service-Hub` folder.

### Build the project (compile + test + package)

```
mvn package
```

This does everything in one go:
1. Compiles the Java source files.
2. Runs the unit tests.
3. Packages the application into a `.jar` file.

When it finishes, look for:

```
BUILD SUCCESS
```

The jar file is created at:

```
University-Campus-Service-Hub\target\service-hub-0.1.0-SNAPSHOT.jar
```

### Run only the tests

```
mvn test
```

Look for output like `Tests run: 1, Failures: 0, Errors: 0`. If a test fails, the build will end with `BUILD FAILURE` and show you which test and why.

### Run the application

The application's main class is `servicehub.Main`. Run it with:

```
mvn exec:java
```

You should see something like:

```
=== University Campus Service Hub ===
Database connected and schema initialized
```

The program connects to the SQLite database at `db/service_hub.db` (the `db` folder at the repo root) and creates the tables if they do not exist.

### Running the packaged jar (optional)

If you prefer to run the jar directly after `mvn package`, you can use:

```
mvn exec:java
```

Do not try `java -jar target/service-hub-0.1.0-SNAPSHOT.jar` on its own - the database driver is a separate dependency, so that command will fail. Use `mvn exec:java` instead.

---

## Troubleshooting

**`'java' is not recognized as the name of a cmdlet...` (or similar)**
Java is not on your PATH. Find your JDK install folder (e.g. `C:\Program Files\Eclipse Adoptium\jdk-17...\bin`) and add it to PATH:
- Windows: System Properties -> Environment Variables -> edit `Path` -> add the `bin` folder -> OK, then open a new terminal.
- macOS/Linux: add to `~/.zshrc` or `~/.bashrc`: `export PATH="$PATH:/path/to/jdk/bin"`, then `source ~/.zshrc`.

**`'mvn' is not recognized...`**
Maven's `bin` folder is not on your PATH. Add it the same way as above (e.g. `C:\apache-maven\bin`), then open a new terminal.

**`BUILD FAILURE` during `mvn test`**
Read the error near the top of the failure report - it names the test and the reason. If it mentions a missing file, make sure you are running the command from the correct folder (section 3).

**`BUILD FAILURE` with an "Exception in thread main" when running**
Make sure the `db` folder exists at the repo root (it is created in the repo layout). The program writes the database file there.

**First build is slow / downloads a lot**
That is normal. Maven downloads the dependencies (SQLite JDBC driver, JUnit) on the first run and stores them in your local cache (`~/.m2`). Later builds are faster.

**I still cannot build**
Ask a team member who has it working, or post in the project group with the full error message from the terminal.

---

## Quick command cheat sheet

Run from the `University-Campus-Service-Hub` folder:

| What you want | Command |
| --- | --- |
| Run the tests | `mvn test` |
| Build the jar (compile + test + package) | `mvn package` |
| Run the application | `mvn exec:java` |
| Compile only (no tests) | `mvn compile` |
| Remove the generated `target/` folder | `mvn clean` |
