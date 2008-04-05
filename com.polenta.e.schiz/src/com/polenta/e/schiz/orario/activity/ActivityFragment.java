package com.polenta.e.schiz.orario.activity;

import java.util.ArrayList;

import com.polenta.e.schiz.orario.core.OrarioInterval;

/**
 * Representation of 'some time spent on an activity'.
 * The fragment is a collection of intervals, and the current implementation
 * does not check for overlapping intervals or activity name.
 * 
 * @author fiannetti
 *
 */
public class ActivityFragment
{
	private String name;
	private ArrayList<OrarioInterval> intervals = new ArrayList<OrarioInterval>();
	
	/**
	 * Create an empty fragment, with no intervals.
	 */
	public ActivityFragment()
	{
		name = null;
	}
	
	/**
	 * Get the worked time in minutes.
	 * 
	 * @return the worked minutes
	 */
	public int getWorkedMinutes()
	{
		int minutes = 0;
		
		for (OrarioInterval interval : intervals)
			minutes += interval.geDurationInMinutes();
		
		return minutes;
	}
	
	public void addInterval(OrarioInterval interval)
	{
		intervals.add(interval);
		if(name == null)
			name = interval.getActivityName();
	}

	public String getActivityName()
	{
		return name;
	}
	
//	private void parseModel()
//	{
//		OrarioDay[] days = model.getDays();
//
//		intervals.clear();
//		
//		if(days != null)
//			for (OrarioDay day : days)
//			{
//				if( day != null && day.getWorkedIntervals() != null)
//					for (OrarioInterval interval : day.getWorkedIntervals())
//					{
//						if(name.equals(interval.getActivityName()))
//							intervals.add(interval);
//					}
//			}
//	}
}
