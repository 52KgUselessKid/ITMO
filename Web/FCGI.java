import com.fastcgi.FCGIInterface;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

public class FCGI {
    static ConcurrentHashMap<String, List<String>> sessions = new ConcurrentHashMap<>();
    static FCGIInterface fcgiInterface = new FCGIInterface();

    static long startTime;

    static PrintWriter logWriter = new PrintWriter(System.out, true);

    public static void main(String[] args) {
        System.setProperty("FCGI_PORT", "9001");

        log("FCGI запущен на порту 9001");

        while (true) {
            try {
                int acceptResult = fcgiInterface.FCGIaccept();
                if (acceptResult < 0) {
                    log("FCGIaccept не сработал");
                    continue;
                }

                startTime = System.currentTimeMillis();
                processRequest();

            } catch (Exception e) {
                log(e.getMessage());
            } finally {
                cleanup();
            }
        }
    }

    static void processRequest() throws IOException {
        String requestMethod = System.getProperty("REQUEST_METHOD");
        String contentType = System.getProperty("CONTENT_TYPE");
        String contentLengthStr = System.getProperty("CONTENT_LENGTH");
        String queryString = System.getProperty("QUERY_STRING");

        log("=== FCGI Request Details ===");
        log("REQUEST_METHOD: " + requestMethod);
        log("CONTENT_TYPE: " + contentType);
        log("CONTENT_LENGTH: " + contentLengthStr);
        log("QUERY_STRING: " + queryString);

        if ("OPTIONS".equals(requestMethod)) {
            sendCorsHeaders();
            sendResponse(200, "OK", "");
            return;
        }

        int contentLength;

        try {
            contentLength = Integer.parseInt(contentLengthStr);
        } catch (NumberFormatException e) {
            log("Неверная длинв: " + contentLengthStr);
            sendErrorResponse(400, "Неверная длинв");
            return;
        }

        if (contentLength > 0) {
            byte[] bodyBytes = new byte[contentLength];
            int totalRead = 0;

            while (totalRead < contentLength) {
                int bytesRead = System.in.read(bodyBytes, totalRead, contentLength - totalRead);
                if (bytesRead == -1) {
                    break;
                }
                totalRead += bytesRead;
            }

            if (totalRead != contentLength) {
                log("Неполный запрос: " + totalRead + " из " + contentLength);
                sendErrorResponse(400, "Неполный запрос");
                return;
            }

            String requestBody = new String(bodyBytes, StandardCharsets.UTF_8);
            log("Запрос: " + requestBody);

            processPointData(requestBody);

        } else {
            log("Пустой запрос");
            sendErrorResponse(400, "Пустой запрос");
        }
    }

    static void processPointData(String requestBody) {
        try {
            PointData pointData = parsePointData(requestBody);

            if (!validatePointData(pointData)) {
                sendErrorResponse(400, "Неверные данные");
                return;
            }

            boolean hit = checkPointHit(pointData);

            String remoteAddr = System.getProperty("REMOTE_ADDR", "unknown");

            String resultData = String.format(
                    Locale.US,
                    "{\"x\": %.2f, \"y\": %.2f, \"r\": %.2f, \"result\": %b, \"timestamp\": %d, \"executionTime\": %d}",
                    pointData.x, pointData.y, pointData.r, hit,
                    System.currentTimeMillis(),
                    System.currentTimeMillis() - startTime
            );

            saveToSession(remoteAddr, resultData);

            String jsonResponse = "{\"success\": true, \"data\": " + resultData + "}";
            sendJsonResponse(200, jsonResponse);
            log("Ответ отправлен: (JSON) = " + jsonResponse);

        } catch (Exception e) {
            log(e.getMessage());
            sendErrorResponse(500, "Ошибка сервера: " + e.getMessage());
        }
    }

    static void sendCorsHeaders() {
        System.out.print("Access-Control-Allow-Origin: *\r\n");
        System.out.print("Access-Control-Allow-Methods: POST, GET, OPTIONS\r\n");
        System.out.print("Access-Control-Allow-Headers: Content-Type, Authorization\r\n");
        System.out.print("Access-Control-Max-Age: 86400\r\n");
    }

    static void sendResponse(int statusCode, String statusText, String body) {
        try {
            System.out.print("Status: " + statusCode + " " + statusText + "\r\n");
            System.out.print("Content-Type: application/json\r\n");
            sendCorsHeaders();

            if (body != null && !body.isEmpty()) {
                System.out.print("Content-Length: " + body.length() + "\r\n");
            }

            System.out.print("\r\n");

            if (body != null && !body.isEmpty()) {
                System.out.print(body);
            }

            System.out.flush();

        } catch (Exception e) {
            log("Ошибка отправки: " + e.getMessage());
        }
    }

    static void sendJsonResponse(int statusCode, String json) {
        sendResponse(statusCode, "OK", json);
    }

    static void sendErrorResponse(int statusCode, String message) {
        String jsonResponse = "{\"success\": false, \"error\": \"" + escapeJson(message) + "\"}";
        sendResponse(statusCode, "Error", jsonResponse);
    }

    private static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    static PointData parsePointData(String json) {
        try {
            json = json.replaceAll("[{}\"\\s]", "");
            String[] pairs = json.split(",");

            double x = 0, y = 0, r = 0;
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    try {
                        switch (key) {
                            case "x": x = Double.parseDouble(value); break;
                            case "y": y = Double.parseDouble(value); break;
                            case "r": r = Double.parseDouble(value); break;
                        }
                    } catch (NumberFormatException e) {
                        log("Error parsing value: " + value);
                    }
                }
            }
            return new PointData(x, y, r);
        } catch (Exception e) {
            log("Ошибка парсинга JSON: " + e.getMessage());
        }
        return null;
    }

    static boolean validatePointData(PointData data) {
        double[] validX = {-5, -4, -3, -2, -1, 0, 1, 2, 3};
        boolean validXValue = false;
        for (double valid : validX) {
            if (Math.abs(data.x - valid) < 1e-6) {
                validXValue = true;
                break;
            }
        }

        if (!validXValue) {
            log("Неверный X: " + data.x);
            return false;
        }

        if (data.y < -5 || data.y > 3) {
            log("Неверный Y: " + data.y);
            return false;
        }

        if (data.r < 2 || data.r > 5) {
            log("Неверный R: " + data.r);
            return false;
        }

        return true;
    }

    static boolean checkPointHit(PointData data) {
        double x = data.x;
        double y = data.y;
        double r = data.r;

        if (x >= 0 && y >= 0 && (x * x + y * y <= r * r)) {
            return true;
        }

        if (x >= -r && x <= 0 && y >= 0) {
            double yLine = 0.5 * x + r / 2.0;
            if (y <= yLine) {
                return true;
            }
        }

        if (x >= 0 && x <= r / 2.0 && y <= 0 && y >= -r) {
            return true;
        }

        return false;
    }

    static void saveToSession(String sessionId, String result) {
        sessions.putIfAbsent(sessionId, new ArrayList<>());
        sessions.get(sessionId).add(result);
        log("Сохранено");
    }

    static void cleanup() {
        try {
            System.out.flush();

        } catch (Exception e) {
            log(e.getMessage());
        }
    }

    static class PointData {
        public final double x, y, r;

        public PointData(double x, double y, double r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }

        @Override
        public String toString() {
            return String.format("PointData{x=%.2f, y=%.2f, r=%.2f}", x, y, r);
        }
    }

    static void log(String message) {
        String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        logWriter.println("[" + time + "] " + message);
    }
}