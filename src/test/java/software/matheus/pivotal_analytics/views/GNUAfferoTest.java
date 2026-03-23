package software.matheus.pivotal_analytics.views;

import org.junit.Test;
import software.matheus.pivotal_analytics.MockHttpRequest;
import software.matheus.pivotal_analytics.MockHttpResponse;

import javax.servlet.http.Cookie;
import java.util.HashMap;

import static org.junit.Assert.*;

public class GNUAfferoTest {

    @Test
    public void testDoGetRendersPage() throws Exception {
        GNUAffero servlet = new GNUAffero();
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertNotNull(output);
        assertTrue(output.contains("<html>"));
    }

    @Test
    public void testDoGetContainsLicenseText() throws Exception {
        GNUAffero servlet = new GNUAffero();
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertTrue(output.contains("GNU") || output.contains("Affero") || output.contains("General Public License"));
    }

    @Test
    public void testDoGetContainsFooter() throws Exception {
        GNUAffero servlet = new GNUAffero();
        MockHttpRequest req = new MockHttpRequest(new Cookie[0], new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();
        servlet.doGet(req, res);
        String output = res.getOutput();
        assertTrue(output.contains("Copyright") || output.contains("Matheus"));
    }
}
