package com.subscription;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.subscription.utils.JsonUtil;
import com.subscription.utils.ApiException;
import com.subscription.utils.Authentication;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class CustomerHandler implements HttpHandler {
    private final String apiKey;

    public  CustomerHandler(String apiKey){
        this.apiKey = apiKey;
    }
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
                if (pathSegments.length == 3 && pathSegments[1].equals("customers")) {
                    int customerId = Integer.parseInt(pathSegments[2]);
                    if (exchange.getRequestURI().getPath().endsWith("/cards")) {
                        response = getCustomerCards(customerId);
                    } else if (exchange.getRequestURI().getPath().endsWith("/subscriptions")) {
                        String subscriptionStatus = exchange.getRequestURI().getQuery();
                        response = getCustomerSubscriptions(customerId, subscriptionStatus);
                    } else {
                        response = getCustomerById(customerId);
                    }
                } else if (pathSegments.length == 2) {
                    response = getAllCustomers();
                }
            } else if (method.equals("POST")) {
                if (pathSegments.length == 2) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    response = createCustomer(requestBody);
                }
            } else if (method.equals("PUT")) {
                if (pathSegments.length == 3) {
                    int customerId = Integer.parseInt(pathSegments[2]);
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    response = updateCustomer(customerId, requestBody);
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

    private String getAllCustomers() throws SQLException {
        Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM customers");

        return JsonUtil.resultSetToJson(rs);
    }

    private String getCustomerCards(int customerId) throws SQLException, ApiException {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cards WHERE customer_id = ?");
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        return JsonUtil.resultSetToJson(rs);
    }

    private String getCustomerSubscriptions(int customerId, String subscriptionStatus) throws SQLException, ApiException {
        String query = "SELECT * FROM subscriptions WHERE customer_id = ?";
        if (subscriptionStatus != null) {
            switch (subscriptionStatus) {
                case "active":
                    query += " AND status = 'active'";
                    break;
                case "cancelled":
                    query += " AND status = 'cancelled'";
                    break;
                case "non-renewing":
                    query += " AND status = 'non-renewing'";
                    break;
                default:
                    throw new ApiException(400, "Invalid subscription status filter");
            }
        }

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        return JsonUtil.resultSetToJson(rs);
    }

    private String getCustomerById(int customerId) throws SQLException, ApiException {

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customers WHERE id = ?");
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return JsonUtil.resultSetToJson(rs);
        } else {
            throw new ApiException(404, "Customer not found");
        }
    }

    private String createCustomer(String requestBody) throws SQLException, ApiException, IOException {
        Map<String, Object> customerData = JsonUtil.jsonToMap(requestBody);
        if (!customerData.containsKey("email") || !customerData.containsKey("first_name") || !customerData.containsKey("last_name")) {
            throw new ApiException(400, "Missing required fields");
        }

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO customers (email, first_name, last_name, phone_number) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, (String) customerData.get("email"));
        stmt.setString(2, (String) customerData.get("first_name"));
        stmt.setString(3, (String) customerData.get("last_name"));
        stmt.setString(4, (String) customerData.getOrDefault("phone_number", ""));

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new ApiException(500, "Creating customer failed, no rows affected");
        }

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            return "{ \"id\": " + generatedKeys.getInt(1) + " }";
        } else {
            throw new ApiException(500, "Creating customer failed, no ID obtained");
        }
    }

    private String updateCustomer(int customerId, String requestBody) throws SQLException, ApiException, IOException {
        Map<String, Object> customerData = JsonUtil.jsonToMap(requestBody);
        if (customerData.isEmpty()) {
            throw new ApiException(400, "Missing fields to update");
        }

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE customers SET email = ?, first_name = ?, last_name = ?, phone_number = ? WHERE id = ?"
        );
        stmt.setString(1, (String) customerData.getOrDefault("email", ""));
        stmt.setString(2, (String) customerData.getOrDefault("first_name", ""));
        stmt.setString(3, (String) customerData.getOrDefault("last_name", ""));
        stmt.setString(4, (String) customerData.getOrDefault("phone_number", ""));
        stmt.setInt(5, customerId);

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new ApiException(404, "Customer not found");
        }

        return "{ \"id\": " + customerId + " }";
    }
}
