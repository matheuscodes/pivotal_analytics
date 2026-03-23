package software.matheus.pivotal_analytics;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Minimal in-process HTTP server that simulates the Pivotal Tracker API v5
 * for use in unit tests.
 */
public class TestApiServer {

    private final HttpServer server;
    private final int port;

    private static final String MEMBERS_JSON =
        "[{\"person\":{\"id\":1001,\"name\":\"Alice Test\",\"username\":\"alicetest\"}}]";

    private static final String ITERATION_JSON =
        "[{\"number\":1,\"stories\":[" +
        "{\"id\":1001,\"story_type\":\"feature\",\"url\":\"https://www.pivotaltracker.com/story/show/1001\"," +
        "\"estimate\":3,\"current_state\":\"accepted\",\"name\":\"Test Feature\"," +
        "\"requested_by_id\":1001,\"owner_ids\":[1001]," +
        "\"created_at\":\"2023-01-01T00:00:00Z\",\"accepted_at\":\"2023-01-10T00:00:00Z\"," +
        "\"labels\":[{\"id\":1,\"name\":\"[1]\"}]}," +
        "{\"id\":1002,\"story_type\":\"bug\",\"url\":\"https://www.pivotaltracker.com/story/show/1002\"," +
        "\"current_state\":\"started\",\"name\":\"Test Bug\"," +
        "\"requested_by_id\":1001,\"owner_ids\":[1001]," +
        "\"created_at\":\"2023-01-05T00:00:00Z\",\"labels\":[]}" +
        "]}]";

    private static final String PROJECT_JSON =
        "{\"id\":99999,\"name\":\"Test Project\",\"account_id\":100001," +
        "\"start_date\":\"2023-01-01\",\"current_iteration_number\":10,\"iteration_length\":2}";

    private static final String STORIES_JSON = "[]";

    public TestApiServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();

        server.createContext("/services/v5/projects/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String path = exchange.getRequestURI().getPath();
                String body;
                if (path.endsWith("/memberships")) {
                    body = MEMBERS_JSON;
                    respond(exchange, body, null, null);
                } else if (path.endsWith("/iterations")) {
                    body = ITERATION_JSON;
                    respond(exchange, body, "1", "100000");
                } else if (path.endsWith("/stories")) {
                    body = STORIES_JSON;
                    respond(exchange, body, "0", "100000");
                } else {
                    body = PROJECT_JSON;
                    respond(exchange, body, null, null);
                }
            }
        });

        server.start();
    }

    private void respond(HttpExchange ex, String body, String total, String limit) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");
        if (total != null) {
            ex.getResponseHeaders().set("X-Tracker-Pagination-Total", total);
        }
        if (limit != null) {
            ex.getResponseHeaders().set("X-Tracker-Pagination-Limit", limit);
        }
        ex.sendResponseHeaders(200, bytes.length);
        OutputStream os = ex.getResponseBody();
        os.write(bytes);
        os.close();
    }

    public int getPort() {
        return port;
    }

    public String getBaseUrl() {
        return "http://localhost:" + port;
    }

    public void stop() {
        server.stop(0);
    }
}
