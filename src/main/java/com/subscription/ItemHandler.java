package com.subscription;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.subscription.utils.ApiException;
import com.subscription.utils.JsonUtil;
import com.subscription.utils.Authentication;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class ItemHandler implements HttpHandler {
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
                    int itemId = Integer.parseInt(pathSegments[2]);
                    response = getItemById(itemId);
                } else if (pathSegments.length == 2 && pathSegments[1].equals("items")) {
                    String query = exchange.getRequestURI().getQuery();
                    String isActiveParam = null;

                    // Parse query parameters
                    if (query != null) {
                        String[] queryParams = query.split("&");
                        for (String param : queryParams) {
                            String[] pair = param.split("=");
                            if (pair.length == 2 && pair[0].equals("is_active")) {
                                isActiveParam = pair[1];
                                break;
                            }
                        }
                    }

                    response = getAllItems(isActiveParam);
                }
            } else if (method.equals("POST")) {
                if (pathSegments.length == 2) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    response = createItem(requestBody);
                }
            } else if (method.equals("PUT")) {
                if (pathSegments.length == 3) {
                    int itemId = Integer.parseInt(pathSegments[2]);
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    response = updateItem(itemId, requestBody);
                }
            } else if (method.equals("DELETE")) {
                if (pathSegments.length == 3 && pathSegments[1].equals("items")) {
                    int itemId = Integer.parseInt(pathSegments[2]);
                    response = deleteItem(itemId);
                } else {
                    JsonUtil.sendResponse(exchange, 404, "Not Found");
                    return;
                }
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

    private String getAllItems(String isActiveParam) throws SQLException {
        String query = "SELECT * FROM items";

        if (isActiveParam != null && !isActiveParam.isEmpty()) {
            boolean isActive = Boolean.parseBoolean(isActiveParam);
            query += " WHERE is_active = " + (isActive ? 1 : 0);
        }

        Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        return JsonUtil.resultSetToJson(rs);
    }


    private String getItemById(int itemId) throws SQLException, ApiException {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM items WHERE id = ?");
        stmt.setInt(1, itemId);
        ResultSet rs = stmt.executeQuery();

            return JsonUtil.resultSetToJson(rs);
    }

    private String createItem(String requestBody) throws SQLException, ApiException, IOException {
        Map<String, Object> itemData = JsonUtil.jsonToMap(requestBody);
        if (!itemData.containsKey("name") || !itemData.containsKey("price") || !itemData.containsKey("type")) {
            throw new ApiException(400, "Missing required fields");
        }

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO items (name, price, type, is_active) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, (String) itemData.get("name"));
        stmt.setDouble(2, (double) itemData.get("price"));
        stmt.setString(3, (String) itemData.get("type"));
        stmt.setInt(4, (int) itemData.getOrDefault("is_active", 1));

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new ApiException(500, "Creating item failed, no rows affected");
        }

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            return "{ \"id\": " + generatedKeys.getInt(1) + " }";
        } else {
            throw new ApiException(500, "Creating item failed, no ID obtained");
        }
    }

    private String updateItem(int itemId, String requestBody) throws SQLException, ApiException, IOException {
        Map<String, Object> itemData = JsonUtil.jsonToMap(requestBody);
        if (itemData.isEmpty()) {
            throw new ApiException(400, "Missing fields to update");
        }

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE items SET name = ?, price = ?, type = ?, is_active = ? WHERE id = ?"
        );
        stmt.setString(1, (String) itemData.getOrDefault("name", ""));
        stmt.setDouble(2, (double) itemData.getOrDefault("price", 0.0));
        stmt.setString(3, (String) itemData.getOrDefault("type", ""));
        stmt.setInt(4, (int) itemData.getOrDefault("is_active", 1));
        stmt.setInt(5, itemId);

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new ApiException(404, "main.java.com.subscription.models.Item not found");
        }

        return "{ \"id\": " + itemId + " }";
    }

    private String deleteItem(int itemId) throws SQLException, ApiException {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM items WHERE id = ?");
        stmt.setInt(1, itemId);

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new ApiException(404, "Item not found");
        }

        return "{ \"message\": \"Item deleted successfully\" }";
    }
}
