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
package org.arkanos.pivotal_analytics.pivotal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * The {@code Project} class models a Pivotal Tracker Project.
 * 
 * Not all information provided by the Pivotal Tracker API is processed.
 * Only data relevant to Pivotal Analytics is extracted from the documents.
 *  
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
public class Project {
	/** All Project Stories (Bugs, Features, Chores and Releases) **/
	private TicketSet stories;
	
	/** Current Iteration Number **/
	int current_iteration = 0;
	
	/** Iteration size in ms **/
	long iteration_size = 0;
	
	/** Exact date where the first Iteration starts **/
	Date iterations_start = null;
	
	/** Name of the Project **/
	String name;
	
	/** Name of the Company using the Project **/
	String company;
	
	/**
	 * Downloads and constructs a given project.
	 * 
	 * @param projectID specifies Pivotal ID reference to the Project.
	 * @param token specifies User API Token from Pivotal.
	 * @param offset defines the number of stories to be ignored from the oldest accepted.
	 */
	public Project(int projectID, String token, String offset){
		PivotalAPI api = new PivotalAPI(token);
		String downloaded = api.downloadProject(projectID);
		JSONParser jp = new JSONParser();
		try {
			JSONObject jo = (JSONObject) jp.parse(downloaded);
			String date_start = (String)jo.get("start_date");
			
			iterations_start = new SimpleDateFormat("yyyy-MM-dd").parse(date_start);
			 
			current_iteration = ((Long)jo.get("current_iteration_number")).intValue();
			
			int size = ((Long)jo.get("iteration_length")).intValue();
			iteration_size = size*7*24*60*60*1000;
			
			name = (String)jo.get("name");
			company = (Long)jo.get("account_id")+"";
			
			loadStories(projectID,token,offset);		
		} catch (ParseException e) {
			System.out.println("[ERROR:ParseException] There was an Exception while parsing Pivotal Project Content, see error logs for stack trace.");
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			System.out.println("[ERROR:ParseException] There was an Exception while parsing Pivotal Project JSON, see error logs for stack trace.");
			e.printStackTrace();
		}		
	}
	
	/**
	 * Downloads the stories for the project.
	 * Method broken from constructor for clarity and modularization.
	 * 
	 * @param projectID specifies Pivotal ID reference to the Project.
	 * @param token specifies User API Token from Pivotal.
	 * @param offset defines the number of stories to be ignored from the oldest accepted.
	 */
	private void loadStories(int projectID, String token, String offset){
		PivotalAPI api = new PivotalAPI(token);
		Vector<String> downloaded = api.downloadProjectContent(projectID);
		JSONParser jp = new JSONParser();
		JSONArray list;
		try {
			stories = new TicketSet();
			for(String d: downloaded){
				list = (JSONArray)jp.parse(d);
	
				
				for(int i = 0; i < list.size(); i++){
					stories.add(new Ticket((JSONObject)list.get(i)));
				}
			}
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets all stories.
	 * @return stories.
	 */
	public TicketSet getStories(){
		return stories;
	}
	
	/**
	 * Gets when the Project (and iterations) started.
	 * @return the date when iterations started.
	 */
	public Date getStart(){
		return iterations_start;
	}
	
	/**
	 * Gets the current iteration.
	 * @return the current iteration.
	 */
	public int getCurrentIteration() {
		return current_iteration;
	}
	
	/**
	 * Gets the iteration size in ms.
	 * @return the iteration size.
	 */
	public long getIterationSize(){
		return iteration_size;
	}

	/**
	 * Gets the project title.
	 * @return the project title.
	 */
	public String getDisplayName() {
		return company+", "+name;
	}

	

}
