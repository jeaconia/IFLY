package com.subscription.utils;
import com.subscription.Main;

public class Authentication {
    public static boolean isAuthenticated(String apiKey) {
        return Main.API_KEY.equals(apiKey);
    }
}
