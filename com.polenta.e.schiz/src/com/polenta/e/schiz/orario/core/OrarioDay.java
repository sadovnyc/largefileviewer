package com.polenta.e.schiz.orario.core;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OrarioDay implements IDay
{
	//private final long MS_PER_HOUR = 1000*60*60;
	//private final long MS_PER_MINUTE = 1000*60;
    private String comment;

    private Date dayDate;
    private OrarioInterval[] workedIntervals;
    
    private boolean stored = true;
    
    //formatter used to parse and display date
    private SimpleDateFormat dateformat = new SimpleDateFormat("yy.MM.dd");
    
    /**
     * Build an OrarioDay with standard hours at
     * the specified date. Comment is set to:
     * "default"
     * The stored attribute is set to false.
     * 
     * @param d the date
     * @param intervals intervals worked during the day
     */
    public OrarioDay(Date d, OrarioInterval[] intervals, String comment)
    {
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        
    	dayDate = d;
    	this.comment = comment;
    	
    	if(intervals != null)
    		stored = true;
    	else
    		stored = false;
    	
    	workedIntervals = intervals;
    	
    	cal.setTime(d);
    }
    
    public Date getDayDate()
    {
        return dayDate;
    }

	public boolean isStored()
	{
		return stored;
	}
	
	public int getWorkedHours()
	{
		return getWorkedMinutes() / 60;
	}
	
	public int getWorkedMinutes()
	{
		int duration = 0;
		if(workedIntervals != null)
			for (OrarioInterval interval : workedIntervals)
				duration += interval.geDurationInMinutes();
		return duration;
	}
	
	public String toString()
	{
        Calendar cal = Calendar.getInstance(Locale.ITALY);
            	
    	cal.setTime(dayDate);
    	
    	return dateformat.format(dayDate);
	}
	
	public OrarioInterval[] getWorkedIntervals()
	{
		return workedIntervals;
	}
    
	public void setWorkedIntervals(OrarioInterval[] intervals)
	{
		workedIntervals = intervals;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	public boolean setDay(IDay day)
	{
		//empty implementation
		return false;
	}

	@Override
	public boolean equals(Object arg0)
	{
		if(!(arg0 instanceof OrarioDay))
			return false;

		OrarioDay other = (OrarioDay)arg0;

		if(this == other)
			return true;

		if(this.dayDate.equals(other.dayDate) && this.comment.equals(other.comment) && Arrays.equals(this.workedIntervals, other.workedIntervals))
			return true;

		return false;
	}
}
