If you are using a Gradle project with Cucumber to run tests that connect to both PostgreSQL and IBM Db2 databases, you can follow these steps to set up the necessary dependencies, configurations, and code to run your Cucumber tests with database connections.

### Step-by-Step Guide for Setting Up a Gradle Project with Cucumber

#### 1. **Create a New Gradle Project**

If you don't already have a Gradle project, you can create a new one using the following command:

```sh
gradle init --type java-application
```

This will create a basic Java project with Gradle.

#### 2. **Configure `build.gradle`**

You need to set up your `build.gradle` file to include the necessary dependencies for Cucumber, PostgreSQL, and IBM Db2.

Here's an example `build.gradle` configuration:

```groovy
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    // Cucumber dependencies
    testImplementation 'io.cucumber:cucumber-java:7.11.0' // Check for the latest version
    testImplementation 'io.cucumber:cucumber-junit:7.11.0' // Check for the latest version
    testImplementation 'io.cucumber:cucumber-spring:7.11.0' // Optional, if using Spring context

    // PostgreSQL JDBC Driver
    implementation 'org.postgresql:postgresql:42.2.24' // Check for the latest version

    // IBM Db2 JDBC Driver
    implementation 'com.ibm.db2:jcc:11.5.7.0' // Check for the correct version for your environment

    // JUnit for running tests
    testImplementation 'junit:junit:4.13.2'
}

test {
    useJUnit()
}
```

#### 3. **Create Cucumber Feature Files**

Create a directory for your Cucumber feature files under `src/test/resources`:

```
src
 └── test
     └── resources
         └── features
             └── database.feature
```

**Example `database.feature` file:**

```gherkin
Feature: Database connectivity and data comparison

  Scenario: Fetch and compare data from PostgreSQL and Db2 databases
    Given I connect to PostgreSQL database
    And I connect to Db2 database
    When I fetch data from both databases
    Then I should be able to compare the data successfully
```

#### 4. **Create Cucumber Step Definitions**

Create a directory for your step definitions under `src/test/java`:

```
src
 └── test
     └── java
         └── steps
             └── DatabaseSteps.java
```

**Example `DatabaseSteps.java` file:**

```java
package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseSteps {

    private Connection pgConnection;
    private Connection db2Connection;

    @Given("I connect to PostgreSQL database")
    public void connectToPostgres() throws Exception {
        String pgUrl = "jdbc:postgresql://your_postgres_host:5432/your_postgres_database";
        String pgUser = "your_postgres_user";
        String pgPassword = "your_postgres_password";

        pgConnection = DriverManager.getConnection(pgUrl, pgUser, pgPassword);
        System.out.println("Connected to PostgreSQL database");
    }

    @Given("I connect to Db2 database")
    public void connectToDb2() throws Exception {
        String db2Url = "jdbc:db2://your_db2_host:50000/your_db2_database";
        String db2User = "your_db2_user";
        String db2Password = "your_db2_password";

        db2Connection = DriverManager.getConnection(db2Url, db2User, db2Password);
        System.out.println("Connected to Db2 database");
    }

    @When("I fetch data from both databases")
    public void fetchData() throws Exception {
        // Fetch data from PostgreSQL
        Statement pgStatement = pgConnection.createStatement();
        ResultSet pgResultSet = pgStatement.executeQuery("SELECT * FROM your_postgres_table LIMIT 10");
        while (pgResultSet.next()) {
            System.out.println("PostgreSQL Data: " + pgResultSet.getString("column_name"));
        }

        // Fetch data from Db2
        Statement db2Statement = db2Connection.createStatement();
        ResultSet db2ResultSet = db2Statement.executeQuery("SELECT * FROM your_db2_table FETCH FIRST 10 ROWS ONLY");
        while (db2ResultSet.next()) {
            System.out.println("Db2 Data: " + db2ResultSet.getString("column_name"));
        }
    }

    @Then("I should be able to compare the data successfully")
    public void compareData() {
        if (accountsFromDb1 == null || accountsFromDb2 == null) {
            logger.severe("Failed to fetch accounts from one or both databases.");
            return;
        }

        // Find accounts missing in DB2
        Set<String> missingInDb2 = accountsFromDb1.stream()
                .filter(account -> !accountsFromDb2.contains(account))
                .collect(Collectors.toSet());

        // Find accounts missing in DB1
        Set<String> missingInDb1 = accountsFromDb2.stream()
                .filter(account -> !accountsFromDb1.contains(account))
                .collect(Collectors.toSet());

        // Log missing accounts
        missingInDb2.forEach(account -> logger.info("Account missing in DB2: " + account));
        missingInDb1.forEach(account -> logger.info("Account missing in DB1: " + account));
    }

    // Fetch accounts from a specific database
    private static Set<String> fetchAccountsFromDatabase(String url, String username, String password) {
        Set<String> accountNumbers = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT account_no FROM accounts")) {

            while (resultSet.next()) {
                accountNumbers.add(resultSet.getString("account_no"));
            }

        } catch (Exception e) {
            logger.severe("Error fetching accounts from database: " + e.getMessage());
            return null;
        }

        return accountNumbers;
    }
}
        System.out.println("Data comparison completed successfully");
    }
}
```

#### 5. **Create a Cucumber Runner Class**

Create a directory for your test runners under `src/test/java`:

```
src
 └── test
     └── java
         └── runners
             └── RunCucumberTest.java
```

**Example `RunCucumberTest.java` file:**

```java
package runners;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "steps"
)
public class RunCucumberTest {
}
```

#### 6. **Run the Cucumber Tests**

To run the Cucumber tests, execute the following command from the root directory of your project:

```sh
./gradlew test
```

### Explanation:

1. **Cucumber Dependencies:**
   - `cucumber-java`, `cucumber-junit`, and `cucumber-spring` provide the necessary dependencies for running Cucumber tests in Java.

2. **Database Connections:**
   - The step definitions in `DatabaseSteps.java` include methods to connect to both PostgreSQL and IBM Db2 databases using JDBC.

3. **Feature File:**
   - The Cucumber feature file (`database.feature`) defines the scenarios and steps for database connectivity and data comparison.

4. **Runner Class:**
   - `RunCucumberTest.java` is the test runner class configured to run the Cucumber tests using JUnit.

### Notes:

- Make sure the JDBC drivers for PostgreSQL and IBM Db2 are correctly included in your classpath.
- Update the database connection details in `DatabaseSteps.java` with your actual credentials and host information.
- You can add more scenarios and steps to the Cucumber feature file to expand your test coverage.

By following these steps, you will have a Gradle project set up with Cucumber to connect to both PostgreSQL and IBM Db2 databases and perform data comparison as needed.
