public record DatabaseConfig(String url, String username, String password) {

    public static DatabaseConfig fromEnvironment() {
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");
        return new DatabaseConfig(url, username, password);
    }
}


