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
package org.arkanos.pivotal_analytics;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.arkanos.pivotal_analytics.pivotal.PivotalAPI;

/**
 * The {@code AppContextListener} class initializes the application context.
 * It redirects the Pivotal API base URL to the local mock endpoints,
 * allowing the application to function without a real Pivotal Tracker account.
 *
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce) {
        String port = System.getenv("PORT");
        if (port == null || port.isEmpty()) {
            port = System.getProperty("jetty.http.port", "8080");
        }
        PivotalAPI.API_LOCATION_URL = "http://localhost:" + port + "/services/v5";
        System.out.println("[INFO] Pivotal API redirected to local mock: " + PivotalAPI.API_LOCATION_URL);
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
