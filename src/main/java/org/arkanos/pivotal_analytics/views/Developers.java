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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.pivotal_analytics.io.DataSource;
import org.arkanos.pivotal_analytics.managers.CalculationManager;
import org.arkanos.pivotal_analytics.managers.CookieManager;
import org.arkanos.pivotal_analytics.pivotal.Project;
import org.arkanos.pivotal_analytics.pivotal.Ticket;
import org.arkanos.pivotal_analytics.pivotal.TicketSet;
import org.arkanos.pivotal_analytics.printers.CommonHTML;
import org.arkanos.pivotal_analytics.printers.SVGPrinter;

/**
 * The {@code Developers} class serves Pivotal Analytics story-owners page.
 * There are two overviews displayed by this page:
 * - Load distribution: shows the load on each story owner.
 * For the load, the whole project history is considered.
 * Only people which have at least one story will be considered.
 * 
 * - Specific performance: shows the progress and status of a person.
 * The start date of the line graphs is specified by the cookie.
 * The pie charts only take into consideration the current iteration.
 * The list of stories shows all active tickets.
 *  
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
@WebServlet("/Developers")
public class Developers extends HttpServlet {
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
		
		int projectID = new Integer(CookieManager.matchCookie(cookies, "project_id").getValue()).intValue();
		
		Project project = DataSource.readProject(projectID,CookieManager.matchCookie(cookies, "token").getValue());
		TicketSet active = project.getStories().queryActive();
		
		page.println("<html>");
		page.println(CommonHTML.getBasicHeaders("Pivotal Analytics - "+project.getDisplayName()+" - Developers"));
		page.println("<body>");
		page.println(CommonHTML.getMenu("  "));
		
		if(request.getParameter("dev") != null && request.getParameter("dev").length()>0){
			/** There is a developer selected, so print the overview **/
			page.println("  <div class='content'>");
			page.println("    <h1>Developers Overview</h1>");
			
			page.println("    <center>");

			for(String s: active.queryUniqueOwners()){
					page.print("<a href='Developers?dev="+s+"'>"+s+"</a> | ");	
			}
			page.println("<a href='Developers'>All</a><br>");
					
			page.println("    </center>");
			
			printDeveloper(request.getParameter("dev"),page,active,cookies,project);
		}
		else{
			/** There is no developer selected, so print the task load **/
			page.println("  <div class='content' id='text'>");
			page.println("    <h1>Developers Overview</h1>");
			
			for(String s: active.queryUniqueOwners()){
				TicketSet currentdev = project.getStories().queryOwner(s);
				long now = System.currentTimeMillis();
				long start = now;
				/** Count start from oldest completed task **/
				if(currentdev.queryOldestAccepted() != null){
					start = currentdev.queryOldestAccepted().getAccepted().getTime();
				}
				
				/** Sum up ticket count and story points **/
				int storypoints = 0;
				int ticketcount = 0;
				for(Ticket t: currentdev.queryAcceptedBetween(new Date(start), new Date(now))){
					if(t.getType().compareTo("feature") == 0){
						storypoints += t.getPoints(); 
					}
					ticketcount += 1;
				}
				
				/** Gather current assignments **/
				int current_storypoints = 0;
				int current_ticketcount = 0;
				for(Ticket t: currentdev.queryActive()){
					if(t.getType().compareTo("feature") == 0){
						current_storypoints += t.getPoints(); 
					}
					current_ticketcount += 1;
				}
				
				int oneday = (1000*60*60*24);
				int size = (int) (project.getIterationSize()/oneday);
				int days = (int)((now - start)/oneday);
				
				/** The load is calculated using two informations
				 * 1) The amount of tickets that the developer can deliver.
				 * 2) The amount of story points per assigned ticket.
				 * 
				 * Current data is compared to history average to define loads.
				 * Average load from the two systems is used to draw the bar.
				 * 
				 * The project iteration size is taken into consideration!
				 */
				
				float average_storyperticket = storypoints/(float)ticketcount;
				float average_ticketperiteration = ticketcount*size/(float) days;
				float current_storyperticket = current_storypoints/(float)current_ticketcount;
				float load = ((current_storyperticket/average_storyperticket)+(current_ticketcount/average_ticketperiteration))/2;
				
				/** Print the load bar, set up to max 20% extra **/
				page.println("    <table cellspacing=0 cellpadding=2 border=0>");
				page.println("      <tr>");
				page.println("        <td>");
				page.println(SVGPrinter.horizontalLoadBar(load, 1.2f, 30, 200, "          "));
				page.println("        </td>");
				page.println("        <td><a href='Developers?dev="+s+"'>"+s+"</a></td>");
				page.println("      </tr>");
			}
			page.println("    </table>");
		}	
		
		page.println("  </div>");

		page.println(CommonHTML.getFooter("  "));
		
		page.println("</body>");
		page.println("</html>");
	}
	
	/**
	 * Prints the details of a particular person in the Project.
	 * 
	 * @param s defines the owner whose details are being printed.
	 * @param page defines the reference where the page is to be printed.
	 * @param active defines all active stories.
	 * @param cookies provides the configuration cookies.
	 * @param p specifies the project which is currently active.
	 */
	private void printDeveloper(String s, PrintWriter page, TicketSet active, Cookie[] cookies, Project p){
		String content = new String();
		TicketSet currentdev = active.queryOwner(s);
		/** Starting status box**/
		content +="<div class='developer_status'>\n";		
		
		/** Type Distribution **/
		Map<String,float[]> piedata;
		piedata = new LinkedHashMap<String,float[]>();
		for(String t: currentdev.queryUniqueTypes()){
			TicketSet temp = currentdev.queryType(t);
			float percentage = temp.size()/(float)currentdev.size();
			piedata.put(t+"s", new float[]{percentage});
		}
		content += SVGPrinter.percentualPieChart(piedata,300,200,"              ")+"\n";
		
		/** Status Distribution **/
		piedata = new LinkedHashMap<String,float[]>();
		for(String t: currentdev.queryUniqueStates()){
			TicketSet temp = currentdev.queryState(t);
			float percentage = temp.size()/(float)currentdev.size();
			piedata.put(t, new float[]{percentage});
		}
		content += SVGPrinter.percentualPieChart(piedata,300,200,"              ")+"\n";
		
		/** Tag Distribution **/
		piedata = new LinkedHashMap<String,float[]>();
		int sum = 0;
		for(String t: CookieManager.extractLabels(cookies)){
			TicketSet temp = currentdev.queryLabel(t);
			float percentage = temp.size()/(float)currentdev.size();
			piedata.put(t, new float[]{percentage});
			sum += temp.size();
		}
		piedata.put("others", new float[]{(currentdev.size()-sum)/(float)currentdev.size()});		
		content += SVGPrinter.percentualPieChart(piedata,300,200,"              ")+"\n";
		
		/** Closing status box **/
		content +="</div>\n";
		
		/** Calculating performance related data **/
		long now = System.currentTimeMillis();
		long oneday = 24*60*60*1000;
		long oneweek = 7*oneday;

		/** Single control of the start in the overview **/
		long configured_time = 0;
		long start = 0;
		if(p.getStories().queryOldestAccepted() != null){
			start = p.getStories().queryOldestAccepted().getCreated().getTime();
		}
		else{
			if(p.getStories().queryOldestActive() != null){
				start = p.getStories().queryOldestActive().getCreated().getTime();
			}
		}
		
		try {
			configured_time = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(CookieManager.matchCookie(cookies, "date_start").getValue()).getTime();
		} catch (ParseException e) {
			System.err.println("[WARNING] Parsing exception on starting date. Using default 2012.06.30.");
			System.out.println("[WARNING] Parsing exception on starting date. Using default 2012.06.30.");
		}
		/** Only uses configured if more recent than oldest **/
		if(start < configured_time){
			start = configured_time;
		}		
		
		/** Buffers for Graph data **/
		int[] bugs = new int[(int) ((now-start)/oneweek+1)];
		int[] chores = new int[(int) ((now-start)/oneweek)+1];
		int[] features = new int[(int) ((now-start)/oneweek)+1];
		int[] story_points = new int[(int) ((now-start)/oneweek)+1];
		int[] all = new int[(int) ((now-start)/oneweek)+1];
		String[] labels = new String[(int) ((now-start)/oneweek+1)];
		
		long i = start;
		int max = 0;
		while(i < now){
			int count = (int)((i - start)/oneweek);
			Date current = new Date(i);
			Date next = new Date(i+oneweek);
			
			all[count] = p.getStories().queryOwner(s).queryAcceptedBetween(current,next).size();
			bugs[count] = p.getStories().queryOwner(s).queryType("bug").queryAcceptedBetween(current,next).size();
			chores[count] = p.getStories().queryOwner(s).queryType("chore").queryAcceptedBetween(current,next).size();
			features[count] = p.getStories().queryOwner(s).queryType("feature").queryAcceptedBetween(current,next).size();
			
			if(p.getStories().queryAcceptedBetween(current,next).size() > max ){
				max = p.getStories().queryAcceptedBetween(current,next).size();
			}
			
			int points = 0;
			for(Ticket t: p.getStories().queryOwner(s).queryType("feature").queryAcceptedBetween(current,next)){
				points += t.getPoints();
			}
			story_points[count] = points;
			
			GregorianCalendar help = new GregorianCalendar();
			help.setTime(current);
			labels[count] = help.get(Calendar.YEAR)+"."+(help.get(Calendar.MONTH)+1)+"."+help.get(Calendar.DATE)+" - ";
			help.setTime(next);
			labels[count] += (help.get(Calendar.MONTH)+1)+"."+help.get(Calendar.DATE);
			
			i += oneweek;
		}
		
		/** Building maps **/
		int top_limit = 3 * max / active.queryUniqueOwners().length;
		Map<String,int[]> data;
		data = new LinkedHashMap<String,int[]>();
		data.put("All Work", all);
		data.put("Bugs Fixed", bugs);
		data.put("Features Created", features);
		data.put("Chores Completed", chores);

		/** Printing performance HTML data **/
		content += "<div class='developer_performance'>\n";
		content += SVGPrinter.labeledLineGraph(data, 0, top_limit, labels, "        ",2)+"\n";
		content += "</div>\n";
		
		/** Based on performance, calculating velocities **/
		data = new LinkedHashMap<String,int[]>();
		data.put("Velocity in Story Points", CalculationManager.calculateVelocity(story_points));
		data.put("Velocity in Story Count", CalculationManager.calculateVelocity(features));
		
		/** Printing velocity HTML data **/
		content += "<div class='developer_performance'>";
		content += SVGPrinter.labeledLineGraph(data, 0, top_limit, labels, "        ",2)+"\n";
		content += "</div>";
		
		/** Printing stories HTML data **/
		content += "<div class='developer_stories'>";
		content += CommonHTML.ticketTable("Recent assigned stories", currentdev, "              ");
		content += "</div>";
		page.println(CommonHTML.wrapWindow("developer",s, content,"    "));
	}
}
