package com.subscription;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.subscription.utils.JsonUtil;
import com.subscription.utils.ApiException;
import com.subscription.utils.Authentication;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
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
        String query = exchange.getRequestURI().getQuery();
        String[] pathSegments = path.split("/");
        String response = "";

        try {
            if (method.equals("GET")) {
                System.out.println("Path segments: " + Arrays.toString(pathSegments));
                if (pathSegments.length == 3 && pathSegments[1].equals("customers")) {
                    int customerId = Integer.parseInt(pathSegments[2]);
                    response = getCustomerById(customerId);
                } else if (pathSegments.length == 4 && pathSegments[1].equals("customers")) {
                    int customerId = Integer.parseInt(pathSegments[2]);
                    if (pathSegments[3].equals("cards")) {
                        response = getCustomerCards(customerId);
                    } else if (pathSegments[3].equals("subscriptions")) {
                        if (query != null && query.startsWith("subscription_status=")) {
                            String status = query.split("=")[1];
                            response = getCustomerSubscriptionsByStatus(customerId, status);
                        } else {
                            response = getCustomerSubscriptions(customerId);
                        }
                    }
                } else if (pathSegments.length == 2 && pathSegments[1].equals("customers")) {
                    response = getAllCustomers();
                } else {
                    JsonUtil.sendResponse(exchange, 404, "Not Found"); // Handle jika path tidak sesuai
                    return;
                }
            } else if (method.equals("POST")) {
                if (pathSegments.length == 2 && pathSegments[1].equals("customers")) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    response = createCustomer(requestBody);
                }
            } else if (method.equals("PUT")) {
                if (pathSegments.length == 3 && pathSegments[1].equals("customers")) {
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
            e.printStackTrace(); // Tambahkan logging untuk detail exception
            JsonUtil.sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private String getCustomerSubscriptions(int customerId) throws SQLException, ApiException{
        String query = "SELECT * FROM subscriptions WHERE customer = ?";
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        return JsonUtil.resultSetToJson(rs);
    }

    private String getAllCustomers() throws SQLException {
        Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM customers");

        return JsonUtil.resultSetToJson(rs);
    }

    private String getCustomerCards(int customerId) throws SQLException, ApiException {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cards WHERE customer = ?");
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        return JsonUtil.resultSetToJson(rs);
    }

    private String getCustomerSubscriptionsByStatus(int customerId, String status) throws SQLException, ApiException {
        String query = "SELECT * FROM subscriptions WHERE customer = ?";
        if (status != null && !status.isEmpty()) {
            switch (status.toLowerCase()) {
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
                    throw new ApiException(400, "Invalid subscription status filter: " + status);
            }
        }

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        return JsonUtil.resultSetToJson(rs);
    }


    private String getCustomerById(int customerId) throws SQLException, ApiException {
        System.out.println("Fetching customer with ID: " + customerId);
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customers WHERE id = ?");
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        return JsonUtil.resultSetToJson(rs);
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
