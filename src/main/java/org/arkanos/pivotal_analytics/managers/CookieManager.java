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
package org.arkanos.pivotal_analytics.managers;

import java.util.Map;
import java.util.Vector;

import javax.servlet.http.Cookie;

/**
 * The {@code CookieManager} class takes care of HTTP {@link javax.servlet.http.Cookie Cookie} manipulation.
 * 
 * This class is entirely based on static behavior!
 * 
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
public class CookieManager {
	/** Sets Cookie lifetime to two weeks **/
	private static final int cookieMaxAge = 14*24*60*60;
	
	/**
	 * Creates a simple Vector with all desired Cookies from a String-to-String Map.
	 * 
	 * @param contents specifies the cookies to be created.
	 * @return all desired configured Cookies to be saved.
	 */
	public static Vector<Cookie> createCookies(Map<String,String> contents){
		Vector<Cookie> results = new Vector<Cookie>();
		for(Map.Entry<String, String> e: contents.entrySet()){
			Cookie newone = new Cookie(e.getKey(),e.getValue());
			newone.setVersion(1);
			newone.setMaxAge(cookieMaxAge);
			
			results.add(newone);
		}
		return results;
	}
	
	/**
	 * Counts and verifies that all required Cookies are in place.
	 *  
	 * @param cookies informs which cookies have been provided.
	 * @return true if all required cookies are in place, false otherwise.
	 */
	public static boolean countCookies(Cookie[] cookies){
		if(cookies == null || cookies.length <= 0) return false;
		int count = 0;
		for(int i = 0; i < cookies.length; i++){
			if(cookies[i].getName().compareTo("token") == 0){
				count++;
			}
			if(cookies[i].getName().compareTo("project_id") == 0){
				count++;
			}
			if(cookies[i].getName().compareTo("special_labels") == 0){
				count++;
			}
			if(cookies[i].getName().compareTo("iteration_start") == 0){
				count++;
			}
			if(cookies[i].getName().compareTo("date_start") == 0){
				count++;
			}
		}		
		/** Check if all needed Cookies were found **/
		return count == 5;
	}
	
	/**
	 * Searches for a particular Cookie in a given set.
	 * 
	 * @param set defines an array with all cookies.
	 * @param name specifies the name of the cookie to be found.
	 * @return null if the Cookie is not found, or the first match otherwise.
	 */
	public static Cookie matchCookie(Cookie[] set, String name){
		if(set == null) return null;
		for(Cookie c: set){
			if(c.getName().compareTo(name) == 0) return c;
		}
		return null;
	}
	
	/**
	 * Breaks one special Cookie into an array of Strings.
	 * This Cookie must contain the labels separated by comma.
	 * Usage of spaces should be avoided.
	 * 
	 * @param set specifies all given Cookies.
	 * @return an array with all broken labels as individual Strings.
	 */
	public static Vector<String> extractLabels(Cookie[] set){
		String c = matchCookie(set,"special_labels").getValue();
		Vector<String> data = new Vector<String>();
		c = c.replaceAll(", ",",");
		c = c.replaceAll(" , ",",");
		c = c.replaceAll(" ,",",");
		while(c.indexOf(",") > 0){
			data.add(c.substring(0,c.indexOf(",")));
			c = c.substring(c.indexOf(",")+1,c.length());
		}
		if(c.length()>0){
			data.add(c);
		}
		return data;
	}
	
}
