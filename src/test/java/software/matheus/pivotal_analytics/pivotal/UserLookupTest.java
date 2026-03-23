package software.matheus.pivotal_analytics.pivotal;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserLookupTest {

    private UserLookup lookup;

    @Before
    public void setUp() {
        lookup = new UserLookup();
    }

    @Test
    public void testGetUserNotFound() {
        assertNull(lookup.getUser("999"));
    }

    @Test
    public void testAddAndGetUser() {
        JSONObject jo = new JSONObject();
        jo.put("id", Long.valueOf(1L));
        jo.put("name", "Alice");
        lookup.addUser(jo);
        assertEquals("Alice", lookup.getUser("1"));
    }

    @Test
    public void testAddMultipleUsers() {
        JSONObject jo1 = new JSONObject();
        jo1.put("id", Long.valueOf(1L));
        jo1.put("name", "Alice");
        JSONObject jo2 = new JSONObject();
        jo2.put("id", Long.valueOf(2L));
        jo2.put("name", "Bob");
        lookup.addUser(jo1);
        lookup.addUser(jo2);
        assertEquals("Alice", lookup.getUser("1"));
        assertEquals("Bob", lookup.getUser("2"));
    }

    @Test
    public void testOverwriteUser() {
        JSONObject jo1 = new JSONObject();
        jo1.put("id", Long.valueOf(1L));
        jo1.put("name", "Alice");
        JSONObject jo2 = new JSONObject();
        jo2.put("id", Long.valueOf(1L));
        jo2.put("name", "AliceNew");
        lookup.addUser(jo1);
        lookup.addUser(jo2);
        assertEquals("AliceNew", lookup.getUser("1"));
    }
}
