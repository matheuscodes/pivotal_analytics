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

public class AboutTest {

    @Test
    public void testDoGetRendersPage() throws Exception {
        About servlet = new About();
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("<html>"));
        assertTrue(output.contains("About"));
    }

    @Test
    public void testDoGetContainsMenu() throws Exception {
        About servlet = new About();
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertTrue(output.contains("Overview"));
        assertTrue(output.contains("Config"));
    }

    @Test
    public void testDoGetContainsInstructions() throws Exception {
        About servlet = new About();
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertTrue(output.contains("mandatory") || output.contains("Pivotal") || output.contains("parameters"));
    }
}
