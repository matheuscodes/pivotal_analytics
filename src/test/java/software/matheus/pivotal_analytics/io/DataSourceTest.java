package software.matheus.pivotal_analytics.io;

import org.junit.Test;
import static org.junit.Assert.*;

public class DataSourceTest {

    @Test
    public void testFlushProjectWhenNothingLoaded() {
        // Should not throw when no projects are loaded
        DataSource.flushProject(99999, "no-such-token");
    }

    @Test
    public void testFlushProjectAfterLoad() {
        // We can't easily test readProject (requires network)
        // But we can verify flushProject is safe to call after a flush
        DataSource.flushProject(12345, "testtoken");
        DataSource.flushProject(12345, "testtoken"); // Double flush should not throw
    }
}
