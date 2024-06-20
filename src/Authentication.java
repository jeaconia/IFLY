public class Authentication {
    public static boolean isAuthenticated(String apiKey) {
        return Main.API_KEY.equals(apiKey);
    }
}
