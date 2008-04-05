package com.polenta.e.schiz.orario.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OrarioXMLHandler extends DefaultHandler
{

	public OrarioXMLHandler(Collection<String> activityCollection, Collection<String> activityTypeCollection)
	{
		activityNames = activityCollection;
		activityTypes = activityTypeCollection;
	}
	
	public void endElement( String inNamespaceURI, String inLocalName, String inQName ) throws SAXException
	{
		if(kDay.equals(inQName))
		{
			//day parsed, create element using collected data
			OrarioInterval[] intervalArray = new OrarioInterval[intervalList.size()];
			intervalList.toArray(intervalArray);
			OrarioDay d = new OrarioDay(dayDate, intervalArray, null);
			dayList.add(d);
		}
		if(kOrario.equals(inQName))
		{
			//orario fully parsed, generate the array of days
			days = new OrarioDay[dayList.size()];
			dayList.toArray(days);
			activityNames.addAll(activityList);
			activityTypes.addAll(activityTypeList);
			
			//give lists to garbage collector 
			dayList = null;
			intervalList = null;
		}
	}

	public void startElement( String inNamespaceURI, String inLocalName, String inQName, Attributes inAttributes ) throws SAXException
	{
		if(kOrario.equals(inQName))
		{
		}
		else if(kDay.equals(inQName))
		{
			String date = inAttributes.getValue(kDate);
			intervalList.clear();
			dayDate = OrarioCoreUtil.getDateFromString(date);
		}
		else if(kActivity.equals(inQName))
		{
			OrarioInterval interval;
			
			String range = inAttributes.getValue(kRange);
			String type = inAttributes.getValue(kType);
			String name = inAttributes.getValue(kName);
			
			interval = OrarioCoreUtil.getIntervalFromString(range);
			interval.setActivity(name,type);
			
			activityList.add(name);
			activityTypeList.add(type);
			
			//TODO interval should be linked to activity
			intervalList.add(interval);
		}
	}

	public OrarioDay[] getDays()
	{
		return days;
	}
	
	public String[] getActivityNames()
	{
		String[] names = new String[activityList.size()];
		
		activityList.toArray(names);
		
		return names;
	}

	//collected attributes for the day being parsed
	private ArrayList<OrarioInterval> intervalList = new ArrayList<OrarioInterval>(10);
	private ArrayList<OrarioDay> dayList = new ArrayList<OrarioDay>(400); //enough for a year
	private HashSet<String> activityList = new HashSet<String>();
	private HashSet<String> activityTypeList = new HashSet<String>();
	private Collection<String> activityNames;
	private Collection<String> activityTypes;
	
	private Date dayDate;
	private OrarioDay days[] = null;
	
	//XML tags handled here
	private static final String	kOrario				= "orario";
	private static final String	kDay				= "day";
	private static final String	kActivity			= "activity";

	//attributes
	private static final String	kDate				= "date";
	private static final String	kRange				= "range";
	private static final String	kType				= "type";
	private static final String	kName				= "name";

}
