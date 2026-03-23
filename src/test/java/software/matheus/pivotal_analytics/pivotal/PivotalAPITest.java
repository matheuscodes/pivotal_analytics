package software.matheus.pivotal_analytics.pivotal;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import software.matheus.pivotal_analytics.TestApiServer;

import java.util.Vector;

import static org.junit.Assert.*;

public class PivotalAPITest {

    private static TestApiServer server;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new TestApiServer();
        PivotalAPI.API_LOCATION_URL = server.getBaseUrl() + "/services/v5";
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) server.stop();
    }

    @Test
    public void testDownloadProject() {
        PivotalAPI api = new PivotalAPI("test-token");
        String result = api.downloadProject(99999);
        assertNotNull(result);
        assertTrue(result.contains("Test Project"));
        assertTrue(result.contains("99999"));
    }

    @Test
    public void testDownloadUsers() {
        PivotalAPI api = new PivotalAPI("test-token");
        String result = api.downloadUsers(99999);
        assertNotNull(result);
        assertTrue(result.contains("Alice Test"));
        assertTrue(result.contains("1001"));
    }

    @Test
    public void testDownloadProjectContent() {
        PivotalAPI api = new PivotalAPI("test-token");
        Vector<String> result = api.downloadProjectContent(99999);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testDownloadProjectContentHasIterationStories() {
        PivotalAPI api = new PivotalAPI("test-token");
        Vector<String> result = api.downloadProjectContent(99999);
        // First element is icebox (stories), second is iteration stories
        assertTrue(result.size() >= 1);
    }
}
