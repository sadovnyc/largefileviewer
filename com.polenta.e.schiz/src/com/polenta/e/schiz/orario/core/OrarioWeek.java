package com.polenta.e.schiz.orario.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class OrarioWeek implements IDayComposite
{
	private OrarioDay days[] = new OrarioDay[7];
	private int year;
	private int week;

	public OrarioWeek(int year, int week)
	{
		int i;
		
        Calendar cal = Calendar.getInstance(Locale.ITALY);
    	cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR,week);
		cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		this.year = year;
		this.week = week;
		
    	for(i = 0 ; i < 7 ; i++)
    		days[i] = null;
	}
	
	public Collection<IDay> getDays()
	{
		Collection<IDay> daysCollection = new ArrayList<IDay>(days.length);
		
		for (OrarioDay day : days)
			if(day != null)
				daysCollection.add(day);
		return daysCollection;
	}
	
	public OrarioDay[] getDaysArray()
	{
		return days;
	}
	
	public int getWorkedHours()
	{
		return getWorkedMinutes() / 60;
	}
	
	public int getWorkedMinutes()
	{
		int minutes = 0;
		
		for (IDay day : days)
			if(day != null)
				minutes += day.getWorkedMinutes();
		
		return minutes;
	}
	
	public boolean setDay(OrarioDay day)
	{
        Calendar cal = Calendar.getInstance(Locale.ITALY);

		cal.setTime(day.getDayDate());
		if(cal.get(Calendar.YEAR) == year &&
				cal.get(Calendar.WEEK_OF_YEAR) == week)
		{
			int dayIndex = OrarioCoreUtil.getDayIndex(cal.get(Calendar.DAY_OF_WEEK));
			days[dayIndex] = day;
			return true;
		}
		else
			return false;
	}
	
	public boolean setDay(IDay day)
	{
		//operation supported for OrarioDay only
		if(day instanceof OrarioDay)
			return setDay((OrarioDay)day);
		else
			return false;
	}

	/**
	 * This implementation returns the date of the
	 * first day of the week.
	 * 
	 * @return the date of the first day of the week.
	 */
	public Date getDayDate()
	{
		return days[0].getDayDate();
	}

	public OrarioInterval[] getWorkedIntervals()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
