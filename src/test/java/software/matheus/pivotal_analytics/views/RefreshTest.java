package software.matheus.pivotal_analytics.views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import software.matheus.pivotal_analytics.MockHttpRequest;
import software.matheus.pivotal_analytics.MockHttpResponse;

import javax.servlet.http.Cookie;
import java.util.HashMap;

import static org.junit.Assert.*;

public class RefreshTest {

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
        Refresh servlet = new Refresh();
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        assertEquals("Config", res.getRedirect());
    }

    @Test
    public void testWithFullCookiesAndNoParamRedirects() throws Exception {
        Refresh servlet = new Refresh();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("referer", "http://localhost/Overview");
        MockHttpRequest req = new MockHttpRequest(ViewTestBase.makeFullCookies(), new HashMap<String, String>(), headers);
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        assertEquals("http://localhost/Overview", res.getRedirect());
    }

    @Test
    public void testWithPurgeParamClearsCookiesAndRenderPage() throws Exception {
        Refresh servlet = new Refresh();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("purge", "true");
        MockHttpRequest req = new MockHttpRequest(ViewTestBase.makeFullCookies(), params, new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        assertNull(res.getRedirect());
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue("Expected flush confirmation message", output.contains("flushed"));
        assertFalse("Expected cookies to be cleared", res.getAddedCookies().isEmpty());
    }
}
