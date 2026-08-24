import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private static final Map<String, String> LEADS = new ConcurrentHashMap<>();
    private static final List<String> LEAD_ORDER = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        int port = 8003;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/health", Main::health);
        server.createContext("/api/leads", Main::leads);
        server.setExecutor(null);
        server.start();
        System.out.println("Aurum Java API listening on port " + port);
    }

    private static void health(HttpExchange exchange) throws IOException {
        String body = "{" + quote("status") + ":" + quote("ok") + "," + quote("service") + ":" + quote("aurum-java-api") + "}";
        sendJson(exchange, 200, body);
    }

    private static void leads(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            StringBuilder body = new StringBuilder("[");
            synchronized (LEAD_ORDER) {
                for (int i = 0; i < LEAD_ORDER.size(); i++) {
                    if (i > 0) {
                        body.append(",");
                    }
                    body.append(LEADS.get(LEAD_ORDER.get(i)));
                }
            }
            body.append("]");
            sendJson(exchange, 200, body.toString());
            return;
        }
        if ("POST".equalsIgnoreCase(method)) {
            String raw = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String id = UUID.randomUUID().toString();
            String record = "{" + quote("id") + ":" + quote(id) + "," + quote("createdAt") + ":" + quote(Instant.now().toString()) + "," + quote("payload") + ":" + quote(escapeJson(raw.trim())) + "}";
            LEADS.put(id, record);
            LEAD_ORDER.add(id);
            sendJson(exchange, 201, record);
            return;
        }
        sendJson(exchange, 405, "{" + quote("detail") + ":" + quote("method not allowed") + "}");
    }

    private static void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String quote(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append((char) 34);
        sb.append(value);
        sb.append((char) 34);
        return sb.toString();
    }

    private static String escapeJson(String value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            int code = (int) c;
            if (code == 34) {
                sb.append((char) 92);
                sb.append((char) 34);
            } else if (code == 92) {
                sb.append((char) 92);
                sb.append((char) 92);
            } else if (code == 10) {
                sb.append((char) 92);
                sb.append('n');
            } else if (code == 13) {
                sb.append((char) 92);
                sb.append('r');
            } else if (code == 9) {
                sb.append((char) 92);
                sb.append('t');
            } else if (code < 32) {
                sb.append((char) 92);
                sb.append(String.format("u%04x", code));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
