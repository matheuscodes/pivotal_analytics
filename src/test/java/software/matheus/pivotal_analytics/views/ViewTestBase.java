package software.matheus.pivotal_analytics.views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import software.matheus.pivotal_analytics.MockHttpRequest;
import software.matheus.pivotal_analytics.MockHttpResponse;
import software.matheus.pivotal_analytics.TestApiServer;
import software.matheus.pivotal_analytics.io.DataSource;
import software.matheus.pivotal_analytics.pivotal.PivotalAPI;

import javax.servlet.http.Cookie;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Base helper for view tests that require full cookies and a running API server.
 */
abstract class ViewTestBase {

    static final int TEST_PROJECT_ID = 99999;
    static final String TEST_TOKEN = "test-token";

    static TestApiServer server;

    static void startServer() throws Exception {
        server = new TestApiServer();
        PivotalAPI.API_LOCATION_URL = server.getBaseUrl() + "/services/v5";
        DataSource.flushProject(TEST_PROJECT_ID, TEST_TOKEN);
        // Pre-load the project so tests are faster
        DataSource.readProject(TEST_PROJECT_ID, TEST_TOKEN);
    }

    static void stopServer() {
        DataSource.flushProject(TEST_PROJECT_ID, TEST_TOKEN);
        if (server != null) server.stop();
    }

    static Cookie[] makeFullCookies() {
        return new Cookie[]{
            new Cookie("token", TEST_TOKEN),
            new Cookie("project_id", String.valueOf(TEST_PROJECT_ID)),
            new Cookie("special_labels", "[1]"),
            new Cookie("iteration_start", "1"),
            new Cookie("date_start", "2023/01/01 00:00:00")
        };
    }
}
