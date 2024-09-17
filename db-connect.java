public class DatabaseSteps {

    private ResultSet pgResultSet;
    private ResultSet db2ResultSet;

    @When("I fetch data from both databases")
    public void fetchData() throws Exception {
        // Fetch data from PostgreSQL
        Statement pgStatement = pgConnection.createStatement();
        pgResultSet = pgStatement.executeQuery("SELECT account_no FROM public.bst_account LIMIT 5;");
        while (pgResultSet.next()) {
            logger.info("PostgreSQL Data: {}", pgResultSet.getString("account_no"));
        }

        // Fetch data from Db2
        Statement db2Statement = db2Connection.createStatement();
        db2ResultSet = db2Statement.executeQuery("SELECT account_no FROM ISGEND1.BST_ACCOUNT WHERE ACCOUNT_TYPE ='C' AND DATE(LAST_UPDT_TSTAMP) >= DATE('2023-12-13') FETCH FIRST 5 ROWS ONLY");
        while (db2ResultSet.next()) {
            logger.info("Db2 Data: {}", db2ResultSet.getString("account_no"));
        }
    }

    @Then("I should be able to compare the data successfully")
    public void compareData() throws SQLException {
        if (pgResultSet == null || db2ResultSet == null) {
            logger.error("Failed to fetch accounts from one or both databases.");
            return;
        }

        try {
            // Convert ResultSets to Lists
            List<String> pgAccounts = resultSetToList(pgResultSet);
            List<String> db2Accounts = resultSetToList(db2ResultSet);

            // Find accounts missing in DB2 (while preserving duplicates)
            List<String> missingInDb2 = pgAccounts.stream()
                .filter(account -> !db2Accounts.remove(account))
                .collect(Collectors.toList());

            // Find accounts missing in PG (while preserving duplicates)
            List<String> missingInPg = db2Accounts.stream()
                .filter(account -> !pgAccounts.remove(account))
                .collect(Collectors.toList());

            // Log missing accounts
            missingInDb2.forEach(account -> logger.info("Account missing in DB2: " + account));
            missingInPg.forEach(account -> logger.info("Account missing in PG: " + account));
            
            logger.info("Data comparison completed successfully.");

        } catch (SQLException e) {
            logger.error("Error processing ResultSet", e);
        }
    }

    // Helper method to convert ResultSet to List
    private List<String> resultSetToList(ResultSet resultSet) throws SQLException {
        List<String> resultSetData = new ArrayList<>();
        while (resultSet.next()) {
            // Assuming the column name is "account_no"
            resultSetData.add(resultSet.getString("account_no"));
        }
        return resultSetData;
    }
}
