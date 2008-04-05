package com.polenta.e.schiz.orario.core;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OrarioInterval implements Comparable<OrarioInterval>
{
	int start;
	int end;
	String activityName;
	String activityType;
	
	/**
	 * Creates an interval, given the start and end time in minutes.
	 * e.g for an interval from 10:00 to 10:30:
	 * - start = 600 (10*60)
	 * - end = 630 (
	 * Name and Type are set to empty strings.
	 * an IllegalArgumentException is thrown if start >= end
	 * 
	 * @param start start time in minutes since midnight
	 * @param end end time in minutes since midnight
	 * 
	 * @throws IllegalArgumentException
	 */
	public OrarioInterval(int start, int end)
	{
		// TODO: check that start and end are on the same day
		if(start < end)
		{
			this.start = start;
			this.end = end;
			this.activityName = this.activityType = "";
		}
		else
			throw new IllegalArgumentException();
	}
	
	/**
	 * Creates an interval, given the start and end time as Date objects.
	 * Note that the interval express 
	 * 
	 * @param start start time as Date object
	 * @param end end time as Date Object
	 */
	public OrarioInterval(Date start, Date end)
	{
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        
		// TODO: check that start and end are on the same day
		if(start.before(end))
		{
			cal.setTime(start);
			this.start = cal.get(Calendar.MINUTE) + 60 * cal.get(Calendar.HOUR_OF_DAY);
			cal.setTime(end);
			this.end = cal.get(Calendar.MINUTE) + 60 * cal.get(Calendar.HOUR_OF_DAY);;
		}
	}
	
	/* (non-Javadoc)
	 * Return the interval duration in the form hh:mm.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		int diff = end - start;
		
		return diff/60 + ":" + diff % 60;
	}
	
	public int geDurationInMinutes()
	{
		return end - start;
	}
	
	public void setActivity(String name, String type)
	{
		activityName = name;
		activityType = type;
	}

	public String getActivityName()
	{
		return activityName;
	}

	public String getActivityType()
	{
		return activityType;
	}

	/**
	 * This implementation returns true iff obj is an instance
	 * of OrarioInterval and has same:
	 * - start
	 * - end
	 * - activity name
	 * - activity type
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof OrarioInterval))
			return false;
		OrarioInterval other = (OrarioInterval)obj;
		
		if(this == other)
			return true;

		if(this.start == other.start &&
				this.end == other.end &&
				this.activityName.equals(other.activityName) &&
				this.activityType.equals(other.activityType))
			return true;

		return false;
	}

	public int getEnd()
	{
		return end;
	}

	public int getStart()
	{
		return start;
	}

	/**
	 * Compare this to another interval.
	 * @param other
	 * @return
	 */
	public int compareTo(OrarioInterval other)
	{
		return this.start - other.start;
	}
	
	
}
 