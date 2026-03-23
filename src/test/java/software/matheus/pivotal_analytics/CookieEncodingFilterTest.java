package software.matheus.pivotal_analytics;

import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

import java.util.HashMap;

import static org.junit.Assert.*;

public class CookieEncodingFilterTest {

    @Test
    public void testEncodeNull() {
        assertNull(CookieEncodingFilter.encode(null));
    }

    @Test
    public void testEncodeWithSpace() {
        assertEquals("hello%20world", CookieEncodingFilter.encode("hello world"));
    }

    @Test
    public void testEncodeNoSpace() {
        assertEquals("nospace", CookieEncodingFilter.encode("nospace"));
    }

    @Test
    public void testEncodeMultipleSpaces() {
        assertEquals("a%20b%20c", CookieEncodingFilter.encode("a b c"));
    }

    @Test
    public void testDecodeNull() {
        assertNull(CookieEncodingFilter.decode(null));
    }

    @Test
    public void testDecodeWithEncoded() {
        assertEquals("hello world", CookieEncodingFilter.decode("hello%20world"));
    }

    @Test
    public void testDecodeNoEncoding() {
        assertEquals("nospace", CookieEncodingFilter.decode("nospace"));
    }

    @Test
    public void testDecodeMultipleEncoded() {
        assertEquals("a b c", CookieEncodingFilter.decode("a%20b%20c"));
    }

    @Test
    public void testInitAndDestroy() throws Exception {
        CookieEncodingFilter filter = new CookieEncodingFilter();
        filter.init(null);
        filter.destroy();
    }

    @Test
    public void testDoFilterDecodesRequestCookies() throws Exception {
        CookieEncodingFilter filter = new CookieEncodingFilter();

        Cookie[] cookies = new Cookie[]{new Cookie("date_start", "2023/01/01%2000:00:00")};
        MockHttpRequest req = new MockHttpRequest(cookies, new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();

        final Cookie[][] captured = new Cookie[1][];
        FilterChain chain = new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws java.io.IOException, javax.servlet.ServletException {
                captured[0] = ((javax.servlet.http.HttpServletRequest) request).getCookies();
            }
        };

        filter.doFilter(req, res, chain);
        assertNotNull(captured[0]);
    }

    @Test
    public void testDoFilterEncodesResponseCookie() throws Exception {
        CookieEncodingFilter filter = new CookieEncodingFilter();
        MockHttpRequest req = new MockHttpRequest(null, new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();

        FilterChain chain = new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws java.io.IOException, javax.servlet.ServletException {
                Cookie c = new Cookie("date_start", "2023/01/01 00:00:00");
                c.setSecure(true);
                ((javax.servlet.http.HttpServletResponse) response).addCookie(c);
            }
        };

        filter.doFilter(req, res, chain);
        assertEquals(1, res.getAddedCookies().size());
        assertEquals("2023/01/01%2000:00:00", res.getAddedCookies().get(0).getValue());
    }

    @Test
    public void testDoFilterEncodesOnlyWhenSpacePresent() throws Exception {
        CookieEncodingFilter filter = new CookieEncodingFilter();
        MockHttpRequest req = new MockHttpRequest(null, new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();

        FilterChain chain = new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws java.io.IOException, javax.servlet.ServletException {
                Cookie c = new Cookie("token", "mytoken");
                c.setSecure(true);
                ((javax.servlet.http.HttpServletResponse) response).addCookie(c);
            }
        };

        filter.doFilter(req, res, chain);
        assertEquals(1, res.getAddedCookies().size());
        assertEquals("mytoken", res.getAddedCookies().get(0).getValue());
    }

    @Test
    public void testDoFilterDecodesEncodedSpaceCookies() throws Exception {
        CookieEncodingFilter filter = new CookieEncodingFilter();

        Cookie[] cookies = new Cookie[]{
            new Cookie("date_start", "2023/01/01%2000:00:00"),
            new Cookie("token", "mytoken")
        };
        MockHttpRequest req = new MockHttpRequest(cookies, new HashMap<String, String>(), new HashMap<String, String>());
        MockHttpResponse res = new MockHttpResponse();

        final Cookie[][] captured = new Cookie[1][];
        FilterChain chain = new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws java.io.IOException, javax.servlet.ServletException {
                captured[0] = ((javax.servlet.http.HttpServletRequest) request).getCookies();
            }
        };

        filter.doFilter(req, res, chain);
        assertNotNull(captured[0]);
        assertEquals(2, captured[0].length);
        assertEquals("2023/01/01 00:00:00", captured[0][0].getValue());
        assertEquals("mytoken", captured[0][1].getValue());
    }
}
