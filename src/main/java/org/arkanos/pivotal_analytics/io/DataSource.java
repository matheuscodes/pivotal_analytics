/**
 *  Copyright (C) 2014 Matheus Borges Teixeira
 *  
 *  This file is part of Pivotal Analytics, a web tool for statistical
 *  observation and measurement of Pivotal Projects.
 *
 *  Pivotal Analytics is free so/goalsftware: you can redistribute it and/or 
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
package org.arkanos.pivotal_analytics.io;

import java.util.HashMap;

import org.arkanos.pivotal_analytics.pivotal.Project;

/**
 * The {@code DataSource} class handles in-memory Project data.
 * The Projects are stored in {@link java.util.HashMap Hash Maps}
 * that concatenate both User ID and Project ID, allowing each user
 * to have a different snapshot of the project independently.
 * 
 * This class is entirely based on static behavior!
 * 
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
public class DataSource {
	
	/** HashMap with all user projects loaded in memory **/
	static private HashMap<String,Project> loaded_projects;

	/**
	 * Reads the given project to the given user and stores it in memory.
	 * In case the project is already stored, it will use that copy.
	 * If it is not stored, the newest snapshot of the project will be loaded.
	 * Unless the project is {@linkplain #flushProject(int, String) flushed}, the snapshot won't be updated.
	 * 
	 * @param projectID specifies Pivotal ID reference to the Project.
	 * @param token specifies User API Token from Pivotal.
	 * @return the last project snapshot requested by the given user.
	 */
	static public Project readProject(int projectID, String token){
		if(loaded_projects == null){
			loaded_projects = new HashMap<String,Project>();
		}
		Project selected = loaded_projects.get(projectID+"/"+token);
		if(selected == null){
			Project newone = new Project(projectID,token);
			loaded_projects.put(projectID+"/"+token, newone);
			return newone;
		}
		else{
			return selected;
		}
	}

	/**
	 * Removes the loaded snapshot of a given project for a given user.
	 *
	 * @param projectID specifies Pivotal ID reference to the Project.
	 * @param token specifies User API Token from Pivotal.
	 */
	public static void flushProject(int projectID, String token) {
		if(loaded_projects != null){
			loaded_projects.remove(projectID+"/"+token);
		}
		return;
	}

}
