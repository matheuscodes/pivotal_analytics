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

import java.util.Map;

/**
 * The {@code SVGPrinter} class handles creation of SVG images.
 * Implementation accordingly to Scalable Vector Graphics (SVG) 1.1 (Second Edition).
 * W3C Recommendation 16 August 2011.
 * 
 * This class is entirely based on static behavior!
 * 
 * @version 1.0
 * @see http://www.w3.org/TR/2011/REC-SVG11-20110816/
 * @author Matheus Borges Teixeira
 */
public class SVGPrinter {

	/** Set of colors to use for classes not overwritten via CSS **/
	static String[] colors = {"#FF0000","#00FF00","#0000FF","#FFFF00","#00FFFF","#FF00FF","#CCCCCC","#8000FF","#FF8000","#00FF80","#808000","#008080","#800080","#777777","#804040","#408040","#404080","#000000"};
	
	
	/** Space in pixels for labels in the Y axis for line graphs**/
	static int LEFT_GAP = 30;
	/** Space in pixels for labels in the X axis for line graphs **/
	static int BOTTOM_GAP = 100;
	/** Space in pixels for data and color labels for line graphs**/
	static int LABEL_GAP = 160;
	/** Space in pixels forming a border for line graphs **/
	static int BORDER = 2;
	/** Reference width size in pixels for line graphs**/
	static int width = 6*LABEL_GAP;
	/** Reference height size in pixels for line graphs **/
	static int height = 4*BOTTOM_GAP;
	
	/**
	 * Returns a SVG image with a line graph.
	 * All data must have a title and a content.
	 * A legend will be printed on the right side.
	 * All data arrays share the same X axis labels and must have the same quantity.
	 * The Y axis is always broken in 10 blocks, proportional to the max value.
	 * 
	 * The image will be built in fixed proportions using {@link #width width} and {@link #height height}.
	 *
	 * @param data defines the data to be plotted on the graph.
	 * @param min_value informs the minimum value inside the data set.
	 * @param max_value informs the maximum value inside (or desired for) the data set.
	 * @param points defines the labels for X axis.
	 * @param indent defines the string which will prefix all printed lines.
	 * @param skip specifies an amount of X axis labels to skip, in case of long sets of data.
	 * @return a complete SVG image with the plotted data in form of a String.
	 */
	public static String labeledLineGraph(Map<String,int[]> data,int min_value, int max_value, String[] points, String indent, int skip){
		String output = new String();
		float y_canvas = height - BOTTOM_GAP - 2*BORDER;
		float unit =  y_canvas / (float)(max_value-min_value);
		float gap = (width - LABEL_GAP - LEFT_GAP - 2*BORDER) / (float)points.length;

		output += indent+"<svg xmlns='http://www.w3.org/2000/svg' version='1.1' viewBox='0 0 "+width+" "+height+"' preserveAspectRatio='xMidYMid meet'>\n";
		if(data.entrySet().size() <= 0){
			output += indent+"</svg>";
			return output;
		}
		
		for(int layer = 0; layer < 10; layer++){
			float level = height-layer*unit*(max_value-min_value)/10.0f-BOTTOM_GAP+BORDER;
			output += indent+"  <path d='M "+ (LEFT_GAP + BORDER) +" "+ level;
			output += " L "+(width - LABEL_GAP - BORDER)+" "+level+"' stroke='#CCC'/>\n";
			output += indent+"  <text text-anchor='end' x='"+(LEFT_GAP+BORDER)+"' y='"+level+"' font-size='85%'>";
			output += (int)(min_value+(layer*(max_value-min_value)/10.0f))+"";
			output += "</text>\n";
		}
		
		int i = 0;
		for(Map.Entry<String, int[]> d: data.entrySet()){
			String s = "<path class='"+d.getKey()+"' d='M ";
			int j = 0;
			for(int k: d.getValue()){
				if(j == 0){
					s += (LEFT_GAP+BORDER+j*gap)+" "+(height-(k*unit)-BOTTOM_GAP+min_value*unit)+" C "+(LEFT_GAP+BORDER+j*gap+gap)+","+(height-(k*unit)-BOTTOM_GAP+min_value*unit)+" ";
				}
				else {
					s += (LEFT_GAP+BORDER+j*gap-gap)+","+(height-(k*unit)-BOTTOM_GAP+min_value*unit)+" "+(LEFT_GAP+BORDER+j*gap)+" "+(height-(k*unit)-BOTTOM_GAP+min_value*unit)+" S ";
				}
				j++;
			}
			
			char[] temp = s.toCharArray();
			temp[temp.length-2] = 'L';
			s = String.valueOf(temp);
			s += (LEFT_GAP+BORDER+(j-1)*gap)+" "+(height-BOTTOM_GAP);
			s += " L "+(LEFT_GAP+BORDER)+" "+(height-BOTTOM_GAP);
			output += indent+"  " + s + "' ";
			output += "fill='"+colors[i%colors.length]+"' fill-opacity='0.5' ";
			output += "stroke='"+colors[i%colors.length]+"' stroke-width='2'/>\n";
			i++;
		}
		i = 0;
		for(String s: points){
			/** Skipping on graphs with too many labels **/
			if(i % skip == 0 && i > 1){
				output += indent+"  <g transform='translate("+(int)(LEFT_GAP+i*gap+BORDER)+","+(height-BOTTOM_GAP+BORDER)+")'>\n";
				output += indent+"    <g transform=rotate(-60)>\n";
				output += indent+"      <text text-anchor='end' x='-5' y='5' font-size='85%'>";
				output += s + "</text>\n";
				output += indent+"    </g>\n";
				output += indent+"  </g>\n";
			}
			i++;
		}
		
		
		int labels_gap = ((6*height)/10)/data.entrySet().size();
		int label = height/2 - ((data.entrySet().size()-1)*labels_gap)/2;
		
		i = 0;
		for(Map.Entry<String, int[]> d: data.entrySet()){
			/**
			 * "Random" numbers:
			 * 10 is the gap between data-line-text
			 * 20 is the length of the line 
			 */
			output += indent+"  <path  class='"+d.getKey()+"' d=' M "+(width - LABEL_GAP + 10 - BORDER)+" "+label;
			output += " L "+(width - LABEL_GAP + 10 + 20 - BORDER)+" "+label;
			output += "' stroke-width='2' stroke='"+colors[i%colors.length]+"'/>\n";
			output += indent+"  <text text-anchor='start' x='"+(width - LABEL_GAP + 10 + 20 + 10 - BORDER)+"' y='"+label+"' font-size='85%'>";
			output += d.getKey() + "</text>\n";
			i++;
			label += labels_gap;
		}
			
		output += indent+"</svg>\n";
		return output;		
	}
	
