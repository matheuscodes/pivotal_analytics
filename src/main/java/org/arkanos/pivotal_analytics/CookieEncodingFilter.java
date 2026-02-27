/**
 *  Copyright (C) 2014 Matheus Borges Teixeira
 *  
 *  This file is part of Pivotal Analytics, a web tool for statistical
 *  observation and measurement of Pivotal Projects.
 *
 *  Pivotal Analytics is free software: you can redistribute it and/or 
 *  modify it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Pivotal Analytics.  If not, see <http://www.gnu.org/licenses/>
 */
package org.arkanos.pivotal_analytics;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * The {@code CookieEncodingFilter} ensures compatibility between the existing
 * cookie-handling code and strict RFC 6265 cookie value validation enforced
 * by modern Jetty versions.
 *
 * <p>Cookie values that contain whitespace (e.g. the {@code date_start} cookie
 * stored in {@code yyyy/MM/dd hh:mm:ss} format) are percent-encoded on the
 * way <em>out</em> (response) and decoded on the way <em>in</em> (request),
 * so that the rest of the application always sees the original, unencoded
 * values.
 *
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
@WebFilter("/*")
public class CookieEncodingFilter implements Filter {

    /** Encoded representation of a single space character. **/
    private static final String ENCODED_SPACE = "%20";

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(
            new DecodingRequestWrapper((HttpServletRequest) request),
            new EncodingResponseWrapper((HttpServletResponse) response)
        );
    }

    /**
     * Encodes a cookie value so that it contains only RFC 6265-safe characters.
     * Currently only encodes space characters as {@code %20}.
     *
     * @param value the raw cookie value.
     * @return the encoded value safe for use in an RFC 6265 Set-Cookie header.
     */
    static String encode(String value) {
        if (value == null) {
            return null;
        }
        return value.replace(" ", ENCODED_SPACE);
    }

    /**
     * Decodes a cookie value that was encoded by {@link #encode(String)}.
     *
     * @param value the encoded cookie value.
     * @return the original decoded value.
     */
    static String decode(String value) {
        if (value == null) {
            return null;
        }
        return value.replace(ENCODED_SPACE, " ");
    }

    /**
     * Response wrapper that encodes cookie values before they are sent to the
     * browser, preventing RFC 6265 validation errors for values with spaces.
     */
    private static class EncodingResponseWrapper extends HttpServletResponseWrapper {

        EncodingResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void addCookie(Cookie cookie) {
            String original = cookie.getValue();
            if (original != null && original.contains(" ")) {
                Cookie encoded = new Cookie(cookie.getName(), encode(original));
                encoded.setMaxAge(cookie.getMaxAge());
                encoded.setPath(cookie.getPath());
                encoded.setSecure(cookie.getSecure());
                encoded.setVersion(cookie.getVersion());
                if (cookie.getDomain() != null) {
                    encoded.setDomain(cookie.getDomain());
                }
                super.addCookie(encoded);
            } else {
                super.addCookie(cookie);
            }
        }
    }

    /**
     * Request wrapper that decodes cookie values before they are presented to
     * servlets, restoring original values that were encoded by the response wrapper.
     */
    private static class DecodingRequestWrapper extends HttpServletRequestWrapper {

        DecodingRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public Cookie[] getCookies() {
            Cookie[] cookies = super.getCookies();
            if (cookies == null) {
                return null;
            }
            Cookie[] decoded = new Cookie[cookies.length];
            for (int i = 0; i < cookies.length; i++) {
                String value = cookies[i].getValue();
                if (value != null && value.contains(ENCODED_SPACE)) {
                    Cookie c = new Cookie(cookies[i].getName(), decode(value));
                    c.setMaxAge(cookies[i].getMaxAge());
                    c.setPath(cookies[i].getPath());
                    c.setSecure(cookies[i].getSecure());
                    c.setVersion(cookies[i].getVersion());
                    if (cookies[i].getDomain() != null) {
                        c.setDomain(cookies[i].getDomain());
                    }
                    decoded[i] = c;
                } else {
                    decoded[i] = cookies[i];
                }
            }
            return decoded;
        }
    }
}
