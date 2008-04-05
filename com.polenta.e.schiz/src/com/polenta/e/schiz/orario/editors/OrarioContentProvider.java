package com.polenta.e.schiz.orario.editors;


import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.polenta.e.schiz.orario.core.IDayComposite;
import com.polenta.e.schiz.orario.core.IOrario;
import com.polenta.e.schiz.orario.core.IOrarioModelListener;
import com.polenta.e.schiz.orario.core.OrarioFormat;

public class OrarioContentProvider implements ITreeContentProvider, IOrarioModelListener
{
	private IOrario source;
	private Viewer viewer;
	
	public Object[] getChildren(Object parentElement)
	{
		if(parentElement instanceof IDayComposite)
		{
			Object[] result = ((IDayComposite)parentElement).getDays().toArray();
			int nullcount = 0;
			for (Object object : result)
				if(object == null)
					nullcount++;

			if(nullcount > 0)
			{
				Object[] temp = new Object[result.length - nullcount];
				int index = 0;
				for (Object object : result)
					if(object != null)
						temp[index++] = object;
			}
			return result;
		}
		
		return null;
	}

	public Object getParent(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element)
	{
		//only year and week have children
		if(element instanceof IDayComposite)
			return true;
		else
			return false;
	}

	public Object[] getElements(Object inputElement)
	{
		if(source instanceof OrarioFormat)
			return ((OrarioFormat)source).getYears().toArray();
		return null;
	}

	public void dispose()
	{
		//deregister from model
		if(source != null)
			source.removeListener(this);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
        if(newInput instanceof IOrario && newInput != source)
        {
        	if(source != null)
        		source.removeListener(this);

        	source = (IOrario) newInput;
        	source.addListener(this);
        }

        this.viewer = viewer;
	}

	/* (non-Javadoc)
	 * @see orario.core.IOrarioModelListener#modelChanged(orario.core.IOrario)
	 */
	public void modelChanged(IOrario source)
	{
		if(viewer != null)
			viewer.refresh();
	}

}
