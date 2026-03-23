package software.matheus.pivotal_analytics.views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import software.matheus.pivotal_analytics.MockHttpRequest;
import software.matheus.pivotal_analytics.MockHttpResponse;

import javax.servlet.http.Cookie;
import java.util.HashMap;

import static org.junit.Assert.*;

public class StarvationTest {

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
        Starvation servlet = new Starvation();
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        assertEquals("Config", res.getRedirect());
    }

    @Test
    public void testNullCookiesRedirectsToConfig() throws Exception {
        Starvation servlet = new Starvation();
        MockHttpRequest req = new MockHttpRequest(null, new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        assertEquals("Config", res.getRedirect());
    }

    @Test
    public void testWithFullCookiesRendersPage() throws Exception {
        Starvation servlet = new Starvation();
        MockHttpRequest req = new MockHttpRequest(ViewTestBase.makeFullCookies(), new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        assertNull(res.getRedirect());
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("<html>"));
    }

    @Test
    public void testWithFilterParameter() throws Exception {
        Starvation servlet = new Starvation();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("filter", "5");
        MockHttpRequest req = new MockHttpRequest(ViewTestBase.makeFullCookies(), params, new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        assertNull(res.getRedirect());
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("<html>"));
    }
}
