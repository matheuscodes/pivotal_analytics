package software.matheus.pivotal_analytics.pivotal;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import java.util.Calendar;
import java.util.Date;
import static org.junit.Assert.*;

public class TicketSetTest {

    private TicketSet ticketSet;
    private UserLookup users;
    private Date pastDate;
    private Date midDate;
    private Date futureDate;

    @Before
    public void setUp() {
        users = new UserLookup();
        JSONObject user = new JSONObject();
        user.put("id", Long.valueOf(1L));
        user.put("name", "Alice");
        users.addUser(user);

        JSONObject user2 = new JSONObject();
        user2.put("id", Long.valueOf(2L));
        user2.put("name", "Bob");
        users.addUser(user2);

        Calendar cal = Calendar.getInstance();
        cal.set(2023, 0, 1);
        pastDate = cal.getTime();
        cal.set(2023, 5, 15);
        midDate = cal.getTime();
        cal.set(2023, 11, 31);
        futureDate = cal.getTime();

        ticketSet = new TicketSet();

        // Add an accepted feature owned by Alice with labels
        JSONObject jo1 = new JSONObject();
        jo1.put("id", Long.valueOf(1L));
        jo1.put("story_type", "feature");
        jo1.put("url", "http://www.pivotaltracker.com/story/show/1");
        jo1.put("estimate", Long.valueOf(3L));
        jo1.put("current_state", "accepted");
        jo1.put("name", "Feature 1");
        jo1.put("requested_by_id", Long.valueOf(1L));
        JSONArray owners1 = new JSONArray();
        owners1.add(Long.valueOf(1L));
        jo1.put("owner_ids", owners1);
        jo1.put("created_at", "2023-02-01T00:00:00Z");
        jo1.put("accepted_at", "2023-07-01T00:00:00Z");
        JSONArray labels1 = new JSONArray();
        JSONObject lbl1 = new JSONObject();
        lbl1.put("name", "backend");
        labels1.add(lbl1);
        jo1.put("labels", labels1);
        ticketSet.add(new Ticket(jo1, users));

        // Add a bug not accepted, owned by Bob
        JSONObject jo2 = new JSONObject();
        jo2.put("id", Long.valueOf(2L));
        jo2.put("story_type", "bug");
        jo2.put("url", "http://www.pivotaltracker.com/story/show/2");
        jo2.put("estimate", Long.valueOf(1L));
        jo2.put("current_state", "started");
        jo2.put("name", "Bug 1");
        jo2.put("requested_by_id", Long.valueOf(1L));
        JSONArray owners2 = new JSONArray();
        owners2.add(Long.valueOf(2L));
        jo2.put("owner_ids", owners2);
        jo2.put("created_at", "2023-04-01T00:00:00Z");
        JSONArray labels2 = new JSONArray();
        JSONObject lbl2 = new JSONObject();
        lbl2.put("name", "frontend");
        labels2.add(lbl2);
        jo2.put("labels", labels2);
        ticketSet.add(new Ticket(jo2, users));

        // Add a chore, unstarted, no owner
        JSONObject jo3 = new JSONObject();
        jo3.put("id", Long.valueOf(3L));
        jo3.put("story_type", "chore");
        jo3.put("url", "http://www.pivotaltracker.com/story/show/3");
        jo3.put("current_state", "unstarted");
        jo3.put("name", "Chore 1");
        jo3.put("requested_by_id", Long.valueOf(1L));
        JSONArray owners3 = new JSONArray();
        jo3.put("owner_ids", owners3);
        jo3.put("created_at", "2023-03-01T00:00:00Z");
        JSONArray labels3 = new JSONArray();
        jo3.put("labels", labels3);
        ticketSet.add(new Ticket(jo3, users));
    }

    @Test
    public void testQueryAcceptedBetween() {
        // The accepted ticket has accepted_at 2023-07-01, which is between pastDate(2023-01-01) and futureDate(2023-12-31)
        TicketSet result = ticketSet.queryAcceptedBetween(pastDate, futureDate);
        assertEquals(1, result.size());
        assertEquals("Feature 1", result.get(0).getTitle());
    }

    @Test
    public void testQueryAcceptedBetweenEmpty() {
        Calendar cal = Calendar.getInstance();
        cal.set(2020, 0, 1);
        Date d1 = cal.getTime();
        cal.set(2021, 0, 1);
        Date d2 = cal.getTime();
        TicketSet result = ticketSet.queryAcceptedBetween(d1, d2);
        assertEquals(0, result.size());
    }

    @Test
    public void testQueryCreatedBetween() {
        // Bug 1 has created_at 2023-04-01, between pastDate(2023-01-01) and midDate(2023-06-15)
        TicketSet result = ticketSet.queryCreatedBetween(pastDate, midDate);
        // Feature 1 created 2023-02-01, Bug 1 created 2023-04-01, Chore 1 created 2023-03-01
        // All 3 are between 2023-01-01 and 2023-06-15
        assertTrue(result.size() >= 2);
    }

