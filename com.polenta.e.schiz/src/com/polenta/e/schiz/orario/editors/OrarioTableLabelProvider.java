package com.polenta.e.schiz.orario.editors;


import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.polenta.e.schiz.orario.core.OrarioCoreUtil;
import com.polenta.e.schiz.orario.core.OrarioDay;
import com.polenta.e.schiz.orario.core.OrarioInterval;
import com.polenta.e.schiz.orario.core.OrarioWeek;
import com.polenta.e.schiz.orario.core.OrarioYear;

public class OrarioTableLabelProvider implements ITableLabelProvider
{

    public Image getColumnImage(Object element, int columnIndex)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getColumnText(Object element, int columnIndex)
    {
        String s = null;
        
        if (element instanceof OrarioDay)
        {
            //OrarioDay od = (OrarioDay) element;
            if(columnIndex == 0)
            	s = "day";
            else if(columnIndex < 7)
                s = "NA";//od.getElement(columnIndex);
            else
                s = "--";
        }
        if(element instanceof OrarioYear)
        {
            if(columnIndex == 0)
            	s = "year";
            else if(columnIndex < 7)
                s = "c" + columnIndex;
            else
                s = "--";
        }
        if(element instanceof OrarioWeek)
        {
            if(columnIndex == 0)
            	s = "week";
            else if(columnIndex < 7)
                s = "c" + columnIndex;
            else
                s = "--";
        }
        if(element instanceof OrarioInterval)
        {
        	OrarioInterval interval = (OrarioInterval) element;
        	switch(columnIndex)
        	{
        	case 0:
        		s = interval.getActivityName();
        		break;
        	case 1:
        		s = interval.getActivityType();
        		break;
        	case 2:
        		s = OrarioCoreUtil.formatToHours(interval.getStart());
        		break;
        	case 3:
        		s = OrarioCoreUtil.formatToHours(interval.getEnd());
        		break;
        	default:
        		s = "";
        		break;
        	}
        }
        
        return s;
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

}
