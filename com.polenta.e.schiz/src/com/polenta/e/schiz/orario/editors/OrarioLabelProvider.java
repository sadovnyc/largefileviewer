package com.polenta.e.schiz.orario.editors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

import com.polenta.e.schiz.orario.core.OrarioActivity;
import com.polenta.e.schiz.orario.core.OrarioCoreUtil;
import com.polenta.e.schiz.orario.core.OrarioDay;
import com.polenta.e.schiz.orario.core.OrarioWeek;
import com.polenta.e.schiz.orario.core.OrarioYear;

public class OrarioLabelProvider implements ILabelProvider, IColorProvider
{

    private SimpleDateFormat dateformat = new SimpleDateFormat("EE dd.MM");
    private Device colorDevice;
    
	public OrarioLabelProvider(Device colorDevice)
	{
		super();
		this.colorDevice = colorDevice;
	}

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element)
	{
		if(element == null)
			return "<empty>";
		
		if(element instanceof OrarioDay)
		{
			OrarioDay d = (OrarioDay)element;
			return dateformat.format(d.getDayDate()) + " (" + d.getWorkedMinutes()/60 + ":" + d.getWorkedMinutes()%60 +")";
		}
		if(element instanceof OrarioWeek)
		{
			OrarioWeek w = (OrarioWeek)element;
			Calendar cal = Calendar.getInstance(Locale.ITALY);
			
			cal.setTime(w.getDaysArray()[0].getDayDate());
			
			return "w" + cal.get(Calendar.WEEK_OF_YEAR) + " (" + w.getWorkedMinutes()/60 + ":" + w.getWorkedMinutes()%60 +")";
		}
		if(element instanceof OrarioYear)
		{
			OrarioYear y = (OrarioYear)element;
			
			return "year " + y.getYear() + " (" + y.getWorkedHours() + " hrs)";
		}
		if(element instanceof OrarioActivity)
		{
			OrarioActivity a = (OrarioActivity)element;
			
			return a.getName() + " (" + OrarioCoreUtil.formatToHours(a) + " hrs)";
		}
		
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public Color getForeground(Object element)
	{
		Color retVal = colorDevice.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
		
		if(element instanceof OrarioDay)
		{
			OrarioDay d = (OrarioDay)element;
	        Calendar cal = Calendar.getInstance(Locale.ITALY);
	    	cal.setTime(d.getDayDate());
	    	
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
					cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				retVal = colorDevice.getSystemColor(SWT.COLOR_RED);
		}

		return retVal;
	}

	public Color getBackground(Object element)
	{
		return colorDevice.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	}

}
