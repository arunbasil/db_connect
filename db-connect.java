import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AccountComparison {

    // Logger setup
    private static final Logger logger = Logger.getLogger(AccountComparison.class.getName());

    public static void main(String[] args) {
        Set<String> accountsFromDb1 = fetchAccountsFromDatabase("jdbc:postgresql://localhost:5432/db1", "user1", "password1");
        Set<String> accountsFromDb2 = fetchAccountsFromDatabase("jdbc:postgresql://localhost:5432/db2", "user2", "password2");

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
