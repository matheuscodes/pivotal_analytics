package software.matheus.pivotal_analytics;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Minimal mock implementation of HttpServletResponse for unit testing.
 */
public class MockHttpResponse implements HttpServletResponse {

    private final StringWriter sw = new StringWriter();
    private final PrintWriter pw = new PrintWriter(sw);
    private final List<Cookie> addedCookies = new ArrayList<Cookie>();
    private final Map<String, String> headers = new HashMap<String, String>();
    private String redirect = null;
    private String contentType = null;
    private String encoding = "UTF-8";
    private int status = 200;

    public PrintWriter getWriter() throws IOException { return pw; }
    public String getOutput() { pw.flush(); return sw.toString(); }
    public String getRedirect() { return redirect; }
    public List<Cookie> getAddedCookies() { return addedCookies; }

    public void sendRedirect(String location) throws IOException { this.redirect = location; }
    public void addCookie(Cookie cookie) { addedCookies.add(cookie); }
    public void setContentType(String type) { this.contentType = type; }
    public void setCharacterEncoding(String charset) { this.encoding = charset; }
    public void setHeader(String name, String value) { headers.put(name, value); }
    public void addHeader(String name, String value) { headers.put(name, value); }
    public void setStatus(int sc) { this.status = sc; }
    public void setStatus(int sc, String sm) { this.status = sc; }
    public int getStatus() { return status; }
    public String getContentType() { return contentType; }
    public String getCharacterEncoding() { return encoding != null ? encoding : "UTF-8"; }

    public boolean isCommitted() { return false; }
    public void flushBuffer() throws IOException {}
    public void reset() {}
    public void resetBuffer() {}
    public void setBufferSize(int size) {}
    public int getBufferSize() { return 0; }

    // -- Unimplemented stubs --
    public void sendError(int sc, String msg) throws IOException { this.status = sc; }
    public void sendError(int sc) throws IOException { this.status = sc; }
    public void setDateHeader(String name, long date) {}
    public void addDateHeader(String name, long date) {}
    public void setIntHeader(String name, int value) {}
    public void addIntHeader(String name, int value) {}
    public boolean containsHeader(String name) { return headers.containsKey(name); }
    public String getHeader(String name) { return headers.get(name); }
    public Collection<String> getHeaders(String name) {
        List<String> result = new ArrayList<String>();
        if (headers.containsKey(name)) result.add(headers.get(name));
        return result;
    }
    public Collection<String> getHeaderNames() { return headers.keySet(); }
    public String encodeURL(String url) { return url; }
    public String encodeRedirectURL(String url) { return url; }
    public String encodeUrl(String url) { return url; }
    public String encodeRedirectUrl(String url) { return url; }
    public void setContentLength(int len) {}
    public void setContentLengthLong(long len) {}
    public void setLocale(Locale loc) {}
    public Locale getLocale() { return Locale.ENGLISH; }
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {
            public void write(int b) throws IOException { sw.write(b); }
            public boolean isReady() { return true; }
            public void setWriteListener(WriteListener wl) {}
        };
    }
}
