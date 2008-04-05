package com.polenta.e.schiz.orario.editors;


import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

import com.polenta.e.schiz.orario.core.OrarioCoreUtil;
import com.polenta.e.schiz.orario.core.OrarioInterval;

public class OrarioCellModifier implements ICellModifier {

	public boolean canModify(Object element, String property)
	{
		if (element instanceof OrarioInterval)
		{
			switch( property.charAt(0))
			{
			case 'n':
			case 't':
			case 's':
			case 'e':
				return true;
			case 'c':
				//c - comment, non modifiable
				break;
			}
		}
		return false;
	}

	public Object getValue(Object element, String property)
	{
		Object result = null;
		if(element instanceof OrarioInterval)
		{
			OrarioInterval interval = (OrarioInterval) element;
			switch(property.charAt(0))
			{
			case 'n':
				//n - activity name
				result = interval.getActivityName();
				break;
			case 't':
				//t - activity type
				result = interval.getActivityType();
				break;
			case 's':
				//s - interval start
				result = "" + OrarioCoreUtil.formatToHours(interval.getStart());
				break;
			case 'e':
				//e - interval end
				result = "" + OrarioCoreUtil.formatToHours(interval.getEnd());
				break;
			case 'c':
				//c - comment
				result = "";
				break;
			}
		}
		return result;
	}

	public void modify(Object element, String property, Object value)
	{
		//an Item could be passed instead of the object directly
		if (element instanceof Item)
			element = ((Item) element).getData();

		if(element instanceof OrarioInterval)
		{
			OrarioInterval interval = (OrarioInterval) element;
			switch(property.charAt(0))
			{
			case 'n':
				//n - activity name
				interval.setActivity((String) value, interval.getActivityType());
				break;
			case 't':
				//t - activity type
				interval.setActivity(interval.getActivityName(), (String) value);
				break;
			case 's':
				//s - interval start
				break;
			case 'e':
				//e - interval end
				break;
			case 'c':
				//c - comment
				//should never happen
				break;
			}
		}
	}

}
