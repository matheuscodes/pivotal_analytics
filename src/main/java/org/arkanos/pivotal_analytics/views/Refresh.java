/**
 *  Copyright (C) 2013 Matheus Borges Teixeira
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
package org.arkanos.pivotal_analytics.views;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.pivotal_analytics.io.DataSource;
import org.arkanos.pivotal_analytics.managers.CookieManager;
import org.arkanos.pivotal_analytics.printers.CommonHTML;

/**
 * The {@code Refresh} class serves Pivotal Analytics to reload projects.
 * No page is returned, simply caches are released and user redirected.
 *  
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
@WebServlet("/Refresh")
public class Refresh extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		
		if(!CookieManager.countCookies(request.getCookies())){
			response.sendRedirect("Config");
			return;
		}
		
		int projectID = new Integer(CookieManager.matchCookie(cookies, "project_id").getValue()).intValue();
		String token = CookieManager.matchCookie(cookies, "token").getValue();
		DataSource.flushProject(projectID,token);
		if(request.getParameter("purge") != null){
			PrintWriter page = response.getWriter();
			
			page.println("<html>");
			page.println(CommonHTML.getBasicHeaders("Pivotal Analytics - Configurations"));
			page.println("<body>");
			Cookie c = null;
			if(CookieManager.matchCookie(cookies, "token") != null){
				c = CookieManager.matchCookie(cookies, "token");
				c.setMaxAge(0);
				response.addCookie(c);
			}
			if(CookieManager.matchCookie(cookies, "project_id") != null){
				c = CookieManager.matchCookie(cookies, "project_id");
				c.setMaxAge(0);
				response.addCookie(c);
			}
			if(CookieManager.matchCookie(cookies, "special_labels") != null){
				c = CookieManager.matchCookie(cookies, "special_labels");
				c.setMaxAge(0);
				response.addCookie(c);
			}
			if(CookieManager.matchCookie(cookies, "iteration_start") != null){
				c = CookieManager.matchCookie(cookies, "iteration_start");
				c.setMaxAge(0);
				response.addCookie(c);
			}
			if(CookieManager.matchCookie(cookies, "date_start") != null){
				c = CookieManager.matchCookie(cookies, "date_start");
				c.setMaxAge(0);
				response.addCookie(c);
			}
			if(CookieManager.matchCookie(cookies, "offset") != null){
				c = CookieManager.matchCookie(cookies, "offset");
				c.setMaxAge(0);
				response.addCookie(c);
			}
			page.println(CommonHTML.getMenu("  "));
			page.println("  <div class='content' id='text'>");
			page.println("      <h1>All your data was flushed!</h1>");
			page.println("      <p>Your project data was flushed from our caches and all cookies deleted.</p>");
			page.println("  </div>");

			page.println(CommonHTML.getFooter("  "));
			
			page.println("</body>");
			page.println("</html>");
		}
		else{
			response.sendRedirect(request.getHeader("referer"));		
		}
	}
}
