package software.matheus.pivotal_analytics;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.Principal;
import java.util.*;

/**
 * Minimal mock implementation of HttpServletRequest for unit testing.
 */
public class MockHttpRequest implements HttpServletRequest {

    private final Cookie[] cookies;
    private final Map<String, String> params;
    private final Map<String, String> headers;
    private String pathInfo = null;
    private String method = "GET";

    public MockHttpRequest(Cookie[] cookies, Map<String, String> params, Map<String, String> headers) {
        this.cookies = cookies;
        this.params = params != null ? params : new HashMap<String, String>();
        this.headers = headers != null ? headers : new HashMap<String, String>();
    }

    public void setPathInfo(String pathInfo) { this.pathInfo = pathInfo; }
    public void setMethod(String method) { this.method = method; }

    public Cookie[] getCookies() { return cookies; }
    public String getParameter(String name) { return params.get(name); }
    public String getHeader(String name) { return headers.get(name); }
    public String getPathInfo() { return pathInfo; }
    public String getMethod() { return method; }
    public StringBuffer getRequestURL() { return new StringBuffer("http://localhost/test"); }
    public String getRequestURI() { return "/test"; }
    public String getServletPath() { return ""; }
    public String getContextPath() { return ""; }
    public String getQueryString() { return null; }

    public String[] getParameterValues(String name) {
        String v = params.get(name);
        return v != null ? new String[]{v} : null;
    }

    public Map<String, String[]> getParameterMap() { return new HashMap<String, String[]>(); }

    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(new ArrayList<String>());
    }

    public Locale getLocale() { return Locale.ENGLISH; }

    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(Collections.singletonList(Locale.ENGLISH));
    }

    public String getServerName() { return "localhost"; }
    public int getServerPort() { return 80; }
    public String getScheme() { return "http"; }

    // -- Unimplemented stubs --
    public String getAuthType() { return null; }
    public long getDateHeader(String name) { return -1; }
    public Enumeration<String> getHeaders(String name) { return Collections.enumeration(new ArrayList<String>()); }
    public Enumeration<String> getHeaderNames() { return Collections.enumeration(new ArrayList<String>()); }
    public int getIntHeader(String name) { return -1; }
    public String getPathTranslated() { return null; }
    public String getRemoteUser() { return null; }
    public boolean isUserInRole(String role) { return false; }
    public Principal getUserPrincipal() { return null; }
    public String getRequestedSessionId() { return null; }
    public HttpSession getSession(boolean create) { return null; }
    public HttpSession getSession() { return null; }
    public String changeSessionId() { return null; }
    public boolean isRequestedSessionIdValid() { return false; }
    public boolean isRequestedSessionIdFromCookie() { return false; }
    public boolean isRequestedSessionIdFromURL() { return false; }
    public boolean isRequestedSessionIdFromUrl() { return false; }
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException { return false; }
    public void login(String username, String password) throws ServletException {}
    public void logout() throws ServletException {}
    public Collection<Part> getParts() throws IOException, ServletException { return null; }
    public Part getPart(String name) throws IOException, ServletException { return null; }
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException { return null; }
    public Object getAttribute(String name) { return null; }
    public Enumeration<String> getAttributeNames() { return Collections.enumeration(new ArrayList<String>()); }
    public String getCharacterEncoding() { return "UTF-8"; }
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {}
    public int getContentLength() { return -1; }
    public long getContentLengthLong() { return -1; }
    public String getContentType() { return null; }
    public ServletInputStream getInputStream() throws IOException { return null; }
    public BufferedReader getReader() throws IOException { return null; }
    public String getRemoteAddr() { return "127.0.0.1"; }
    public String getRemoteHost() { return "localhost"; }
    public void setAttribute(String name, Object o) {}
    public void removeAttribute(String name) {}
    public boolean isSecure() { return false; }
    public RequestDispatcher getRequestDispatcher(String path) { return null; }
    public String getRealPath(String path) { return null; }
    public int getRemotePort() { return 0; }
    public String getLocalName() { return "localhost"; }
    public String getLocalAddr() { return "127.0.0.1"; }
    public int getLocalPort() { return 80; }
    public ServletContext getServletContext() { return null; }
    public AsyncContext startAsync() throws IllegalStateException { return null; }
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException { return null; }
    public boolean isAsyncStarted() { return false; }
    public boolean isAsyncSupported() { return false; }
    public AsyncContext getAsyncContext() { return null; }
    public DispatcherType getDispatcherType() { return DispatcherType.REQUEST; }
    public String getProtocol() { return "HTTP/1.1"; }
}
