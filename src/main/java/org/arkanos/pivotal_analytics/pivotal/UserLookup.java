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

import java.util.HashMap;

import org.json.simple.JSONObject;

public class UserLookup {
	/** Map from ID to Username **/
	HashMap<String,String> users = null;
	
	/**
	 * Constructs an empty lookup table for users.
	 */
	public UserLookup(){
		users = new HashMap<String,String>();
	}
	
	/**
	 * Adds a new user to the lookup table.
	 * @param jo contains the user as a JSON object.
	 */
	public void addUser(JSONObject jo){
		users.put(jo.get("id").toString(),(String)jo.get("name"));
	}
	
	/**
	 * Retrieves the user from the given key.
	 * @param key specifies the string form of the user ID.
	 * @return the user name if the ID is in the lookup, null otherwise.
	 */
	public String getUser(String key){
		return users.get(key);
	}
}
