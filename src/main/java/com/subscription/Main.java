package com.subscription;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;

public class Main {
    public static final String API_KEY = "jea-vito";

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 9094), 0);

        server.createContext("/customers", new CustomerHandler(API_KEY));
        server.createContext("/subscriptions", new SubscriptionHandler());
        server.createContext("/items", new ItemHandler());

        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.println("Server is running on port 9094");
    }
}
