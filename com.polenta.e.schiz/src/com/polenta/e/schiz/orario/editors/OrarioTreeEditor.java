package com.polenta.e.schiz.orario.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.polenta.e.schiz.orario.core.IOrario;
import com.polenta.e.schiz.orario.core.OrarioFormat;
import com.polenta.e.schiz.orario.core.OrarioModel;
import com.polenta.e.schiz.orario.core.OrarioParser;

public class OrarioTreeEditor extends EditorPart
{
    private TreeViewer viewer;
    private OrarioActivityFilter viewFilter;
    
    private Combo activityNameSelector;
    private Combo activityTypeSelector;
    private ArrayList<String> activityNames = new ArrayList<String>(); 
    private ArrayList<String> activityTypes = new ArrayList<String>(); 
    private IOrario model;
    private IOrario orarioview;
    private Font font = null;
    
    private SelectionListener NameComboListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e)
		{
		}
		public void widgetSelected(SelectionEvent e)
		{
			int index = activityNameSelector.getSelectionIndex();
			String item = index < 0 ? "" : activityNameSelector.getItem(index);
			
			if(item != null)
			{
				System.out.println(item);
				
				if(viewFilter != null)
				{
					viewFilter.resetFilter();
					if(e.data != null && e.data instanceof String)
						viewFilter.addNameToFilter(item);
					viewer.refresh();
				}
			}
			else
				System.out.println("null selection");
		}
	};
    
    public OrarioTreeEditor()
    {
        super();
    }
    
    public void doSave(IProgressMonitor monitor)
    {
        // TODO Auto-generated method stub

    }

    public void doSaveAs()
    {
        // TODO Auto-generated method stub

    }

    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException
    {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
        if (input instanceof IPathEditorInput)
        {
            IPathEditorInput pinput = (IPathEditorInput) input;
            IPath p = pinput.getPath();
            File f = p.toFile();
            //read the file here
            try
            {
                Reader in = new FileReader(f);
               	readXMLFile(in);
            }
            catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }

    public boolean isDirty()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSaveAsAllowed()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void createPartControl(Composite parent)
    {
        GridLayout layout;
        GridData gd;
        
        font = new Font (this.getSite().getShell().getDisplay(), "Courier", 8, SWT.NORMAL);
        
        layout = new GridLayout(4, false);
        parent.setLayout(layout);
        
        gd = new GridData(GridData.BEGINNING);
        
        viewFilter = new OrarioActivityFilter();
        
        Label activityName = new Label(parent, SWT.RIGHT);
        activityName.setText("Activity:");
        
        activityNameSelector = new Combo(parent,SWT.DROP_DOWN | SWT.READ_ONLY);
        activityNameSelector.setLayoutData(gd);
        activityNameSelector.setVisibleItemCount(20);       
        activityNameSelector.addSelectionListener(NameComboListener);
        activityNameSelector.add("<no filter>");
        for (String name : activityNames)
			activityNameSelector.add(name);
        
        Label activityType = new Label(parent, SWT.RIGHT);
        activityType.setText("Type:");
        
        activityTypeSelector = new Combo(parent,SWT.DROP_DOWN | SWT.READ_ONLY);
        activityTypeSelector.setVisibleItemCount(20);
        activityTypeSelector.add("<no filter>");
        for (String name : activityTypes)
        	activityTypeSelector.add(name);
        
        gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 4;
        
        viewer = new TreeViewer(parent);
        viewer.getTree().setLayoutData(gd);
        viewer.getTree().setHeaderVisible(false);
        viewer.getTree().setFont(font);

        viewer.addFilter(viewFilter);
        viewer.setContentProvider(new OrarioContentProvider());
        viewer.setLabelProvider(new OrarioLabelProvider(this.getSite().getShell().getDisplay()));
        
        getSite().setSelectionProvider(viewer);
        
        viewer.setInput(orarioview);
        
    }

    public void setFocus()
    {
        viewer.getTree().setFocus();
    }

    private void readXMLFile(Reader in)
    {
    	if(model == null)
    		model = new OrarioModel(null);
    	OrarioParser.parseXMLFile(in, model, activityNames, activityTypes);
    	Collections.sort(activityNames);
    	orarioview = new OrarioFormat(model);
    }

	public void dispose()
	{
		super.dispose();
		if(font != null)
			font.dispose();
	}
}
