package software.matheus.pivotal_analytics.printers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import software.matheus.pivotal_analytics.pivotal.Ticket;
import software.matheus.pivotal_analytics.pivotal.TicketSet;
import software.matheus.pivotal_analytics.pivotal.UserLookup;
import static org.junit.Assert.*;

public class CommonHTMLTest {

    private UserLookup users;

    @Before
    public void setUp() {
        users = new UserLookup();
        JSONObject user = new JSONObject();
        user.put("id", Long.valueOf(1L));
        user.put("name", "TestUser");
        users.addUser(user);
    }

    @Test
    public void testGetMenu() {
        String result = CommonHTML.getMenu("  ");
        assertNotNull(result);
        assertTrue(result.contains("Overview"));
        assertTrue(result.contains("Starvation"));
        assertTrue(result.contains("Throughput"));
        assertTrue(result.contains("Developers"));
        assertTrue(result.contains("Planning Follow Up"));
        assertTrue(result.contains("All Stories"));
        assertTrue(result.contains("Config"));
        assertTrue(result.contains("Refresh"));
    }

    @Test
    public void testGetMenuWithIndent() {
        String result = CommonHTML.getMenu("    ");
        assertTrue(result.startsWith("    "));
    }

    @Test
    public void testGetFooter() {
        String result = CommonHTML.getFooter("");
        assertNotNull(result);
        assertTrue(result.contains("Copyright"));
        assertTrue(result.contains("Matheus Borges Teixeira"));
        assertTrue(result.contains("GNUAffero"));
    }

    @Test
    public void testGetBasicHeaders() {
        String result = CommonHTML.getBasicHeaders("Test Page");
        assertNotNull(result);
        assertTrue(result.contains("<head>"));
        assertTrue(result.contains("Test Page"));
        assertTrue(result.contains("basic.css"));
        assertTrue(result.contains("pivotal.css"));
        assertTrue(result.contains("</head>"));
    }

    @Test
    public void testWrapWindow() {
        String result = CommonHTML.wrapWindow("extra-class", "My Title", "<p>Content</p>", "  ");
        assertNotNull(result);
        assertTrue(result.contains("extra-class"));
        assertTrue(result.contains("My Title"));
        assertTrue(result.contains("<p>Content</p>"));
    }

    @Test
    public void testTicketTableEmpty() {
        TicketSet empty = new TicketSet();
        String result = CommonHTML.ticketTable("My Table", empty, "");
        assertNotNull(result);
        assertTrue(result.contains("My Table"));
        assertTrue(result.contains("<table"));
    }

    @Test
    public void testTicketTableWithTickets() {
        TicketSet ts = new TicketSet();

        JSONObject jo = new JSONObject();
        jo.put("id", Long.valueOf(42L));
        jo.put("story_type", "feature");
        jo.put("url", "http://www.pivotaltracker.com/story/show/42");
        jo.put("estimate", Long.valueOf(3L));
        jo.put("current_state", "accepted");
        jo.put("name", "My Feature Story");
        jo.put("requested_by_id", Long.valueOf(1L));
        JSONArray owners = new JSONArray();
        owners.add(Long.valueOf(1L));
        jo.put("owner_ids", owners);
        jo.put("created_at", "2023-01-01T00:00:00Z");
        jo.put("accepted_at", "2023-01-15T00:00:00Z");
        JSONArray labels = new JSONArray();
        JSONObject lbl = new JSONObject();
        lbl.put("name", "test-label");
        labels.add(lbl);
        jo.put("labels", labels);

        ts.add(new Ticket(jo, users));

        String result = CommonHTML.ticketTable("Stories", ts, "  ");
        assertNotNull(result);
        assertTrue(result.contains("Stories"));
        assertTrue(result.contains("My Feature Story"));
        assertTrue(result.contains("test-label"));
        assertTrue(result.contains("TestUser"));
        assertTrue(result.contains("accepted"));
    }

    @Test
    public void testTicketTableNoOwnerNoLabel() {
        TicketSet ts = new TicketSet();

        JSONObject jo = new JSONObject();
        jo.put("id", Long.valueOf(99L));
        jo.put("story_type", "chore");
        jo.put("url", "http://www.pivotaltracker.com/story/show/99");
        jo.put("current_state", "unstarted");
        jo.put("name", "Chore Story");
        jo.put("requested_by_id", Long.valueOf(1L));
        JSONArray owners = new JSONArray();
        jo.put("owner_ids", owners);
        jo.put("created_at", "2023-01-01T00:00:00Z");
        JSONArray labels = new JSONArray();
        jo.put("labels", labels);

        ts.add(new Ticket(jo, users));

        String result = CommonHTML.ticketTable("Chores", ts, "");
        assertNotNull(result);
        // Should render without labels span and without owner text
        assertTrue(result.contains("Chore Story"));
    }
}
