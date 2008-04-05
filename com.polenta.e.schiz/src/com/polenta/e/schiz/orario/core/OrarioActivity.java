package com.polenta.e.schiz.orario.core;

import java.util.Date;
import java.util.HashSet;

public class OrarioActivity implements IDay
{
	private String name;
	private String type;
	private HashSet<OrarioDay> days = new HashSet<OrarioDay>();
	
	public OrarioActivity(String name, String type)
	{
		this.name = name;
		this.type = type;
	}
	
	public int getWorkedMinutes()
	{
		int  minutes = 0;
		OrarioInterval[] intervals;
		
		for (OrarioDay day : days)
		{
			intervals = day.getWorkedIntervals();
			if(intervals != null)
				for (OrarioInterval interval : intervals)
				{
					if(interval.getActivityName().equals(name))
						minutes += interval.geDurationInMinutes();
				}
		}
		return minutes;
	}

	public boolean setDay(IDay day)
	{
		if(day instanceof OrarioDay)
		{
			OrarioInterval[] intervals = ((OrarioDay) day).getWorkedIntervals();
			if(intervals != null)
				for (OrarioInterval interval : intervals)
				{
					if(interval.getActivityName().equals(name))
					{
						days.add((OrarioDay)day);
					}
				}
		}
		//operation not supported
		return true;
	}

	public String getName()
	{
		return name;
	}

	public String getType()
	{
		return type;
	}

	public Date getDayDate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public OrarioInterval[] getWorkedIntervals() {
		// TODO Auto-generated method stub
		return null;
	}
}
