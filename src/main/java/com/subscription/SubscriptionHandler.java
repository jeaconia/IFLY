package com.subscription;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.subscription.utils.ApiException;
import com.subscription.utils.JsonUtil;
import com.subscription.utils.Authentication;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class SubscriptionHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String apiKey = exchange.getRequestHeaders().getFirst("API-Key");
        if (!Authentication.isAuthenticated(apiKey)) {
            JsonUtil.sendResponse(exchange, 401, "Unauthorized");
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathSegments = path.split("/");
        String response = "";

        try {
            if (method.equals("GET")) {
                if (pathSegments.length == 3) {
                    int subscriptionId = Integer.parseInt(pathSegments[2]);
                    response = getSubscriptionById(subscriptionId);
                } else if (pathSegments.length == 2 && pathSegments[1].equals("subscriptions")) {
                    String query = exchange.getRequestURI().getQuery();
                    String sortBy = null;
                    String sortType = null;

                    // Parse query parameters
                    if (query != null) {
                        String[] queryParams = query.split("&");
                        for (String param : queryParams) {
                            String[] pair = param.split("=");
                            if (pair.length == 2) {
                                if (pair[0].equals("sort_by")) {
                                    sortBy = pair[1];
                                } else if (pair[0].equals("sort_type")) {
                                    sortType = pair[1];
                                }
                            }
                        }
                    }

                    response = getAllSubscriptions(sortBy, sortType);
                }
            } else if (method.equals("POST")) {
                if (pathSegments.length == 2) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    response = createSubscription(requestBody);
                }
            } else if (method.equals("PUT")) {
                if (pathSegments.length == 3) {
                    int subscriptionId = Integer.parseInt(pathSegments[2]);
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    response = updateSubscription(subscriptionId, requestBody);
                }
            } else if (method.equals("DELETE")) {
                JsonUtil.sendResponse(exchange, 405, "Method Not Allowed");
                return;
            } else {
                JsonUtil.sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            JsonUtil.sendResponse(exchange, 200, response);
        } catch (ApiException e) {
            JsonUtil.sendResponse(exchange, e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private String getAllSubscriptions(String sortBy, String sortType) throws SQLException, ApiException {
        String query = "SELECT * FROM subscriptions";

        if (sortBy != null && sortType != null) {
            // Validate sort type (asc or desc)
            if (!sortType.equalsIgnoreCase("asc") && !sortType.equalsIgnoreCase("desc")) {
                throw new ApiException(400, "Invalid sort type. Should be 'asc' or 'desc'.");
            }

            // Sort by current_term_end
            query += " ORDER BY " + sortBy + " " + sortType;
        }

        Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        return JsonUtil.resultSetToJson(rs);
    }


    private String getSubscriptionById(int subscriptionId) throws SQLException, ApiException {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM subscriptions WHERE id = ?");
        stmt.setInt(1, subscriptionId);
        ResultSet rs = stmt.executeQuery();

        return JsonUtil.resultSetToJson(rs);
    }

    private String createSubscription(String requestBody) throws SQLException, ApiException {
        Map<String, Object> subscriptionData = null;
        try {
            subscriptionData = JsonUtil.jsonToMap(requestBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!subscriptionData.containsKey("customer") || !subscriptionData.containsKey("billing_period") || !subscriptionData.containsKey("billing_period_unit") || !subscriptionData.containsKey("total_due") || !subscriptionData.containsKey("activated_at") || !subscriptionData.containsKey("current_term_start") || !subscriptionData.containsKey("current_term_end") || !subscriptionData.containsKey("status")) {
            throw new ApiException(400, "Missing required fields");
        }

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO subscriptions (customer, billing_period, billing_period_unit, total_due, activated_at, current_term_start, current_term_end, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, (int) subscriptionData.get("customer"));
        stmt.setInt(2, (int) subscriptionData.get("billing_period"));
        stmt.setString(3, (String) subscriptionData.get("billing_period_unit"));
        stmt.setDouble(4, (double) subscriptionData.get("total_due"));
        stmt.setString(5, (String) subscriptionData.get("activated_at"));
        stmt.setString(6, (String) subscriptionData.get("current_term_start"));
        stmt.setString(7, (String) subscriptionData.get("current_term_end"));
        stmt.setString(8, (String) subscriptionData.get("status"));

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new ApiException(500, "Creating subscription failed, no rows affected");
        }

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            return "{ \"id\": " + generatedKeys.getInt(1) + " }";
        } else {
            throw new ApiException(500, "Creating subscription failed, no ID obtained");
        }
    }

    private String updateSubscription(int subscriptionId, String requestBody) throws SQLException, ApiException {
        Map<String, Object> subscriptionData = null;
        try {
            subscriptionData = JsonUtil.jsonToMap(requestBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (subscriptionData.isEmpty()) {
            throw new ApiException(400, "Missing fields to update");
        }

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE subscriptions SET customer = ?, billing_period = ?, billing_period_unit = ?, total_due = ?, activated_at = ?, current_term_start = ?, current_term_end = ?, status = ? WHERE id = ?"
        );
        stmt.setInt(1, (int) subscriptionData.getOrDefault("customer", 0));
        stmt.setInt(2, (int) subscriptionData.getOrDefault("billing_period", 0));
        stmt.setString(3, (String) subscriptionData.getOrDefault("billing_period_unit", ""));
        stmt.setDouble(4, (double) subscriptionData.getOrDefault("total_due", 0.0));
        stmt.setString(5, (String) subscriptionData.getOrDefault("activated_at", ""));
        stmt.setString(6, (String) subscriptionData.getOrDefault("current_term_start", ""));
        stmt.setString(7, (String) subscriptionData.getOrDefault("current_term_end", ""));
        stmt.setString(8, (String) subscriptionData.getOrDefault("status", ""));
        stmt.setInt(9, subscriptionId);

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new ApiException(404, "main.java.com.subscription.models.Subscription not found");
        }

        return "{ \"id\": " + subscriptionId + " }";
    }
}
