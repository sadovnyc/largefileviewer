package com.polenta.e.schiz.orario.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

/**
 * Wraps an OrarioModel with a hierarchical presentation:
 * the days are grouped in years and weeks.
 */
public class OrarioFormat implements IOrario, IOrarioModelListener
{
	/** The array of years */
	private ArrayList<OrarioYear> years;

	/** The underlying model */
	private IOrario model;
	
	/**
	 * Get the years.
	 * Note that this getter returns the internal collection,
	 * that should not be changed directly.
	 * 
	 * @return the collection containing the years
	 */
	public Collection<OrarioYear> getYears()
	{
		return years;
	}

	/**
	 * Get the amount of worked minutes in this timesheet.
	 * 
	 * @return the amount of worked minutes.
	 */
	public int getWorkedMinutes()
	{
		int minutes = 0;
		
		if(years != null)
			for (OrarioYear year : years)
				minutes += year.getWorkedMinutes();
		
		return minutes;
	}

	/**
	 * Constructor.
	 * Wraps the given model with a year-week-day
	 * presentation of the time sheet.
	 * 
	 * @param model the model to wrap
	 */
	public OrarioFormat(IOrario model)
	{
		this.model = model;

		OrarioDay[] days = model.getDays();
		int minYear = model.getMinYear();
		int maxYear = model.getMaxYear();
		
		years = new ArrayList<OrarioYear>(maxYear - minYear + 1);
		
		makeYears(minYear, maxYear, days);
		
		//register as listener
		model.addListener(this);
	}

    /**
     * Create the hierarchy.
     * OrarioYear and OrarioWeek object are created
     * as part of the presentation model while the IDay
     * instances are those belonging to the wrapped model.
     * 
     * @param minYear min year
     * @param maxYear max year
     * @param days array of days to use
     */
    private void makeYears(int minYear, int maxYear, OrarioDay[] days)
    {
    	int year;
    	int i;
    	Calendar cal = Calendar.getInstance(Locale.ITALY);
    	OrarioDay[] alldays = makeAllDays(minYear, maxYear, days);
    	
        for(year = minYear ; year <= maxYear ; year++)
        	years.add( new OrarioYear(year));
        
        for(i = 0 ; i < alldays.length ; i++)
        {
        	cal.setTime(alldays[i].getDayDate());
        	
        	year = cal.get(Calendar.YEAR);
        	
        	years.get(year-minYear).setDay(alldays[i]);
        }
    }
    
    /**
     * Generate from the days array another one containing
     * also empty (i.e. with no activities) days.
     * 
     * @param minYear
     * @param maxYear
     * @param days
     * @return
     */
    private OrarioDay[] makeAllDays(int minYear, int maxYear, OrarioDay[] days)
    {
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        int i,j,l,k;
        int dayCount;
        OrarioDay[] alldays;

        //TODO: take in account leap years more correctly
        dayCount = 0;
        for(i = minYear ; i <= maxYear ; i++)
            dayCount += 365 + ((i % 4 == 0) ? 1 : 0);
        
        alldays = new OrarioDay[dayCount];

        //fill the complete array
        j = 0;
        l = 0;
        for(i = minYear ; i <= maxYear ; i++)
            for(k = 1 ; k <= 365 + ((i % 4 == 0) ? 1 : 0) ; k++)
	        {
	            if(j < days.length)
	            	cal.setTime(days[j].getDayDate());
	            if(j < days.length && i == cal.get(Calendar.YEAR) && k == cal.get(Calendar.DAY_OF_YEAR))
	                alldays[l++] = days[j++];
	            else
	            {
	            	cal.set(Calendar.YEAR, i);
	            	cal.set(Calendar.DAY_OF_YEAR, k);
	                alldays[l++] = new OrarioDay(cal.getTime(), null, null);
	            }
	        }

        return alldays;
    }
    /**
     * Set a day on the underlying model.
     * If the day is not among the real entries, it
     * is added.
     * 
     * @param day the day to add.
     * @return true : the operation is supported.
     */
    public void setDay(IDay day)
    {
    	model.setDay(day);
    }

	/* (non-Javadoc)
	 * @see orario.core.IOrarioModelListener#modelChanged(orario.core.OrarioModel)
	 */
	public void modelChanged(IOrario source)
	{
		OrarioDay[] days = model.getDays();
		int minYear = model.getMinYear();
		int maxYear = model.getMaxYear();

		years.clear();

		makeYears(minYear, maxYear, days);		
	}

	/**
	 * Get the wrapped model.
	 * 
	 * @return the underlying model.
	 */
	public IOrario getModel()
	{
		return model;
	}

	public void addListener(IOrarioModelListener listener)
	{
		model.addListener(listener);
	}

	public OrarioDay[] getDays()
	{
		return model.getDays();
	}

	public int getMaxYear()
	{
		return model.getMaxYear();
	}

	public int getMinYear()
	{
		return model.getMinYear();
	}

	public void removeListener(IOrarioModelListener listener)
	{
		model.removeListener(listener);
	}

	public void set(OrarioDay[] dayArray)
	{
		model.set(dayArray);
	}
}
