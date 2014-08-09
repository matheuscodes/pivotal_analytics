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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.arkanos.pivotal_analytics.printers.CommonHTML;

/**
 * The {@code About} class serves Pivotal Analytics information and instructions page.
 *  
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
@WebServlet({"/About","/Home","/index.htm","/index.html","/main"})
public class About extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter page = response.getWriter();
		
		page.println("<html>");
		page.println(CommonHTML.getBasicHeaders("Pivotal Analytics - About"));
		page.println("<body>");
		
		page.println(CommonHTML.getMenu("  "));
		page.println("  <div class='content' id='text'>");
		
		page.println("    <h1>About Pivotal Analytics</h1>");
		page.println("     <p align='center'> Pivotal Analytics is a web tool for statistical observation and performance measurement of Pivotal Projects");
		page.println("     <table>\n");
		page.println("       <tr>\n");
		page.println("         <td width=50% align=right>Copyright &copy; 2013</td>\n");
		page.println("         <td><img src='icons/logo.svg' width=24/></td>\n");
		page.println("         <td width=50%>Matheus Borges Teixeira</td>\n");
		page.println("       </tr>\n");
		page.println("     </table>\n");
		
		page.println("     Icon Design by <a href='http://www.dryicons.com/'>DryIcons.com</a> <br>");
		page.println("     Pivotal Analytics is released under <strong><a href='GNUAffero'>GNU Affero GPL</a></strong> <br>");
		
		page.println("     Pivotal Tracker is project management tool from <a href='http://pivotallabs.com/'>Pivotal Labs, Inc.</a><br/>");
		page.println("     Code can be found in <a href='https://github.com/matheuscodes/pivotal_analytics'>GitHub</a> but Javadoc can be found <strong><a href='doc'>here.</a></strong> <br>");
		page.println("     For reporting Bugs either contact the Author via email to <strong>matheus.bt</strong> at <strong>gmail.com</strong> or report directly an <a href='https://github.com/matheuscodes/pivotal_analytics/issues'>issue in GitHub.</a></p>");
		
		page.println("    <h3>Purge <img src='icons/download.png' /></h3>");
		page.println("    <p>This will remove all user data from Pivotal Analytics caches and delete all cookies.</p>");
		
		page.println("    <h3>Refresh <img src='icons/refresh.png' /></h3>");
		page.println("    <p>Pivotal Analytics does not renew the Project snapshot on its own.<br />");
		page.println("      If the user wishes to reload the project, this button must be triggered.</p>");
		
		page.println("    <h3>Configurations <img src='icons/tools.png' /></h3>");
		page.println("    <p>All parameters here are mandatory and need to be properly filled.<br />");
		page.println("      In case of missing data, normal pages will not be loaded and user will be routed to this.</p>");
		page.println("    <ul>");
		page.println("      <li><strong>User Token: </strong>Can be obtained in Pivotal &gt; Profile. Used for authentication.</li>");
		page.println("      <li><strong>Project ID: </strong>Pivotal ID of the project desired. <br />");
		page.println("        Note that the above given User Token must have access to this project.<br />");
		page.println("        Only one Project can be used per user at a time. </li>");
		page.println("      <li><strong>Special Labels: </strong>A list of labels, separated by commas.<br />");
		page.println("        Will be used for highlighting some porportions in the Project and Developer overviews.</li>");
		page.println("      <li><strong>Iteration to start Follow Up: </strong>Number of the iteration where this tool will start reporting from.<br />");
		page.println("        Note that it must be lower or equal than the Project current iteration, in case of wrong input the fallback is 1.</li>");
		page.println("      <li><strong>Date for Reference: </strong>Specifies a date where the graphs will start plotting data. <br />");
		page.println("        If the wrong format is specified, the fallback date is the oldest available in the project.</li>");
		page.println("      <li><strong>Pivotal API Offset:</strong> Specifies how many of the oldest accepted stories must be skipped.<br />");
		page.println("        This is to be used for big projects due to the Pivotal API Vesion 3 limitation on a max download of 3000 stories.</li>");
		page.println("    </ul>");
		
		page.println("    <h3>Info and About <img src='icons/info.png' /></h3>");
		page.println("    <p>Opens this page.</p>");
		
		page.println("    <h3>Overview</h3>");
		page.println("    <p>Displays statistics at project level, compiling a dashboard.</p>");
		page.println("    <h5>Graphs</h5>");
		page.println("    <ul>");
		page.println("      <li><strong>Team Velocity for Features:</strong> plots the variation of the team's weekly velocity for features.<br />");
		page.println("        For the calculation of a given week, the two previous weeks are taken into consideration, independently of iteration size.<br />");
		page.println("        Note that this   only counts the features <strong>accepted</strong> during the course of the week and its storypoints.</li>");
		page.println("      <li><strong>Request Response Time in Days: </strong>plots the time stories take to be completed, from the date of creation to date of acceptance.<br />");
		page.println("        The graph looks into all stories"); 
		page.println("      <strong>accepted</strong> during the course of the week, plotting min, max and average of that week. </li>");
		page.println("      <li><strong>Backlog Daily Activity: </strong>plots the number of closed and created stories on a daily basis.</li>");
		page.println("      <li><strong>Planning Daily Burn-Down: </strong>plots the daily count of open stories for each iteration.<br />");
		page.println("        <em>Note that special labels need to be added to planned tickets with [iteration_number] in order to make the graph work.<br />");
		page.println("        This is necessary for Pivotal has no long-term follow up on iterations, and non completed tickets automatically move. </em></li>");
		page.println("    </ul>");
		page.println("    <h5>Status</h5>");
		page.println("    <ul>");
		page.println("      <li><strong>Pie Charts: </strong>displays the distribution of the open stories in three categories.");
		page.println("        <ul>");
		page.println("          <li>Status (Backlog,  Started or Icebox);</li>");
		page.println("          <li>Story Type (Bug, Feature, Chore or Release);</li>");
		page.println("          <li>Among the labels  specified in the configuration.</li>");
		page.println("        </ul>");
		page.println("      </li>");
		page.println("      <li><strong>List: </strong>displays all open stories and their status, owner, labels and type. </li>");
		page.println("    </ul>");
		
		page.println("    <h3>Starvation</h3>");
		page.println("    <p>Displays an overview of the waiting time in open stories.<br />");
		page.println("      The number of days which a story has been waiting can be seen in the center of the bar.<br />");
		page.println("      The bar will be completely full on the course of one year wait.<br />");
		page.println("      Any stories which have a label containing the words <i>on hold</i> will be displayed in gray, otherwise in blue.<br />");
		page.println("      The stories are divided in two groups: Unscheduled (Icebox) and Scheduled (Backlog &amp; Current)<br />"); 
		page.println("      There are a couple of filters which can be used, based on how much wait time is wanted.</p>");
		
		page.println("    <h3>Throughput</h3>");
		page.println("    <p>Displays a weekly overview of how many stories were requested and how many were accepted. <br />");
		page.println("      There are four graphs, one which displays the overall count, and one for each story type (Bug, Feature and Chore).</p>");
		
		page.println("    <h3> Developers  </h3>");
		page.println("    <p>Displays an overview based on the Owners of active stories.</p>");
		page.println("    <h5>Task Load</h5>");
		page.println("    <p>If no Owner is selected, an overview of the load for each unique Owner of open stories in the project is displayed.<br />");
		page.println("      Pivotal Analytics looks into the entire available history and calculates the typical iteration delivery of the Owner and compares with the current assigments.<br />");
		page.println("      The load calculation takes into consideration both number of stories (all types) and its sizes (in case of features).<br />");
		page.println("    If the current assignment is over 100%, the bar will gradually change from black to red.<br />");
		page.println("    When the color becomes fully vivid red means that the Owner has 120% or more of its typical iteration delivery assigned. </p>");
		page.println("    <h5>Developer Overview</h5>");
		page.println("    <p>Once a Owner is selected, detailed information can be observed.</p>");
		page.println("    <ul>");
		page.println("      <li><strong>Story Distribution: </strong>the current assignments are divided in three categories.");
		page.println("        <ul>");
		page.println("          <li>Story Type (Bugs, Features, Chores and Releases);</li>");
		page.println("          <li>Story State (All possible states available in Pivotal);</li>");
		page.println("          <li>Among the labels  specified in the configuration.</li>");
		page.println("        </ul>");
		page.println("      </li>");
		page.println("      <li><strong>Delivery Count:</strong> plots the weekly delivered story counts.<br />");
		page.println("        An individual count for each Story Type is also available.</li>");
		page.println("      <li><strong>Velocity Overview: </strong>plots the velocity only for features and its sizes.</li>");
		page.println("      <li><strong>Recent assigned Stories:</strong> are the current and open stories assigned to the selected Owner.</li>");
		page.println("    </ul>");
		
		page.println("    <h3>Planning Follow Up</h3>");
		page.println("    <p>Displays a detailed overview of each planning. <br />");
		page.println("      <em>Note that special labels need to be added to planned tickets with [iteration_number] in order to make this entire view to work.<br />");
		page.println("      This is necessary for Pivotal has no long-term follow up on iterations, and non completed tickets automatically move.   </em></p>");
		page.println("    <p>A list will be provided, with all iterations which have a proper label set, by clicking any of those, the completion statistics will be shown.<br />");
		page.println("      They clarify how much of the iteration is complete and how much was actually delivered in time.<br />");
		page.println("      Three further lists will be given:</p>");
		page.println("    <ul>");
		page.println("      <li><strong>Stories Planned for Iteration X:</strong> lists all stories which contain the label [X] with its states, type, labels and owner. </li>");
		page.println("      <li><strong>Sidetracking Stories created and completed during Iteration X: </strong>lists all stories which were created and accepted while the iteration X was ongoing.</li>");
		page.println("      <li><strong>Previously Accumulated Stories decluttered during Iteration X:</strong> lists all other older stories, which were accepted while the iteration X was ongoing.</li>");
		page.println("    </ul>");
		page.println("    <p>Further distributions are displayed, giving absolute counts of the lists and the actual in-time deliveries.</p>");
		
		page.println("    <h3>All Stories</h3>");
		page.println("    <p>Displays a list with all downloaded stories in the project. </p>");
		page.println("  </div>");

		page.println(CommonHTML.getFooter("  "));
		
		page.println("</body>");
		page.println("</html>");
	}
}
