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
package org.arkanos.pivotal_analytics.views;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

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
 * The {@code Config} class serves Pivotal Analytics configuration page.
 * There are six configurations:
 * - token: user defined key for accessing Pivotal.
 * - project_id: code for which project to load.
 * - special_labels: comma separated labels for highlight.
 * - iteration_start: which iteration to start counting from.
 * - date_start: what date is important to start observing the project.
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
@WebServlet("/Config")
public class Config extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter page = response.getWriter();
		Cookie[] cookies = request.getCookies();
		page.println("<html>");
		page.println(CommonHTML.getBasicHeaders("Pivotal Analytics - Configurations"));
		page.println("<body>");
		
		page.println(CommonHTML.getMenu("  "));
		page.println("  <div class='content' id='text'>");
		
		page.println("    <h1>Configuring a project to Pivotal Analytics</h1>");
		
		page.println("    <form action='' method='post' name='configing' id='configuration'>");
		page.println("      <h3>User Token:</h3>");
		if(CookieManager.matchCookie(cookies, "token") != null){
			page.println("    Current value: "+CookieManager.matchCookie(cookies, "token").getValue()+"<br>");
		}
		else{
			page.println("      <p class='warning'> Please fill in this information, it is important! </p>");
		}
		page.println("      <input id='token' name='token' type='text' size='100%' />");
		
		page.println("      <h3>Project ID:</h3>");
		if(CookieManager.matchCookie(cookies, "project_id") != null){
			page.println("      Current value: "+CookieManager.matchCookie(cookies, "project_id").getValue()+"<br>");
		}
		else{
			page.println("      <p class='warning'> Please fill in this information, it is important! </p>");
		}
		page.println("      <input id='project_id' name='project_id' type='text' size='100%' />");
		
		page.println("      <h3>Special Labels:</h3> Separate labels with commas.<br>");
		if(CookieManager.matchCookie(cookies, "special_labels") != null){
			page.println("    Current value: "+CookieManager.matchCookie(cookies, "special_labels").getValue()+"<br>");
		}
		else{
			page.println("      <p class='warning'> Please fill in this information, it is important! </p>");
		}
		page.println("      <input id='special_labels' name='special_labels' type='text' size='100%' /><br>");
		
		page.println("      <h3>Iteration to start Follow Up:</h3>");
		if(CookieManager.matchCookie(cookies, "iteration_start") != null){
			page.println("    Current value: "+CookieManager.matchCookie(cookies, "iteration_start").getValue()+"<br>");
		}
		else{
			page.println("      <p class='warning'> Please fill in this information, it is important! </p>");
		}
		page.println("      <input id='iteration_start' name='iteration_start' type='text' size='100%' /><br>");
		
		page.println("      <h3>Date for Reference:</h3>(format must be yyyy/MM/dd hh:mm:ss)<br>");
		if(CookieManager.matchCookie(cookies, "date_start") != null){
			page.println("      Current value: "+CookieManager.matchCookie(cookies, "date_start").getValue()+"<br>");
		}
		else{
			page.println("      <p class='warning'> Please fill in this information, it is important! </p>");
		}
		page.println("      <input id='date_start' name='date_start' type='text' size='100%' /><br>");
		
		page.println("      <input type='submit' name='button' value='Save Configurations' />");
		page.println("    </form>");
		page.println("  </div>");

		page.println(CommonHTML.getFooter("  "));
		
		page.println("</body>");
		page.println("</html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HashMap<String,String> data_to_save = new HashMap<String,String>();
		if(request.getParameter("token").length()>0){
			data_to_save.put("token", request.getParameter("token"));
		}
		if(request.getParameter("project_id").length()>0){
			data_to_save.put("project_id", request.getParameter("project_id"));
		}
		if(request.getParameter("special_labels").length()>0){
			data_to_save.put("special_labels", request.getParameter("special_labels"));
		}
		if(request.getParameter("iteration_start").length()>0){
			data_to_save.put("iteration_start", request.getParameter("iteration_start"));
		}
		if(request.getParameter("date_start").length()>0){
			data_to_save.put("date_start", request.getParameter("date_start"));
		}
		for(Cookie c: CookieManager.createCookies(data_to_save)){
			response.addCookie(c);
		}
		Cookie cproject = CookieManager.matchCookie(request.getCookies(), "project_id");
		Cookie ctoken = CookieManager.matchCookie(request.getCookies(), "token");
		if(cproject != null && ctoken!= null){
			int projectID = new Integer(cproject.getValue()).intValue();
			String token = ctoken.getValue();
			DataSource.flushProject(projectID,token);
		}
		response.sendRedirect("Overview");
	}

}
