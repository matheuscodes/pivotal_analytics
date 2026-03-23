package software.matheus.pivotal_analytics.mock;

import org.junit.Test;
import software.matheus.pivotal_analytics.MockHttpRequest;
import software.matheus.pivotal_analytics.MockHttpResponse;

import javax.servlet.http.Cookie;
import java.util.HashMap;

import static org.junit.Assert.*;

public class MockPivotalAPITest {

    private MockHttpRequest makeRequest(String pathInfo) {
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], new HashMap<String, String>(), new HashMap<String, String>());
        req.setPathInfo(pathInfo);
        return req;
    }

    @Test
    public void testProjectEndpoint() throws Exception {
        MockPivotalAPI servlet = new MockPivotalAPI();
        MockHttpRequest req = makeRequest("/99999");
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("\"id\":99999"));
        assertTrue(output.contains("Demo Analytics Project"));
        assertTrue(output.contains("current_iteration_number"));
    }

    @Test
    public void testMembershipsEndpoint() throws Exception {
        MockPivotalAPI servlet = new MockPivotalAPI();
        MockHttpRequest req = makeRequest("/99999/memberships");
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("person"));
        assertTrue(output.contains("Alice Johnson"));
    }

    @Test
    public void testIterationsEndpoint() throws Exception {
        MockPivotalAPI servlet = new MockPivotalAPI();
        MockHttpRequest req = makeRequest("/99999/iterations");
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("stories"));
        assertTrue(output.contains("number"));
        assertEquals("52", res.getHeader("X-Tracker-Pagination-Total"));
    }

    @Test
    public void testStoriesEndpoint() throws Exception {
        MockPivotalAPI servlet = new MockPivotalAPI();
        MockHttpRequest req = makeRequest("/99999/stories");
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.startsWith("["));
        assertTrue(output.endsWith("]"));
    }

    @Test
    public void testNullPathInfoFallsToProject() throws Exception {
        MockPivotalAPI servlet = new MockPivotalAPI();
        MockHttpRequest req = makeRequest(null);
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("Demo Analytics Project"));
    }

    @Test
    public void testIterationsHasMultipleIterations() throws Exception {
        MockPivotalAPI servlet = new MockPivotalAPI();
        MockHttpRequest req = makeRequest("/99999/iterations");
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertTrue(output.contains("\"number\":1"));
        assertTrue(output.contains("\"number\":2"));
    }

    @Test
    public void testStoriesHasIceboxItems() throws Exception {
        MockPivotalAPI servlet = new MockPivotalAPI();
        MockHttpRequest req = makeRequest("/99999/stories");
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertTrue(output.contains("unscheduled"));
    }

    @Test
    public void testMembershipsHasMultipleUsers() throws Exception {
        MockPivotalAPI servlet = new MockPivotalAPI();
        MockHttpRequest req = makeRequest("/99999/memberships");
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertTrue(output.contains("Bob Smith"));
        assertTrue(output.contains("Carol Davis"));
    }
}
