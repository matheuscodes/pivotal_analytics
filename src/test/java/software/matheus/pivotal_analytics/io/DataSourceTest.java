package software.matheus.pivotal_analytics.io;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import software.matheus.pivotal_analytics.TestApiServer;
import software.matheus.pivotal_analytics.pivotal.PivotalAPI;
import software.matheus.pivotal_analytics.pivotal.Project;

import static org.junit.Assert.*;

public class DataSourceTest {

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
    public void testFlushProjectWhenNothingLoaded() {
        DataSource.flushProject(99999, "no-such-token");
    }

    @Test
    public void testFlushProjectAfterLoad() {
        DataSource.flushProject(12345, "testtoken");
        DataSource.flushProject(12345, "testtoken");
    }

    @Test
    public void testReadProjectReturnsProject() {
        DataSource.flushProject(99999, "test-token");
        Project p = DataSource.readProject(99999, "test-token");
        assertNotNull(p);
        DataSource.flushProject(99999, "test-token");
    }

    @Test
    public void testReadProjectCachesResult() {
        DataSource.flushProject(99999, "test-token");
        Project p1 = DataSource.readProject(99999, "test-token");
        Project p2 = DataSource.readProject(99999, "test-token");
        assertSame(p1, p2);
        DataSource.flushProject(99999, "test-token");
    }

    @Test
    public void testFlushProjectRemovesCache() {
        DataSource.flushProject(99999, "test-token");
        Project p1 = DataSource.readProject(99999, "test-token");
        DataSource.flushProject(99999, "test-token");
        Project p2 = DataSource.readProject(99999, "test-token");
        assertNotSame(p1, p2);
        DataSource.flushProject(99999, "test-token");
    }

    @Test
    public void testReadProjectDifferentTokens() {
        DataSource.flushProject(99999, "token-a");
        DataSource.flushProject(99999, "token-b");
        Project pa = DataSource.readProject(99999, "token-a");
        Project pb = DataSource.readProject(99999, "token-b");
        assertNotSame(pa, pb);
        DataSource.flushProject(99999, "token-a");
        DataSource.flushProject(99999, "token-b");
    }
}
