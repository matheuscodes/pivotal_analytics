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
package org.arkanos.pivotal_analytics.printers;

import org.arkanos.pivotal_analytics.pivotal.Ticket;
import org.arkanos.pivotal_analytics.pivotal.TicketSet;

/**
 * The {@code CommonHTML} class handles constant HTML code. Basically everything
 * that is common to all pages is here. Each method return an entire and
 * complete HTML Tag.
 * 
 * This class is entirely based on static behavior!
 * 
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
public class CommonHTML {

	/**
	 * Returns the top menu which contains links to control and other pages.
	 * 
	 * @param indent
	 *            defines the string which will prefix all printed lines.
	 * @return the complete {@code <div>} containing the top menu in form of a
	 *         String.
	 */
	public static String getMenu(String indent) {
		String output = new String();
		output += indent + "<div id='main_menu'>\n";

		output += indent + "  <a class='menu_item' href='Overview'>";
		output += "Overview" + "</a>\n";

		output += indent + "  <a class='menu_item' href='Starvation'>";
		output += "Starvation" + "</a>\n";

		output += indent + "  <a class='menu_item' href='Throughput'>";
		output += "Throughput" + "</a>\n";

		output += indent + "  <a class='menu_item' href='Developers'>";
		output += "Developers" + "</a>\n";

		output += indent + "  <a class='menu_item' href='PlanningFollowup'>";
		output += "Planning Follow Up" + "</a>\n";

		output += indent + "  <a class='menu_item' href='All'>";
		output += "All Stories" + "</a>\n";

		output += indent + "  <a class='button' href='About'>";
		output += "<img src='icons/info.png' /></a>\n";

		output += indent + "  <a class='button' href='Config'>";
		output += "<img src='icons/tools.png' /></a>\n";

		output += indent + "  <a class='button' href='Refresh'>";
		output += "<img src='icons/refresh.png' /></a>\n";
		
		output += indent + "  <a class='button' href='Refresh?purge=true'>";
		output += "<img src='icons/download.png' /></a>\n";

		output += indent + "</div>\n";
		return output;
	}

	/**
	 * Returns the footer which contains all copyright notices.
	 * 
	 * @param indent
	 *            defines the string which will prefix all printed lines.
	 * @return the complete {@code <div>} containing the copyright notices in
	 *         form of a String.
	 */
	public static String getFooter(String indent) {
		String output = new String();
		output += indent + "<div id='footer'>\n";

		output += indent + "  <table id='copyright' width=100%>\n";
		output += indent + "    <tr>\n";
		output += indent + "      <td width=50% align=right>Copyright &copy; 2014</td>\n";
		output += indent + "      <td><img src='icons/logo.svg' width=24/></td>\n";
		output += indent + "      <td width=50%>Matheus Borges Teixeira</td>\n";
		output += indent + "    </tr>\n";
		output += indent + "  </table>\n";

		output += indent + "  Icon Design by <a href='http://www.dryicons.com/'>DryIcons.com</a> <br>\n";

		output += indent + "  Pivotal Tracker is project management tool from <a href='http://pivotallabs.com/'>Pivotal Labs, Inc.</a><br/>";
		output += indent + "  Pivotal Analytics is released under <a href='GNUAffero'>GNU Affero GPL</a>\n";

		output += indent + "</div>\n";
		return output;
	}

	/**
	 * Returns the generic headers with a given title.
	 * 
	 * @param title
	 *            defines the desired page title.
	 * @return the whole HTML {@code <head>} tag in form of a String.
	 */
	public static String getBasicHeaders(String title) {
		String output = new String();
		output += "<head>\n";
		output += "  <meta charset='ISO-8859-1'>\n";
		output += "  <link rel='stylesheet' type='text/css' href='css/basic.css'>\n";
		output += "  <link rel='stylesheet' type='text/css' href='css/pivotal.css'>\n";
		output += "  <link rel='icon' type='image/png' href='icons/logo_small.png'>\n";
		output += "  <title>" + title + "</title>\n";
		output += "</head>\n";
		return output;
	}

	/**
	 * Returns a particular content wrapped inside a HTML {@code <div>} tag. The
	 * resulting {@code <div>} models a window-like view of the content. This
	 * method ignores indentation for content itself.
	 * 
	 * @param secondaryclass
	 *            defines a auxiliary CSS class.
	 * @param title
	 *            defines the title of the {@code <div>} window.
	 * @param content
	 *            specifies the content of the window.
	 * @param indent
	 *            defines the string which will prefix all printed lines.
	 * @return the whole HTML {@code <div>} tag in form of a String.
	 */
	public static String wrapWindow(String secondaryclass, String title, String content, String indent) {
		String output = new String();
		output += indent + "<div class='content-box " + secondaryclass + "'>\n";
		output += indent + "  <div class='content-box-header'>\n";
		output += indent + "    <h3>" + title + "</h3>\n";
		output += indent + "    <div class='clear'></div>\n";
		output += indent + "  </div>\n";
		output += indent + "  <div class='content-box-content'>\n";
		output += content;
		output += indent + "  </div>\n";
		output += indent + "</div>\n";
		return output;
	}

	/**
	 * Returns a set of stories wrapped around a HTML {@code <table>} tag. The
	 * state of the story is mapped in CSSs classes. The type of the story is
	 * displayed in an icon. Only titles and labels are printed. All text offer
	 * direct links to Pivotal.
	 * 
	 * @param title
	 *            defines a title to be given in this window-like table.
	 * @param content
	 *            specifies which stories are to be printed.
	 * @param indent
	 *            defines the string which will prefix all printed lines.
	 * @return the whole HTML {@code <table>} tag in form of a String.
	 */
	public static String ticketTable(String title, TicketSet content, String indent) {
		String output = new String();
		output += indent + "<table class='tickets' width='100%' border=0 cellspacing=0 cellpadding=0>\n";
		output += indent + "  <tr class='tickets_title'>\n";
		output += indent + "    <td scope='col' colspan='4'>" + title + "</td>\n";
		output += indent + "  </tr>\n";
		for (Ticket t : content) {
			output += indent + "  <tr class='" + t.getState() + "'>\n";
			output += indent + "    <td>";
			output += "<img src='icons/" + t.getType() + ".png' />";
			output += "</td>\n";
			output += indent + "    <td>\n";
			output += indent + "      <a href='" + t.getURL() + "'>";
			output += t.getTitle();
			output += "</a>\n";
			if (t.getLabels() != null) {
				output += indent + "      <span class='labels'>" + t.getLabels() + "</span>\n";
			}
			output += indent + "    </td>\n";
			output += indent + "    <td class='owner'>";
			if (t.getOwner() != null)
				output += t.getOwner();
			output += "</td>\n";
			output += indent + "  </tr>\n";
		}
		output += indent + "</table>\n";
		return output;
	}

}
