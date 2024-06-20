package main.java.com.subscription;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.com.subscription.utils.ApiException;

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
                if (pathSegments.length == 2) {
                    response = getAllItems();
                } else if (pathSegments.length == 3) {
                    int itemId = Integer.parseInt(pathSegments[2]);
                    response = getItemById(itemId);
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

    private String getAllItems() throws SQLException {
        Connection conn = Database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM items");

        return JsonUtil.resultSetToJson(rs);
    }

    private String getItemById(int itemId) throws SQLException, ApiException {
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM items WHERE id = ?");
        stmt.setInt(1, itemId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return JsonUtil.resultSetToJson(rs);
        } else {
            throw new ApiException(404, "main.java.com.subscription.models.Item not found");
        }
    }

    private String createItem(String requestBody) throws SQLException, ApiException {
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

    private String updateItem(int itemId, String requestBody) throws SQLException, ApiException {
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
}
