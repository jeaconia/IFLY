package main.java.com.subscription.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
    private static final Gson gson = new Gson();

    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    public static Map<String, Object> jsonToMap(String json) {
        return gson.fromJson(json, HashMap.class);
    }

    public static String mapToJson(Map<String, Object> map) {
        return gson.toJson(map);
    }

    public static String resultSetToJson(ResultSet rs) throws SQLException {
        JsonObject jsonObject = new JsonObject();
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();

        int i = 0;
        while (rs.next()) {
            JsonObject row = new JsonObject();
            for (int c = 1; c <= columnCount; ++c) {
                row.addProperty(metadata.getColumnName(c), rs.getString(c));
            }
            jsonObject.add(String.valueOf(++i), row);
        }

        return jsonObject.toString();
    }
}
