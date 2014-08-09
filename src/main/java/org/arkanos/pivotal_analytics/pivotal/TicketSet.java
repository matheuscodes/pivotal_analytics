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

import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

/**
 * The {@code TicketSet} class models a group of Tickets extending an array.
 * This class also handles all search queries for Tickets.
 *  
 * The methods are implemented very simply, a lot of optimization can be done.
 * Due to intrinsic connection, and to keep code simple, makes reference
 * directly to Ticket fields instead of getters. 
 *  
 * @see org.arkanos.pivotal_analytics.pivotal.Ticket
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
public class TicketSet extends Vector<Ticket> {
	/** Mandatory serial version **/
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the TicketSet calling its Vector parent constructor.
	 */
	public TicketSet(){
		super();
	}
	
	/**
	 * Returns all Tickets accepted between the given dates.
	 * It does not include Tickets exactly at the start and end dates.
	 * 
	 * @param start defines the start date.
	 * @param end defines the end date.
	 * @return Tickets accepted in between the dates.
	 */
	public TicketSet queryAcceptedBetween(Date start, Date end){
		TicketSet result = new TicketSet();
		
		for(Object o: elementData){
			Ticket t = (Ticket)o;
			if(t!=null && t.acceptedAt!= null && t.acceptedAt.before(end) && t.acceptedAt.after(start)){
				result.add(t);
			}
		}
		return result;
	}
	
	
	/**
	 * Returns all Tickets created between the given dates.
	 * It does not include Tickets exactly at the start and end dates.
	 * 
	 * @param start defines the start date.
	 * @param end defines the end date.
	 * @return Tickets created in between the dates.
	 */
	public TicketSet queryCreatedBetween(Date start, Date end){
		TicketSet result = new TicketSet();
		
		for(Object o: elementData){
			Ticket t = (Ticket)o;
			if(t!=null && t.createdAt!= null && t.createdAt.before(end) && t.createdAt.after(start)){
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Returns all Tickets which are not in state "accepted"
	 * 
	 * @return all not yet accepted Tickets.
	 */
	public TicketSet queryActive() {
		TicketSet result = new TicketSet();
		
		for(Object o: elementData){
			Ticket t = (Ticket)o;
			if(t != null && t.currentState.compareTo("accepted") != 0){
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Returns all Tickets, which are in state "accepted" and have both
	 * creation and acceptance dates between a given range.
	 * 
	 * @param start defines the start date.
	 * @param end defines the end date.
	 * @return all Tickets create and also accepted between the dates.
	 */
	public TicketSet queryCreatedAndAcceptedBetween(Date start, Date end) {
		return this.queryCreatedBetween(start, end).queryAcceptedBetween(start, end);
	}
	
	/**
	 * Returns all Tickets that match to a given state.
	 * 
	 * @param state specifies the desired state.
	 * @return all tickets of the state.
	 */
	public TicketSet queryState(String state) {
		TicketSet result = new TicketSet();
		
		for(Object o: elementData){
			Ticket t = (Ticket)o;
			if(t!=null && t.currentState.equals(state)){
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Returns all Tickets that are assigned to a given owner.
	 * 
	 * @param owner specifies the desired owner.
	 * @return all tickets assigned to the owner.
	 */
	public TicketSet queryOwner(String owner){
		TicketSet result = new TicketSet();
		
		for(Object o: elementData){
			Ticket t = (Ticket)o;
			if(t!=null && t.ownedBy != null &&  t.ownedBy.indexOf(owner) >= 0){
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Returns all Tickets that match to a given label.
	 * 
	 * Known issue: partial matches leading to ambiguity.
	 * 
	 * @param label specifies the desired label.
	 * @return all tickets which contain the given label.
	 */
	public TicketSet queryLabel(String label){
		TicketSet result = new TicketSet();
		
		for(Object o: elementData){
			Ticket t = (Ticket)o;
			if(t!=null && t.labels!=null && t.labels.indexOf(label) >= 0){
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Returns all Tickets that do not match to a given label.
	 * 
	 * Known issue: partial matches leading to ambiguity.
	 * 
	 * @param label specifies the undesired label.
	 * @return all tickets which do not contain the given label.
	 */
	public TicketSet queryNotLabel(String label){
		TicketSet result = new TicketSet();
		
		for(Object o: elementData){
			Ticket t = (Ticket)o;
			if(t!=null){
				if(t.labels!=null && t.labels.indexOf(label) < 0){
					result.add(t);
				}
				else{
					if(t.labels ==null){
						result.add(t);
					}
				}
			}
				
		}
		return result;
	}
	
	/**
	 * Returns all Tickets that match to a given type.
	 * @param type specifies the desired type. 
	 * @return all tickets of the type.
	 */
	public TicketSet queryType(String type){
		TicketSet result = new TicketSet();
		
		for(Object o: elementData){
			Ticket t = (Ticket)o;
			if(t!=null && t.StoryType!=null && t.StoryType.equals(type)){
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Returns a String array with all possible owners.
	 * Each owner is given only once.
	 *  
	 * @return array of unique owners.
	 */
	public String[] queryUniqueOwners(){
		HashSet<String> owners = new HashSet<String>();
		
		for(Object o: elementData){
			Ticket t = (Ticket)o;
			if(t!=null && t.ownedBy != null){
				if(t.ownedBy.length()>0){
					owners.add(t.ownedBy);
				}
			}
		}
		String[] results = new String[owners.size()];
		int i = 0;
		for(Object o: owners.toArray()){
			results[i++] = o.toString();
		}
		return results;
	}
	
	/**
	 * Returns all possible Ticket states.
	 * @return all possible Ticket states.
	 */
	public String[] queryUniqueStates(){
		return new String[]{"accepted","rejected","delivered","finished","started","unstarted","unscheduled"};
	}
	
	/**
	 * Returns all possible Ticket types.
	 * @return all possible Ticket types.
	 */
	public String[] queryUniqueTypes(){
		return new String[]{"bug","chore","feature","release"};
	}
		
	/**
	 * Returns the oldest created Ticket which is not in state "accepted".
	 * 
	 * @return the oldest active Ticket.
	 */
	public Ticket queryOldestActive(){
		TicketSet result = this.queryActive();
		Ticket oldest = null;
		for(Ticket t: result){
			if(oldest == null || oldest.getCreated().getTime() > t.getCreated().getTime()){
				oldest = t;
			}
		}
		return oldest;
	}
	
	/**
	 * Returns the oldest accepted Ticket.
	 * 
	 * @return the oldest accepted Ticket.
	 */
	public Ticket queryOldestAccepted(){
		Ticket oldest = null;
		for(Ticket t: this){
			//TODO npe fixed, review
			if(t != null && t.getState().compareTo("accepted") == 0 && (oldest == null || (oldest.getAccepted() != null && oldest.getAccepted().getTime() > t.getAccepted().getTime()))){
				oldest = t;
			}
		}
		return oldest;
	}
	
	/**
	 * Returns all tickets which are not in a given state
	 * @param state specifies the undesired state.
	 * @return all tickets which are not in the state.
	 */
	public TicketSet queryNotState(String state){
		TicketSet result = new TicketSet();
		
		for(Object o: elementData){
			Ticket t = (Ticket)o;
			if(t != null && t.currentState.compareTo(state) != 0){
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString(){
		String printed = "TicketSet: "+this.size()+"\n";
		for(Object o: elementData){
			if(o != null){
				Ticket t = (Ticket)o;
				printed = printed.concat(t+"\n");
			}
		}
		return printed;
	}
}
