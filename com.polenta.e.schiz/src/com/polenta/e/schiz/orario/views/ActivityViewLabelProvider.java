package com.polenta.e.schiz.orario.views;


import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.polenta.e.schiz.orario.activity.ActivityFragment;

public class ActivityViewLabelProvider implements ITableLabelProvider
{
	private int workingHoursInDay = 8;
	private boolean showDurationInDays = true;

	public Image getColumnImage(Object element, int columnIndex)
	{
		//no images
		return null;
	}

	public String getColumnText(Object element, int columnIndex)
	{
		String retVal = null;
		
		if(element != null && element instanceof ActivityFragment)
		{
			switch(columnIndex)
			{
				case 0:
					retVal = formatActivityDuration(((ActivityFragment)element).getWorkedMinutes());
					break;
				case 1:
					retVal = ((ActivityFragment)element).getActivityName();
					break;
				default:
					retVal = "invalid column";
					break;
			}
		}
		return retVal;
	}

	public void addListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	private String formatActivityDuration(int durationInMinutes)
	{
		if(showDurationInDays)
			return "" + (durationInMinutes/(60*workingHoursInDay)) + "." + (durationInMinutes % (60*workingHoursInDay) * 10 / (60*workingHoursInDay)) +"d";
		else
			return "" + (durationInMinutes/60) + "h" + (durationInMinutes % 60) + "m";
	}

	public boolean isShowDurationInDays()
	{
		return showDurationInDays;
	}

	public void setShowDurationInDays(boolean showDurationInDays)
	{
		this.showDurationInDays = showDurationInDays;
	}


}