	/**
	 * Draws only a slice of the pie chart.
	 * This is a helper method for the main printer method.
	 * 
	 * @param center_x defines the x coordinate of the center of the pie in the image.
	 * @param center_y defines the y coordinate of the center of the pie in the image.
	 * @param radius defines the radius of the pie.
	 * @param percentage_start specifies where, in proportion, the slice should start.
	 * @param percentage defines the size, in proportion, of the slice.
	 * @param title defines the name (CSS class) of the slice.
	 * @param color defines a color for the slice.
	 * @return the complete {@code <path>} node for the slice in form of a String.
	 */
	static private String pieSlice(int center_x, int center_y, int radius, float percentage_start, float percentage, String title, String color){
		String output = new String();
		
		float start_dx = (float) (Math.cos(2*Math.PI*percentage_start)*radius);
		float start_dy = (float) (Math.sin(2*Math.PI*percentage_start)*radius);
		
		float end_dx = (float) (Math.cos(2*Math.PI*(percentage_start+percentage))*radius);
		float end_dy = (float) (Math.sin(2*Math.PI*(percentage_start+percentage))*radius);
		
		if(percentage < 1.0f){		
			output += "<path class='"+title+"' d='";
			output += "M "+ center_x + " " + center_y + " ";
			output += "l "+ start_dx + " " + start_dy + " ";
			
			output += "a "+ radius + " " + radius + " ";
			
			if(percentage > 0.5f){
				output += "0 1 1 ";
			}
			else{
				output += "0 0 1 ";
			}
			
			output += (end_dx-start_dx) + " " + (end_dy-start_dy) + " z' ";
			output += "fill='"+color+"' stroke='none' />\n";
		}
		else{
			output += "<circle class='"+title+"' cx='"+center_x+"' cy='"+center_y+"' ";
			output += "r='"+radius+"' fill='"+color+"' stroke='none' />";
		}
		return output;
	}
	
	/**
	 * Returns a SVG image with a pie chart graph.
	 * The start point for the data is the right side of the pie.
	 * The data is printed clockwise and is not bounded to 100%
	 * Please note if the sum of the content data is {@code >1} it will start overriding. 
	 * @param data defines the data with all parts to be in the chart.
	 * @param width defines the image width.
	 * @param height defines the image height.
	 * @param indent defines the string which will prefix all printed lines.
	 * @return a complete SVG image with the divided pie graph in form of a String.
	 */
	public static String percentualPieChart(Map<String,float[]> data, int width, int height, String indent){
		String output = new String();
		int LABEL_GAP = 120;
		int radius;
		if(8*(width - LABEL_GAP)/10 > 8*height/10){
			radius = 8*height/20;  
		}
		else{
			radius = 8*(width - LABEL_GAP)/20;
		}
		
		
		
		output += indent+"<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='"+width+"' height='"+height+"'>\n";
		if(data.entrySet().size() <= 0){
			output += indent+"</svg>";
			return output;
		}
		
		float position = 0f;
		int i = 0;
		for(Map.Entry<String, float[]> d: data.entrySet()){
			output += indent+"  "+pieSlice((width - LABEL_GAP)/2,height/2,radius,position,d.getValue()[0],d.getKey(),colors[i%colors.length]);
			position += d.getValue()[0];
			i++;
		}
		
		int labels_gap = ((6*height)/10)/data.entrySet().size();
		int label = height/2 - ((data.entrySet().size()-1)*labels_gap)/2;
		
		i = 0;
		for(Map.Entry<String, float[]> d: data.entrySet()){
			output += indent+"  <rect class='"+d.getKey()+"' x='"+(width - LABEL_GAP + 10)+"' y='"+(label-10)+"' ";
			output += "height='10' width='10'";
			output += "stroke='black' fill='"+colors[i%colors.length]+"'/>\n";
			output += indent+"  <text text-anchor='start' x='"+(width - LABEL_GAP + 10 + 30)+"' y='"+label+"' font-size='85%'>";
			output += d.getKey() + "</text>\n";
			i++;
			label += labels_gap;
		}
		
		output += indent+"</svg>";
		return output;
	}
	
