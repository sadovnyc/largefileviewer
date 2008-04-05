package com.polenta.e.schiz.orario.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class OrarioYear implements IDayComposite
{
	private OrarioWeek[] weeks;
	private int year;
	
	public OrarioYear(int year)
	{
		int weekIndex;
		
		this.year = year;
		
        Calendar cal = Calendar.getInstance(Locale.ITALY);
    	cal.set(Calendar.YEAR, year);

    	weeks = new OrarioWeek[cal.getActualMaximum(Calendar.WEEK_OF_YEAR) - cal.getActualMinimum(Calendar.WEEK_OF_YEAR) + 1];
    	
    	for(weekIndex = 0 ; weekIndex < weeks.length ; weekIndex++)
    		weeks[weekIndex] = new OrarioWeek(year,weekIndex + cal.getActualMinimum(Calendar.WEEK_OF_YEAR));
	}

	public OrarioWeek[] getWeeks()
	{
		return weeks;
	}
	
	public int getWorkedMinutes()
	{
		int minutes = 0;
		
		for (IDay week : weeks)
			if(week != null)
				minutes += week.getWorkedMinutes();
		
		return minutes;
	}
	
	public int getWorkedHours()
	{
		return getWorkedMinutes() / 60;
	}

	public boolean setDay(OrarioDay day)
	{
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        int weekIndex;
        
		cal.setTime(day.getDayDate());
        weekIndex = cal.get(Calendar.WEEK_OF_YEAR) - cal.getActualMinimum(Calendar.WEEK_OF_YEAR);
		if(cal.get(Calendar.YEAR) == year && weekIndex < weeks.length)
		{
			if(weeks[weekIndex] == null)
	    		weeks[weekIndex] = new OrarioWeek(year,weekIndex + cal.getActualMinimum(Calendar.WEEK_OF_YEAR));

			weeks[weekIndex].setDay(day);
			return true;
		}
		else
			return false;
	}

	public boolean setDay(IDay day)
	{
		if(day instanceof OrarioDay)
			return setDay((OrarioDay)day);
		else
			return false;
	}
	
	public int getYear()
	{
		return year;
	}

	public Collection<IDay> getDays()
	{
		Collection<IDay> weeksCollection = new ArrayList<IDay>(weeks.length);
		
		for (OrarioWeek week : weeks)
			if(week != null)
				weeksCollection.add(week);
		
		return weeksCollection;
	}
	
	/**
	 * This implementation returns the date of the
	 * first day of the year.
	 * 
	 * @return the date of the first day of the year.
	 */
	public Date getDayDate()
	{
		return weeks[0].getDayDate();
	}

	public OrarioInterval[] getWorkedIntervals() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
