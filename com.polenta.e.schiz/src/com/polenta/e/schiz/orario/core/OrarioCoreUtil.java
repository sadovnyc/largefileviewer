package com.polenta.e.schiz.orario.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrarioCoreUtil
{
    //formatter used to parse and display time
    private static SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
    //formatter used to parse and display date
    private static SimpleDateFormat dateformat = new SimpleDateFormat("yy.MM.dd");

    //regexp pattern used to parse a line
    private static Pattern datepattern = Pattern.compile("\\d+.\\d+.\\d+");
    private static Pattern rangepattern = Pattern.compile("(\\d+:\\d+)-(\\d+:\\d+)");
    
	/**
	 * Translate a day as defined in java.util.Calendar in
	 * an index from 0 to 6, where 0 corresponds to Calendar.MONDAY,
	 * 1 to Calendar.TUESDAY ... 6 to Calendar.SUNDAY.
	 * 
	 * If the value specified as day is out of the valid range -1 is
	 * returned.
	 *  
	 * @param day day as specified in java.util.Calendar.
	 * @return the index, -1 upon error.
	 */
	public static int getDayIndex(int day)
	{
		int dayIndex = -1;
		
		switch(day)
		{
		case Calendar.MONDAY:
			dayIndex = 0;
			break;
		case Calendar.TUESDAY:
			dayIndex = 1;
			break;
		case Calendar.WEDNESDAY:
			dayIndex = 2;
			break;
		case Calendar.THURSDAY:
			dayIndex = 3;
			break;
		case Calendar.FRIDAY:
			dayIndex = 4;
			break;
		case Calendar.SATURDAY:
			dayIndex = 5;
			break;
		case Calendar.SUNDAY:
			dayIndex = 6;
			break;
		}
		
		return dayIndex;
	}

	public static String formatToHours(Date d)
	{
		return timeformat.format(d);
	}

	public static String formatToHours(OrarioActivity activity)
	{
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(Calendar.MINUTE, activity.getWorkedMinutes());
        return formatToHours(cal.getTime());
	}

	public static String formatToHours(int hours)
	{
		return hours/60 + ":" + hours % 60;
	}

	public static OrarioInterval getIntervalFromString(String str)
	{
		OrarioInterval interval = null;
		Matcher m = rangepattern.matcher(str);
		
		if(m.matches())
		{
			try
			{
				interval = new OrarioInterval(timeformat.parse(m.group(1)),timeformat.parse(m.group(2)));
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return interval;
	}
	
	public static Date getDateFromString(String str)
	{
		Date d = null;
		
        try
        {
            d = dateformat.parse(str);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        
		return d;
	}
	
	public static OrarioDay getOrarioDayFromString(String str)
	{
		Matcher m = datepattern.matcher(str);
		
		//find first group (date)
		if(m.find(1))
		{
			
		}

		return null;
	}
	
	public static boolean getOverlap(OrarioInterval first, OrarioInterval second)
	{
		boolean disjoint = first.getEnd() <= second.getStart() || first.getStart() >= second.getEnd(); 
		return !disjoint;
	}
}
