package com.polenta.e.schiz.orario.core;

import java.util.Date;

/**
 * Interface for the day model.
 * 
 */
public interface IDay
{
	/**
	 * Get the number of minutes worked in the day.
	 * 
	 * @return minutes worked
	 */
	public int getWorkedMinutes();
	
	public boolean setDay(IDay day);
	
	/**
	 * Get the date of this day.
	 * 
	 * @return date for this day.
	 */
	public Date getDayDate();

	/**
	 * Get an array of the intervals of worked time
	 * during this day.
	 * 
	 * @return the array of intervals.
	 */
	public OrarioInterval[] getWorkedIntervals();
}
