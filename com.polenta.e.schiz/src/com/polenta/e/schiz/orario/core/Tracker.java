package com.polenta.e.schiz.orario.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Utility class to track user activity in real time.
 *
 */
public class Tracker
{
	private ArrayList<OrarioInterval> intervals = new ArrayList<OrarioInterval>();
	private Date currentStart;
	private String currentName;
	private String currentType;
	
	/**
	 * Start tracking an activity.
	 * If another activity was already ongoing it is stopped.
	 * 
	 * @param name activity name
	 */
	public void startActivity(String name, String type)
	{
		stopActivity();
		currentName = name;
		currentType = type;
		currentStart = new Date();
	}
	
	/**
	 * Stop tracking the current activity.
	 * Does nothing if no activity is started.
	 */
	public void stopActivity()
	{
		if(currentName != null)
		{
			OrarioInterval current = new OrarioInterval(currentStart,new Date());
			current.setActivity(currentName, currentType);
			intervals.add(current);
			currentName = null;
		}
	}
	
	/**
	 * Get the completed intervals.
	 * The current activity is not included.
	 * 
	 * @return the collection of intervals (not a copy!)
	 */
	public Collection<OrarioInterval> getIntervals()
	{
		return intervals;
	}
	
	/**
	 * Clear the list of completed intervals.
	 * The current activity is not deleted, to delete
	 * that one too, call stopActivity() before.
	 */
	public void clear()
	{
		intervals.clear();
	}
}
