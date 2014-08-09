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
import java.util.HashMap;
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
import org.json.simple.JSONObject;

/**
 * The {@code Overview} class serves Pivotal Analytics project overview page.
 * There are two graphs on weekly basis:
 * - Team Velocity: which displays a graph with velocity per story count and size.
 * - Team Response time: which displays a graph with the longest, shortest and average story waiting time.
 * There is one graph on daily basis:
 * - Backlog size: which displays how many stories have been created and closed.
 * 
 * There is also the status which shows the current state of the project.
 * Here it is included a Backlog size variation, story distribution and a list of open stories.
 *  
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
@WebServlet("/Overview")
public class Overview extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter page = response.getWriter();
		Cookie[] cookies = request.getCookies();
				
		if(!CookieManager.countCookies(request.getCookies())){
			response.sendRedirect("Config");
			return;
		}
		
		int projectID = new Integer(CookieManager.matchCookie(cookies, "project_id").getValue()).intValue();
		Project project = DataSource.readProject(projectID,CookieManager.matchCookie(cookies, "token").getValue(),CookieManager.matchCookie(cookies, "offset").getValue());
		TicketSet non_resolved = project.getStories().queryActive();
		
		 
		
		page.println("<html>");
		page.println(CommonHTML.getBasicHeaders("Pivotal Analytics - "+project.getDisplayName()+" - Overview"));
		page.println("<body>");
		
		page.println(CommonHTML.getMenu("  "));
		page.println("  <div class='content'>");
		
		
		page.println("    <h1>Project Overview</h1>");
		
		Map<String,int[]> data;
		String[] labels;
		long oneday = 24*60*60*1000;
		long oneweek = oneday*7;
		long now = System.currentTimeMillis();
		TicketSet all = project.getStories();
		
		/** Single control of the start in the overview **/
		long start = project.getStories().queryOldestAccepted().getCreated().getTime();
		long configured_time = 0;
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
		
		int[] features = new int[(int)((now-start)/oneweek)+1];
		int[] story_points = new int[(int)((now-start)/oneweek)+1];
		int k = 0;
		labels = new String[(int)((now-start)/oneweek)+1];
		int max_points = 0;
		long time = start;
		while(time < now){
			Date current = new Date(time);
			Date next = new Date(time+oneweek);
			
			features[k] = all.queryType("feature").queryAcceptedBetween(current, next).size();
			int points = 0;
			for(Ticket t: all.queryType("feature").queryAcceptedBetween(current, next)){
				points += t.getPoints();
			}
			story_points[k] = points;
			
			GregorianCalendar help = new GregorianCalendar();
			help.setTime(current);
			labels[k] = help.get(Calendar.YEAR)+"."+(help.get(Calendar.MONTH)+1)+"."+help.get(Calendar.DATE)+" - ";
			help.setTime(next);
			labels[k] += (help.get(Calendar.MONTH)+1)+"."+help.get(Calendar.DATE);
			
			if(points > max_points){
				max_points = points;
			}
			time+=oneweek;
			k++;
		}
		
		data = new LinkedHashMap<String,int[]>();
		data.put("Velocity in Story Points", CalculationManager.calculateVelocity(story_points));
		data.put("Velocity in Story Count", CalculationManager.calculateVelocity(features));
		
		page.println(CommonHTML.wrapWindow("overview","Team Velocity for Features", SVGPrinter.labeledLineGraph(data, 0, max_points, labels, "        ",2),"    "));
		
		time = start;
		int[] max = new int[(int)((now-time)/oneweek)+1];
		int[] min = new int[(int)((now-time)/oneweek)+1];
		int[] avg = new int[(int)((now-time)/oneweek)+1];
		
		k = 0;
		max_points = 0;
		while(time < now){
			Date current = new Date(time);
			Date next = new Date(time+oneweek);
			
			max[k] = 0;
			min[k] = 999999999;
			int total = all.queryAcceptedBetween(current, next).size();
						
			int tickets_time = 0;
			for(Ticket t: all.queryAcceptedBetween(current, next)){
				long difference = (t.getAccepted().getTime() - t.getCreated().getTime())/oneday;
				tickets_time += difference;
				if(difference > max[k]) max[k] = (int) difference;
				if(difference < min[k]) min[k] = (int) difference;
				if(difference > max_points) max_points = (int) difference;
			}
			/* To avoid jumps up in the graph in case nothing is delivered*/
			if(min[k]==999999999) min[k] = 0;
			
			if(total > 0){
				avg[k] = tickets_time/total;
			}
			else{
				avg[k] = 0;
			}
			
			time+=oneweek;
			k++;
		}
		
		data = new LinkedHashMap<String,int[]>();
		data.put("Max", max);
		data.put("Average", avg);
		data.put("Min", min);
		
		page.println(CommonHTML.wrapWindow("overview","Request Response Time in Days", SVGPrinter.labeledLineGraph(data, 0, max_points, labels, "        ",2),"    "));
		
		//FIXME When there are no Active tickets
		long oldest = all.queryOldestActive().getCreated().getTime();
		int days = (int)((now-oldest)/(oneday))+1; 
		int[] open = new int[days];
		int[] closed = new int[days];
		labels = new String[days];
		int constant_open = all.queryCreatedBetween(new Date(0),new Date(oldest)).size();
		int constant_closed = all.queryAcceptedBetween(new Date(0),new Date(oldest)).size();
		for(int i = 0; i < days; i++){
			open[i] = all.queryCreatedBetween(new Date(oldest), new Date(oldest+i*(oneday))).size() + constant_open;
			closed[i] = all.queryCreatedAndAcceptedBetween(new Date(oldest), new Date(oldest+i*(oneday))).size() + constant_closed;
			if(i % 7 == 0){
				GregorianCalendar help = new GregorianCalendar();
				help.setTime(new Date(oldest+i*(oneday)));
				labels[i] = help.get(Calendar.YEAR)+"."+(help.get(Calendar.MONTH)+1)+"."+help.get(Calendar.DATE);
			}
			else{
				labels[i] = "";
			}
		}
				
		data = new LinkedHashMap<String,int[]>();
		data.put("Opened", open);
		data.put("Closed", closed);
		
		page.println(CommonHTML.wrapWindow("overview","Backlog Daily Activity", SVGPrinter.labeledLineGraph(data, closed[0], open[open.length-1], labels, "        ",2),"    "));
		
		
		int iteration_start = project.getCurrentIteration()- 8;
		try{
			iteration_start = new Integer(CookieManager.matchCookie(cookies, "iteration_start").getValue()).intValue();
			if(project.getCurrentIteration() - iteration_start > 8){
				iteration_start = project.getCurrentIteration() - 8;
			}
		}
		catch (NumberFormatException e){
			System.err.println("[WARNING] Parsing exception on iteration. Using default 1.");
			System.out.println("[WARNING] Parsing exception on iteration. Using default 1.");
		}
		start = project.getStart().getTime() + (iteration_start-1)*project.getIterationSize();
		data = new LinkedHashMap<String,int[]>();
		int max_all = 0;
		for(int iteration = iteration_start; iteration <= project.getCurrentIteration();iteration++){
			int[] weekly = new int[(int)((now-start)/oneday)+2];
			int allstories = project.getStories().queryLabel("["+iteration+"]").size();
			if(allstories > 0){
				for(long i = start + (iteration-iteration_start)*project.getIterationSize(); i < now; i+=oneday){
					int accepted = project.getStories().queryLabel("["+iteration+"]").queryAcceptedBetween(new Date(start), new Date(i)).size();
					weekly[(int)((i-start)/oneday)] = allstories - accepted;
					
				}
				if(allstories > max_all){
					max_all = allstories;
				}
				data.put("Iteration "+iteration, weekly);
			}
		}
		if(now > start){
			labels = new String[(int)((now-start)/oneday)+1];
		}
		for(int i = 0; start+i*oneday < now; i++){
			if(i % 7 == 0){
				GregorianCalendar help = new GregorianCalendar();
				help.setTime(new Date(start+i*(oneday)));
				labels[i] = help.get(Calendar.YEAR)+"."+(help.get(Calendar.MONTH)+1)+"."+help.get(Calendar.DATE);
			}
			else{
				labels[i] = "";
			}
		}
		
		page.println(CommonHTML.wrapWindow("overview","Planning Daily Burn-Down", SVGPrinter.labeledLineGraph(data, 0, max_all, labels, "      ",1),"    "));
		
		String s = "";
				
		HashMap<String,float[]> piechart;
		piechart = new HashMap<String,float[]>();
		float total = (float)non_resolved.size();
		float rest = total - (non_resolved.queryState("unscheduled").size()+non_resolved.queryState("unstarted").size());
		piechart.put("icebox", new float[]{non_resolved.queryState("unscheduled").size()/total});
		piechart.put("backlog", new float[]{non_resolved.queryState("unstarted").size()/total});
		piechart.put("started", new float[]{rest/total});
		
		s+="    <table width='100%' cellpadding=0 cellspacing=0 border=0>\n";
		s+="      <tr>\n";
		s+="        <td width='300' align='center' valign='top'>\n";
		s+=SVGPrinter.percentualPieChart(piechart,300,200,"          ");

		piechart = new HashMap<String,float[]>();
		piechart.put("bugs", new float[]{non_resolved.queryType("bug").size()/total});
		piechart.put("chores", new float[]{non_resolved.queryType("chore").size()/total});
		piechart.put("features", new float[]{non_resolved.queryType("feature").size()/total});
		piechart.put("releases", new float[]{non_resolved.queryType("release").size()/total});
		s+=SVGPrinter.percentualPieChart(piechart,300,200,"          ");
		
		piechart = new HashMap<String,float[]>();
		int sum = 0;
		for(String t: CookieManager.extractLabels(cookies)){
			TicketSet temp = non_resolved.queryLabel(t);
			float percentage = temp.size()/(float)non_resolved.size();
			piechart.put(t, new float[]{percentage});
			sum += temp.size();
		}
		piechart.put("others", new float[]{(non_resolved.size()-sum)/(float)non_resolved.size()});
				
		s+=SVGPrinter.percentualPieChart(piechart,300,200,"          ");
		
		s+="        </td>\n";
		s+="      </tr>\n";
		s+="      <tr>\n";
		s+="        <td colspan='2'>\n";
		s+=CommonHTML.ticketTable("Open Stories", non_resolved, "          ")+"\n";
		s+="        </td>\n";
		s+="      </tr>\n";
		s+="    </table>\n";
		
		page.println(CommonHTML.wrapWindow("overview_status","Current Status", s,"    "));
		
		page.println("  </div>");

		page.println(CommonHTML.getFooter("  "));
		
		page.println("</body>");
		page.println("</html>");
	}
}
