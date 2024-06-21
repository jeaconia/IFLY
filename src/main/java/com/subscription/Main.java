package com.subscription;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;

public class Main {
    public static final String API_KEY = "your-api-key";

    public static void main(String[] args) throws IOException {
        int port = 9000 + Integer.parseInt(args[0].substring(args[0].length() - 3));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/customers", new CustomerHandler());
        server.createContext("/subscriptions", new SubscriptionHandler());
        server.createContext("/items", new ItemHandler());

        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.println("Server is running on port " + port);
    }
}
