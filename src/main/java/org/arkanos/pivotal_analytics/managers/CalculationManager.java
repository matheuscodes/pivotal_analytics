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

/**
 * The {@code CalculationManager} class provides static methods for common calculations.
 * 
 * This class is entirely based on static behavior!
 * 
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
public class CalculationManager {
	
	/**
	 * Calculates the <i>velocity</i> based on a given set of integers.
	 * <i>Velocity</i> is defined by the average amount from the last three samples.
	 * No <i>velocity</i> is calculated for first three samples, using only base data.
	 * 
	 * @param base array with the delivered count
	 * @return array of the same size as the base, with calculated velocity.
	 */
	public static int[] calculateVelocity(int[] base){
		if(base.length > 3){
			int[] result = new int[base.length];
			result[0] = base[0];
			result[1] = base[1];
			result[2] = base[2];
			for(int i = 3; i < base.length;i++){
				result[i] = (base[i]+result[i-1]+result[i-2])/3;
			}
			return result;
		}
		return base;
	}
}