    @Test
    public void testQueryActive() {
        // Bug (started) and Chore (unstarted) are active
        TicketSet result = ticketSet.queryActive();
        assertEquals(2, result.size());
    }

    @Test
    public void testQueryCreatedAndAcceptedBetween() {
        TicketSet result = ticketSet.queryCreatedAndAcceptedBetween(pastDate, futureDate);
        assertEquals(1, result.size());
        assertEquals("Feature 1", result.get(0).getTitle());
    }

    @Test
    public void testQueryState() {
        TicketSet result = ticketSet.queryState("accepted");
        assertEquals(1, result.size());
        TicketSet result2 = ticketSet.queryState("started");
        assertEquals(1, result2.size());
        TicketSet result3 = ticketSet.queryState("unstarted");
        assertEquals(1, result3.size());
    }

    @Test
    public void testQueryStateNotFound() {
        TicketSet result = ticketSet.queryState("delivered");
        assertEquals(0, result.size());
    }

    @Test
    public void testQueryOwner() {
        TicketSet result = ticketSet.queryOwner("Alice");
        assertEquals(1, result.size());
        assertEquals("Feature 1", result.get(0).getTitle());
    }

    @Test
    public void testQueryOwnerNotFound() {
        TicketSet result = ticketSet.queryOwner("NonExistent");
        assertEquals(0, result.size());
    }

    @Test
    public void testQueryLabel() {
        TicketSet result = ticketSet.queryLabel("backend");
        assertEquals(1, result.size());
    }

    @Test
    public void testQueryLabelNotFound() {
        TicketSet result = ticketSet.queryLabel("nonexistent");
        assertEquals(0, result.size());
    }

    @Test
    public void testQueryNotLabel() {
        // "frontend" only appears on Bug 1. So not-frontend should include Feature 1 and Chore 1 (Chore has null labels)
        TicketSet result = ticketSet.queryNotLabel("frontend");
        assertEquals(2, result.size());
    }

    @Test
    public void testQueryNotLabelNullLabels() {
        // Chore 1 has no labels, should be included in queryNotLabel
        TicketSet result = ticketSet.queryNotLabel("backend");
        // Feature 1 has "backend" label -> excluded
        // Bug 1 has "frontend" label -> not "backend" -> included
        // Chore 1 has null labels -> included
        assertEquals(2, result.size());
    }

    @Test
    public void testQueryType() {
        TicketSet features = ticketSet.queryType("feature");
        assertEquals(1, features.size());
        TicketSet bugs = ticketSet.queryType("bug");
        assertEquals(1, bugs.size());
        TicketSet chores = ticketSet.queryType("chore");
        assertEquals(1, chores.size());
        TicketSet releases = ticketSet.queryType("release");
        assertEquals(0, releases.size());
    }

    @Test
    public void testQueryUniqueOwners() {
        String[] owners = ticketSet.queryUniqueOwners();
        // Alice and Bob own tickets; chore has no owner
        assertEquals(2, owners.length);
    }

    @Test
    public void testQueryUniqueStates() {
        String[] states = ticketSet.queryUniqueStates();
        assertEquals(7, states.length);
    }

    @Test
    public void testQueryUniqueTypes() {
        String[] types = ticketSet.queryUniqueTypes();
        assertEquals(4, types.length);
    }

    @Test
    public void testQueryOldestActive() {
        // Bug (started) created 2023-04-01, Chore (unstarted) created 2023-03-01
        // Oldest active is Chore (earlier creation date)
        Ticket oldest = ticketSet.queryOldestActive();
        assertNotNull(oldest);
        assertEquals("Chore 1", oldest.getTitle());
    }

    @Test
    public void testQueryOldestActiveEmpty() {
        TicketSet empty = new TicketSet();
        assertNull(empty.queryOldestActive());
    }

    @Test
    public void testQueryOldestAccepted() {
        Ticket oldest = ticketSet.queryOldestAccepted();
        assertNotNull(oldest);
        assertEquals("Feature 1", oldest.getTitle());
    }

    @Test
    public void testQueryOldestAcceptedEmpty() {
        TicketSet noAccepted = new TicketSet();
        assertNull(noAccepted.queryOldestAccepted());
    }

    @Test
    public void testQueryNotState() {
        TicketSet result = ticketSet.queryNotState("accepted");
        assertEquals(2, result.size());
    }

    @Test
    public void testToString() {
        String s = ticketSet.toString();
        assertTrue(s.contains("TicketSet"));
        assertTrue(s.contains("Feature 1"));
    }

    @Test
    public void testEmptyTicketSet() {
        TicketSet empty = new TicketSet();
        assertEquals(0, empty.size());
        assertEquals(0, empty.queryActive().size());
        assertEquals(0, empty.queryType("feature").size());
    }
}
