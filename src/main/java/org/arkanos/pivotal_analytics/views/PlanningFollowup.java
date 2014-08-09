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
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.pivotal_analytics.io.DataSource;
import org.arkanos.pivotal_analytics.managers.CookieManager;
import org.arkanos.pivotal_analytics.pivotal.Project;
import org.arkanos.pivotal_analytics.pivotal.TicketSet;
import org.arkanos.pivotal_analytics.printers.CommonHTML;
import org.arkanos.pivotal_analytics.printers.SVGPrinter;

/**
 * The {@code PlanningFollowup} class serves Pivotal Analytics planning page.
 * There are three overviews displayed by this page
 * - Burn down charts for each iteration.
 * - List of iteration's stories and other stories delivered.
 * - Pie charts of story distribution.
 * 
 * This page is configured by the iteration_start cookie.
 * In order to use it properly the project must contain the iteration labels.
 *  
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
@WebServlet("/PlanningFollowup")
public class PlanningFollowup extends HttpServlet {
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
		
		PrintWriter page = response.getWriter();
		Project thisone = null;
		int iteration_start = 1;
		try{
			int projectID = new Integer(CookieManager.matchCookie(cookies, "project_id").getValue()).intValue();
			thisone = DataSource.readProject(projectID,CookieManager.matchCookie(cookies, "token").getValue(),CookieManager.matchCookie(cookies, "offset").getValue());
			
			iteration_start = new Integer(CookieManager.matchCookie(cookies, "iteration_start").getValue()).intValue();
			if(thisone.getCurrentIteration() - iteration_start > 9){
				iteration_start = thisone.getCurrentIteration() - 9;
			}
		}
		catch (NumberFormatException e){
			System.err.println("[WARNING] Parsing exception on iteration. Using default 1.");
			System.out.println("[WARNING] Parsing exception on iteration. Using default 1.");
		}
		
		page.println("<html>");
		page.println(CommonHTML.getBasicHeaders("Pivotal Analytics - "+thisone.getDisplayName()+" - Planning Follow Up"));
		page.println("<body>");
		
		page.println(CommonHTML.getMenu("  "));
		
		page.println("  <div class='content' id='text'>");
		
		
		page.println("    <h1>Planning Follow Up</h1>");
		
		long start = thisone.getStart().getTime() + (iteration_start-1)*thisone.getIterationSize();
		
		page.println("    <p align='center'>");
		for(int i = 0; i < thisone.getCurrentIteration(); i++){
			if(thisone.getStories().queryLabel("["+i+"]").size()>0){
				page.println("<a href='PlanningFollowup?iteration="+i+"'>"+i+"</a> | ");
			}
		}
		page.println("<a href='PlanningFollowup?iteration="+thisone.getCurrentIteration()+"'>"+thisone.getCurrentIteration()+"</a> ");
		page.println("    </p>");
		//TODO remove idents
		int iteration = thisone.getCurrentIteration();
		if(request.getParameter("iteration") != null && request.getParameter("iteration").length()>0){
			iteration = new Integer(request.getParameter("iteration")).intValue();
		}
		TicketSet completed = thisone.getStories().queryAcceptedBetween(new Date(thisone.getStart().getTime()+(iteration-1)*thisone.getIterationSize()), new Date(thisone.getStart().getTime()+(iteration)*thisone.getIterationSize()));
		TicketSet planned = thisone.getStories().queryLabel("["+iteration+"]");
		TicketSet sidetracked = completed.queryCreatedBetween(new Date(thisone.getStart().getTime()+(iteration-1)*thisone.getIterationSize()), new Date(thisone.getStart().getTime()+(iteration)*thisone.getIterationSize())).queryNotLabel("["+iteration+"]");
		TicketSet acumulated = completed.queryCreatedBetween(new Date(thisone.getStart().getTime()),new Date(thisone.getStart().getTime()+(iteration-1)*thisone.getIterationSize())).queryNotLabel("["+iteration+"]");
		if(planned.size() > 0){
			page.println("    <h2>Current status of iteration "+iteration+": "+(((planned.size()-planned.queryActive().size())*100)/planned.size())+"% completed</h2>");
			page.println("    <h2>Delivered in time for iteration "+iteration+": "+(completed.queryLabel("["+iteration+"]").size()*100/planned.size())+"%</h2>");
		}
		else{
			page.println("    <h2>No stories planned for iteration "+iteration+"</h2>");
		}
		
		page.println(CommonHTML.ticketTable("Stories Planned for Iteration "+iteration, planned, "      "));
		page.println("      <br>");
		page.println(CommonHTML.ticketTable("Sidetracking Stories created and completed during Iteration "+iteration, sidetracked, "      "));
		page.println("      <br>");
		page.println(CommonHTML.ticketTable("Previously Accumulated Stories decluttered during Iteration "+iteration, acumulated, "      "));
		page.println("      <br>");
		
		page.println("    <h2>Past distributions for plannings</h2>");
		
		page.println("    <table width='100%'>");
		page.println("      <tr>");
		for(int i = iteration_start; i <= thisone.getCurrentIteration()-1;i++){
			TicketSet stories = thisone.getStories().queryAcceptedBetween(new Date(start+(i-iteration_start)*thisone.getIterationSize()), new Date(start+(i-iteration_start+1)*thisone.getIterationSize()));
			float all = stories.size();
			/** Interested only if there are delivered stories **/
			if(all > 0){				
				int thisiteration = stories.queryLabel("["+i+"]").size();
				int newones = stories.queryNotLabel("["+i+"]").queryCreatedBetween(new Date(start+(i-iteration_start)*thisone.getIterationSize()), new Date(start+(i-iteration_start+1)*thisone.getIterationSize())).size();
				
				Map<String,float[]> three;
				three = new LinkedHashMap<String,float[]>();
				three.put("Sidetracked", new float[]{(newones)/all});
				three.put("Accumulated", new float[]{(all-thisiteration-newones)/all});
				three.put("Planned", new float[]{(thisiteration)/all});
				page.println("        <td width='33%'>");
				page.println("          <h4>Iteration "+i+"'s completed tasks from:</h4>");
				page.println(SVGPrinter.percentualPieChart(three, 300, 300, "          "));
				page.println("        </td>");
			}
			else{
				page.println("        <td width='33%'>");
				page.println("          <h4>No stories delivered for Iteration "+i+"</h4>");				
				page.println("        </td>");
			}
			if((i-iteration_start) % 3 == 2){
				page.println("      </tr>");
			}
			
		}		
		page.println("      </tr>");
		page.println("    </table>");
		page.println("  </div>");

		page.println(CommonHTML.getFooter("  "));
		
		page.println("</body>");
		page.println("</html>");
	}
}
