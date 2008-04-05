package com.polenta.e.schiz.orario.editors;


import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.polenta.e.schiz.orario.core.IOrario;
import com.polenta.e.schiz.orario.core.IOrarioModelListener;

/**
 * Outline page for an orario timesheet.
 * The worked time is shown in a hierarchy
 * year - week - day.
 * 
 */
public class OrarioOutlinePage extends ContentOutlinePage implements IOrarioModelListener
{
	//the model
	private IOrario orario;
	
	/**
	 * Constructor, the model is passed as parameter.
	 * 
	 * @param orario
	 */
	public OrarioOutlinePage(IOrario orario)
	{
		super();
		this.orario = orario;
	}

	@Override
	public void createControl(Composite parent)
	{
		TreeViewer tw;
		
		super.createControl(parent);
		tw = super.getTreeViewer();

        tw.getTree().setHeaderVisible(false);

        tw.setContentProvider(new OrarioContentProvider());
        tw.setLabelProvider(new OrarioLabelProvider(this.getSite().getShell().getDisplay()));
        
        tw.addFilter(new OrarioActivityFilter());

        tw.setInput(orario);
	}

	/**
	 * @param source
	 */
	public void modelChanged(IOrario source)
	{
	}

}
