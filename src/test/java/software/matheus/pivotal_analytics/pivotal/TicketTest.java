package software.matheus.pivotal_analytics.pivotal;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TicketTest {

    private UserLookup users;

    @Before
    public void setUp() {
        users = new UserLookup();
        JSONObject user = new JSONObject();
        user.put("id", Long.valueOf(1L));
        user.put("name", "TestUser");
        users.addUser(user);

        JSONObject user2 = new JSONObject();
        user2.put("id", Long.valueOf(2L));
        user2.put("name", "OtherUser");
        users.addUser(user2);
    }

    private JSONObject buildTicketJSON(String type, String state, boolean hasEstimate, boolean hasOwner, boolean hasLabels, boolean accepted) {
        JSONObject jo = new JSONObject();
        jo.put("id", Long.valueOf(100L));
        jo.put("story_type", type);
        jo.put("url", "http://www.pivotaltracker.com/story/show/100");
        if (hasEstimate) {
            jo.put("estimate", Long.valueOf(3L));
        }
        jo.put("current_state", state);
        jo.put("name", "Test Story");
        jo.put("requested_by_id", Long.valueOf(1L));

        JSONArray owners = new JSONArray();
        if (hasOwner) {
            owners.add(Long.valueOf(2L));
        }
        jo.put("owner_ids", owners);

        jo.put("created_at", "2023-01-01T00:00:00Z");
        if (accepted) {
            jo.put("accepted_at", "2023-01-15T00:00:00Z");
        }

        JSONArray labels = new JSONArray();
        if (hasLabels) {
            JSONObject label1 = new JSONObject();
            label1.put("name", "backend");
            JSONObject label2 = new JSONObject();
            label2.put("name", "urgent");
            labels.add(label1);
            labels.add(label2);
        }
        jo.put("labels", labels);
        return jo;
    }

    @Test
    public void testBasicTicketCreation() {
        JSONObject jo = buildTicketJSON("feature", "started", true, false, false, false);
        Ticket t = new Ticket(jo, users);
        assertEquals(100L, t.getID());
        assertEquals("feature", t.getType());
        assertEquals("started", t.getState());
        assertEquals("Test Story", t.getTitle());
        assertEquals(3, t.getPoints());
    }

    @Test
    public void testTicketWithNoEstimate() {
        JSONObject jo = buildTicketJSON("chore", "unstarted", false, false, false, false);
        Ticket t = new Ticket(jo, users);
        assertEquals(0, t.getPoints());
    }

    @Test
    public void testTicketAcceptedState() {
        JSONObject jo = buildTicketJSON("feature", "accepted", true, false, false, true);
        Ticket t = new Ticket(jo, users);
        assertEquals("accepted", t.getState());
        assertNotNull(t.getAccepted());
        assertNotNull(t.getCreated());
    }

    @Test
    public void testTicketNotAccepted() {
        JSONObject jo = buildTicketJSON("bug", "started", true, false, false, false);
        Ticket t = new Ticket(jo, users);
        assertNull(t.getAccepted());
    }

    @Test
    public void testTicketWithOwner() {
        JSONObject jo = buildTicketJSON("feature", "started", true, true, false, false);
        Ticket t = new Ticket(jo, users);
        assertNotNull(t.getOwner());
        assertTrue(t.getOwner().contains("OtherUser"));
    }

    @Test
    public void testTicketWithNoOwner() {
        JSONObject jo = buildTicketJSON("feature", "started", true, false, false, false);
        Ticket t = new Ticket(jo, users);
        assertNull(t.getOwner());
    }

    @Test
    public void testTicketWithLabels() {
        JSONObject jo = buildTicketJSON("feature", "started", true, false, true, false);
        Ticket t = new Ticket(jo, users);
        assertNotNull(t.getLabels());
        assertTrue(t.getLabels().contains("backend"));
        assertTrue(t.getLabels().contains("urgent"));
    }

    @Test
    public void testTicketWithNoLabels() {
        JSONObject jo = buildTicketJSON("feature", "started", true, false, false, false);
        Ticket t = new Ticket(jo, users);
        assertNull(t.getLabels());
    }

    @Test
    public void testTicketURL() {
        JSONObject jo = buildTicketJSON("feature", "started", true, false, false, false);
        Ticket t = new Ticket(jo, users);
        assertNotNull(t.getURL());
        assertEquals("http://www.pivotaltracker.com/story/show/100", t.getURL().toString());
    }

    @Test
    public void testTicketToString() {
        JSONObject jo = buildTicketJSON("feature", "started", true, false, false, false);
        Ticket t = new Ticket(jo, users);
        String s = t.toString();
        assertTrue(s.contains("Test Story"));
        assertTrue(s.contains("100"));
    }

    @Test
    public void testAddMissingIterationLabelFeatureNoExistingLabel() {
        JSONObject jo = buildTicketJSON("feature", "started", true, false, false, false);
        Ticket t = new Ticket(jo, users);
        // No labels initially
        t.addMissingIterationLabel(5);
        assertNotNull(t.getLabels());
        assertTrue(t.getLabels().contains("[5]"));
    }

    @Test
    public void testAddMissingIterationLabelFeatureWithExistingLabel() {
        JSONObject jo = buildTicketJSON("feature", "started", true, false, true, false);
        Ticket t = new Ticket(jo, users);
        // Has labels "backend,urgent"
        t.addMissingIterationLabel(3);
        assertTrue(t.getLabels().contains("[3]"));
        assertTrue(t.getLabels().contains("backend"));
    }

    @Test
    public void testAddMissingIterationLabelFeatureAlreadyHasLabel() {
        JSONObject jo = buildTicketJSON("feature", "started", true, false, true, false);
        Ticket t = new Ticket(jo, users);
        t.addMissingIterationLabel(3);
        String labelsAfterFirst = t.getLabels();
        t.addMissingIterationLabel(3);
        // Should not add duplicate
        assertEquals(labelsAfterFirst, t.getLabels());
    }

    @Test
    public void testAddMissingIterationLabelNotFeature() {
        JSONObject jo = buildTicketJSON("bug", "started", true, false, false, false);
        Ticket t = new Ticket(jo, users);
        t.addMissingIterationLabel(5);
        // Bug type should NOT get the iteration label
        assertNull(t.getLabels());
    }

    @Test
    public void testTicketWithMultipleOwners() {
        JSONObject jo = new JSONObject();
        jo.put("id", Long.valueOf(200L));
        jo.put("story_type", "feature");
        jo.put("url", "http://www.pivotaltracker.com/story/show/200");
        jo.put("estimate", Long.valueOf(2L));
        jo.put("current_state", "started");
        jo.put("name", "Multi-owner Story");
        jo.put("requested_by_id", Long.valueOf(1L));

        JSONArray owners = new JSONArray();
        owners.add(Long.valueOf(1L));
        owners.add(Long.valueOf(2L));
        jo.put("owner_ids", owners);

        jo.put("created_at", "2023-01-01T00:00:00Z");

        JSONArray labels = new JSONArray();
        jo.put("labels", labels);

        Ticket t = new Ticket(jo, users);
        assertNotNull(t.getOwner());
        assertTrue(t.getOwner().contains("TestUser"));
        assertTrue(t.getOwner().contains("OtherUser"));
    }

    @Test
    public void testTicketMalformedURL() {
        JSONObject jo = buildTicketJSON("feature", "started", true, false, false, false);
        jo.put("url", "not-a-valid-url");
        // Should not throw, just handle gracefully
        Ticket t = new Ticket(jo, users);
        // URL would be null if malformed
        assertNull(t.getURL());
    }
}
