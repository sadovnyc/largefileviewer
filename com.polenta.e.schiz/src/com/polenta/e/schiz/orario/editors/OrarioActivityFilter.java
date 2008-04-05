package com.polenta.e.schiz.orario.editors;

import java.util.HashSet;


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.polenta.e.schiz.orario.core.IDay;
import com.polenta.e.schiz.orario.core.OrarioDay;
import com.polenta.e.schiz.orario.core.OrarioInterval;
import com.polenta.e.schiz.orario.core.OrarioWeek;

public class OrarioActivityFilter extends ViewerFilter
{
	private HashSet<String> namesToFilter = new HashSet<String>(); 

	public void addNameToFilter(String name)
	{
		namesToFilter.add(name);
	}
	
	public void removeNameToFilter(String name)
	{
		namesToFilter.remove(name);
	}
	
	public void resetFilter()
	{
		namesToFilter.clear();
	}
	
	public boolean select(Viewer viewer, Object parent, Object element)
	{
		System.out.println(element);
		
		if (element != null )
		{
			if( element instanceof OrarioDay)
				if( isDayToFilter((OrarioDay) element))
					return false;
			if(element instanceof OrarioWeek)
			{
				if(isWeekToFilter((OrarioWeek)element))
					return false;
			}
		}
		return true;
	}

	private boolean isDayToFilter(OrarioDay day)
	{
		if(day.getWorkedIntervals() != null)
			for (OrarioInterval interval : day.getWorkedIntervals())
			{
				for (String activityName : namesToFilter)
				{
					if(activityName.startsWith(interval.getActivityName()))
						return true;
				}
				return false;
			}
		return true;
	}

	private boolean isWeekToFilter(OrarioWeek week)
	{
		if(week.getDays() != null)
		{
			for (IDay day : week.getDays())
			{
				if(!isDayToFilter((OrarioDay)day))
					return false;
			}
			return true;
		}
		else
			return true;
	}
}