	/**
	 * Returns a SVG image with a progress bar.
	 * The bar is the whole image with its percentage in the center.
	 *
	 * @param data specifies the current state of the progress bar.
	 * @param max_value specifies what value is considered to be 100%
	 * @param width defines the image width.
	 * @param height defines the image height.
	 * @param color_fill defines a color for the filling by name or hex code.
	 * @param color_text defines a color for the text by name or hex code.
	 * @param indent defines the string which will prefix all printed lines.
	 * @return a complete SVG image filled according to the state in form of a String.
	 */
	public static String horizontalProgressBar(int data, int max_value, int height, int width, String color_fill, String color_text, String indent){
		String output = new String();
		float unit = width / (float)max_value;
		output += indent+"<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='"+width+"' height='"+height+"'>\n";
		output += indent+"  <rect x='0' y='0' width='"+(int)(data*unit)+"' height='"+height+"'";
		output += "fill='"+color_fill+"' stroke='none'/>\n";
		output += indent+"  <rect x='0' y='0' width='"+width+"' height='"+height+"'";
		output += "fill='none' stroke='black' stroke-width='1'/>\n";
		output += indent+"  <text x='"+width/2+"' y='"+(height-4)+"' text-anchor='middle'>\n";
		output += indent+"    <tspan font-size='80%' font-weight='bold' fill='black' >"+data+"</tspan>\n";
		output += indent+"  </text>\n";
		output += indent+"</svg>";
		return output;		
	}
	
	/**
	 * Returns a SVG image with a load bar.
	 * The bar is the whole image with its percentage in the center.
	 * Unlike the progress bar, this does not ignore values above 100%.
	 * The color of the bar will get a brighter red, if over 100%.
	 * The visual effect stops if value is higher than 120%.
	 *
	 * @param data specifies the current state of the progress bar.
	 * @param max_value specifies what value is considered to be 100%
	 * @param width defines the image width.
	 * @param height defines the image height.
	 * @param indent defines the string which will prefix all printed lines.
	 * @return a complete SVG image filled according to the state in form of a String.
	 */
	public static String horizontalLoadBar(float data, float max_value, int height, int width, String indent){
		String output = new String();
		int redism_level = (int)(((data-1)/(max_value-1))*255);

		output += indent+"<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='"+width+"' height='"+height+"'>\n";
		
		if(data >= 1){
			output += indent+"  <rect x='0' y='0' width='"+width+"' height='"+height+"'";
			output += "fill='#"+to2ByteHex(redism_level)+"0000' stroke='none'/>\n";			
		}
		else{
			output += indent+"  <rect x='0' y='0' width='"+(int)(data*width)+"' height='"+height+"'";
			output += "fill='black' stroke='none'/>\n";
		}
		
		
		output += indent+"  <rect x='0' y='0' width='"+width+"' height='"+height+"'";
		output += "fill='none' stroke='black' stroke-width='1'/>\n";
		output += indent+"  <text x='"+width/2+"' y='"+(height-4)+"' text-anchor='middle'>\n";
		if(redism_level > 0){
			output += indent+"    <tspan font-size='80%' font-weight='bold' fill='#"+to2ByteHex(redism_level)+"0000' >"+(int)(data*100)+"%</tspan>\n";
		}
		else{
			output += indent+"    <tspan font-size='80%' font-weight='bold' fill='#CCC' >"+(int)(data*100)+"%</tspan>\n";
		}
		output += indent+"  </text>\n";
		output += indent+"</svg>";
		return output;		
	}
	
	/**
	 * Transforms a number into its Hex Code.
	 * This is a helper method for colors.
	 * 
	 * @param x defines the number to be converted.
	 * @return the hex code of the number.
	 */
	static private String to2ByteHex(int x){
		int _a = x & 0x0F;
		int _b = (x & 0xF0) >> 4;
		if(x >= 255) return "FF";
		String a,b;
		switch(_a){
			case 10:
				a = "A";
				break;
			case 11:
				a = "B";
				break;
			case 12:
				a = "C";
				break;
			case 13:
				a = "D";
				break;
			case 14:
				a = "E";
				break;
			case 15:
				a = "F";
				break;
			default:
				a=""+_a;
				break;
		}
		
		switch(_b){
			case 10:
				b = "A";
				break;
			case 11:
				b = "B";
				break;
			case 12:
				b = "C";
				break;
			case 13:
				b = "D";
				break;
			case 14:
				b = "E";
				break;
			case 15:
				b = "F";
				break;
			default:
				b=""+_b;
				break;
		}
		return b+a;
	}
}
