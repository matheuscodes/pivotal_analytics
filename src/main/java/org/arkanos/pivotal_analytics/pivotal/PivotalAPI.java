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

import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The {@code PivotalAPI} class wraps Pivotal Tracker API.
 * 
 * The Pivotal Tracker API version called from here is v3.0
 * This only takes care of handling the downloads.
 * No information is extracted from the contents.
 * 
 * Note: this class makes use of "offset" due to API v3.0 limitation.
 * From the API is only possible to get 3000 stories.
 * If project is bigger than that, this offset needs to be used.
 * 
 * This class is entirely based on static behavior!
 * 
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
public class PivotalAPI {
	
	/**URL used for connecting to Pivotal Tracker API**/
	public static String API_LOCATION_URL = "https://www.pivotaltracker.com/services/v5";
	//TODO: doc
	String token = null;
	//TODO: doc
	public PivotalAPI(String token){
		this.token = token;
	}
	
	/**
	 * Downloads the stories for a given project.
	 * 
	 * @param projectID specifies Pivotal ID reference to the Project.
	 * @param token specifies User API Token from Pivotal.
	 * @param offset defines the number of stories to be ignored from the oldest accepted.
	 * @return an XML Document provided by the API.
	 */
	public Vector<String> downloadProjectContent(int projectID){
		Vector<String> pages = new Vector<String>();
		int max = 1;
		int current = 0;
		int page = 500;
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		System.out.println("----------------------------------------");
        try {
        	HttpGet httpget = new HttpGet(API_LOCATION_URL + "/projects/" + projectID + "/stories?limit=100000");
            httpget.addHeader("X-TrackerToken", token);
            System.out.println("Executing request for Project Content:\n" + httpget.getRequestLine());
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            System.out.println(response.getStatusLine());
            if(response.getFirstHeader("X-Tracker-Pagination-Total") != null){
            	max = Integer.parseInt(response.getFirstHeader("X-Tracker-Pagination-Total").getValue());
            }
            if(response.getFirstHeader("X-Tracker-Pagination-Limit") != null){
            	page = Integer.parseInt(response.getFirstHeader("X-Tracker-Pagination-Limit").getValue());
            }
            while (entity != null && current < max) {
            	System.out.println("Response content length: " + entity.getContentLength());
            	String result = "";
                int r = 0;
                byte[] b = new byte[2048];
                do{
                	
                	r = entity.getContent().read(b);
                	if(r > 0){
                		result += new String(b,0,r);
                	}
                } while(r > 0);
                

                pages.add(result);

                current += page;
                if(current < max){
                	httpclient.close();
                	httpclient = HttpClientBuilder.create().build();
	                httpget = new HttpGet(API_LOCATION_URL + "/projects/" + projectID + "/stories?limit="+page+"&offset="+current);
	                httpget.addHeader("X-TrackerToken", token);
	                System.out.println("Executing request for Project Content:\n" + httpget.getRequestLine());
	                response = httpclient.execute(httpget);
	                entity = response.getEntity();
	                System.out.println(response.getStatusLine());
                }
            }
            /**Releasing System and Connection resources**/
            httpclient.close();
        } catch (ClientProtocolException e) {
        	System.out.println("[ERROR:ClientProtocolException] Error while downloading file, see error logs for stack trace.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[ERROR:IOException] Error while saving the downloaded file, see error logs for stack trace.");
			e.printStackTrace();
		}
        System.out.println("----------------------------------------");
        return pages;	
	}
		
	/**
	 * Downloads the basic data for a given project.
	 * 
	 * @param projectID specifies Pivotal ID reference to the Project.
	 * @param token specifies User API Token from Pivotal.
	 * @return an XML Document provided by the API.
	 */
	public String downloadProject(int projectID){
		String result = null;
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        System.out.println("----------------------------------------");
        try {
        	HttpGet httpget = new HttpGet(API_LOCATION_URL + "/projects/" + projectID + "");
            httpget.addHeader("X-TrackerToken", token);
            System.out.println("Executing request for Project:\n" + httpget.getRequestLine());
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
                result = "";
                int r = 0;
                byte[] b = new byte[2048];
                do{
                	r = entity.getContent().read(b);
                	if(r > 0){
                		result += new String(b,0,r);
                	}
                } while(r > 0);
            }
            /**Releasing System and Connection resources**/
            httpclient.close();
        } catch (ClientProtocolException e) {
        	System.out.println("[ERROR:ClientProtocolException] Error while downloading file, see error logs for stack trace.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[ERROR:IOException] Error while saving the downloaded file, see error logs for stack trace.");
			e.printStackTrace();
		}
        System.out.println("----------------------------------------");
        return result;
	}

	public String downloadUsers(int projectID) {
		String result = null;
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        System.out.println("----------------------------------------");
        try {
        	HttpGet httpget = new HttpGet(API_LOCATION_URL + "/projects/" + projectID + "/memberships");
            httpget.addHeader("X-TrackerToken", token);
            System.out.println("Executing request for Project:\n" + httpget.getRequestLine());
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
                result = "";
                int r = 0;
                byte[] b = new byte[2048];
                do{
                	r = entity.getContent().read(b);
                	if(r > 0){
                		result += new String(b,0,r);
                	}
                } while(r > 0);
            }
            /**Releasing System and Connection resources**/
            httpclient.close();
        } catch (ClientProtocolException e) {
        	System.out.println("[ERROR:ClientProtocolException] Error while downloading file, see error logs for stack trace.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[ERROR:IOException] Error while saving the downloaded file, see error logs for stack trace.");
			e.printStackTrace();
		}
        System.out.println("----------------------------------------");
        return result;
	}
}
