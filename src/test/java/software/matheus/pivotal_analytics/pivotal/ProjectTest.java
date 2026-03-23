package software.matheus.pivotal_analytics.pivotal;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import software.matheus.pivotal_analytics.TestApiServer;

import static org.junit.Assert.*;

public class ProjectTest {

    private static TestApiServer server;
    private static Project project;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new TestApiServer();
        PivotalAPI.API_LOCATION_URL = server.getBaseUrl() + "/services/v5";
        project = new Project(99999, "test-token");
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) server.stop();
    }

    @Test
    public void testGetStoriesNotNull() {
        assertNotNull(project.getStories());
    }

    @Test
    public void testGetStartNotNull() {
        assertNotNull(project.getStart());
    }

    @Test
    public void testGetCurrentIteration() {
        assertEquals(10, project.getCurrentIteration());
    }

    @Test
    public void testGetIterationSizePositive() {
        assertTrue(project.getIterationSize() > 0);
    }

    @Test
    public void testGetDisplayNameContainsProjectName() {
        String name = project.getDisplayName();
        assertNotNull(name);
        assertTrue(name.contains("Test Project"));
    }

    @Test
    public void testGetDisplayNameContainsAccountId() {
        String name = project.getDisplayName();
        assertTrue(name.contains("100001"));
    }

    @Test
    public void testStoriesHaveTickets() {
        TicketSet stories = project.getStories();
        assertNotNull(stories);
        assertTrue(stories.size() > 0);
    }
}
