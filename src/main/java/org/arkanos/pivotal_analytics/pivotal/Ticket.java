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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The {@code Ticket} class models a Pivotal Tracker story.
 * Stories can be Bugs, Features, Chores or Releases.
 * 
 * Not all information provided by the Pivotal Tracker API is processed.
 * Only data relevant to Pivotal Analytics is extracted from the documents.
 *  
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
public class Ticket {
	/** Unique ID for the Ticket **/
	long ID;
	/** Ticket summary **/
	String title;
	/** Comma separated list of labels **/
	String labels;
	/** Iteration the Ticket is currently assigned to **/
	int Iteration;
	/** Ticket type **/
	String StoryType;
	/** Estimated size of the Ticket **/
	int estimate;
	/** Defines the state of the ticket **/
	String currentState;
	/** Creation date **/
	Date createdAt;
	/** Closure date **/
	Date acceptedAt;
	/** Requester of the Ticket **/
	String requestedBy;
	/** Owner of the Ticket **/
	String ownedBy;
	/** URL to open the Ticket directly in Pivotal **/
	URL URL;
	
	/**
	 * Constructs the Ticket based on an XML node given by the API.
	 * 
	 * @param xmlnode defines the particular node for the desired Ticket.
	 */
	public Ticket(JSONObject jo,UserLookup users){
		try{
			this.ID = ((Long)jo.get("id")).intValue();
			this.StoryType = (String)jo.get("story_type");
			this.URL = new URL((String)jo.get("url"));
			if(jo.get("estimate") != null){
				this.estimate = ((Long)jo.get("estimate")).intValue();
			}
			else{
				this.estimate = 0;
			}
			this.currentState = (String)jo.get("current_state");
			this.title = (String)jo.get("name");
			this.requestedBy = (Long)jo.get("requested_by_id")+"";

			this.ownedBy = null;
			if(jo.get("owner_ids") != null){
				JSONArray owners = (JSONArray)jo.get("owner_ids");
				for(Object o: owners.toArray()){
					if(this.ownedBy != null){
						this.ownedBy += ",";
						this.ownedBy += users.getUser(o.toString());
					}
					else{
						this.ownedBy = users.getUser(o.toString());
					}
				}
			}
			
			this.createdAt = DatatypeConverter.parseDateTime((String)jo.get("created_at")).getTime();
			if(currentState.compareTo("accepted") == 0){
				this.acceptedAt = DatatypeConverter.parseDateTime((String)jo.get("accepted_at")).getTime();
			}
			else{
				this.acceptedAt = null;
			}
			
			this.labels = null;
			for(Object l: ((JSONArray)jo.get("labels")).toArray()){
				if(this.labels != null){
					this.labels += ","+((JSONObject)l).get("name");
				}
				else{
					this.labels = ((JSONObject)l).get("name").toString();
				}
				
			}
		}
		catch (MalformedURLException e){
			System.out.println("Malformed URL. "+(String)jo.get("url"));
			//e.printStackTrace();
		}
		catch (NullPointerException e){
			System.out.println("Ticket could not be made, strange null error. Data: "+jo.toJSONString());
			//e.printStackTrace();
		}		
	}
	
	/**
	 * Gets the title of the Ticket.
	 * @return the title.
	 */
	public String getTitle(){
		return title;
	}
	
	/**
	 * Gets the estimated size of the Ticket.
	 * @return the estimated size.
	 */
	public int getPoints(){
		return estimate;
	}
	
	/**
	 * Gets the date when the Ticket was created.
	 * @return the created date.
	 */
	public Date getCreated(){
		return createdAt;
	}
	
	/**
	 * Gets the date when the Ticket was accepted.
	 * @return the accepted date.
	 */
	public Date getAccepted(){
		return acceptedAt;
	}
	
	/**
	 * Gets the Ticket URL with a direct link to Pivotal.
	 * @return the direct link to Pivotal.
	 */
	public URL getURL(){
		return URL;
	}

	/**
	 * Gets the type of the Ticket.
	 * Expected values: bug, feature, chore or release.
	 * @see org.arkanos.pivotal_analytics.pivotal.TicketSet#queryUniqueTypes()
	 * @return the type.
	 */
	public String getType() {
		return StoryType;
	}

	/**
	 * Gets the state of the Ticket.
	 * Expected values: unscheduled, unstarted, started, finished, delivered, accepted or rejected.
	 * @see org.arkanos.pivotal_analytics.pivotal.TicketSet#queryUniqueStates()
	 * @return the state.
	 */
	public String getState() {
		return currentState;
	}

	/**
	 * Gets the comma separated values of labels in the Ticket.  
	 * @return the labels.
	 */
	public String getLabels() {
		return labels;
	}

	/**
	 * Gets the owner of the Ticket.
	 * @return the owner.
	 */
	public String getOwner() {
		return ownedBy;
	}

	/**
	 * Gets the ID of the Ticket.
	 * @return the ID.
	 */
	public long getID() {
		return ID;
	}
	
	public String toString(){
		return this.ID + " - "+ this.title;
	}
	
}
