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
import java.util.Map;

import static org.junit.Assert.*;

public class ConfigTest {

    private static TestApiServer server;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new TestApiServer();
        PivotalAPI.API_LOCATION_URL = server.getBaseUrl() + "/services/v5";
        DataSource.flushProject(99999, "test-token");
    }

    @AfterClass
    public static void stopServer() {
        DataSource.flushProject(99999, "test-token");
        if (server != null) server.stop();
    }

    private Cookie[] makeFullCookies() {
        return new Cookie[]{
            new Cookie("token", "test-token"),
            new Cookie("project_id", "99999"),
            new Cookie("special_labels", "[1]"),
            new Cookie("iteration_start", "1"),
            new Cookie("date_start", "2023/01/01 00:00:00")
        };
    }

    @Test
    public void testDoGetNoCookies() throws Exception {
        Config servlet = new Config();
        MockHttpRequest req = new MockHttpRequest(null, new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("<html>"));
        assertTrue(output.contains("warning") || output.contains("Please fill"));
    }

    @Test
    public void testDoGetWithCookies() throws Exception {
        Config servlet = new Config();
        MockHttpRequest req = new MockHttpRequest(makeFullCookies(), new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("test-token"));
        assertTrue(output.contains("99999"));
        assertTrue(output.contains("[1]"));
    }

    @Test
    public void testDoGetContainsForm() throws Exception {
        Config servlet = new Config();
        MockHttpRequest req = new MockHttpRequest(null, new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertTrue(output.contains("<form"));
        assertTrue(output.contains("token"));
        assertTrue(output.contains("project_id"));
    }

    @Test
    public void testDoPostSavesConfigAndRedirects() throws Exception {
        Config servlet = new Config();
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", "new-token");
        params.put("project_id", "99999");
        params.put("special_labels", "[1]");
        params.put("iteration_start", "1");
        params.put("date_start", "2023/01/01 00:00:00");
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], params, new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doPost(req, res);
        assertEquals("Overview", res.getRedirect());
        assertFalse(res.getAddedCookies().isEmpty());
    }

    @Test
    public void testDoPostWithExistingCookieFlushesProject() throws Exception {
        Config servlet = new Config();
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", "test-token");
        params.put("project_id", "99999");
        params.put("special_labels", "[1]");
        params.put("iteration_start", "1");
        params.put("date_start", "2023/01/01 00:00:00");
        MockHttpRequest req = new MockHttpRequest(makeFullCookies(), params, new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doPost(req, res);
        assertEquals("Overview", res.getRedirect());
    }

    @Test
    public void testDoPostWithEmptyParams() throws Exception {
        Config servlet = new Config();
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", "");
        params.put("project_id", "");
        params.put("special_labels", "");
        params.put("iteration_start", "");
        params.put("date_start", "");
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], params, new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doPost(req, res);
        assertEquals("Overview", res.getRedirect());
    }
}
