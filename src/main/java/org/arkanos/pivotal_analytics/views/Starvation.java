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
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.pivotal_analytics.io.DataSource;
import org.arkanos.pivotal_analytics.managers.CookieManager;
import org.arkanos.pivotal_analytics.pivotal.Project;
import org.arkanos.pivotal_analytics.pivotal.Ticket;
import org.arkanos.pivotal_analytics.pivotal.TicketSet;
import org.arkanos.pivotal_analytics.printers.CommonHTML;
import org.arkanos.pivotal_analytics.printers.SVGPrinter;

/**
 * The {@code Starvation} class serves Pivotal Analytics story waiting page.
 * Basically it displays the waiting time, in days, for Pivotal stories.
 * They are divided in Backlog (scheduled) and Icebox (unscheduled).
 * For bigger projects, it is offered a couple of time range filters.
 *  
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
@WebServlet("/Starvation")
public class Starvation extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	/** Filter for stories starving for less than a week **/
	private static final int LESS_ONE_WEEK = 0;
	/** Filter for stories starving for 2 to 3 weeks **/
	private static final int TWO_TO_THREE_WEEKS = 1;
	/** Filter for stories starving for 3 to 4 weeks **/
	private static final int THREE_TO_FOUR_WEEKS = 2;
	/** Filter for stories starving for 1 to 2 months **/
	private static final int ONE_TO_TWO_MONTHS = 3;
	/** Filter for stories starving for more than two months **/
	private static final int MORE_TWO_MONTHS = 4;
		
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		
		if(!CookieManager.countCookies(request.getCookies())){
			response.sendRedirect("Config");
			return;
		}
		
		PrintWriter page = response.getWriter();
		long today = System.currentTimeMillis();
		long oneday = 24*60*60*1000;
		
		Date start = null;
		Date end = null;
		
		if(request.getParameter("filter") != null && request.getParameter("filter").length()>0){
			int filter = new Integer(request.getParameter("filter")).intValue();
			long now = System.currentTimeMillis();
			switch(filter){
				case LESS_ONE_WEEK:
					start = new Date(now - 7*oneday);
					end = new Date(now);
					break;
				case TWO_TO_THREE_WEEKS:
					start = new Date(now - 14*oneday);
					end = new Date(now - 7*oneday);
					break;
				case THREE_TO_FOUR_WEEKS:
					start = new Date(now - 21*oneday);
					end = new Date(now - 14*oneday);
					break;
				case ONE_TO_TWO_MONTHS:
					start = new Date(now - 60*oneday);
					end = new Date(now - 30*oneday);
					break;
				case MORE_TWO_MONTHS:
					start = new Date(0);
					end = new Date(now - 60*oneday);
					break;
				default:
					start = new Date(0);
					end = new Date(System.currentTimeMillis());
					break;
			}
		}
		else{
			start = new Date(0);
			end = new Date(System.currentTimeMillis());
		}
		
		int projectID = new Integer(CookieManager.matchCookie(cookies, "project_id").getValue()).intValue();
		Project project = DataSource.readProject(projectID,CookieManager.matchCookie(cookies, "token").getValue(),CookieManager.matchCookie(cookies, "offset").getValue());
		TicketSet queryUnscheduled = project.getStories().queryState("unscheduled");
		queryUnscheduled = queryUnscheduled.queryCreatedBetween(start, end);
		
		page.println("<html>");
		page.println(CommonHTML.getBasicHeaders("Pivotal Analytics - "+project.getDisplayName()+" - Starvation"));
		page.println("<body>");
		
		page.println(CommonHTML.getMenu("  "));
		
		page.println("  <div class='content' id='text'>");
		
		page.println("    <h1>Starvation on the unscheduled stories</h1>");
		
		page.println("<center>");
		
		page.print("<a href='Starvation?filter=0'>Less than one week ago</a> | ");
		page.print("<a href='Starvation?filter=1'>2 to 3 weeks ago</a> | ");
		page.print("<a href='Starvation?filter=2'>3 to 4 weeks ago</a> | ");
		page.print("<a href='Starvation?filter=3'>1 to 2 months ago</a> | ");
		page.println("<a href='Starvation?filter=4'>More than two months ago</a>");
		
		page.println("</center>");
				
		page.println("    <table cellspacing=0 cellpadding=0 border=0>");
		for(Ticket t: queryUnscheduled){
			int help = (int)((today-t.getCreated().getTime())/oneday);
			String color;
			if(t.getLabels()!= null && t.getLabels().indexOf("on hold")>=0){
				color = "gray";
			}
			else{
				color = "blue";
			}
			page.println("      <tr>");
			page.println("        <td>");
			page.println(SVGPrinter.horizontalProgressBar(help, 365, 20, 300, color, "black", "        "));
			page.println("        </td>");
			page.println("        <td><img src='icons/"+t.getType()+".png' /></td>");
			page.println("        <td>");
			page.print("          <a class='starving' href='"+t.getURL());
			page.print("'>");
			page.print(t.getTitle());
			page.println("</a>");
			if(t.getLabels()!=null){
				page.println("          <span class='labels'>"+t.getLabels()+"</span>");
			}
			page.println("        </td>");
			page.println("      </tr>");
		}
		page.println("    </table>");
		
		page.println("    <h1>Starvation on the scheduled stories</h1>");
		
		page.println("<center>");
		
		page.print("<a href='Starvation?filter=0'>Less than one week ago</a> | ");
		page.print("<a href='Starvation?filter=1'>2 to 3 weeks ago</a> | ");
		page.print("<a href='Starvation?filter=2'>3 to 4 weeks ago</a> | ");
		page.print("<a href='Starvation?filter=3'>1 to 2 months ago</a> | ");
		page.println("<a href='Starvation?filter=4'>More than two months ago</a>");
		
		page.println("</center>");
		
		
		TicketSet queryActive = project.getStories().queryActive().queryNotState("unscheduled");
		queryActive = queryActive.queryCreatedBetween(start, end);
		
		page.println("    <table cellspacing=0 cellpadding=0 border=0>");
		for(Ticket t: queryActive){
			int help = (int)((today-t.getCreated().getTime())/oneday);
			String color;
			if(t.getLabels()!= null && t.getLabels().indexOf("on hold")>=0){
				color = "gray";
			}
			else{
				color = "blue";
			}
			page.println("      <tr>");
			page.println("        <td>");
			page.println(SVGPrinter.horizontalProgressBar(help, 365, 20, 300, color, "black", "        "));
			page.println("        </td>");
			page.println("        <td><img src='icons/"+t.getType()+".png' /></td>");
			page.println("        <td>");
			page.print("          <a class='starving' href='"+t.getURL());
			page.print("'>");
			page.print(t.getTitle());
			page.println("</a>");
			if(t.getLabels()!=null){
				page.println("          <span class='labels'>"+t.getLabels()+"</span>");
			}
			page.println("        </td>");
			page.println("      </tr>");
		}
		page.println("    </table>");
		
		page.println("  </div>");

		page.println(CommonHTML.getFooter("  "));
		
		page.println("</body>");
		page.println("</html>");
	}
}
