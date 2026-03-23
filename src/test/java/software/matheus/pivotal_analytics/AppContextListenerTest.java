package software.matheus.pivotal_analytics;

import org.junit.Test;
import software.matheus.pivotal_analytics.pivotal.PivotalAPI;

import javax.servlet.ServletContextEvent;

import static org.junit.Assert.*;

public class AppContextListenerTest {

    @Test
    public void testContextInitializedSetsApiUrl() {
        AppContextListener listener = new AppContextListener();
        listener.contextInitialized(null);
        assertNotNull(PivotalAPI.API_LOCATION_URL);
        assertTrue(PivotalAPI.API_LOCATION_URL.contains("localhost"));
        assertTrue(PivotalAPI.API_LOCATION_URL.contains("/services/v5"));
    }

    @Test
    public void testContextDestroyedNoException() {
        AppContextListener listener = new AppContextListener();
        listener.contextDestroyed(null);
    }

    @Test
    public void testContextInitializedUsesPortEnvOrDefault() {
        AppContextListener listener = new AppContextListener();
        listener.contextInitialized(null);
        String url = PivotalAPI.API_LOCATION_URL;
        assertNotNull(url);
        assertTrue(url.startsWith("http://localhost:"));
    }
}
