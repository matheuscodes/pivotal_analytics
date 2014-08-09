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
import org.arkanos.pivotal_analytics.managers.CookieManager;
import org.arkanos.pivotal_analytics.pivotal.Project;
import org.arkanos.pivotal_analytics.pivotal.TicketSet;
import org.arkanos.pivotal_analytics.printers.CommonHTML;
import org.arkanos.pivotal_analytics.printers.SVGPrinter;

/**
 * The {@code Throughput} class serves Pivotal Analytics story income/outcome page.
 * There are four graphs, which are on weekly basis:
 * - Overview with total count of stories created and delivered.
 * - Detailed view per story type: bugs, features and chores. 
 *  
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
@WebServlet("/Throughput")
public class Throughput extends HttpServlet {
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
		
		/*Queries*/
		Project project = DataSource.readProject(projectID,CookieManager.matchCookie(cookies, "token").getValue(),CookieManager.matchCookie(cookies, "offset").getValue());
		TicketSet queryAll = project.getStories();
		TicketSet queryFeatures = queryAll.queryType("feature");
		TicketSet queryBugs = queryAll.queryType("bug");
		TicketSet queryChores = queryAll.queryType("chore");
		
		page.println("<html>");
		page.println(CommonHTML.getBasicHeaders("Pivotal Analytics - "+project.getDisplayName()+" - Throughput"));
		page.println("<body>");
		
		page.println(CommonHTML.getMenu("  "));
		
		page.println("  <div class='content'>");
		
		page.println("    <h1>Throughput Overview</h1>");
		
		/*Constants*/
		int ACCEPTED = 0;
		int CREATED = 1;
		long now = System.currentTimeMillis();
		long oneday = 24*60*60*1000;
		long oneweek = 7*oneday;
		
		/** Single control of the start in the overview **/
		long configured_time = 0;
		long start = 0;
		if(project.getStories().queryOldestAccepted() != null){
			start = project.getStories().queryOldestAccepted().getCreated().getTime();
		}
		else{
			if(project.getStories().queryOldestActive() != null){
				start = project.getStories().queryOldestActive().getCreated().getTime();
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
				
		/*Buffer for graph data*/
		int[][] bugs = new int[2][(int) ((now-start)/oneweek+1)];
		int[][] chores = new int[2][(int) ((now-start)/oneweek)+1];
		int[][] features = new int[2][(int) ((now-start)/oneweek)+1];
		int[][] all = new int[2][(int) ((now-start)/oneweek)+1];
		String[] labels = new String[(int) ((now-start)/oneweek+1)];
		int max = 0;
		long i = start;
		while(i < now){
			int count = (int)((i - start)/oneweek);
			Date current = new Date(i);
			Date next = new Date(i+oneweek);
			bugs[ACCEPTED][count] = queryBugs.queryAcceptedBetween(current,next).size();
			bugs[CREATED][count] = queryBugs.queryCreatedBetween(current,next).size();
			
			features[ACCEPTED][count] = queryFeatures.queryAcceptedBetween(current,next).size();
			features[CREATED][count] = queryFeatures.queryCreatedBetween(current,next).size();
			
			chores[ACCEPTED][count] = queryChores.queryAcceptedBetween(current,next).size();
			chores[CREATED][count] = queryChores.queryCreatedBetween(current,next).size();
			
			all[ACCEPTED][count] = queryAll.queryAcceptedBetween(current,next).size();
			all[CREATED][count] = queryAll.queryCreatedBetween(current,next).size();
			
			GregorianCalendar help = new GregorianCalendar();
			help.setTime(current);
			labels[count] = help.get(Calendar.YEAR)+"."+(help.get(Calendar.MONTH)+1)+"."+help.get(Calendar.DATE)+" - ";
			help.setTime(next);
			labels[count] += (help.get(Calendar.MONTH)+1)+"."+help.get(Calendar.DATE);
						
			max = Math.max(all[CREATED][count], Math.max(all[ACCEPTED][count],max));

			i += oneweek;
		}
		
		Map<String,int[]> data;
		
		data = new LinkedHashMap<String,int[]>();
		data.put("Accepted Stories", all[ACCEPTED]);
		data.put("Requested Stories", all[CREATED]);		
		
		page.println(CommonHTML.wrapWindow("throughput","Throughput on all stories",SVGPrinter.labeledLineGraph(data, 0, max+10, labels, "        ",2),"    "));
				
		data = new LinkedHashMap<String,int[]>();
		data.put("Accepted Features", features[ACCEPTED]);
		data.put("Requested Features", features[CREATED]);		
		
		page.println(CommonHTML.wrapWindow("throughput","Throughput only for Features",SVGPrinter.labeledLineGraph(data, 0, max+10, labels, "        ",2),"    "));
		
		data = new LinkedHashMap<String,int[]>();
		data.put("Accepted Bugs", bugs[ACCEPTED]);
		data.put("Requested Bugs", bugs[CREATED]);		
		
		page.println(CommonHTML.wrapWindow("throughput","Throughput only for Bugs",SVGPrinter.labeledLineGraph(data, 0, max+10, labels, "        ",2),"    "));
		
		data = new LinkedHashMap<String,int[]>();
		data.put("Accepted Chores", chores[ACCEPTED]);
		data.put("Requested Chores", chores[CREATED]);		
		
		page.println(CommonHTML.wrapWindow("throughput","Throughput only for Chores",SVGPrinter.labeledLineGraph(data, 0, max+10, labels,"        ",2),"    "));
		
		page.println("  </div>");
		
		page.println(CommonHTML.getFooter("  "));
		
		page.println("</body>");
		page.println("</html>");
	}
}
