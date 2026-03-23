package software.matheus.pivotal_analytics.views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import software.matheus.pivotal_analytics.MockHttpRequest;
import software.matheus.pivotal_analytics.MockHttpResponse;

import javax.servlet.http.Cookie;
import java.util.HashMap;

import static org.junit.Assert.*;

public class PlanningFollowupTest {

    @BeforeClass
    public static void startServer() throws Exception {
        ViewTestBase.startServer();
    }

    @AfterClass
    public static void stopServer() {
        ViewTestBase.stopServer();
    }

    @Test
    public void testNoCookiesRedirectsToConfig() throws Exception {
        PlanningFollowup servlet = new PlanningFollowup();
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        assertEquals("Config", res.getRedirect());
    }

    @Test
    public void testWithFullCookiesRendersPage() throws Exception {
        PlanningFollowup servlet = new PlanningFollowup();
        MockHttpRequest req = new MockHttpRequest(ViewTestBase.makeFullCookies(), new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        assertNull(res.getRedirect());
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("<html>"));
    }

    @Test
    public void testWithIterationParameter() throws Exception {
        PlanningFollowup servlet = new PlanningFollowup();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("iteration", "1");
        MockHttpRequest req = new MockHttpRequest(ViewTestBase.makeFullCookies(), params, new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        assertNull(res.getRedirect());
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("<html>"));
    }
}
