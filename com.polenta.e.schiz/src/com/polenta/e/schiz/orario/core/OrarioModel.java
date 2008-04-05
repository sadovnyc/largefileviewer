package com.polenta.e.schiz.orario.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import com.polenta.e.schiz.orario.activity.ActivityFragment;


/**
 * Model of a timesheet (orario).
 * The model keeps a list of days, each implementing the
 * IDay interface.
 * Two sets of days are kept internally:
 * - the days which have an entry stored
 * - a full list, where 'empty' days are created to fill
 * the full array.
 *
 */
public class OrarioModel implements IOrario
{
	/** Array of real entries */
    private OrarioDay[] days;
    /** minimum year for which an entry exist */
    private int minYear;
    /** maximum year for which an entry exist */
    private int maxYear;
    private ActivityFragment[] activities;
    //listeners
    private ArrayList<IOrarioModelListener> listeners = new ArrayList<IOrarioModelListener>();
    
    
    /**
     * Build a model using the specified day array.
     * 
     * @param dayArray the array of days to use for the model (can be NULL).
     */
    public OrarioModel(OrarioDay[] dayArray)
    {
    	set(dayArray);
    }
    
    /* (non-Javadoc)
	 * @see orario.core.IOrarioModel#set(orario.core.OrarioDay[])
	 */
    public void set(OrarioDay[] dayArray)
    {
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        int i;
        int currYear;
        
        if(dayArray == null)
    		dayArray = new OrarioDay[0];

    	days = dayArray;
    	
        minYear = 3000;
        maxYear = 0;
        
        //find min and max years
        for(i = 0 ; i < days.length ; i++ )
        {
            cal.setTime(days[i].getDayDate());
            currYear = cal.get(Calendar.YEAR);
            if(currYear < minYear)
                minYear = currYear;
            if(currYear > maxYear)
                maxYear = currYear;
        }

        fireModelChange();
    }
    
    /* (non-Javadoc)
	 * @see orario.core.IOrarioModel#setDay(orario.core.IDay)
	 */
    public void setDay(IDay day)
    {
    }
        
    /* (non-Javadoc)
	 * @see orario.core.IOrarioModel#getDays()
	 */
    public OrarioDay[] getDays()
    {
        return days;
    }

	/* (non-Javadoc)
	 * @see orario.core.IOrarioModel#getMaxYear()
	 */
	public int getMaxYear()
	{
		return maxYear;
	}

	/* (non-Javadoc)
	 * @see orario.core.IOrarioModel#getMinYear()
	 */
	public int getMinYear()
	{
		return minYear;
	}
	
	/**
	 * Create an array of activity fragments collecting
	 * all intervals in the model.
	 * Note that the activity fragments are created but the
	 * intervals they reference are in the model.
	 *  
	 * @return the array of activity fragments
	 */
	ActivityFragment[] getActivityFragments()
	{
		HashMap<String,ActivityFragment> map = new HashMap<String,ActivityFragment>();
		ActivityFragment activity;
		
		if(activities == null)
		{
			
			if(days != null)
				for (OrarioDay day : days)
				{
					if( day != null && day.getWorkedIntervals() != null)
						for (OrarioInterval interval : day.getWorkedIntervals())
						{
							activity = map.get(interval.getActivityName());
							if(activity == null)
							{
								activity = new ActivityFragment();
								map.put(interval.getActivityName(), activity);
							}
							activity.addInterval(interval);
						}
				}
			activities = new ActivityFragment[map.size()];
			activities = map.entrySet().toArray(activities);
		}
		return activities;
	}

	/* (non-Javadoc)
	 * @see orario.core.IOrarioModel#addListener(orario.core.IOrarioModelListener)
	 */
	public void addListener(IOrarioModelListener listener)
	{
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see orario.core.IOrarioModel#removeListener(orario.core.IOrarioModelListener)
	 */
	public void removeListener(IOrarioModelListener listener)
	{
		listeners.remove(listener);
	}
	
	private void fireModelChange()
	{
		//use a copy of the listener list to support removal
		//during callback
		IOrarioModelListener[] toNotify = listeners.toArray(new IOrarioModelListener[listeners.size()]);
		
		for (IOrarioModelListener listener : toNotify) {
			listener.modelChanged(this);
		}
	}
	
}
