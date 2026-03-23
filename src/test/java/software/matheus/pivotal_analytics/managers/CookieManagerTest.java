package software.matheus.pivotal_analytics.managers;

import org.junit.Test;
import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import static org.junit.Assert.*;

public class CookieManagerTest {

    private Cookie[] makeCookies(String... pairs) {
        Cookie[] arr = new Cookie[pairs.length / 2];
        for (int i = 0; i < pairs.length; i += 2) {
            arr[i / 2] = new Cookie(pairs[i], pairs[i + 1]);
        }
        return arr;
    }

    @Test
    public void testCreateCookies() {
        Map<String, String> contents = new HashMap<String, String>();
        contents.put("token", "mytoken");
        contents.put("project_id", "12345");
        Vector<Cookie> cookies = CookieManager.createCookies(contents);
        assertEquals(2, cookies.size());
        boolean foundToken = false, foundProject = false;
        for (Cookie c : cookies) {
            if ("token".equals(c.getName())) {
                assertEquals("mytoken", c.getValue());
                foundToken = true;
            }
            if ("project_id".equals(c.getName())) {
                assertEquals("12345", c.getValue());
                foundProject = true;
            }
        }
        assertTrue(foundToken);
        assertTrue(foundProject);
    }

    @Test
    public void testCreateCookiesEmpty() {
        Map<String, String> contents = new HashMap<String, String>();
        Vector<Cookie> cookies = CookieManager.createCookies(contents);
        assertEquals(0, cookies.size());
    }

    @Test
    public void testCountCookiesNullArray() {
        assertFalse(CookieManager.countCookies(null));
    }

    @Test
    public void testCountCookiesEmptyArray() {
        assertFalse(CookieManager.countCookies(new Cookie[0]));
    }

    @Test
    public void testCountCookiesMissingCookies() {
        Cookie[] cookies = makeCookies("token", "abc", "project_id", "123");
        assertFalse(CookieManager.countCookies(cookies));
    }

    @Test
    public void testCountCookiesAllPresent() {
        Cookie[] cookies = makeCookies(
            "token", "abc",
            "project_id", "123",
            "special_labels", "label1",
            "iteration_start", "1",
            "date_start", "2023/01/01"
        );
        assertTrue(CookieManager.countCookies(cookies));
    }

    @Test
    public void testCountCookiesExtraCookies() {
        Cookie[] cookies = makeCookies(
            "token", "abc",
            "project_id", "123",
            "special_labels", "label1",
            "iteration_start", "1",
            "date_start", "2023/01/01",
            "extra", "value"
        );
        assertTrue(CookieManager.countCookies(cookies));
    }

    @Test
    public void testMatchCookieNull() {
        assertNull(CookieManager.matchCookie(null, "token"));
    }

    @Test
    public void testMatchCookieFound() {
        Cookie[] cookies = makeCookies("token", "mytoken", "other", "value");
        Cookie c = CookieManager.matchCookie(cookies, "token");
        assertNotNull(c);
        assertEquals("mytoken", c.getValue());
    }

    @Test
    public void testMatchCookieNotFound() {
        Cookie[] cookies = makeCookies("token", "mytoken");
        Cookie c = CookieManager.matchCookie(cookies, "missing");
        assertNull(c);
    }

    @Test
    public void testExtractLabelsSingle() {
        Cookie[] cookies = makeCookies("special_labels", "label1");
        Vector<String> labels = CookieManager.extractLabels(cookies);
        assertEquals(1, labels.size());
        assertEquals("label1", labels.get(0));
    }

    @Test
    public void testExtractLabelsMultiple() {
        Cookie[] cookies = makeCookies("special_labels", "label1,label2,label3");
        Vector<String> labels = CookieManager.extractLabels(cookies);
        assertEquals(3, labels.size());
        assertEquals("label1", labels.get(0));
        assertEquals("label2", labels.get(1));
        assertEquals("label3", labels.get(2));
    }

    @Test
    public void testExtractLabelsWithSpaces() {
        Cookie[] cookies = makeCookies("special_labels", "label1 , label2");
        Vector<String> labels = CookieManager.extractLabels(cookies);
        assertEquals(2, labels.size());
        assertEquals("label1", labels.get(0));
        assertEquals("label2", labels.get(1));
    }

    @Test
    public void testExtractLabelsWithSpacesBefore() {
        Cookie[] cookies = makeCookies("special_labels", " ,label2");
        Vector<String> labels = CookieManager.extractLabels(cookies);
        assertNotNull(labels);
        // After replacing " ," with "," we get ",label2"
        // which after splitting gives ["", "label2"] or similar
    }
}
